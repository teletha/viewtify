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

import java.util.Map;

import psychopath.File;
import psychopath.Locator;

@SuppressWarnings("serial")
class ExewrapPlatform extends ApplicationPlatform {

    /** The executable file. */
    private File exe;

    /**
     * For desirialization.
     */
    ExewrapPlatform() {
    }

    /**
     * Build site.
     * 
     * @param directory
     * @param name
     */
    ExewrapPlatform(String directory, String name) {
        this.root = Locator.directory(directory).absolutize();
        this.exe = root.file(name);
        this.jre = root.directory("jre");
        this.classPath = "lib/*";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean boot(Map<String, String> params) {
        try {
            ProcessBuilder process = new ProcessBuilder().directory(root.asJavaFile()).inheritIO().command(exe.path());
            process.environment().putAll(params);
            process.start();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationPlatform updater() {
        JREPlatform updater = new JREPlatform();
        updater.root = root.directory(".updater");
        updater.jre = updater.root.directory("jre");
        updater.application = Updater.class;
        updater.classPath = classPath;

        return updater;
    }
}
