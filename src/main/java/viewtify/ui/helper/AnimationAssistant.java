/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import viewtify.ui.UserInterfaceProvider;

public class AnimationAssistant {

    /**
     * Apply fade in effect.
     * 
     * @param ui A target UI.
     */
    public static void fadeIn(UserInterfaceProvider<? extends Node> ui) {
        fadeIn(ui.ui());
    }

    /**
     * Apply fade in effect.
     * 
     * @param node A target UI.
     */
    public static void fadeIn(Node node) {
        if (node != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(666), node);
            fade.setFromValue(0);
            fade.setToValue(node.getOpacity());
            fade.play();
        }
    }
}