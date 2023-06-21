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

import psychopath.File;
import psychopath.Locator;

public enum Theme {

    Gray, Light, Dark;

    /** The location. */
    public final String location;

    /**
     * @param path
     */
    private Theme() {
        this.location = locate(name().toLowerCase());
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
        return ClassLoader.getSystemResource("viewtify/" + name + ".css").toExternalForm();
    }
}