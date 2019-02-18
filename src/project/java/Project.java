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

        require("com.github.teletha", "sinobu", "LATEST");
        require("com.github.teletha", "psychopath", "LATEST");
        require("com.github.teletha", "stylist", "LATEST");
        require("com.github.teletha", "antibug", "LATEST").atTest();
        require("net.bytebuddy", "byte-buddy", "LATEST");
        require("net.bytebuddy", "byte-buddy-agent", "LATEST");
        require("org.openjfx", "javafx-controls", "LATEST");
        require("org.openjfx", "javafx-media", "LATEST");
        require("org.openjfx", "javafx-web", "LATEST");
        require("org.controlsfx", "controlsfx", "9.0.0");

        versionControlSystem("https://github.com/teletha/viewtify");
    }
}
