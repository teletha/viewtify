/*
 * Copyright (C) 2021 viewtify Development Team
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
     * @param view
     */
    public UIPane(View view) {
        super(new Pane(), view);
    }

    /**
     * @param pane
     * @param view
     */
    public UIPane(Pane pane, View view) {
        super(pane, view);
    }

    /**
     * Set the single child content.
     * 
     * @param provider
     * @return
     */
    public final UIPane content(UserInterfaceProvider<Node> provider) {
        if (provider != null) {
            Node node = provider.ui();
            ObservableList<Node> children = ui.getChildren();

            if (children.isEmpty()) {
                children.add(node);
            } else {
                children.set(0, node);
            }
        }
        return this;
    }
}