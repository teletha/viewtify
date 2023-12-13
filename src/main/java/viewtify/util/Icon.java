/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @version 2018/08/28 22:50:57
 */
public enum Icon {

    Error, Info, Success, Warning;

    /**
     * 
     */
    private Icon() {
    }

    /**
     * Resource URI.
     * 
     * @return
     */
    public String path() {
        return "viewtify/icon/" + name().toLowerCase() + ".png";
    }

    /**
     * Get as {@link ImageView}.
     * 
     * @return
     */
    public ImageView image() {
        return new ImageView(new Image(path()));
    }
}