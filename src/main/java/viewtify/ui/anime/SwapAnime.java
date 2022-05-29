/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.anime;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import kiss.WiseRunnable;

public abstract class SwapAnime {

    private static final Duration BASE_DURATION = Duration.seconds(0.2);

    /** Built-in swap animation. */
    public static final SwapAnime FadeOutIn = new SwapAnime() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void run(Pane parent, Node before, WiseRunnable action, Node after) {
            int index = parent.getChildren().indexOf(before);

            StackPane pane = new StackPane(after, before);
            parent.getChildren().add(index, pane);

            Timeline line = new Timeline();
            ObservableList<KeyFrame> frames = line.getKeyFrames();
            frames.add(new KeyFrame(BASE_DURATION, new KeyValue(before.opacityProperty(), 0)));
            frames.add(new KeyFrame(BASE_DURATION, new KeyValue(after.opacityProperty(), 1)));
            line.setOnFinished(e -> {
                parent.getChildren().set(index, after);
            });
            line.play();

            // Timeline beforeAnime = new Timeline(new KeyFrame(BASE_DURATION, new
            // KeyValue(before.opacityProperty(), 0)));
            // beforeAnime.setOnFinished(finishBefore -> {
            // before.setCache(false);
            // after.setOpacity(0);
            // action.run();
            //
            // Timeline afterAnime = new Timeline(new KeyFrame(BASE_DURATION, new
            // KeyValue(after.opacityProperty(), 1)));
            // afterAnime.play();
            // });
            // beforeAnime.play();

        }
    };

    /** Built-in swap animation. */
    public static final SwapAnime ZoomIn = new SwapAnime() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void run(Pane parent, Node before, WiseRunnable action, Node after) {
            int index = parent.getChildren().indexOf(before);

            StackPane pane = new StackPane(after, before);
            parent.getChildren().add(index, pane);

            after.setOpacity(0);
            after.setScaleX(1);
            after.setScaleY(1);

            Timeline line = new Timeline();
            ObservableList<KeyFrame> frames = line.getKeyFrames();
            frames.add(new KeyFrame(BASE_DURATION, new KeyValue(before.opacityProperty(), 0), new KeyValue(before
                    .scaleXProperty(), 0.6), new KeyValue(before.scaleYProperty(), 0.6)));
            frames.add(new KeyFrame(BASE_DURATION, new KeyValue(after.opacityProperty(), 1), new KeyValue(after
                    .scaleXProperty(), 1), new KeyValue(after.scaleYProperty(), 1)));
            line.setOnFinished(e -> {
                parent.getChildren().set(index, after);
            });
            line.play();
        }
    };

    public abstract void run(Pane parent, Node before, WiseRunnable action, Node after);

    /**
     * Start animation.
     * 
     * @param anime
     * @param action
     */
    public static void start(SwapAnime anime, Pane parent, Node before, WiseRunnable action, Node after) {
        if (anime == null) {
            action.run();
        } else {
            anime.run(parent, before, action, after);
        }
    }
}
