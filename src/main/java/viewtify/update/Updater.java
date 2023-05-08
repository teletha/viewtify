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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kiss.I;
import kiss.Managed;
import kiss.Signal;
import kiss.Singleton;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;

@Managed(Singleton.class)
public class Updater {

    /** The application home. */
    private final Directory home = Locator.directory("").absolutize();

    /** The current JVM. */
    private final Directory jvm = Locator.directory(System.getProperty("java.home"));

    /** The loaded libraries. */
    private final Set<File> libraries = detectLibraries("jdk.module.path").concat(detectLibraries("java.class.path")).toSet();

    /** The timestamp. */
    private final long lastModified = libraries.stream().mapToLong(File::lastModifiedMilli).max().orElse(0);

    /** The configurable option. */
    private LocalSite local = new LocalSite(Locator.directory(""));

    /** The configurable option. */
    private final List<UpdateTask> tasks = new ArrayList();

    /**
     * Add update site.
     * 
     * @param site
     * @return
     */
    public Updater addTask(String site) {
        if (site != null && !site.isBlank()) {
            if (site.startsWith("http")) {

            } else {
                addTask(Locator.directory(site));
            }
        }
        return this;
    }

    /**
     * @param site
     */
    public Updater addTask(Directory site) {
        if (site != null) {
            tasks.add(UpdateTask.copy(site, local.locateRoot()));
        }
        return this;
    }

    /**
     * @param site
     */
    public Updater addTask(Path site) {
        if (site != null) {
            addTask(Locator.directory(site));
        }
        return this;
    }

    /**
     * Configure the root directory.
     * 
     * @return
     */
    public Updater setRoot(Directory directory) {
        if (directory != null) {
            this.local = new LocalSite(directory);
        }
        return this;
    }

    /**
     * Configure the root directory.
     * 
     * @return
     */
    public Updater setRoot(Path directory) {
        if (directory != null) {
            setRoot(Locator.directory(directory));
        }
        return this;
    }

    public void update() {

    }

    public void detectEnvironment() {
        System.out.println(home);
        System.out.println(jvm);
        System.out.println(libraries);
    }

    public UpdateResult updateByZip(String path) {
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
    private Signal<File> detectLibraries(String key) {
        return I.signal(System.getProperty(key))
                .skipNull()
                .flatArray(value -> value.split(java.io.File.pathSeparator))
                .take(path -> path.endsWith(".jar"))
                .map(path -> Locator.file(path));
    }
}
