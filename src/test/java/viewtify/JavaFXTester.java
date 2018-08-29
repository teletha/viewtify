/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;

import org.junit.jupiter.api.BeforeAll;

/**
 * @version 2018/08/29 11:41:15
 */
public abstract class JavaFXTester {

    @BeforeAll
    static void initialize() {
        new JFXPanel();
    }

    /**
     * Helper method to retrieve {@link Hyperlink}.
     * 
     * @param node
     * @return
     */
    protected final <T extends Node> T as(Node node, Class<T> type) {
        assert type.isInstance(node);

        return (T) node;
    }
}
