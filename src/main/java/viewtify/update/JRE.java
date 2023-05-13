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

import java.util.Set;

import kiss.I;
import kiss.Signal;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;

class JRE {

    /** The singleton. */
    static final JRE Current = new JRE();

    /** The application root. */
    private final Directory rootAPP = Locator.directory("").absolutize();

    /** The Java root. */
    private final Directory rootJRE = Locator.directory(System.getProperty("java.home")).absolutize();

    /** The loaded libraries. */
    private final Set<File> libraries = detectLibraries("jdk.module.path").concat(detectLibraries("java.class.path")).toSet();

    private final Directory rootLib = I.signal(libraries)
            .scan(lib -> lib.parent().path(), (prev, next) -> common(prev, next.parent().path()))
            .last()
            .map(path -> Locator.directory(path))
            .to()
            .exact();

    /** The updatable state. */
    final boolean canJREUpdate = rootJRE.path().startsWith(rootAPP.path());

    /** The updatable state. */
    final boolean canLibUpdate = rootLib.path().startsWith(rootAPP.path());

    /**
     * Hide constructor.
     */
    private JRE() {
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
}
