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

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import kiss.Extensible;
import kiss.I;

/**
 * @version 2017/11/29 8:22:19
 */
public abstract class Viewty implements Extensible {

    /** The associated root node. */
    private final Node root;

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
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * Describe your initialization.
     */
    protected abstract void initialize();

    /**
     * Retrieve the root node.
     * 
     * @return
     */
    public final <N extends Node> N root() {
        return (N) root;
    }
}
