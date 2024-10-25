/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

import kiss.I;
import psychopath.File;
import psychopath.Locator;

public enum Theme {

    Light, Dark, BlueHawaii, CaffeLatte, GreenTea, PeachFizz;

    /** The location. */
    public final String location;

    /** The managed variable colors. */
    private final Map<String, Color> variables = new HashMap();

    /**
     * @param path
     */
    private Theme() {
        this.location = locate(Character.toLowerCase(name().charAt(0)) + name().substring(1));

        try {
            URL url = URI.create(location).toURL();
            try (InputStream inputStream = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.strip();
                    if (line.startsWith("-fx-")) {
                        int index = line.indexOf(":");
                        String key = line.substring(0, index).strip();
                        String value = line.substring(index + 1, line.length() - 1).strip();

                        variables.put(key, color(value));
                    }
                }
            } catch (IOException e) {
                throw I.quiet(e);
            }
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * Parse color code.
     * 
     * @param code
     * @return
     */
    private Color color(String code) {
        if (code.startsWith("-fx-")) {
            Color color = variables.get(code);
            if (color == null) {
                return color(Modena.colors.get(code));
            } else {
                return color;
            }
        }

        if (code.startsWith("derive")) {
            int start = code.indexOf('(');
            int end = code.indexOf(',');
            return color(code.substring(start + 1, end).strip());
        }

        if (code.startsWith("hsb(")) {
            String[] values = code.replaceAll("[hsb\\(%\\)]", "").split(",");
            double hue = Double.parseDouble(values[0]);
            double saturation = Double.parseDouble(values[1]) / 100.0;
            double brightness = Double.parseDouble(values[2]) / 100.0;
            return Color.hsb(hue, saturation, brightness);
        }

        if (code.startsWith("hsba(")) {
            String[] values = code.replaceAll("[hsba\\(%\\)]", "").split(",");
            double hue = Double.parseDouble(values[0]);
            double saturation = Double.parseDouble(values[1]) / 100.0;
            double brightness = Double.parseDouble(values[2]) / 100.0;
            double opacity = Double.parseDouble(values[3]);
            return Color.hsb(hue, saturation, brightness, opacity);
        }

        return Color.web(code);
    }

    /**
     * Locate css file resource.
     * 
     * @param name
     * @return
     */
    static String locate(String name) {
        File css = Locator.directory("").absolutize().parent().file("viewtify/src/main/resources/viewtify/" + name + ".css");
        if (css.isPresent()) {
            return css.externalForm();
        }

        URL resource = ClassLoader.getSystemResource("viewtify/" + name + ".css");
        if (resource != null) {
            return resource.toExternalForm();
        }

        throw new Error("Theme [" + name + "] is not found.");
    }

    /**
     * Retrieve the variable color.
     * 
     * @return
     */
    public Color accent() {
        return variables.getOrDefault("-fx-accent", Color.BLACK);
    }

    /**
     * Retrieve the variable color.
     * 
     * @return
     */
    public Color color() {
        return variables.getOrDefault("-fx-color", Color.BLACK);
    }

    /**
     * Retrieve the variable color.
     * 
     * @return
     */
    public Color focus() {
        return variables.getOrDefault("-fx-focus-color", Color.BLACK);
    }

    /**
     * Retrieve the variable color.
     * 
     * @return
     */
    public Color edit() {
        return variables.getOrDefault("-fx-edit-color", Color.BLACK);
    }

    /**
     * Retrieve the variable color.
     * 
     * @return
     */
    public Color error() {
        return variables.getOrDefault("-fx-error", Color.DARKRED);
    }

    /**
     * Retrieve the variable color.
     * 
     * @return
     */
    public Color warning() {
        return variables.getOrDefault("-fx-warning", Color.YELLOW);
    }

    /**
     * Retrieve the variable color.
     * 
     * @return
     */
    public Color success() {
        return variables.getOrDefault("-fx-success", Color.GREEN);
    }

    /**
     * Retrieve the variable color.
     * 
     * @return
     */
    public Color text() {
        return variables.getOrDefault("-fx-color", Color.BLACK);
    }

    /**
     * Retrieve the variable color.
     * 
     * @return
     */
    public Color textLight() {
        return variables.getOrDefault("-fx-light-text-color", Color.BLACK);
    }

    /**
     * Retrieve the variable color.
     * 
     * @return
     */
    public Color textMid() {
        return variables.getOrDefault("-fx-mid-text-color", Color.BLACK);
    }

    /**
     * Retrieve the variable color.
     * 
     * @return
     */
    public Color textDark() {
        return variables.getOrDefault("-fx-dark-text-color", Color.BLACK);
    }

    /**
     * Retrieve the variable color.
     * 
     * @return
     */
    public Color background() {
        return variables.getOrDefault("-fx-background", Color.BLACK);
    }
}