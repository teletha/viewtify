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

import viewtify.Viewtify;

@SuppressWarnings("serial")
public class JREPlatform extends ApplicationPlatform {

    /** The application class. */
    Class application;

    /** The application classpath. */
    String classPath;

    /**
     * 
     */
    JREPlatform() {
        application = Viewtify.application().launcher();

        classPath = System.getProperty("java.class.path");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean boot(Map<String, String> params) {
        try {
            ArrayList<String> commands = new ArrayList();

            // Java
            commands.add(jre.path() + java.io.File.separator + "bin" + java.io.File.separator + "java");

            // classpath
            commands.add("-cp");
            commands.add(classPath);

            // Class to be executed
            commands.add(application.getName());

            // execute process
            ProcessBuilder process = new ProcessBuilder(commands).directory(root.asJavaFile()).inheritIO();
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
        updater.root = root;
        updater.jre = jre;
        updater.application = Updater.class;
        updater.classPath = classPath;

        return updater;
    }
}
