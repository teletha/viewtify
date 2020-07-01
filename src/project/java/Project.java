/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
public class Project extends bee.api.Project {

    {
        product("com.github.teletha", "viewtify", "2.0.0");

        require("com.github.teletha", "altfx");
        require("com.github.teletha", "sinobu");
        require("com.github.teletha", "psychopath");
        require("com.github.teletha", "stylist");
        require("com.github.teletha", "icymanipulator").atAnnotation();
        require("com.github.teletha", "antibug").atTest();
        require("org.openjfx", "javafx-base");
        require("org.openjfx", "javafx-controls");
        require("org.openjfx", "javafx-graphics");
        require("org.openjfx", "javafx-media");
        require("org.openjfx", "javafx-web");
        require("org.controlsfx", "controlsfx");

        versionControlSystem("https://github.com/teletha/viewtify");
    }
}