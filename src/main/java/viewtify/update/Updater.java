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

public class Updater {

    /** The application home. */
    private static final Directory home = Locator.directory("").absolutize();

    /** The current JVM. */
    private static final Directory jvm = Locator.directory(System.getProperty("java.home"));

    /** The loaded libraries. */
    private static final Set<File> libraries = detectLibraries("jdk.module.path").concat(detectLibraries("java.class.path")).toSet();

    /** The timestamp. */
    private static final long lastModified = libraries.stream().mapToLong(File::lastModifiedMilli).max().orElse(0);

    public static void detectEnvironment() {
        System.out.println(home);
        System.out.println(jvm);
        System.out.println(libraries);
    }

    public static UpdateResult updateByZip(String path) {
        File zip = Locator.file(path).absolutize();

        if (zip.isAbsent()) {
            return new UpdateResult(false, "Archive file [" + zip + "] is not found.");
        }

        if (zip.lastModifiedMilli() <= lastModified) {
            return new UpdateResult(false, "The latest version is used.");
        }

        return null;
    }

    /**
     * Detect loaded libraries.
     * 
     * @param key
     * @return
     */
    private static Signal<File> detectLibraries(String key) {
        return I.signal(System.getProperty(key))
                .skipNull()
                .flatArray(value -> value.split(java.io.File.pathSeparator))
                .take(path -> path.endsWith(".jar"))
                .map(path -> Locator.file(path));
    }

    public static void main(String[] args) {
        System.out.println(Updater.updateByZip("test.zip"));
    }
}
