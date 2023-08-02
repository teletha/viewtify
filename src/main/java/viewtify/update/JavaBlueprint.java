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

import java.util.ArrayList;
import java.util.Map;

import viewtify.Viewtify;

@SuppressWarnings("serial")
public class JavaBlueprint extends Blueprint {

    /** The application class. */
    Class application;

    /**
     * 
     */
    JavaBlueprint() {
        application = Viewtify.application().launcher();
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
    public Blueprint updater() {
        JavaBlueprint updater = new JavaBlueprint();
        updater.root = root.directory(".updater");
        updater.jre = jre;
        updater.application = Updater.class;
        updater.classPath = classPath;

        return updater;
    }
}