/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumnBase;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import kiss.Extensible;
import kiss.I;
import kiss.Manageable;
import kiss.Singleton;
import kiss.Variable;
import kiss.model.Model;
import viewtify.Viewtify;
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
    protected abstract UI declareUI();

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
     * {@inheritDoc}
     */
    @Override
    public final Node ui() {
        if (root != null) {
            return root;
        }

        if (parent != null) {
            return parent.ui();
        }
        throw new Error();
    }

    /**
     * Returns the root {@link View}.
     * 
     * @return The root view.
     */
    public final View root() {
        return parent == null ? this : parent.root();
    }

    /**
     * Returns the {@link Screen} which this {@link View} is displayed.
     * 
     * @return The {@link Screen} which this {@link View} is displayed.
     */
    public final Screen screen() {
        Window window = root().ui().getScene().getWindow();

        for (Screen screen : Screen.getScreens()) {
            if (screen.getBounds().contains(window.getX(), window.getY())) {
                return screen;
            }
        }
        return Screen.getPrimary();
    }

    /**
     * Force to show the current application window which this {@link View} is displayed.
     */
    public final void show() {
        I.signal(stage()).skipNull().skip(Stage::isAlwaysOnTop).on(Viewtify.UIThread).to(e -> {
            e.setAlwaysOnTop(true);
            e.setAlwaysOnTop(false);
        });
    }

    /**
     * Force to blink the current application window which this {@link View} is displayed.
     */
    public final void blink() {
        I.signal(stage()).skipNull().on(Viewtify.UIThread).to(Stage::toFront);
    }

    /**
     * Find {@link Stage} of this {@link View}.
     * 
     * @return
     */
    private Variable<Stage> stage() {
        Window window = ui().getScene().getWindow();

        if (window instanceof Stage) {
            return Variable.of((Stage) window);
        } else {
            return Variable.empty();
        }
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
    final synchronized void initializeLazy(View parent) {
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

                Object assigned = field.get(this);

                if (View.class.isAssignableFrom(type)) {
                    if (assigned != null) {
                        ((View) assigned).initializeLazy(this);
                    } else {
                        View view = findViewFromAncestor(type);

                        if (view == null) {
                            view = View.build((Class<View>) type, this);
                        }

                        field.set(this, view);
                    }
                } else if (UserInterfaceProvider.class.isAssignableFrom(type)) {
                    if (assigned == null) {
                        Constructor constructor = Model.collectConstructors(type)[0];
                        constructor.setAccessible(true);

                        UserInterfaceProvider provider = (UserInterfaceProvider) constructor.newInstance(this);

                        assignId(provider.ui(), field.getName());
                        field.set(this, provider);
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
    private <V extends View> V findViewFromAncestor(Class type) {
        if (type.isInstance(this)) {
            return (V) this;
        }
        return parent == null ? null : parent.findViewFromAncestor(type);
    }

    /**
     * Assign id to {@link Styleable}.
     * 
     * @param ui
     * @param id
     */
    private void assignId(Object ui, String id) {
        if (ui instanceof Node) {
            ((Node) ui).setId(id);
        } else if (ui instanceof TableColumnBase) {
            ((TableColumnBase) ui).setId(id);
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
