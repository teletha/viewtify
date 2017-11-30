/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeTableColumn;

import kiss.Extensible;
import kiss.I;
import kiss.model.Model;

/**
 * @version 2017/11/30 12:43:32
 */
public abstract class View implements Extensible {

    /** The human-readable ID separator. */
    public static final String IDSeparator = " ➝ ";

    /** The associated root node. */
    private Node root;

    /** The parent view. */
    private View parent;

    /** The initialization flag. */
    private boolean initialized = false;

    /**
     * Use class name as view name.
     */
    protected View() {
        // initialize UI structure
        try {
            this.root = new FXMLLoader(ClassLoader.getSystemResource(getClass().getSimpleName() + ".fxml")).load();
        } catch (Exception e) {
            // FXML for this view is not found, use parent view's root
        }

        // initialize user system lazily
        Viewtify.inUI(this::init);
    }

    /**
     * Initialization for system.
     */
    final synchronized void init() {
        if (initialized == false) {
            initialized = true;

            try {
                // Inject various types
                for (Field field : getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(FXML.class)) {
                        field.setAccessible(true);

                        Class<?> type = field.getType();

                        if (View.class.isAssignableFrom(type)) {
                            // check from call stack
                            View view = findViewFromParent(type);

                            if (view == null) {
                                view = I.make((Class<View>) type);
                                view.parent = this;
                            }
                            field.set(this, view);
                        } else {
                            String id = "#" + field.getName();

                            Object node = root().lookup(id);

                            if (node == null) {
                                // If this exception will be thrown, it is bug of this program. So
                                // we must rethrow the wrapped error in here.
                                throw new Error(name() + ": Node [" + id + "] is not found.");
                            }

                            if (type == TableColumn.class || type == TreeTableColumn.class) {
                                // TableColumn returns c.s.jfx.scene.control.skin.TableColumnHeader
                                // so we must unwrap to javafx.scene.control.TreeTableColumn
                                node = ((com.sun.javafx.scene.control.skin.TableColumnHeader) node).getTableColumn();
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
                        }
                    }
                }
                initialize();
            } catch (Exception e) {
                throw I.quiet(e);
            }
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
    public final <N extends Node> N root() {
        return root != null ? (N) root : parent != null ? parent.root() : (N) Viewtify.stage.getScene().getRoot();
    }
}
