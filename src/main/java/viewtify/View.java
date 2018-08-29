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

import java.awt.Desktop;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.function.BiConsumer;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;

import kiss.Extensible;
import kiss.I;
import kiss.model.Model;
import viewtify.fxml.FXML;
import viewtify.ui.UITableColumn;
import viewtify.ui.UITreeTableColumn;
import viewtify.ui.UserInterface;

/**
 * @version 2018/08/29 10:16:30
 */
public abstract class View implements Extensible {

    /** The human-readable ID separator. */
    public static final String IDSeparator = " ➝ ";

    /** The associated root node. */
    private Node root;

    /** The parent view. */
    private View parent;

    /** The flag whether this view is sub or not. */
    private String prefix;

    /**
     * Use class name as view name.
     */
    protected View() {
        // initialize UI structure
        if (isUIDefiend()) {
            // initialize user system lazily
            try {
                buildUI();

                this.root = defineUI().build();
            } catch (Exception e) {
                e.printStackTrace();
                throw I.quiet(e);
            }
        } else {
            try {
                this.root = new FXMLLoader(findFXML()).load();
            } catch (Exception e) {
                // FXML for this view is not found, use parent view's root
            }

            // initialize user system lazily
            Platform.runLater(this::init);
        }
    }

    private boolean isUIDefiend() {
        try {
            getClass().getDeclaredMethod("defineUI");
            return true;
        } catch (NoSuchMethodException | SecurityException e) {
            return false;
        }
    }

    private void buildUI() {
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
                            view = I.make((Class<View>) type);
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
                        // find by id
                        Object node = I.make((Class) Model.collectParameters(field.getType(), UserInterface.class)[1]);

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
            }
            initialize();
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * Search FXML file by name.
     * 
     * @return
     */
    private URL findFXML() {
        String name = getClass().getSimpleName() + ".fxml";

        // find from view package
        URL u = getClass().getResource(name);

        if (u == null) {
            u = ClassLoader.getSystemResource(name);
        }
        return u;
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
                            view = I.make((Class<View>) type);
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

    protected FXML defineUI() {
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
    private <R, T> void localize(R resource, Class<R> resourceClass, Class<T> type, BiConsumer<T, String> localizer) {
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

                                TextFlow flow = composeTextFlow(message);
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

    static TextFlow composeTextFlow(String message) {
        TextFlow flow = new TextFlow();
        ObservableList<Node> children = flow.getChildren();

        boolean inLink = false;
        boolean inURL = false;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);

            switch (c) {
            case '[':
                inLink = true;
                if (builder.length() != 0) {
                    children.add(new Label(builder.toString()));
                    builder = new StringBuilder();
                }
                break;

            case ']':
                if (inLink) {
                    inLink = false;
                    if (builder.length() != 0) {
                        children.add(new Hyperlink(builder.toString()));
                        builder = new StringBuilder();

                        if (message.charAt(i + 1) == '(') {
                            inURL = true;
                            i++;
                        }
                    }
                } else {
                    builder.append(c);
                }
                break;

            case ')':
                if (inURL) {
                    inURL = false;
                    String uri = builder.toString();
                    Hyperlink link = (Hyperlink) children.get(children.size() - 1);
                    link.setOnAction(e -> {
                        try {
                            Desktop.getDesktop().browse(new URI(uri));
                        } catch (Throwable error) {
                            throw I.quiet(error);
                        }
                    });
                    builder = new StringBuilder();
                    break;
                }

            default:
                builder.append(c);
                break;
            }
        }

        if (builder.length() != 0) {
            children.add(new Label(builder.toString()));
        }

        return flow;
    }
}
