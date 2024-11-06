/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.update;

import java.io.Serializable;
import java.util.HashMap;
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
    public Directory jre;

    /** The application classpath. */
    public String classPath = System.getProperty("java.class.path");

    /** The environment variables. */
    private Map<String, String> env = new HashMap();

    protected Blueprint() {
        String home = System.getProperty("java.home");
        if (home != null) {
            jre = Locator.directory(home).absolutize();
        }
    }

    /**
     * Boot this application.
     */
    public final boolean boot() {
        return boot(env);
    }

    /**
     * Boot this application.
     */
    public final boolean boot(MonitorableTask<Progress> task) {
        return env(Updater.class.getName(), MonitorableTask.store(task)).boot();
    }

    /**
     * Boot this application.
     */
    public abstract boolean boot(Map<String, String> params);

    /**
     * Deactivate the current application and boot this application.
     */
    public final boolean reboot() {
        try {
            return env("UpdateOnStartup", "false").boot();
        } finally {
            Viewtify.application().deactivate();
        }
    }

    /**
     * Deactivate the current application and boot this application.
     */
    public final boolean reboot(MonitorableTask<Progress> task) {
        return env(Updater.class.getName(), MonitorableTask.store(task)).reboot();
    }

    /**
     * Create new updater application.
     * 
     * @return
     */
    public abstract Blueprint updater();

    /**
     * Assign environment variable.
     * 
     * @param key A key.
     * @param value A value.
     * @return Chainable API
     */
    public final Blueprint env(String key, String value) {
        if (key != null && value != null) {
            env.put(key, value);
        }
        return this;
    }

    /**
     * Detect the current site.
     * 
     * @return
     */
    public static Blueprint detect() {
        if (System.getProperty("org.graalvm.nativeimage.imagecode") != null) {
            return new NativeBlueprint();
        }

        String directory = System.getProperty("java.application.path");
        String name = System.getProperty("java.application.name");

        if (directory == null || name == null) {
            return new JavaBlueprint();
        } else {
            return new ExewrapBlueprint(directory, name);
        }
    }
}