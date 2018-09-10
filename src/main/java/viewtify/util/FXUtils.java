/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.util;

import java.util.Optional;

import javafx.scene.paint.Color;

import kiss.Decoder;
import kiss.Encoder;
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
        Optional<CSSValue> color = rule.properties.get(propertyName);

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
                .rgba((int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), color.getOpacity());
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
            return stylist.value.Color.rgb(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String encode(stylist.value.Color value) {
            return value.toRGB();
        }
    }
}
