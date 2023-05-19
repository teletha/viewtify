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
        new AnimeDefinition(action) {

            @Override
            public void initialize() {
                after.setOpacity(0);
            }

            @Override
            public void before() {
                effect(before.opacityProperty(), 0, 0.15);
            }

            @Override
            public void after() {
                effect(after.opacityProperty(), 1, 0.15);
            }
        };
    };

    /** Built-in swap animation. */
    SwapAnime ZoomIn = (parent, before, after, action) -> {
        double scale = 0.15;
        int index = parent.getChildren().indexOf(before);
        Node clip = parent.getClip();

        new AnimeDefinition(action) {

            @Override
            public void initialize() {
                parent.getChildren().add(index, new StackPane(after, before));
                parent.setClip(new Rectangle(parent.getWidth(), parent.getHeight()));

                after.setOpacity(0);
                after.setScaleX(1 + scale);
                after.setScaleY(1 + scale);
            }

            @Override
            public void before() {
                effect(before.opacityProperty(), 0);
                effect(before.scaleXProperty(), 1 - scale);
                effect(before.scaleYProperty(), 1 - scale);

                effect(after.opacityProperty(), 1);
                effect(after.scaleXProperty(), 1);
                effect(after.scaleYProperty(), 1);
            }

            @Override
            public void cleanup() {
                parent.getChildren().set(index, after);
                parent.setClip(clip);
            }
        };
    };

    /** Built-in swap animation. */
    SwapAnime ZoomOut = (parent, before, after, action) -> {
        double scale = 0.15;
        int index = parent.getChildren().indexOf(before);
        Node clip = parent.getClip();

        new AnimeDefinition(action) {

            @Override
            public void initialize() {
                parent.getChildren().add(index, new StackPane(after, before));
                parent.setClip(new Rectangle(parent.getWidth(), parent.getHeight()));

                after.setOpacity(0);
                after.setScaleX(1 - scale);
                after.setScaleY(1 - scale);
            }

            @Override
            public void before() {
                effect(before.opacityProperty(), 0);
                effect(before.scaleXProperty(), 1 + scale);
                effect(before.scaleYProperty(), 1 + scale);

                effect(after.opacityProperty(), 1);
                effect(after.scaleXProperty(), 1);
                effect(after.scaleYProperty(), 1);
            }

            @Override
            public void cleanup() {
                parent.getChildren().set(index, after);
                parent.setClip(clip);
            }
        };
    };

    void run(Pane parent, Node before, Node after, WiseRunnable action);
}