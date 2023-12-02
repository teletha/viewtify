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

import java.util.function.DoubleUnaryOperator;

import javafx.animation.Interpolator;

/**
 * Built-in enhanced interpolaters.
 */
public class Interpolators extends Interpolator {

    /** Built-in Interpolator */
    public static final Interpolators SHAKE = new Interpolators(0.1, (t, amplitude) -> Math.sin(t * 10.0) * amplitude);

    /** Built-in Interpolator */
    public static final Interpolators SHAKE_INCREASE = new Interpolators(0.1, (t, amplitude) -> {
        return Math.sin(t * 10.0) * amplitude * Math.pow(1.2, t);
    });

    /** Built-in Interpolator */
    public static final Interpolators SHAKE_DECREASE = new Interpolators(0.1, (t, amplitude) -> {
        return Math.sin(t * 10.0) * amplitude * Math.pow(0.8, t);
    });

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_SINE = new Interpolators(t -> 1 - Math.cos(t * Math.PI / 2));

    /** Built-in Interpolator */
    public static final Interpolators EASE_OUT_SINE = new Interpolators(t -> 1 - Math.sin(t * Math.PI / 2));

    /** Built-in Interpolator */
    public static final Interpolators EASE_INOUT_SINE = new Interpolators(t -> -0.5 * (Math.cos(Math.PI * t) - 1));

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_CIRC = new Interpolators(t -> 1 - Math.sqrt(1 - t * t));

    /** Built-in Interpolator */
    public static final Interpolators EASE_OUT_CIRC = new Interpolators(t -> Math.sqrt(1 - (t - 1) * (t - 1)));

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_OUT_CIRC = new Interpolators(t -> {
        t *= 2;
        if (t < 1) {
            return -0.5 * (Math.sqrt(1 - t * t) - 1);
        } else {
            t -= 2;
            return 0.5 * (Math.sqrt(1 - t * t) + 1);
        }
    });

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_CUBIC = new Interpolators(t -> t * t * t);

    /** Built-in Interpolator */
    public static final Interpolators EASE_OUT_CUBIC = new Interpolators(t -> {
        double x = t - 1;
        return x * x * x + 1;
    });

    /** Built-in Interpolator */
    public static final Interpolators EASE_INOUT_CUBIC = new Interpolators(t -> {
        t *= 2;
        if (t < 1) {
            return 0.5 * t * t * t;
        } else {
            t -= 2;
            return 0.5 * (t * t * t + 2);
        }
    });

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_QUINT = new Interpolators(t -> t * t * t * t * t);

    /** Built-in Interpolator */
    public static final Interpolators EASE_OUT_QUINT = new Interpolators(t -> 1 - Math.pow(1 - t, 5));

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_OUT_QUINT = new Interpolators(t -> {
        t *= 2;
        if (t < 1) {
            return 0.5 * t * t * t * t * t;
        } else {
            t -= 2;
            return 0.5 * (t * t * t * t * t + 2);
        }
    });

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_ELASTIC = new Interpolators(t -> {
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
    public static final Interpolators EASE_OUT_ELASTIC = new Interpolators(t -> {
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
    public static final Interpolators EASE_IN_OUT_ELASTIC = new Interpolators(t -> {
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
    public static final Interpolators EASE_IN_QUAD = new Interpolators(t -> t * t);

    /** Built-in Interpolator */
    public static final Interpolators EASE_OUT_QUAD = new Interpolators(t -> 1 - (1 - t) * (1 - t));

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_OUT_QUAD = new Interpolators(t -> {
        t *= 2;
        if (t < 1) {
            return 0.5 * t * t;
        } else {
            t -= 1;
            return -0.5 * (t * (t - 2) - 1);
        }
    });

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_QUART = new Interpolators(t -> t * t * t * t);

    /** Built-in Interpolator */
    public static final Interpolators EASE_OUT_QUART = new Interpolators(t -> 1 - Math.pow(1 - t, 4));

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_OUT_QUART = new Interpolators(t -> {
        t *= 2;
        if (t < 1) {
            return 0.5 * t * t * t * t;
        } else {
            t -= 2;
            return -0.5 * (t * t * t * t - 2);
        }
    });

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_EXPO = new Interpolators(t -> (t == 0) ? 0 : Math.pow(2, 10 * (t - 1)));

    /** Built-in Interpolator */
    public static final Interpolators EASE_OUT_EXPO = new Interpolators(t -> (t == 1) ? 1 : 1 - Math.pow(2, -10 * t));

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_OUT_EXPO = new Interpolators(t -> {
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
    public static final Interpolators EASE_IN_BACK = new Interpolators(1.70158, (t, c) -> t * t * ((c + 1) * t - c));

    /** Built-in Interpolator */
    public static final Interpolators EASE_OUT_BACK = new Interpolators(1.70158, (t, c) -> {
        t -= 1;
        return t * t * ((c + 1) * t + c) + 1;
    });

    /** Built-in Interpolator */
    public static final Interpolators EASE_IN_OUT_BACK = new Interpolators(1.70158, (t, c) -> {
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
    public static final Interpolators EASE_OUT_BOUNCE = new Interpolators(t -> {
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
    public static final Interpolators EASE_IN_BOUNCE = new Interpolators(t -> {
        return 1 - EASE_OUT_BOUNCE.curve(1 - t);
    });

    /** Built-in Interpolator */
    public static final Interpolators EASE_INOUT_BOUNCE = new Interpolators(t -> {
        if (t < 0.5) {
            return 0.5 * EASE_IN_BOUNCE.curve(2 * t);
        } else {
            return 0.5 * EASE_OUT_BOUNCE.curve(2 * t - 1) + 0.5;
        }
    });

    private final DoubleBinaryOperator operator;

    private final double constants;

    /**
     * @param operator
     */
    Interpolators(DoubleUnaryOperator operator) {
        this.constants = 0;
        this.operator = (a, b) -> operator.applyAsDouble(a);
    }

    /**
     * @param operator
     */
    Interpolators(double constants, DoubleBinaryOperator operator) {
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
    public final Interpolators enhance(double constants) {
        return new Interpolators(constants, operator);
    }

    /**
     * Generate the enhanced function.
     * 
     * @param constants
     * @return
     */
    public final Interpolators enhance(DoubleUnaryOperator constants) {
        return new Interpolators(constants.applyAsDouble(this.constants), operator);
    }

    /**
     * 
     */
    private interface DoubleBinaryOperator {
        double apply(double x, double y);
    }
}