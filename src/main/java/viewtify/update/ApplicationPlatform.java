/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.update;

import java.io.Serializable;
import java.util.Map;

import kiss.Observer;
import psychopath.Directory;
import psychopath.Progress;

public abstract class ApplicationPlatform implements Serializable {

    private static final long serialVersionUID = -2784979601406683792L;

    /**
     * Get the last modified time.
     * 
     * @return
     */
    abstract long lastModified();

    /**
     * Locate the root directory.
     */
    abstract Directory locateRoot();

    /**
     * Locate library directory.
     * 
     * @return
     */
    abstract Directory locateLibrary();

    /**
     * Locate JRE directory.
     * 
     * @return
     */
    abstract Directory locateJRE();

    /**
     * Boot this site.
     */
    public boolean boot() {
        return boot(Map.of());
    }

    /**
     * Boot this site.
     */
    public abstract boolean boot(Map<String, String> params);

    /**
     * Create the updater platform.
     * 
     * @return
     */
    public abstract ApplicationPlatform createUpdater(Observer<? super Progress> observer);

    /**
     * Create the updater platform.
     * 
     * @return
     */
    public abstract ApplicationPlatform updater2();

    /**
     * Check updatable state.
     * 
     * @return
     */
    public boolean canUpdateLibrary() {
        return locateLibrary().path().startsWith(locateRoot().path());
    }

    /**
     * Check updatable state.
     * 
     * @return
     */
    public boolean canUpdateJRE() {
        return locateJRE().path().startsWith(locateRoot().path());
    }

    /**
     * Detect the current site.
     * 
     * @return
     */
    public static ApplicationPlatform current() {
        String directory = System.getProperty("java.application.path");
        String name = System.getProperty("java.application.name");

        if (directory == null || name == null) {
            return new JREPlatform().initialize();
        } else {
            return new ExewrapPlatform(directory, name);
        }
    }
}
