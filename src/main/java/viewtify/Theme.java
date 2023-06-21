/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.net.URL;

import psychopath.File;
import psychopath.Locator;

public enum Theme {

    // Monotone
    Light, Dark, CaffeLatte, GreenTea,

    // High Contrast
    WhiteOnBlack, YellowOnBlack, BlackOnWhite;

    /** The location. */
    public final String location;

    /**
     * @param path
     */
    private Theme() {
        this.location = locate(Character.toLowerCase(name().charAt(0)) + name().substring(1));
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

        resource = ClassLoader.getSystemResource("com/sun/javafx/scene/control/skin/modena/" + name + ".css");
        if (resource != null) {
            return resource.toExternalForm();
        }

        throw new Error("Theme [" + name + "] is not found.");
    }
}