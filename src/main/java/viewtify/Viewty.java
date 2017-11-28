/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

import kiss.I;

/**
 * @version 2017/11/29 8:22:19
 */
public interface Viewty {

    /**
     * Select your designed FXML.
     * 
     * @return
     */
    default String design() {
        return getClass().getSimpleName() + ".fxml";
    }

    /**
     * Compute root container.
     */
    default <Container> Container root() {
        try {
            return new FXMLLoader(ClassLoader.getSystemResource(design())).load();
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * Describe your initialization.
     */
    void initialize();
}
