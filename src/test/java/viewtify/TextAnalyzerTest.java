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

import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @version 2018/08/29 1:07:32
 */
class TextAnalyzerTest {

    @BeforeAll
    static void initialize() {
        new JFXPanel();
    }

    @Test
    void linkOnly() {
        ObservableList<Node> nodes = View.composeTextFlow("[link](http://test.com/)").getChildren();

        assert nodes.size() == 1;

        Hyperlink link = link(nodes.get(0));
        assert link.getText().equals("link");
    }

    @Test
    void linkAndText() {
        ObservableList<Node> nodes = View.composeTextFlow("[link](http://test.com/) now!").getChildren();

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
