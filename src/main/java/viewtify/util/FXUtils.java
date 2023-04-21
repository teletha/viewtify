/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import static java.lang.Double.parseDouble;

import java.util.Objects;
import java.util.function.Function;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import kiss.Decoder;
import kiss.Encoder;
import kiss.Variable;
import stylist.Style;
import stylist.value.CSSValue;

public class FXUtils {

    /**
     * Animate the specified property.
     * 
     * @param <T>
     * @param durationMills Animation time.
     * @param property A target property to animate.
     * @param endValue A end value of animated property.
     * @param endEffect An optional end effect.
     */
    public static <T> void animate(int durationMills, WritableValue<T> property, T endValue, Runnable... endEffect) {
        animate(Duration.millis(durationMills), property, endValue, endEffect);
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
    public static <T> void animate(Duration duration, WritableValue<T> property, T endValue, Runnable... endEffect) {
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

    /**
     * Compute length property.
     * 
     * @param style A target style.
     * @param propertyName A target property name.
     * @return A computed JavaFX value.
     */
    public static double length(Style style, String propertyName) {
        return length(style, propertyName, 0);
    }

    /**
     * Compute length property.
     * 
     * @param style A target style.
     * @param propertyName A target property name.
     * @param defaultValue A default value when the propoerty is not defined.
     * @return A computed JavaFX value.
     */
    public static double length(Style style, String propertyName, double defaultValue) {
        Variable<CSSValue> value = style.value(propertyName);

        if (value.isPresent()) {
            return Double.parseDouble(value.v.toString().replaceAll("[a-z]", ""));
        } else {
            return defaultValue;
        }
    }

    /**
     * Compute length array property.
     * 
     * @param style A target style.
     * @param propertyName A target property name.
     * @return A computed JavaFX value.
     */
    public static double[] lengths(Style style, String propertyName) {
        return lengths(style, propertyName, new double[0]);
    }

    /**
     * Compute length array property.
     * 
     * @param style A target style.
     * @param propertyName A target property name.
     * @param defaultValue A default value when the propoerty is not defined.
     * @return A computed JavaFX value.
     */
    public static double[] lengths(Style style, String propertyName, double[] defaultValue) {
        Variable<CSSValue> value = style.value(propertyName);

        if (value.isPresent()) {
            String[] values = value.get().toString().split(" ");
            double[] convert = new double[values.length];

            for (int i = 0; i < convert.length; i++) {
                convert[i] = Double.parseDouble(values[i]);
            }
            return convert;
        } else {
            return defaultValue;
        }
    }

    /**
     * Compute color property.
     * 
     * @param style A target style.
     * @param propertyName A target property name.
     * @return A computed JavaFX value.
     */
    public static Color color(Style style, String propertyName) {
        return color(style, propertyName, Color.TRANSPARENT);
    }

    /**
     * Compute color property.
     * 
     * @param style A target style.
     * @param propertyName A target property name.
     * @param defaultValue A default value when the propoerty is not defined.
     * @return A computed JavaFX value.
     */
    public static Color color(Style style, String propertyName, Color defaultValue) {
        String[] names;

        if (propertyName.equals("color") || propertyName.equals("fill") || propertyName.equals("stroke")) {
            names = new String[] {"stroke", "fill", "color"};
        } else {
            names = new String[] {propertyName};
        }

        for (String name : names) {
            Variable<CSSValue> color = style.value(name);

            if (color.isAbsent()) {
                continue;
            }
            CSSValue value = color.get();

            if (value instanceof stylist.value.Color) {
                return Color.web(((stylist.value.Color) value).toRGB());
            } else {
                return Color.web(color.toString());
            }
        }
        return defaultValue;
    }

    /**
     * Convert from Stylist color to JavaFX color.
     * 
     * @param color
     * @return
     */
    public static Color color(stylist.value.Color color) {
        return Color.web(color.toRGB(), color.alpha);
    }

    /**
     * Convert from JavaFX color to Stylist color.
     * 
     * @param color
     * @return
     */
    public static stylist.value.Color color(Color color) {
        return stylist.value.Color
                .rgb((int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), color.getOpacity());
    }

    /**
     * @version 2018/09/10 23:56:53
     */
    @SuppressWarnings("unused")
    private static class JavaFXColorCodec implements Encoder<Color>, Decoder<Color> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Color decode(String value) {
            return Color.web(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String encode(Color value) {
            return value.toString();
        }
    }

    /**
     * @version 2018/09/10 23:34:27
     */
    @SuppressWarnings("unused")
    private static class StylistColorCodec implements Encoder<stylist.value.Color>, Decoder<stylist.value.Color> {

        /**
         * {@inheritDoc}
         */
        @Override
        public stylist.value.Color decode(String value) {
            return stylist.value.Color.of(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String encode(stylist.value.Color value) {
            return value.toRGB();
        }
    }

    /**
     * @version 2018/09/16 17:32:34
     */
    @SuppressWarnings("unused")
    private static class RectangleCodec implements Encoder<Rectangle>, Decoder<Rectangle> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Rectangle decode(String value) {
            String[] values = value.split(" ");

            return new Rectangle(parseDouble(values[0]), parseDouble(values[1]), parseDouble(values[2]), parseDouble(values[3]));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String encode(Rectangle value) {
            return value.getX() + " " + value.getY() + " " + value.getWidth() + " " + value.getHeight();
        }
    }

    /**
     * Codec for {@link Duration}.
     */
    @SuppressWarnings("unused")
    private static class DurationCodec implements Encoder<Duration>, Decoder<Duration> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Duration decode(String value) {
            return Duration.valueOf(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String encode(Duration value) {
            return value.toString();
        }
    }

    /**
     * Listen the nested property.
     * 
     * @param <B>
     * @param <P>
     * @param base
     * @param first
     * @param listener
     */
    public static <B, P> void listen(B base, Function<B, ObservableValue<P>> first, ChangeListener<P> listener) {
        first.apply(base).addListener(listener);
    }

    /**
     * Listen the nested property.
     * 
     * @param <B>
     * @param <P>
     * @param <Q>
     * @param base
     * @param first
     * @param second
     * @param listener
     */
    public static <B, P, Q> void listen(B base, Function<B, ObservableValue<P>> first, Function<P, ObservableValue<Q>> second, ChangeListener<Q> listener) {
        listen(base, first, new ChainListener(second, listener));
    }

    /**
     * Listen the nested property.
     * 
     * @param <B>
     * @param <P>
     * @param <Q>
     * @param <R>
     * @param base
     * @param first
     * @param second
     * @param third
     * @param listener
     */
    public static <B, P, Q, R> void listen(B base, Function<B, ObservableValue<P>> first, Function<P, ObservableValue<Q>> second, Function<Q, ObservableValue<R>> third, ChangeListener<R> listener) {
        listen(base, first, second, new ChainListener(third, listener));
    }

    /**
     * Listen the nested property.
     * 
     * @param <B>
     * @param <P>
     * @param <Q>
     * @param <R>
     * @param <S>
     * @param base
     * @param first
     * @param second
     * @param third
     * @param forth
     * @param listener
     */
    public static <B, P, Q, R, S> void listen(B base, Function<B, ObservableValue<P>> first, Function<P, ObservableValue<Q>> second, Function<Q, ObservableValue<R>> third, Function<R, ObservableValue<S>> forth, ChangeListener<S> listener) {
        listen(base, first, second, third, new ChainListener(forth, listener));
    }

    /**
     * Listen the nested property.
     * 
     * @param <B>
     * @param <P>
     * @param <Q>
     * @param <R>
     * @param <S>
     * @param <T>
     * @param base
     * @param first
     * @param second
     * @param third
     * @param forth
     * @param fifth
     * @param listener
     */
    public static <B, P, Q, R, S, T> void listen(B base, Function<B, ObservableValue<P>> first, Function<P, ObservableValue<Q>> second, Function<Q, ObservableValue<R>> third, Function<R, ObservableValue<S>> forth, Function<S, ObservableValue<T>> fifth, ChangeListener<T> listener) {
        listen(base, first, second, third, forth, new ChainListener(fifth, listener));
    }

    /**
     * 
     */
    private static class ChainListener<PREV, NEXT> implements ChangeListener<PREV> {

        private final Function<PREV, ObservableValue<NEXT>> nextFinder;

        private final ChangeListener<NEXT> nextListener;

        /**
         * @param finder
         */
        private ChainListener(Function<PREV, ObservableValue<NEXT>> finder, ChangeListener<NEXT> nextListener) {
            this.nextFinder = Objects.requireNonNull(finder);
            this.nextListener = Objects.requireNonNull(nextListener);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void changed(ObservableValue<? extends PREV> observable, PREV oldValue, PREV newValue) {
            if (oldValue != null) {
                nextFinder.apply(oldValue).removeListener(nextListener);
            }

            if (newValue != null) {
                ObservableValue<NEXT> next = nextFinder.apply(newValue);
                next.addListener(nextListener);

                nextListener.changed(next, null, next.getValue());
            }
        }
    }
}