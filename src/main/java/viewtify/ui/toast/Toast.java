/*
 * Copyright (C) 2020 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.toast;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;

import stylist.Style;
import stylist.StyleDSL;
import viewtify.ui.helper.StyleHelper;

public class Toast {

    /** The base transparent window. */
    private static final Popup base = new Popup();

    /**
     * Show the specified node.
     * 
     * @param node
     */
    public static synchronized void show(String message) {
        show(new Label(message));
    }

    /**
     * Show the specified node.
     * 
     * @param node
     */
    public static synchronized void show(Node node) {
        base.setX(0);
        base.setY(200);

        VBox box = new VBox(node);
        StyleHelper.of(box).style(Styles.pop);

        ObservableList<Node> content = base.getContent();
        content.clear();
        content.add(box);

        Window window = Window.getWindows().get(0);
        base.show(window);
    }

    private static interface Styles extends StyleDSL {
        Style pop = () -> {
            display.width(250, px).opacity(0.75);
            padding.vertical(7, px).horizontal(9, px);
            background.color("-fx-background");
            border.radius(5, px);
        };
    }
}
