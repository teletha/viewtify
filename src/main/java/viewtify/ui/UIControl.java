/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.function.Consumer;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;

import viewtify.View;

/**
 * @version 2017/12/05 16:14:25
 */
public class UIControl<Self extends UIControl, W extends Control> extends UserInterface<Self, W> {

    private UIContextMenu context;

    /**
     * @param ui
     * @param view
     */
    protected UIControl(W ui, View view) {
        super(ui, view);
    }

    public Self context(Consumer<UIContextMenu> builder) {
        if (context == null) {
            ContextMenu root = new ContextMenu();
            ui.setContextMenu(root);

            context = new UIContextMenu(root);
            builder.accept(context);
        }
        return (Self) this;
    }
}
