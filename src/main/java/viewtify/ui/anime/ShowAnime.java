/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.anime;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import kiss.WiseRunnable;

public interface ShowAnime extends Animatable {

    /** Built-in animation. */
    static ShowAnime FadeIn(double opacity) {
        return (parent, before, action) -> {
            Anime.define().init(before.opacityProperty(), 0).effect(before.opacityProperty(), opacity, 0.3).run(action);
        };
    }

    /** Built-in animation. */
    ShowAnime ZoomIn = (parent, before, action) -> zoom(parent, before, action, -0.15);

    /** Built-in animation. */
    ShowAnime ZoomOut = (parent, before, action) -> zoom(parent, before, action, 0.15);

    /**
     * Run zoom animation.
     * 
     * @param parent
     * @param before
     * @param action
     * @param diff
     */
    private static void zoom(Pane parent, Node before, WiseRunnable action, double diff) {
        Node clip = parent.getClip();
        Anime.define()
                .init(() -> parent.setClip(new Rectangle(parent.getWidth(), parent.getHeight())))
                .effect(before.opacityProperty(), 1)
                .effect(before.scaleXProperty(), 1 + diff)
                .effect(before.scaleYProperty(), 1 + diff)
                .run(() -> parent.setClip(clip), action);
    }
}