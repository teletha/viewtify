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

public interface HideAnime {

    /** Built-in swap animation. */
    HideAnime FadeOut = (parent, before, action) -> {
        new AnimeDefinition(action) {

            @Override
            public void before() {
                effect(before.opacityProperty(), 0, 0.3);
            }
        };
    };

    /** Built-in swap animation. */
    HideAnime ZoomIn = (parent, before, action) -> {
        double scale = 0.15;
        Node clip = parent.getClip();

        new AnimeDefinition(action) {

            @Override
            public void initialize() {
                parent.setClip(new Rectangle(parent.getWidth(), parent.getHeight()));
            }

            @Override
            public void before() {
                effect(before.opacityProperty(), 0);
                effect(before.scaleXProperty(), 1 - scale);
                effect(before.scaleYProperty(), 1 - scale);
            }

            @Override
            public void cleanup() {
                parent.setClip(clip);
            }
        };
    };

    /** Built-in swap animation. */
    HideAnime ZoomOut = (parent, before, action) -> {
        double scale = 0.15;
        Node clip = parent.getClip();

        new AnimeDefinition(action) {

            @Override
            public void initialize() {
                parent.setClip(new Rectangle(parent.getWidth(), parent.getHeight()));
            }

            @Override
            public void before() {
                effect(before.opacityProperty(), 0);
                effect(before.scaleXProperty(), 1 + scale);
                effect(before.scaleYProperty(), 1 + scale);
            }

            @Override
            public void cleanup() {
                parent.setClip(clip);
            }
        };
    };

    void run(Pane parent, Node befor, WiseRunnable action);

    /**
     * Start animation.
     * 
     * @param animes
     * @param action
     */
    public static void play(HideAnime[] animes, Pane parent, Node before, WiseRunnable action) {
        if (animes == null || animes.length == 0 || animes[0] == null) {
            action.run();
        } else {
            animes[0].run(parent, before, action);
        }
    }
}