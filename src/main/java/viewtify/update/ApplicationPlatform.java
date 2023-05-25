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

import psychopath.Directory;
import psychopath.Locator;

@SuppressWarnings("serial")
public abstract class ApplicationPlatform implements Serializable {

    /** The application root directory. */
    public Directory root = Locator.directory("").absolutize();

    /** The JRE direcotry. */
    public Directory jre = Locator.directory(System.getProperty("java.home")).absolutize();

    /** The application classpath. */
    public String classPath = System.getProperty("java.class.path");

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
     * Create new updater application.
     * 
     * @return
     */
    public abstract ApplicationPlatform updater();

    /**
     * Detect the current site.
     * 
     * @return
     */
    public static ApplicationPlatform current() {
        String directory = System.getProperty("java.application.path");
        String name = System.getProperty("java.application.name");

        if (directory == null || name == null) {
            return new JREPlatform();
        } else {
            return new ExewrapPlatform(directory, name);
        }
    }
}
