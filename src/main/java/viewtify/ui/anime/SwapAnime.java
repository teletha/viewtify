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
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import kiss.WiseRunnable;

public interface SwapAnime {

    /** Built-in swap animation. */
    SwapAnime FadeOutIn = (parent, before, after, action) -> {
        Anime.define(() -> after.setOpacity(0))
                .effect(before.opacityProperty(), 0, 0.15)
                .then(action)
                .effect(after.opacityProperty(), 1, 0.15)
                .run();
    };

    /** Built-in animation. */
    SwapAnime ZoomIn = (parent, before, after, action) -> zoom(parent, before, after, action, -0.15);

    /** Built-in animation. */
    SwapAnime ZoomOut = (parent, before, after, action) -> zoom(parent, before, after, action, 0.15);

    /** Built-in animation. */
    SwapAnime SlideLeft = (parent, before, after, action) -> {
        Anime.define()
                .init(after.translateXProperty(), 400)
                .init(after.opacityProperty(), 0)
                .duration(0.12)
                .effect(before.translateXProperty(), -400)
                .effect(before.opacityProperty(), 0)
                .then(action)
                .effect(after.translateXProperty(), 0)
                .effect(after.opacityProperty(), 1)
                .run();
    };

    /** Built-in animation. */
    SwapAnime SlideRight = (parent, before, after, action) -> {
        Anime.define()
                .init(after.translateXProperty(), -400)
                .init(after.opacityProperty(), 0)
                .duration(0.12)
                .effect(before.translateXProperty(), 400)
                .effect(before.opacityProperty(), 0)
                .then(action)
                .effect(after.translateXProperty(), 0)
                .effect(after.opacityProperty(), 1)
                .run();
    };

    /**
     * Run zoom animation.
     * 
     * @param parent
     * @param before
     * @param action
     * @param diff
     */
    private static void zoom(Pane parent, Node before, Node after, WiseRunnable action, double diff) {
        Node clip = parent.getClip();
        int index = parent.getChildren().indexOf(before);

        parent.getChildren().add(index, new StackPane(after, before));
        parent.setClip(new Rectangle(parent.getWidth(), parent.getHeight()));
        after.setOpacity(0);
        after.setScaleX(1 - diff);
        after.setScaleY(1 - diff);

        Anime.define()
                .effect(before.opacityProperty(), 0)
                .effect(before.scaleXProperty(), 1 + diff)
                .effect(before.scaleYProperty(), 1 + diff)
                .then()
                .effect(after.opacityProperty(), 1)
                .effect(after.scaleXProperty(), 1 + diff)
                .effect(after.scaleYProperty(), 1 + diff)
                .run(() -> parent.setClip(clip), action);
    }

    void run(Pane parent, Node before, Node after, WiseRunnable action);
}