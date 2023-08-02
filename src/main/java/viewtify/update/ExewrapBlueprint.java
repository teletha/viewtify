/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.update;

import java.util.Map;

import psychopath.File;
import psychopath.Locator;

@SuppressWarnings("serial")
class ExewrapBlueprint extends Blueprint {

    /** The executable file. */
    private File exe;

    /**
     * Build site.
     * 
     * @param directory
     * @param name
     */
    ExewrapBlueprint(String directory, String name) {
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
    public Blueprint updater() {
        JavaBlueprint updater = new JavaBlueprint();
        updater.root = root.directory(".updater");
        updater.jre = updater.root.directory("jre");
        updater.application = Updater.class;
        updater.classPath = classPath;

        return updater;
    }
}