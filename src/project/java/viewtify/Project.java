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

import javax.lang.model.SourceVersion;

public class Project extends bee.api.Project {

    {
        product("com.github.teletha", "viewtify", ref("version.txt"));

        require(SourceVersion.RELEASE_19, SourceVersion.RELEASE_17);

        require("com.github.teletha", "altfx");
        require("com.github.teletha", "sinobu");
        require("com.github.teletha", "psychopath", "1.12.0");
        require("com.github.teletha", "stylist");
        require("com.github.teletha", "lycoris");
        require("com.github.teletha", "conjure", "1.1.0");
        require("com.github.teletha", "icymanipulator").atAnnotation();
        require("com.github.teletha", "antibug").atTest();
        require("org.openjfx", "javafx-base");
        require("org.openjfx", "javafx-controls");
        require("org.openjfx", "javafx-graphics");
        require("org.openjfx", "javafx-media");
        require("org.openjfx", "javafx-web");
        require("org.testfx", "openjfx-monocle", "jdk-12.0.1+2");
        require("org.controlsfx", "controlsfx");

        versionControlSystem("https://github.com/teletha/viewtify");
    }
}