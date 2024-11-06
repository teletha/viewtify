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

import java.lang.ProcessHandle.Info;
import java.util.Map;

@SuppressWarnings("serial")
public class NativeBlueprint extends Blueprint {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean boot(Map<String, String> params) {
        try {
            Info info = ProcessHandle.current().info();
            ProcessBuilder process = new ProcessBuilder().directory(root.asJavaFile()).inheritIO().command(info.command().get());
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
        throw new UnsupportedOperationException("Updater");
    }
}
