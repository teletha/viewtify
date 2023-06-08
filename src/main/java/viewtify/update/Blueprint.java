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
import psychopath.Progress;
import viewtify.Viewtify;
import viewtify.task.MonitorableTask;

@SuppressWarnings("serial")
public abstract class Blueprint implements Serializable {

    /** The application root directory. */
    public Directory root = Locator.directory("").absolutize();

    /** The JRE direcotry. */
    public Directory jre = Locator.directory(System.getProperty("java.home")).absolutize();

    /** The application classpath. */
    public String classPath = System.getProperty("java.class.path");

    /**
     * Boot this application.
     */
    public final boolean boot() {
        return boot(Map.of());
    }

    /**
     * Boot this application.
     */
    public final boolean boot(MonitorableTask<Progress> task) {
        return boot(Map.of(Updater.class.getName(), MonitorableTask.store(task)));
    }

    /**
     * Boot this application.
     */
    public abstract boolean boot(Map<String, String> params);

    /**
     * Deactivate the current application and boot this application.
     */
    public final boolean reboot() {
        return reboot(Map.of("ignoreStartupUpdate", "true"));
    }

    /**
     * Deactivate the current application and boot this application.
     */
    public final boolean reboot(MonitorableTask<Progress> task) {
        return reboot(Map.of(Updater.class.getName(), MonitorableTask.store(task), "ignoreStartupUpdate", "true"));
    }

    /**
     * Deactivate the current application and boot this application.
     */
    public final boolean reboot(Map<String, String> params) {
        try {
            return boot(params);
        } finally {
            Viewtify.application().deactivate();
        }
    }

    /**
     * Create new updater application.
     * 
     * @return
     */
    public abstract Blueprint updater();

    /**
     * Detect the current site.
     * 
     * @return
     */
    public static Blueprint detect() {
        String directory = System.getProperty("java.application.path");
        String name = System.getProperty("java.application.name");

        if (directory == null || name == null) {
            return new JavaBlueprint();
        } else {
            return new ExewrapBlueprint(directory, name);
        }
    }
}
