/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import viewtify.ui.UserInterfaceProvider;

public interface ContainerHelper<Self extends ContainerHelper, P extends Pane> extends UserInterfaceProvider<P> {

    /**
     * Set the first child content.
     * 
     * @param provider
     * @return
     */
    default Self content(UserInterfaceProvider<Node> provider) {
        if (provider != null) {
            Node node = provider.ui();
            ObservableList<Node> children = ui().getChildren();

            if (children.isEmpty()) {
                children.add(node);
            } else {
                children.set(0, node);
            }
        }
        return (Self) this;
    }
}
