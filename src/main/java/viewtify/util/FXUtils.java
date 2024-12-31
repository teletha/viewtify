/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import static java.lang.Double.*;

import java.util.Objects;
import java.util.function.Function;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.util.Duration;

import kiss.Decoder;
import kiss.Encoder;
import kiss.Variable;

public class FXUtils {

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
            return Duration.millis(Double.parseDouble(value));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String encode(Duration value) {
            return String.valueOf(value.toMillis());
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

    /**
     * Retrieve the focused window.
     * 
     * @return
     */
    public static Variable<Window> findFocusedWindow() {
        for (Window window : Window.getWindows()) {
            if (window.isFocused()) {
                return Variable.of(window);
            }
        }
        return Variable.empty();
    }
}