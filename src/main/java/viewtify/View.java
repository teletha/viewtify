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
import java.lang.reflect.Type;
import java.util.function.BiConsumer;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
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
 * @version 2018/09/09 16:24:47
 */
public abstract class View<B extends Extensible> implements Extensible, UserInterfaceProvider {

    /** The human-readable ID separator. */
    public static final String IDSeparator = " ➝ ";

    /** The associated root node. */
    private Node root;

    /** The parent view. */
    private View<?> parent;

    /** The associated message resources. */
    private final Class<B> messageClass;

    /** The associated message resources. */
    protected final B $;

    /**
     * Use class name as view name.
     */
    protected View() {
        Model.of(View.class);
        Type[] types = Model.collectParameters(getClass(), View.class);
        this.messageClass = (Class<B>) (types == null || types.length == 0 ? Φ.class : types[0]);
        this.$ = I.i18n(messageClass);
    }

    /**
     * Declare user interface.
     * 
     * @return
     */
    protected abstract UIDefinition declareUI();

    /**
     * Initialize {@link View}.
     */
    protected abstract void initialize();

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
        return build(viewType, null);
    }

    /**
     * Build {@link View} properly.
     * 
     * @param viewType
     * @return
     */
    private static <V extends View> V build(Class<V> viewType, View parent) {
        V view = I.make(viewType);
        view.initializeLazy(parent);
        return view;
    }

    /** The initialization state. */
    private boolean initialized;

    /**
     * Initialize myself.
     */
    protected final synchronized void initializeLazy(View parent) {
        if (initialized == false) {
            initialized = true;
            this.parent = parent;

            // initialize user system lazily
            try {
                buildUI();

                this.root = declareUI().build();

                localize($, messageClass, Label.class, Label::setText);
                localize($, messageClass, Button.class, Button::setText);
            } catch (Exception e) {
                e.printStackTrace();
                throw I.quiet(e);
            }
        }
    }

    private void buildUI() {
        try {
            // Inject various types
            for (Field field : getClass().getDeclaredFields()) {
                Class<?> type = field.getType();
                field.setAccessible(true);

                if (View.class.isAssignableFrom(type)) {
                    Object assigned = field.get(this);

                    if (assigned != null) {
                        ((View) assigned).initializeLazy(this);
                    } else {
                        // check from call stack
                        View view = findViewFromParent(type);

                        if (view == null) {
                            view = View.build((Class<View>) type, this);
                            view.parent = this;
                        }
                        field.set(this, view);

                        // if view has been associated with xml and current view has Pane node which
                        // id equals to field name, we should connect them.
                        // if (view.root != null) {
                        // replace(root().lookup("#" + field.getName()), view.root);
                        // }
                    }
                } else if (UITableColumn.class == type) {
                    field.set(this, new UITableColumn());
                } else if (UITreeTableColumn.class == type) {
                    field.set(this, new UITreeTableColumn(new TreeTableColumn(), this));
                } else if (UserInterface.class.isAssignableFrom(type)) {
                    Node value = (Node) field.get(this);

                    if (value == null) {
                        if (type.getPackage() == UserInterface.class.getPackage()) {
                            Constructor constructor = Model.collectConstructors(type)[0];
                            constructor.setAccessible(true);

                            UserInterface ui = (UserInterface) constructor.newInstance(this);
                            ui.ui.setId(field.getName());

                            field.set(this, ui);
                        }
                    }
                }
            }

            Platform.runLater(this::initialize);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * Find the specified typed view from parent view stack.
     * 
     * @param type A target type.
     * @return
     */
    private <V extends View> V findViewFromParent(Class type) {
        if (type.isInstance(this)) {
            return (V) this;
        }
        return parent == null ? null : parent.findViewFromParent(type);
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
