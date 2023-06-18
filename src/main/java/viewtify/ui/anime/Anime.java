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

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.util.Duration;

import kiss.WiseRunnable;

public class Anime {

    /** The initial timeline. */
    private final Timeline initial = new Timeline();

    private Timeline current = initial;

    private Duration defaultDuration = Duration.millis(200);

    private Interpolator defaultInterpolator = Interpolator.LINEAR;

    public final Anime interpolator(Interpolator interpolator) {
        this.defaultInterpolator = interpolator;
        return this;
    }

    public final Anime duration(Duration duration) {
        this.defaultDuration = duration;
        return this;
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime effect(WritableValue<Number> value, Number num) {
        return effect(value, num, (Duration) null);
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime effect(WritableValue<Number> value, Number num, Interpolator interpolator) {
        return effect(value, num, null, interpolator);
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime effect(WritableValue<Number> value, Number num, double sec) {
        return effect(value, num, Duration.seconds(sec));
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime effect(WritableValue<Number> value, Number num, double sec, Interpolator interpolator) {
        return effect(value, num, Duration.seconds(sec), interpolator);
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime effect(WritableValue<Number> value, Number num, Duration duration) {
        return effect(value, num, duration, null);
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime effect(WritableValue<Number> value, Number num, Duration duration, Interpolator interpolator) {
        duration = Objects.requireNonNullElse(duration, defaultDuration);
        interpolator = Objects.requireNonNullElse(interpolator, defaultInterpolator);

        current.getKeyFrames().add(new KeyFrame(duration, new KeyValue(value, num, interpolator)));

        return this;
    }

    /**
     * Define the next action.
     * 
     * @return
     */
    public final Anime then() {
        return then(null);
    }

    /**
     * Define the next action.
     * 
     * @return
     */
    public final Anime then(WiseRunnable finisher) {
        Timeline before = current;
        Timeline after = current = new Timeline();
        before.setOnFinished(e -> {
            if (finisher != null) finisher.run();
            after.play();
        });
        return this;
    }

    public final void run() {

    }
}
