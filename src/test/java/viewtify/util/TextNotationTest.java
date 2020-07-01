/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;

import org.junit.jupiter.api.Test;

import viewtify.JavaFXTester;

/**
 * @version 2018/08/30 2:12:43
 */
class TextNotationTest extends JavaFXTester {

    @Test
    void linkOnly() {
        ObservableList<Node> nodes = as(TextNotation.parse("[link](http://test.com/)"), TextFlow.class).getChildren();

        assert nodes.size() == 1;

        Hyperlink link = link(nodes.get(0));
        assert link.getText().equals("link");
    }

    @Test
    void linkAndText() {
        ObservableList<Node> nodes = as(TextNotation.parse("[link](http://test.com/) now!"), TextFlow.class).getChildren();

        assert nodes.size() == 2;

        Hyperlink link = link(nodes.get(0));
        assert link.getText().equals("link");

        Label label = label(nodes.get(1));
        assert label.getText().equals(" now!");
    }

    /**
     * Helper method to retrieve {@link Hyperlink}.
     * 
     * @param node
     * @return
     */
    private Hyperlink link(Node node) {
        assert node instanceof Hyperlink;

        return (Hyperlink) node;
    }

    /**
     * Helper method to retrieve {@link Hyperlink}.
     * 
     * @param node
     * @return
     */
    private Label label(Node node) {
        assert node instanceof Label;

        return (Label) node;
    }
}