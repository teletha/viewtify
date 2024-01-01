/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
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

public interface HideAnime extends AnimePattern {

    /** Built-in animation. */
    HideAnime FadeOut = (parent, before, action) -> Anime.define().effect(before.opacityProperty(), 0, 0.3).run(action);

    /** Built-in animation. */
    HideAnime ZoomIn = (parent, before, action) -> zoom(parent, before, action, -0.15);

    /** Built-in animation. */
    HideAnime ZoomOut = (parent, before, action) -> zoom(parent, before, action, 0.15);

    /**
     * Run pop animation.
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
                .duration(Anime.BASE_DURATION.multiply(2))
                .interpolator(Interpolators.EASE_IN_CUBIC)
                .effect(before.opacityProperty(), 0)
                .effect(before.scaleXProperty(), 1 + diff)
                .effect(before.scaleYProperty(), 1 + diff)
                .run(() -> parent.setClip(clip), action);
    }

    /** Built-in animation. */
    HideAnime PopIn = (parent, before, action) -> pop(parent, before, action, -0.2);

    /** Built-in animation. */
    HideAnime PopOut = (parent, before, action) -> pop(parent, before, action, 0.2);

    /**
     * Run pop animation.
     * 
     * @param parent
     * @param before
     * @param action
     * @param diff
     */
    private static void pop(Pane parent, Node before, WiseRunnable action, double diff) {
        Node clip = parent.getClip();
        Anime.define()
                .init(() -> parent.setClip(new Rectangle(parent.getWidth(), parent.getHeight())))
                .duration(Anime.BASE_DURATION.multiply(2))
                .interpolator(Interpolators.EASE_IN_OUT_BACK.enhance(v -> v * 2))
                .effect(before.opacityProperty(), 0)
                .effect(before.scaleXProperty(), 1 + diff)
                .effect(before.scaleYProperty(), 1 + diff)
                .run(() -> parent.setClip(clip), action);
    }
}