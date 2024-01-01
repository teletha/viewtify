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

public enum ThemeType {
    Flat, Gradient;

    /** The location. */
    public final String location;

    /**
     * @param path
     */
    private ThemeType() {
        this.location = Theme.locate(Character.toLowerCase(name().charAt(0)) + name().substring(1));
    }
}