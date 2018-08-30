/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.function.BiConsumer;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import kiss.Extensible;
import kiss.I;
import kiss.Manageable;
import kiss.Singleton;
import kiss.model.Model;
import viewtify.dsl.UIDefinition;
import viewtify.ui.UITableColumn;
import viewtify.ui.UITreeTableColumn;
import viewtify.ui.UserInterface;
import viewtify.ui.UserInterfaceProvider;
import viewtify.util.TextNotation;

/**
 * @version 2018/08/29 10:16:30
 */
public abstract class View<B extends Extensible> implements Extensible, UserInterfaceProvider {

    /** The human-readable ID separator. */
    public static final String IDSeparator = " ➝ ";

    /** The associated root node. */
    private Node root;

    /** The parent view. */
    private View<?> parent;

    /** The flag whether this view is sub or not. */
    private String prefix;

    /** The associated message resources. */
    private final Class<B> messageClass;

    /** The associated message resources. */
    protected final B message;

    /**
     * Use class name as view name.
     */
    protected View() {
        Type[] types = Model.collectParameters(getClass(), View.class);
        this.messageClass = (Class<B>) (types == null || types.length == 0 ? Φ.class : types[0]);
        this.message = I.i18n(messageClass);

        // initialize UI structure
        if (isUIDefiend()) {
            // initialized = true;
            // try {
            // buildUI();
            //
            // this.root = declareUI().build();
            //
            // localize(message, messageClass, Label.class, Label::setText);
            // localize(message, messageClass, Button.class, Button::setText);
            // } catch (Exception e) {
            // e.printStackTrace();
            // throw I.quiet(e);
            // }
        } else {
            initialized = true;
            try {
                this.root = new FXMLLoader(load("fxml")).load();
            } catch (Exception e) {
                // FXML for this view is not found, use parent view's root
            }

            // initialize user system lazily
            Platform.runLater(this::init);
        }
    }

    /**
     * Load the file by name.
     * 
     * @return
     */
    URL load(String extension) {
        String name = getClass().getSimpleName() + "." + extension;

        // find from view package
        URL u = getClass().getResource(name);

        if (u == null) {
            u = ClassLoader.getSystemResource(name);
        }
        return u;
    }

    private boolean isUIDefiend() {
        try {
            getClass().getDeclaredMethod("declareUI");
            return true;
        } catch (NoSuchMethodException | SecurityException e) {
            return false;
        }
    }

    private void buildUI() {
        try {
            // Inject various types
            for (Field field : getClass().getDeclaredFields()) {
                Class<?> type = field.getType();
                field.setAccessible(true);

                if (View.class.isAssignableFrom(type)) {
                    // check from call stack
                    View view = findViewFromParent(type);

                    if (view == null) {
                        view = View.build((Class<View>) type);
                        view.parent = this;

                        // check sub view
                        if (type.getEnclosingClass() == getClass()) {
                            view.prefix = field.getName();
                        }
                    }
                    field.set(this, view);

                    // if view has been associated with xml and current view has Pane node which
                    // id equals to field
                    // name, we should connect them.
                    if (view.root != null) {
                        replace(root().lookup("#" + field.getName()), view.root);
                    }
                } else if (UserInterface.class.isAssignableFrom(type)) {
                    // find by id
                    Type t = Model.collectParameters(field.getType(), UserInterface.class)[1];
                    Class c;

                    if (t instanceof ParameterizedType) {
                        c = (Class) ((ParameterizedType) t).getRawType();
                    } else {
                        c = (Class) t;
                    }
                    Object node = I.make(c);

                    if (node instanceof Node) {
                        ((Node) node).setId(field.getName());
                    }

                    Node value = (Node) field.get(this);

                    if (value == null) {
                        if (type == TableColumn.class || type == UITableColumn.class || type == TreeTableColumn.class || type == UITreeTableColumn.class) {
                            // TableColumn returns c.s.jfx.scene.control.skin.TableColumnHeader
                            // so we must unwrap to javafx.scene.control.TreeTableColumn
                            node = ((javafx.scene.control.skin.TableColumnHeader) node).getTableColumn();
                        }

                        if (type.getName().startsWith("viewtify.ui.")) {
                            // viewtify ui widget
                            Constructor constructor = Model.collectConstructors(type)[0];
                            constructor.setAccessible(true);

                            field.set(this, constructor.newInstance(node, this));
                        } else {
                            // javafx ui
                            field.set(this, node);

                            enhanceNode(node);
                        }
                    } else {
                        replace((Node) node, value);
                    }
                }
            }

            Platform.runLater(this::initialize);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * Initialization for system.
     */
    final synchronized void init() {
        try {
            // Inject various types
            for (Field field : getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(UI.class)) {
                    field.setAccessible(true);

                    Class<?> type = field.getType();

                    if (View.class.isAssignableFrom(type)) {
                        // check from call stack
                        View view = findViewFromParent(type);

                        if (view == null) {
                            view = build((Class<View>) type);
                            view.parent = this;

                            // check sub view
                            if (type.getEnclosingClass() == getClass()) {
                                view.prefix = field.getName();
                            }
                        }
                        field.set(this, view);

                        // if view has been associated with xml and current view has Pane node which
                        // id equals to field
                        // name, we should connect them.
                        if (view.root != null) {
                            replace(root().lookup("#" + field.getName()), view.root);
                        }
                    } else {
                        // detect widget id
                        String id = field.getName();

                        if (prefix != null) {
                            id = prefix + capitalize(id);
                        }
                        id = "#" + id;

                        // find by id
                        Object node = root().lookup(id);

                        if (node == null) {
                            // If this exception will be thrown, it is bug of this program. So
                            // we must rethrow the wrapped error in here.
                            throw new Error(name() + ": Node [" + id + "] is not found.");
                        }

                        Node value = (Node) field.get(this);

                        if (value == null) {
                            if (type == TableColumn.class || type == UITableColumn.class || type == TreeTableColumn.class || type == UITreeTableColumn.class) {
                                // TableColumn returns c.s.jfx.scene.control.skin.TableColumnHeader
                                // so we must unwrap to javafx.scene.control.TreeTableColumn
                                node = ((javafx.scene.control.skin.TableColumnHeader) node).getTableColumn();
                            }

                            if (type.getName().startsWith("viewtify.ui.")) {
                                // viewtify ui widget
                                Constructor constructor = Model.collectConstructors(type)[0];
                                constructor.setAccessible(true);

                                field.set(this, constructor.newInstance(node, this));
                            } else {
                                // javafx ui
                                field.set(this, node);

                                enhanceNode(node);
                            }
                        } else {
                            replace((Node) node, value);
                        }
                    }
                }
            }
            initialize();
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * Replace the target node with replacer.
     * 
     * @param replaced
     * @param replacer
     */
    private void replace(Node replaced, Node replacer) {
        if (replaced == null) {
            return;
        }

        Parent parent = replaced.getParent();

        if (parent instanceof BorderPane) {
            BorderPane border = (BorderPane) parent;
            if (border.getCenter() == replaced) {
                border.setCenter(replacer);
            } else if (border.getTop() == replaced) {
                border.setTop(replacer);
            } else if (border.getBottom() == replaced) {
                border.setBottom(replacer);
            } else if (border.getRight() == replaced) {
                border.setRight(replacer);
            } else if (border.getLeft() == replaced) {
                border.setLeft(replacer);
            }
        } else if (parent instanceof Pane) {
            Pane pane = (Pane) parent;
            ObservableList<Node> children = pane.getChildren();
            children.set(children.indexOf(replaced), replacer);
        }
    }

    /**
     * Capitalize helper.
     * 
     * @param value
     * @return
     */
    private String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    /**
     * Find the specified typed view from parent view stack.
     * 
     * @param type A target type.
     * @return
     */
    <V extends View> V findViewFromParent(Class type) {
        if (type.isInstance(this)) {
            return (V) this;
        }
        return parent == null ? null : parent.findViewFromParent(type);
    }

    /**
     * Enhance Node.
     */
    private void enhanceNode(Object node) {
        if (node instanceof Spinner) {
            Spinner spinner = (Spinner) node;
            spinner.setOnScroll(e -> {
                if (e.getDeltaY() > 0) {
                    spinner.increment();
                } else if (e.getDeltaY() < 0) {
                    spinner.decrement();
                }
            });
        }
    }

    /**
     * Describe your initialization.
     */
    protected abstract void initialize();

    protected UIDefinition declareUI() {
        return null;
    }

    /**
     * Compute human-readable name for this view. Default is simple class name.
     * 
     * @return
     */
    protected String name() {
        return getClass().getSimpleName();
    }

    /**
     * Compute computer-awarable identifier for this view.
     * 
     * @return
     */
    public final String id() {
        return parent == null ? name() : parent.name() + IDSeparator + name();
    }

    /**
     * Retrieve the root node.
     * 
     * @return
     */
    public final <N extends Parent> N root() {
        return root != null ? (N) root : parent != null ? parent.root() : (N) Viewtify.stage.getScene().getRoot();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node ui() {
        return root();
    }

    /**
     * Localized by the specified resource class.
     * 
     * @param resourceClass
     */
    protected final <R extends Extensible> R localizeBy(Class<R> resourceClass) {
        R resource = I.i18n(resourceClass);

        localize(resource, resourceClass, Label.class, Label::setText);
        localize(resource, resourceClass, Button.class, Button::setText);

        return resource;
    }

    /**
     * Localize the node text.
     * 
     * @param resource
     * @param type
     * @param localizer
     */
    private <R extends Extensible, T> void localize(R resource, Class<R> resourceClass, Class<T> type, BiConsumer<T, String> localizer) {
        for (Node node : root.lookupAll("." + type.getSimpleName().toLowerCase())) {
            String id = node.getId();

            if (id != null) {
                try {
                    Method method = resourceClass.getDeclaredMethod(id);
                    method.setAccessible(true);
                    Object m = method.invoke(resource);

                    if (m != null) {
                        String message = String.valueOf(m);

                        if (message != null) {
                            if (message.contains("[") && node instanceof Label && node.getParent() instanceof Pane) {
                                Pane parent = (Pane) node.getParent();
                                ObservableList<Node> children = parent.getChildren();
                                int index = children.indexOf(node);

                                Node flow = TextNotation.parse(message);
                                flow.setId(node.getId());
                                flow.getStyleClass().addAll(node.getStyleClass());
                                children.set(index, flow);
                            } else {
                                localizer.accept((T) node, message);
                            }
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Build {@link View} properly.
     * 
     * @param viewType
     * @return
     */
    public static <V extends View> V build(Class<V> viewType) {
        V view = I.make(viewType);
        view.initializeLazy();
        return view;
    }

    /** The initialization state. */
    private boolean initialized;

    /**
     * Initialize myself.
     */
    final synchronized void initializeLazy() {
        if (initialized == false) {
            initialized = true;

            // initialize user system lazily
            try {
                buildUI();

                this.root = declareUI().build();

                localize(message, messageClass, Label.class, Label::setText);
                localize(message, messageClass, Button.class, Button::setText);
            } catch (Exception e) {
                e.printStackTrace();
                throw I.quiet(e);
            }
        }
    }

    /**
     * Empty resource.
     * 
     * @version 2018/08/30 1:55:51
     */
    @Manageable(lifestyle = Singleton.class)
    private static class Φ implements Extensible {
    }
}
