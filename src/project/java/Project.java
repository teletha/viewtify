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
        String fx = "14-ea+2";

        product("com.github.teletha", "viewtify", "1.0");

        require("com.github.teletha", "customfx");
        require("com.github.teletha", "sinobu");
        require("com.github.teletha", "psychopath");
        require("com.github.teletha", "stylist");
        require("com.github.teletha", "transcript");
        require("com.github.teletha", "antibug").atTest();
        require("org.openjfx", "javafx-base", fx);
        require("org.openjfx", "javafx-controls", fx);
        require("org.openjfx", "javafx-graphics", fx);
        require("org.openjfx", "javafx-media", fx);
        require("org.openjfx", "javafx-web", fx);
        require("org.controlsfx", "controlsfx");

        versionControlSystem("https://github.com/teletha/viewtify");
    }
}
