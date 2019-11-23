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
import java.lang.reflect.Modifier;

import javafx.application.Platform;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.control.TableColumnBase;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import kiss.Extensible;
import kiss.I;
import kiss.Variable;
import kiss.model.Model;
import transcript.Lang;
import viewtify.Viewtify;

public abstract class View implements Extensible, UserInterfaceProvider {

    protected static Lang lang() {
        return Lang.current();
    }

    /** The human-readable ID separator. */
    public static final String IDSeparator = " ➝ ";

    /** The associated root node. */
    private Node root;

    /** The parent view. */
    private View parent;

    /**
     * Use class name as view name.
     */
    protected View() {
    }

    /**
     * Declare user interface.
     * 
     * @return
     */
    protected UI declareUI() {
        // auto detect UI definition
        for (Class<?> member : getClass().getDeclaredClasses()) {
            if (UI.class.isAssignableFrom(member)) {
                if (Modifier.isStatic(member.getModifiers())) {
                    return I.make((Class<UI>) member);
                } else {
                    for (Constructor constructor : member.getDeclaredConstructors()) {
                        Class[] paramTypes = constructor.getParameterTypes();

                        if (paramTypes.length == 1 && paramTypes[0] == getClass()) {
                            try {
                                constructor.setAccessible(true);
                                return (UI) constructor.newInstance(this);
                            } catch (Exception e) {
                                throw I.quiet(e);
                            }
                        }
                    }
                }
            }
        }
        throw I.quiet(new ClassNotFoundException(getClass() + " don't have UI definition. Define member class which is subclassed by " + UI.class
                .getName() + "."));
    }

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
     * @param view
     * @return
     */
    public static <V extends View> V build(V view) {
        return build(view, null);
    }

    /**
     * Build {@link View} properly.
     * 
     * @param viewType
     * @return
     */
    private static <V extends View> V build(Class<V> viewType, View parent) {
        return build(I.make(viewType), parent);
    }

    /**
     * Build {@link View} properly.
     * 
     * @param view
     * @return
     */
    private static <V extends View> V build(V view, View parent) {
        view.initializeLazy(parent);
        return view;
    }

    /** The initialization state. */
    private boolean initialized;

    /**
     * Initialize myself.
     */
    synchronized void initializeLazy(View parent) {
        if (initialized == false) {
            initialized = true;
            this.parent = parent;

            // initialize user system lazily
            try {
                buildUI();

                this.root = declareUI().build();
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
}
