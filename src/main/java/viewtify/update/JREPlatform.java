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

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import kiss.I;
import kiss.Managed;
import kiss.Signal;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import viewtify.Viewtify;

public class JREPlatform extends ApplicationPlatform {

    /**
     * 
     */
    private static final long serialVersionUID = 5527997855769445322L;

    /** The application root. */
    @Managed
    Directory rootAPP;

    /** The Java root. */
    @Managed
    Directory rootJRE;

    /** The library root. */
    @Managed
    Directory rootLIB;

    /** The application class. */
    @Managed
    Class application;

    /** The application classpath. */
    @Managed
    String classPath;

    /**
     * Initialization.
     */
    JREPlatform initialize() {
        rootAPP = Locator.directory("").absolutize();
        rootJRE = Locator.directory(System.getProperty("java.home")).absolutize();

        Set<File> libraries = detectLibraries("jdk.module.path").concat(detectLibraries("java.class.path")).toSet();
        rootLIB = I.signal(libraries)
                .scan(lib -> lib.parent().path(), (prev, next) -> common(prev, next.parent().path()))
                .last()
                .map(path -> Locator.directory(path))
                .to()
                .exact();

        application = Viewtify.application().launcher();

        classPath = System.getProperty("java.class.path");

        return this;
    }

    /**
     * Detect loaded libraries.
     * 
     * @param key
     * @return
     */
    private Signal<File> detectLibraries(String key) {
        return I.signal(System.getProperty(key))
                .skipNull()
                .flatArray(value -> value.split(java.io.File.pathSeparator))
                .take(path -> path.endsWith(".jar"))
                .map(path -> Locator.file(path));
    }

    /**
     * Detect common prefix.
     * 
     * @param one
     * @param other
     * @return
     */
    private String common(String one, String other) {
        StringBuilder common = new StringBuilder();
        int min = Math.min(one.length(), other.length());
        for (int i = 0; i < min; i++) {
            if (one.charAt(i) == other.charAt(i)) {
                common.append(one.charAt(i));
            } else {
                break;
            }
        }
        return common.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean boot(Map<String, String> params) {
        try {
            ArrayList<String> commands = new ArrayList();

            // Java
            commands.add(rootJRE.path() + java.io.File.separator + "bin" + java.io.File.separator + "java");

            // classpath
            commands.add("-cp");
            commands.add(classPath);

            // Class to be executed
            commands.add(application.getName());

            // execute process
            ProcessBuilder process = new ProcessBuilder(commands).directory(rootAPP.asJavaFile()).inheritIO();
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
        JREPlatform updater = new JREPlatform();
        updater.rootAPP = rootAPP;
        updater.rootJRE = rootJRE;
        updater.rootLIB = rootLIB;
        updater.application = Updater.class;
        updater.classPath = classPath;

        return updater;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long lastModified() {
        return Long.MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Directory locateRoot() {
        return rootAPP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Directory locateLibrary() {
        return rootLIB;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Directory locateJRE() {
        return rootJRE;
    }
}
