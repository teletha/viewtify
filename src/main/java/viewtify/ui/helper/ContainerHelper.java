/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
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
import kiss.WiseRunnable;
import viewtify.ui.UserInterfaceProvider;
import viewtify.ui.anime.SwapAnime;

public interface ContainerHelper<Self extends ContainerHelper, P extends Pane> extends UserInterfaceProvider<P> {

    /**
     * Set the first child content.
     * 
     * @param provider
     * @return
     */
    default Self content(UserInterfaceProvider<? extends Node> provider, SwapAnime... anime) {
        if (provider != null) {
            Node after = provider.ui();
            AssociativeHelper.of(after).set(Disposable.class, ContainerHelper.class.getName(), provider);

            P parent = ui();
            ObservableList<Node> children = parent.getChildren();

            if (children.isEmpty()) {
                children.add(after);
            } else {
                Node before = children.get(0);
                if (before != after && anime != null && 0 < anime.length) {
                    anime[0].run(parent, before, after, () -> {
                        children.set(0, after);

                        AssociativeHelper.of(before).get(Disposable.class, ContainerHelper.class.getName()).to(Disposable::dispose);
                    });
                }
            }
        }
        return (Self) this;
    }

    /**
     * Set the first child content.
     * 
     * @param provider
     * @return
     */
    default Self content(UserInterfaceProvider<? extends Node> provider, WiseRunnable finisher, SwapAnime... anime) {
        if (provider != null) {
            Node after = provider.ui();
            AssociativeHelper.of(after).set(Disposable.class, ContainerHelper.class.getName(), provider);

            P parent = ui();
            ObservableList<Node> children = parent.getChildren();

            if (children.isEmpty()) {
                children.add(after);
            } else {
                Node before = children.get(0);
                if (before != after && anime != null && 0 < anime.length) {
                    anime[0].run(parent, before, after, () -> {
                        children.set(0, after);

                        AssociativeHelper.of(before).get(Disposable.class, ContainerHelper.class.getName()).to(Disposable::dispose);
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