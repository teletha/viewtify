/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

/**
 * @version 2018/09/16 16:11:12
 */
public enum Theme {

    Light(""), Dark("viewtify/dark.css");

    /** The location. */
    public final String url;

    /**
     * @param path
     */
    private Theme(String path) {
        this.url = ClassLoader.getSystemResource(path).toExternalForm();
    }
}
