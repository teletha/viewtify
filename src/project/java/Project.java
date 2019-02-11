/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
public class Project extends bee.api.Project {

    {
        product("com.github.teletha", "viewtify", "1.0");

        require("com.github.teletha", "sinobu", "[1.2,)");
        require("com.github.teletha", "psychopath", "0.9");
        require("com.github.teletha", "stylist", "0.1");
        require("com.github.teletha", "antibug", "0.6").atTest();
        require("org.openjfx", "javafx-controls", "11");
        require("org.openjfx", "javafx-media", "11");
        require("org.openjfx", "javafx-web", "11");
        require("org.controlsfx", "controlsfx", "9.0.0");
    }
}
