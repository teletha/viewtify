/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

public enum Theme {

    Light(""), Dark("viewtify/dark.css");

    /** The location. */
    public final String location;

    /**
     * @param path
     */
    private Theme(String path) {
        this.location = locate(path);
    }

    /**
     * Locate css file resource.
     * 
     * @param path
     * @return
     */
    static String locate(String path) {
        return ClassLoader.getSystemResource(path).toExternalForm();
    }
}