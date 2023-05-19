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
import java.util.function.DoubleUnaryOperator;

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
     * 
     */
    private interface DoubleBinaryOperator {
        double apply(double x, double y);
    }

    /**
     * 
     */
    public static class BuiltinInterpolator extends Interpolator {

        private final DoubleBinaryOperator operator;

        private final double constants;

        /**
         * @param operator
         */
        private BuiltinInterpolator(DoubleUnaryOperator operator) {
            this.constants = 0;
            this.operator = (a, b) -> operator.applyAsDouble(a);
        }

        /**
         * @param operator
         */
        private BuiltinInterpolator(double constants, DoubleBinaryOperator operator) {
            this.constants = constants;
            this.operator = operator;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected double curve(double t) {
            return operator.apply(t, constants);
        }

        /**
         * Generate the enhanced function.
         * 
         * @param constants
         * @return
         */
        public final BuiltinInterpolator enhance(double constants) {
            return new BuiltinInterpolator(constants, operator);
        }

        /**
         * Generate the enhanced function.
         * 
         * @param constants
         * @return
         */
        public final BuiltinInterpolator enhance(DoubleUnaryOperator constants) {
            return new BuiltinInterpolator(constants.applyAsDouble(this.constants), operator);
        }
    }

    /** Built-in Interpolator */
    public static final BuiltinInterpolator SHAKE = new BuiltinInterpolator(0.1, (t, amplitude) -> Math.sin(t * 10.0) * amplitude);

    /** Built-in Interpolator */
    public static final BuiltinInterpolator SHAKE_INCREASE = new BuiltinInterpolator(0.1, (t, amplitude) -> {
        return Math.sin(t * 10.0) * amplitude * Math.pow(1.2, t);
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator SHAKE_DECREASE = new BuiltinInterpolator(0.1, (t, amplitude) -> {
        return Math.sin(t * 10.0) * amplitude * Math.pow(0.8, t);
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_SINE = new BuiltinInterpolator(t -> 1 - Math.cos(t * Math.PI / 2));

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_OUT_SINE = new BuiltinInterpolator(t -> 1 - Math.sin(t * Math.PI / 2));

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_INOUT_SINE = new BuiltinInterpolator(t -> -0.5 * (Math.cos(Math.PI * t) - 1));

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_CIRC = new BuiltinInterpolator(t -> 1 - Math.sqrt(1 - t * t));

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_OUT_CIRC = new BuiltinInterpolator(t -> Math.sqrt(1 - (t - 1) * (t - 1)));

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_OUT_CIRC = new BuiltinInterpolator(t -> {
        t *= 2;
        if (t < 1) {
            return -0.5 * (Math.sqrt(1 - t * t) - 1);
        } else {
            t -= 2;
            return 0.5 * (Math.sqrt(1 - t * t) + 1);
        }
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_CUBIC = new BuiltinInterpolator(t -> t * t * t);

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_OUT_CUBIC = new BuiltinInterpolator(t -> {
        double x = t - 1;
        return x * x * x + 1;
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_INOUT_CUBIC = new BuiltinInterpolator(t -> {
        t *= 2;
        if (t < 1) {
            return 0.5 * t * t * t;
        } else {
            t -= 2;
            return 0.5 * (t * t * t + 2);
        }
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_QUINT = new BuiltinInterpolator(t -> t * t * t * t * t);

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_OUT_QUINT = new BuiltinInterpolator(t -> 1 - Math.pow(1 - t, 5));

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_OUT_QUINT = new BuiltinInterpolator(t -> {
        t *= 2;
        if (t < 1) {
            return 0.5 * t * t * t * t * t;
        } else {
            t -= 2;
            return 0.5 * (t * t * t * t * t + 2);
        }
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_ELASTIC = new BuiltinInterpolator(t -> {
        if (t == 0) {
            return 0;
        }
        if (t == 1) {
            return 1;
        }
        double p = 0.3;
        double s = p / 4;
        double tMinusOne = t - 1;
        return -Math.pow(2, 10 * tMinusOne) * Math.sin((tMinusOne - s) * (2 * Math.PI) / p);
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_OUT_ELASTIC = new BuiltinInterpolator(t -> {
        if (t == 0) {
            return 0;
        }
        if (t == 1) {
            return 1;
        }
        double p = 0.3;
        double s = p / 4;
        return Math.pow(2, -10 * t) * Math.sin((t - s) * (2 * Math.PI) / p) + 1;
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_OUT_ELASTIC = new BuiltinInterpolator(t -> {
        if (t == 0) {
            return 0;
        }
        if (t == 1) {
            return 1;
        }
        double p = 0.3;
        double s = p / 4;
        double tTimesTwo = t * 2;
        double tMinusOne = tTimesTwo - 1;
        if (tTimesTwo < 1) {
            return -0.5 * Math.pow(2, 10 * tMinusOne) * Math.sin((tMinusOne - s) * (2 * Math.PI) / p);
        } else {
            return Math.pow(2, -10 * tMinusOne) * Math.sin((tMinusOne - s) * (2 * Math.PI) / p) * 0.5 + 1;
        }
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_QUAD = new BuiltinInterpolator(t -> t * t);

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_OUT_QUAD = new BuiltinInterpolator(t -> 1 - (1 - t) * (1 - t));

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_OUT_QUAD = new BuiltinInterpolator(t -> {
        t *= 2;
        if (t < 1) {
            return 0.5 * t * t;
        } else {
            t -= 1;
            return -0.5 * (t * (t - 2) - 1);
        }
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_QUART = new BuiltinInterpolator(t -> t * t * t * t);

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_OUT_QUART = new BuiltinInterpolator(t -> 1 - Math.pow(1 - t, 4));

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_OUT_QUART = new BuiltinInterpolator(t -> {
        t *= 2;
        if (t < 1) {
            return 0.5 * t * t * t * t;
        } else {
            t -= 2;
            return -0.5 * (t * t * t * t - 2);
        }
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_EXPO = new BuiltinInterpolator(t -> (t == 0) ? 0 : Math.pow(2, 10 * (t - 1)));

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_OUT_EXPO = new BuiltinInterpolator(t -> (t == 1) ? 1 : 1 - Math.pow(2, -10 * t));

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_OUT_EXPO = new BuiltinInterpolator(t -> {
        if (t == 0) {
            return 0;
        } else if (t == 1) {
            return 1;
        } else if (t < 0.5) {
            return 0.5 * Math.pow(2, 10 * (2 * t - 1));
        } else {
            return 0.5 * (2 - Math.pow(2, -10 * (2 * t - 1)));
        }
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_BACK = new BuiltinInterpolator(1.70158, (t, c) -> t * t * ((c + 1) * t - c));

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_OUT_BACK = new BuiltinInterpolator(1.70158, (t, c) -> {
        t -= 1;
        return t * t * ((c + 1) * t + c) + 1;
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_OUT_BACK = new BuiltinInterpolator(1.70158, (t, c) -> {
        t *= 2;
        if (t < 1) {
            c *= 1.525;
            return 0.5 * (t * t * ((c + 1) * t - c));
        } else {
            t -= 2;
            c *= 1.525;
            return 0.5 * (t * t * ((c + 1) * t + c) + 2);
        }
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_OUT_BOUNCE = new BuiltinInterpolator(t -> {
        if (t < 1 / 2.75) {
            return 7.5625 * t * t;
        } else if (t < 2 / 2.75) {
            t -= 1.5 / 2.75;
            return 7.5625 * t * t + 0.75;
        } else if (t < 2.5 / 2.75) {
            t -= 2.25 / 2.75;
            return 7.5625 * t * t + 0.9375;
        } else {
            t -= 2.625 / 2.75;
            return 7.5625 * t * t + 0.984375;
        }
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_IN_BOUNCE = new BuiltinInterpolator(t -> {
        return 1 - EASE_OUT_BOUNCE.curve(1 - t);
    });

    /** Built-in Interpolator */
    public static final BuiltinInterpolator EASE_INOUT_BOUNCE = new BuiltinInterpolator(t -> {
        if (t < 0.5) {
            return 0.5 * EASE_IN_BOUNCE.curve(2 * t);
        } else {
            return 0.5 * EASE_OUT_BOUNCE.curve(2 * t - 1) + 0.5;
        }
    });

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