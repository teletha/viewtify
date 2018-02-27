/*
 * Copyright (C) 2017 Viewtify Development Team
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

        require("com.github.teletha", "sinobu", "1.0");
        require("com.github.teletha", "antibug", "0.3");
        require("org.controlsfx", "controlsfx", "8.40.14");
    }
}
