/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import viewtify.ui.UserInterfaceProvider;
import viewtify.ui.anime.SwapAnime;

public interface ContainerHelper<Self extends ContainerHelper, P extends Pane> extends UserInterfaceProvider<P> {

    /**
     * Set the first child content.
     * 
     * @param provider
     * @return
     */
    default Self content(UserInterfaceProvider<Node> provider, SwapAnime... anime) {
        if (provider != null) {
            Node after = provider.ui();

            P parent = ui();
            ObservableList<Node> children = parent.getChildren();

            if (children.isEmpty()) {
                children.add(after);
            } else {
                Node before = children.get(0);
                if (before != after) {
                    SwapAnime.play(anime, parent, before, after, () -> children.set(0, after));
                }
            }
        }
        return (Self) this;
    }
}