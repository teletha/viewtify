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

import java.util.Objects;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

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
        } else if (node instanceof Pane pane) {
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
     * @param before
     */
    default void run(Pane parent, Node before) {
        run(parent, before, null);
    }

    /**
     * Run animation.
     * 
     * @param parent
     * @param before
     * @param action
     */
    void run(Pane parent, Node before, WiseRunnable action);

    /**
     * Animate the specified property.
     * 
     * @param <T>
     * @param durationMills Animation time.
     * @param property A target property to animate.
     * @param endValue A end value of animated property.
     * @param endEffect An optional end effect.
     */
    static <T> void play(int durationMills, WritableValue<T> property, T endValue, Runnable... endEffect) {
        play(Duration.millis(durationMills), property, endValue, endEffect);
    }

    /**
     * Animate the specified property.
     * 
     * @param <T>
     * @param duration Animation time.
     * @param property A target property to animate.
     * @param endValue A end value of animated property.
     * @param endEffect An optional end effect.
     */
    static <T> void play(Duration duration, WritableValue<T> property, T endValue, Runnable... endEffect) {
        Objects.requireNonNull(duration);
        Objects.requireNonNull(property);
        Objects.requireNonNull(endEffect);

        Timeline timeline = new Timeline(new KeyFrame(duration, new KeyValue(property, endValue)));
        if (0 < endEffect.length) {
            timeline.setOnFinished(e -> {
                for (Runnable effect : endEffect) {
                    effect.run();
                }
            });
        }
        timeline.play();
    }
}
