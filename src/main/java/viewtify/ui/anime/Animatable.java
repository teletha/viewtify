/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.anime;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import kiss.WiseRunnable;
import viewtify.ui.UserInterfaceProvider;

public interface Animatable {

    /**
     * Shorhand animation.
     * 
     * @param node
     */
    default void play(Node node) {
        play(node, null);
    }

    /**
     * Shorhand animation.
     * 
     * @param provider
     */
    default void play(UserInterfaceProvider<? extends Node> provider) {
        play(provider.ui());
    }

    /**
     * Shorhand animation.
     * 
     * @param node
     */
    default void play(Node node, WiseRunnable action) {
        Parent parent = node.getParent();
        if (parent instanceof Pane pane) {
            run(pane, node, action);
        }
    }

    /**
     * Shorhand animation.
     * 
     * @param provider
     * @param action
     */
    default void play(UserInterfaceProvider<? extends Node> provider, WiseRunnable action) {
        play(provider.ui(), action);
    }

    /**
     * Run animation.
     * 
     * @param parent
     * @param befor
     * @param action
     */
    void run(Pane parent, Node befor, WiseRunnable action);
}
