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

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import viewtify.View;

/**
 * @version 2018/08/29 16:30:02
 */
public class UIPane extends UserInterface<UIPane, Pane> {

    /**
     * @param ui
     * @param view
     */
    public UIPane(Pane ui, View view) {
        super(ui, view);
    }

    /**
     * Set the specified {@link View}.
     * 
     * @param view
     * @return
     */
    public final <V extends View> UIPane set(Class<V> view) {
        return set(View.build(view));
    }

    /**
     * Set the specified {@link View}.
     * 
     * @param view
     * @return
     */
    public final UIPane set(View view) {
        if (view != null) {
            ObservableList<Node> children = ui.getChildren();

            if (children.isEmpty()) {
                children.add(view.root());
            } else {
                children.set(0, view.root());
            }
        }
        return this;
    }
}
