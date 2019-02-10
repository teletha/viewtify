/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import static java.lang.Double.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import kiss.Decoder;
import kiss.Encoder;
import kiss.Variable;
import stylist.CSSValue;
import stylist.Style;
import stylist.StyleRule;

/**
 * @version 2018/09/10 10:57:23
 */
public class FXUtils {

    /**
     * Compute color as JavaFX {@link Color}.
     * 
     * @return
     */
    public static Color color(Style style, String propertyName) {
        StyleRule rule = StyleRule.create(style);
        Variable<CSSValue> color = rule.properties.get(propertyName);

        if (color.isPresent()) {
            CSSValue value = color.get();

            if (value instanceof stylist.value.Color) {
                return Color.web(((stylist.value.Color) value).toRGB());
            } else {
                return Color.web(color.toString());
            }
        } else {
            return Color.TRANSPARENT;
        }
    }

    /**
     * Convert from Stylist color to JavaFX color.
     * 
     * @param color
     * @return
     */
    public static Color color(stylist.value.Color color) {
        return Color.web(color.toRGB());
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
}
