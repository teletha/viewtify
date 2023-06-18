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

import java.util.Objects;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.util.Duration;

import kiss.WiseRunnable;

public abstract class AnimeDefinition {

    /** The current processing timeline. */
    private Timeline current;

    /** The default duration. */
    private Duration defaultDuration = BASE_DURATION;

    /** The default interpolator. */
    private Interpolator defaultInterpolator = Interpolator.LINEAR;

    /**
     * Declare animation process.
     * 
     * @param action
     */
    public AnimeDefinition(WiseRunnable action) {
        Timeline before = current = new Timeline();
        before();
        before.setOnFinished(e -> {
            if (action != null) action.run();

            Timeline after = current = new Timeline();
            after();
            after.setOnFinished(x -> {
                cleanup();
            });
            after.play();
        });

        initialize();
        before.play();
    }

    /**
     * Initialize effects. You can override to declare your original effect.
     */
    public void initialize() {

    }

    /**
     * Declare animation before the action. You can override to declare your original effect.
     */
    public void before() {
    }

    /**
     * Declare animetion after the action. You can override to declare your original effect.
     */
    public void after() {
    }

    /**
     * Cleanup effects. You can override to declare your original effect.
     */
    public void cleanup() {
    }

    /** The standard effect time. */
    protected static final Duration BASE_DURATION = Duration.seconds(0.2);

    /**
     * Set the default duration.
     * 
     * @param duration
     */
    protected final void duration(Duration duration) {
        if (duration != null) {
            defaultDuration = duration;
        }
    }

    /**
     * Set the default duration.
     * 
     * @param interpolator
     */
    protected final void interpolation(Interpolator interpolator) {
        if (interpolator != null) {
            defaultInterpolator = interpolator;
        }
    }

    /**
     * Shorthand to declare animation effect.
     */
    protected final void effect(WritableValue<Number> value, Number num) {
        effect(value, num, defaultDuration);
    }

    /**
     * Shorthand to declare animation effect.
     */
    protected final void effect(WritableValue<Number> value, Number num, Interpolator interpolator) {
        effect(value, num, defaultDuration, interpolator);
    }

    /**
     * Shorthand to declare animation effect.
     */
    protected final void effect(WritableValue<Number> value, Number num, double sec) {
        effect(value, num, Duration.seconds(sec));
    }

    /**
     * Shorthand to declare animation effect.
     */
    protected final void effect(WritableValue<Number> value, Number num, double sec, Interpolator interpolator) {
        effect(value, num, Duration.seconds(sec), interpolator);
    }

    /**
     * Shorthand to declare animation effect.
     */
    protected final void effect(WritableValue<Number> value, Number num, Duration duration) {
        effect(value, num, duration, null);
    }

    /**
     * Shorthand to declare animation effect.
     */
    protected final void effect(WritableValue<Number> value, Number num, Duration duration, Interpolator interpolator) {
        duration = Objects.requireNonNullElse(duration, defaultDuration);
        interpolator = Objects.requireNonNullElse(interpolator, defaultInterpolator);

        current.getKeyFrames().add(new KeyFrame(duration, new KeyValue(value, num, interpolator)));
    }
}