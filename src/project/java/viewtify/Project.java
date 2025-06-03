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

import javax.lang.model.SourceVersion;

public class Project extends bee.api.Project {

    {
        product("com.github.teletha", "viewtify", ref("version.txt"));
        describe("""
                Viewtify provides API for using [JavaFX](https://openjfx.io/) more declaratively and reactively. It also provides refactoring and type safety by eliminating FXML.

                Declarative and Reactive API
                * UI tree definition
                * UI widget operations (support all built-in JavaFX widgets)
                * Style definition (CSS API via [Stylist](https://github.com/teletha/stylist))
                * Animation
                * Dialog
                * Context menu

                Additional useful functions and widgets that do not exist in JavaFX.
                * Application launcher
                * Application updater
                * Docking Tab
                * Notification UI like Toast
                * Calendar UI
                * Print preview
                * Monitorable task
                * Undo/Redo management
                * Focus management
                * Shortcut management
                * User preference
                * Various theme
                * Automatic translation in real time
                * Tooltip and Popup
                * Headless UI
                * Browser manipulation
                """);

        require(SourceVersion.latest(), SourceVersion.RELEASE_21);

        require("com.github.teletha", "sinobu");
        require("com.github.teletha", "psychopath");
        require("com.github.teletha", "stylist");
        require("com.github.teletha", "lycoris");
        require("com.github.teletha", "conjure");
        require("com.github.teletha", "altfx");
        require("com.github.teletha", "icymanipulator").atAnnotation();
        require("com.github.teletha", "antibug").atTest();
        require("org.testfx", "testfx-junit5").atTest();
        require("org.openjfx", "javafx-base");
        require("org.openjfx", "javafx-controls");
        require("org.openjfx", "javafx-graphics");
        require("org.openjfx", "javafx-media");
        require("org.openjfx", "javafx-web");
        require("org.testfx", "openjfx-monocle", "jdk-12.0.1+2");
        require("org.controlsfx", "controlsfx");
        require("com.catwithawand", "BorderlessSceneFX");
        require("org.kordamp.ikonli", "ikonli-javafx");
        require("org.kordamp.ikonli", "ikonli-fontawesome5-pack");

        unrequire("org.openjfx", "jdk-jsobject");
        versionControlSystem("https://github.com/teletha/viewtify");
    }
}