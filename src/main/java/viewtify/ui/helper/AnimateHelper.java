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

public interface AnimateHelper<Self extends AnimateHelper<Self, N>, N extends Node> extends UserInterfaceProvider<N> {

    /**
     * Animate this UI.
     * 
     * @param animatable
     * @return
     */
    default Self animate(AnimePattern animatable) {
        return animate(animatable, null);
    }

    /**
     * Animate this UI.
     * 
     * @param animatable
     * @param finisher
     * @return
     */
    default Self animate(AnimePattern animatable, WiseRunnable finisher) {
        if (animatable != null) {
            animatable.play(this, finisher);
        }
        return (Self) this;
    }

    /**
     * Animate this UI.
     * 
     * @param delayMilliseconds
     * @param animatable
     * @return
     */
    default Self animateLater(long delayMilliseconds, AnimePattern animatable) {
        return animateLater(delayMilliseconds, animatable, null);
    }

    /**
     * Animate this UI.
     * 
     * @param delayMilliseconds
     * @param animatable
     * @param finisher
     * @return
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
     * Set the x-axis location.
     * 
     * @param value A value to set.
     * @return Chainable API.
     */
    default Self translateX(double value) {
        ui().setTranslateX(value);

        return (Self) this;
    }

    /**
     * Set the y-axis location.
     * 
     * @param value A value to set.
     * @return Chainable API.
     */
    default Self translateY(double value) {
        ui().setTranslateY(value);

        return (Self) this;
    }

    /**
     * Set the z-axis location.
     * 
     * @param value A value to set.
     * @return Chainable API.
     */
    default Self translateZ(double value) {
        ui().setTranslateZ(value);

        return (Self) this;
    }
}