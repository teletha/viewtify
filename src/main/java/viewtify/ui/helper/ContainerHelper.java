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
import kiss.Disposable;
import kiss.Variable;
import viewtify.ui.UserInterfaceProvider;
import viewtify.ui.anime.SwapAnime;
import viewtify.util.FXUtils;

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
            FXUtils.setAssociation(after, Disposable.class, provider);

            P parent = ui();
            ObservableList<Node> children = parent.getChildren();

            if (children.isEmpty()) {
                children.add(after);
            } else {
                Node before = children.get(0);
                if (before != after && anime != null && 0 < anime.length) {
                    anime[0].run(parent, before, after, () -> {
                        children.set(0, after);

                        FXUtils.getAssociation(before, Disposable.class).to(Disposable::dispose);
                    });
                }
            }
        }
        return (Self) this;
    }

    /**
     * Find the first child node.
     * 
     * @return
     */
    default Variable<Node> first() {
        ObservableList<Node> children = ui().getChildren();
        return children.isEmpty() ? Variable.empty() : Variable.of(children.get(0));
    }

    /**
     * Find the last child node.
     * 
     * @return
     */
    default Variable<Node> last() {
        ObservableList<Node> children = ui().getChildren();
        return children.isEmpty() ? Variable.empty() : Variable.of(children.get(children.size() - 1));
    }
}