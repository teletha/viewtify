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
 * @version 2017/11/29 8:22:19
 */
public abstract class Viewty implements Extensible {

    /** The associated root node. */
    private final Node root;

    /** The flag for system initialization. */
    private boolean initialized = false;

    /**
     * Use class name as view name.
     */
    protected Viewty() {
        this(null);
    }

    /**
     * View name.
     * 
     * @param name
     */
    protected Viewty(String name) {
        if (name == null || name.isEmpty()) {
            name = getClass().getSimpleName();
        }

        try {
            this.root = new FXMLLoader(ClassLoader.getSystemResource(name + ".fxml")).load();
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * Initialization for Viewtify.
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

                        if (Viewty.class.isAssignableFrom(type)) {
                            // viewtify view
                            field.set(this, Viewtify.create((Class<? extends Viewty>) type));
                        } else {
                            String id = "#" + field.getName();

                            Object node = root.lookup(id);

                            if (node == null) {
                                // If this exception will be thrown, it is bug of this program. So
                                // we must rethrow the wrapped error in here.
                                throw new Error("Node [" + id + "] is not found.");
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

                // user initialization
                initialize();
            } catch (Exception e) {
                throw I.quiet(e);
            }
        }
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
     * Compute identifier for this view. Default is class name.
     * 
     * @return
     */
    public String id() {
        return getClass().getSimpleName();
    }

    /**
     * Retrieve the root node.
     * 
     * @return
     */
    public final <N extends Node> N root() {
        return (N) root;
    }
}
