/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.concurrent.TimeUnit;

import javafx.scene.Node;

import kiss.I;
import kiss.WiseRunnable;
import viewtify.ui.UserInterfaceProvider;
import viewtify.ui.anime.AnimePattern;

/**
 * An interface providing methods for animating UI elements and setting their locations on the x, y,
 * and z axes.
 *
 * @param <Self> The type of the implementing class, enabling method chaining.
 * @param <N> The type of the UI node.
 */
public interface AnimateHelper<Self extends AnimateHelper<Self, N>, N extends Node> extends UserInterfaceProvider<N> {

    /**
     * Animates this UI using the specified {@link AnimePattern}.
     *
     * @param animatable The {@code AnimePattern} to use for animation.
     * @return The implementing class instance for method chaining.
     */
    default Self animate(AnimePattern animatable) {
        return animate(animatable, null);
    }

    /**
     * Animates this UI using the specified {@link AnimePattern} and executes the finisher when the
     * animation completes.
     *
     * @param animatable The {@code AnimePattern} to use for animation.
     * @param finisher The finisher to execute when the animation completes.
     * @return The implementing class instance for method chaining.
     */
    default Self animate(AnimePattern animatable, WiseRunnable finisher) {
        if (animatable != null) {
            animatable.play(this, finisher);
        }
        return (Self) this;
    }

    /**
     * Animates this UI after a specified delay.
     *
     * @param delayMilliseconds The delay before starting the animation in milliseconds.
     * @param animatable The {@code AnimePattern} to use for animation.
     * @return The implementing class instance for method chaining.
     */
    default Self animateLater(long delayMilliseconds, AnimePattern animatable) {
        return animateLater(delayMilliseconds, animatable, null);
    }

    /**
     * Animates this UI after a specified delay and executes the finisher when the animation
     * completes.
     *
     * @param delayMilliseconds The delay before starting the animation in milliseconds.
     * @param animatable The {@code AnimePattern} to use for animation.
     * @param finisher The finisher to execute when the animation completes.
     * @return The implementing class instance for method chaining.
     */
    default Self animateLater(long delayMilliseconds, AnimePattern animatable, WiseRunnable finisher) {
        if (delayMilliseconds <= 0) {
            animate(animatable, finisher);
        } else {
            I.schedule(delayMilliseconds, TimeUnit.MILLISECONDS).to(() -> animate(animatable, finisher));
        }
        return (Self) this;
    }

    /**
     * Sets the x-axis location of this UI element.
     *
     * @param value The x-axis value to set.
     * @return The implementing class instance for method chaining.
     */
    default Self translateX(double value) {
        ui().setTranslateX(value);

        return (Self) this;
    }

    /**
     * Sets the y-axis location of this UI element.
     *
     * @param value The y-axis value to set.
     * @return The implementing class instance for method chaining.
     */
    default Self translateY(double value) {
        ui().setTranslateY(value);

        return (Self) this;
    }

    /**
     * Sets the z-axis location of this UI element.
     *
     * @param value The z-axis value to set.
     * @return The implementing class instance for method chaining.
     */
    default Self translateZ(double value) {
        ui().setTranslateZ(value);

        return (Self) this;
    }
}