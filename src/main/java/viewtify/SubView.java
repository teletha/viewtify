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

import javafx.scene.Node;

import viewtify.dsl.UIDefinition;
import viewtify.ui.UserInterfaceProvider;

/**
 * @version 2018/08/29 21:01:19
 */
public abstract class SubView implements UserInterfaceProvider {

    protected abstract UIDefinition declareUI();

    protected abstract void initialize();

    /**
     * {@inheritDoc}
     */
    @Override
    public Node ui() {
        Node root = declareUI().build();

        initialize();

        return root;
    }
}
