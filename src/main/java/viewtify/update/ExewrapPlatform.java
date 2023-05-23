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

import kiss.Managed;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;

class ExewrapPlatform extends ApplicationPlatform {

    private static final long serialVersionUID = 7081682284741896593L;

    /** The update site. */
    @Managed
    private Directory root;

    /** The executable file. */
    @Managed
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
    public ApplicationPlatform createUpdater() {
        Directory temp = Locator.temporaryDirectory();

        JREPlatform updater = new JREPlatform();
        updater.rootAPP = temp;
        updater.rootJRE = temp.directory("jre");
        updater.rootLIB = temp.directory("lib");
        updater.application = Updater.class;
        updater.classPath = "lib/*";

        return updater;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long lastModified() {
        return root.lastModifiedMilli();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Directory locateRoot() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Directory locateLibrary() {
        return root.directory("lib");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Directory locateJRE() {
        Directory dir = root.directory("jre");
        if (dir.isPresent()) {
            return dir;
        }

        return root.directory("jdk");
    }
}
