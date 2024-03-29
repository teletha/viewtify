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
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import kiss.WiseRunnable;

public interface SwapAnime {

    /** Built-in swap animation. */
    SwapAnime FadeOutIn = (parent, before, after, action) -> {
        Anime.define()
                .init(after.opacityProperty(), 0)
                .duration(Anime.BASE_DURATION.multiply(0.7))
                .effect(before.opacityProperty(), 0)
                .then(action)
                .effect(after.opacityProperty(), 1)
                .run();
    };

    /** Built-in animation. */
    SwapAnime ZoomIn = (parent, before, after, action) -> zoom(parent, before, after, action, -0.15);

    /** Built-in animation. */
    SwapAnime ZoomOut = (parent, before, after, action) -> zoom(parent, before, after, action, 0.15);

    /** Built-in animation. */
    static SwapAnime slideLeft(WiseRunnable... finisher) {
        return (parent, before, after, action) -> {
            Anime.define()
                    .init(after.translateXProperty(), after.getBoundsInLocal().getWidth())
                    .duration(Anime.BASE_DURATION.multiply(0.7))
                    .effect(before.translateXProperty(), -before.getBoundsInLocal().getWidth())
                    .then(action)
                    .effect(after.opacityProperty(), 1)
                    .run(finisher);
        };
    }

    /** Built-in animation. */
    static SwapAnime slideRight(WiseRunnable... finisher) {
        return (parent, before, after, action) -> {
            Anime.define()
                    .init(after.translateXProperty(), -after.getBoundsInLocal().getWidth())
                    .duration(Anime.BASE_DURATION.multiply(0.7))
                    .effect(before.translateXProperty(), before.getBoundsInLocal().getWidth())
                    .then(action)
                    .effect(after.opacityProperty(), 1)
                    .run();
        };
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