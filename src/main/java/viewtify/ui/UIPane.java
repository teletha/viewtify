/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class UIPane extends UserInterface<UIPane, Pane> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIPane(View view) {
        super(new Pane(), view);
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
            View.build(view, this.view);

            ObservableList<Node> children = ui.getChildren();

            if (children.isEmpty()) {
                children.add(view.ui());
            } else {
                children.set(0, view.ui());
            }
        }
        return this;
    }
}
