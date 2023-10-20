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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.util.Duration;
import kiss.WiseRunnable;
import viewtify.Viewtify;

public class Anime {

    /** The standard effect time. */
    public static final Duration BASE_DURATION = Duration.seconds(0.2);

    /** The registered init task. */
    private final List<WiseRunnable> initializers = new ArrayList();

    /** The initial timeline. */
    private final Timeline initial = new Timeline();

    /** The current timeline. */
    private Timeline current = initial;

    /** The default duraion. */
    private Duration defaultDuration = BASE_DURATION;

    /** The default interpolation. */
    private Interpolator defaultInterpolator = Interpolator.LINEAR;

    /**
     * Create new {@link Anime}.
     * 
     * @return
     */
    public static Anime define() {
        return new Anime();
    }

    /**
     * Hide constructor.
     */
    private Anime() {
    }

    /**
     * Set default interpolation.
     * 
     * @param interpolator
     * @return Chainable API.
     */
    public final Anime interpolator(Interpolator interpolator) {
        this.defaultInterpolator = interpolator;
        return this;
    }

    /**
     * Set default duration.
     * 
     * @param duration
     * @return Chainable API.
     */
    public final Anime duration(double duration) {
        this.defaultDuration = Duration.seconds(duration);
        return this;
    }

    /**
     * Set default duration.
     * 
     * @param duration
     * @return Chainable API.
     */
    public final Anime duration(Duration duration) {
        this.defaultDuration = duration;
        return this;
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime init(WritableValue<Number> value, Number num) {
        if (value != null && num != null) {
            initializers.add(() -> value.setValue(num));
        }
        return this;
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime init(WiseRunnable init) {
        if (init != null) {
            initializers.add(init);
        }
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
    public final Anime then(WiseRunnable... finisher) {
        Timeline before = current;
        Timeline after = current = new Timeline();
        before.setOnFinished(e -> {
            if (finisher != null && finisher.length != 0) {
                for (WiseRunnable runner : finisher) {
                    if (runner != null) {
                        runner.run();
                    }
                }
            }
            after.play();
        });
        return this;
    }

    /**
     * Play animation.
     */
    public final void run(WiseRunnable... finisher) {
        if (finisher != null && finisher.length != 0) {
            current.setOnFinished(e -> {
                for (WiseRunnable runner : finisher) {
                    if (runner != null) {
                        runner.run();
                    }
                }
            });
        }

        for (Runnable initializer : initializers) {
            initializer.run();
        }

        Viewtify.inUI(initial::play);
    }

    /**
     * Play animation with loop.
     */
    public void runInfinitely() {
        current.setAutoReverse(true);
        current.setCycleCount(Integer.MAX_VALUE);
        run();
    }
}