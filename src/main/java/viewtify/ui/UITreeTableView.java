/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import javafx.scene.Node;
import javafx.scene.control.TreeTableView;
import javafx.scene.text.Text;

import viewtify.View;

/**
 * @version 2017/11/15 9:54:15
 */
public class UITreeTableView<T> extends UI<UITreeTableView, TreeTableView<T>> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UITreeTableView(TreeTableView<T> ui, View view) {
        super(ui, view);
    }

    public UITreeTableView placeholder(String text) {
        return placeholder(new Text(text));
    }

    public UITreeTableView placeholder(Node node) {
        ui.setPlaceholder(node);
        return this;
    }
}
