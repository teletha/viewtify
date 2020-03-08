/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import javafx.geometry.Side;
import javafx.scene.Node;

import org.controlsfx.control.HiddenSidesPane;

import viewtify.ui.helper.ContextMenuHelper;

public class UISlidablePane extends UserInterface<UISlidablePane, HiddenSidesPane> implements ContextMenuHelper<UISlidablePane> {

    /**
     * @param ui
     * @param view
     */
    public UISlidablePane(View view) {
        super(new HiddenSidesPane(), view);
    }

    public UISlidablePane set(Side side, UserInterfaceProvider contents) {
        return set(side, contents.ui().getStyleableNode());
    }

    public UISlidablePane set(Side side, Node contents) {
        switch (side) {
        case BOTTOM:
            ui.setBottom(contents);
            break;

        case TOP:
            ui.setTop(contents);
            break;

        case RIGHT:
            ui.setRight(contents);
            break;

        case LEFT:
            ui.setLeft(contents);
            break;
        }
        return this;
    }
}
