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

public enum Theme {

    Light, Dark;

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
            return ClassLoader.getSystemResource("viewtify/" + name + ".css").toExternalForm();
    }
}