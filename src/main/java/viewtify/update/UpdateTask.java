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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.control.ButtonType;
import kiss.I;
import kiss.WiseBiConsumer;
import kiss.WiseConsumer;
import psychopath.File;
import psychopath.Locator;
import psychopath.Progress;
import viewtify.Viewtify;

@SuppressWarnings("serial")
public class UpdateTask implements Serializable {

    /** The all tasks. */
    Code code;

    /** The archive of new application. */
    private File archive;

    /** The origin platform. */
    private final ApplicationPlatform origin = ApplicationPlatform.current();

    /** The updater platform. */
    private final ApplicationPlatform updater = origin.createUpdater();

    /**
     * Build the update task for the specified new version.
     * 
     * @param archive A locaiton of the new version.
     */
    public static void run(String archive) {
        // prepare to update
        UpdateTask prepare = UpdateTask.create((tasks, monitor) -> {
            monitor.message("Verify the update.");

            if (archive == null || archive.isBlank()) {
                throw new Error("Unable to update because the location of the new application is unknown.");
            }

            // check archive
            File zip = Locator.file(archive).absolutize();

            if (zip.isAbsent() || !zip.extension().equals("zip")) {
                throw new Error("Zipped archive [" + archive + "] is not found.");
            }

            if (zip.lastModifiedMilli() <= tasks.origin.locateRoot().lastModifiedMilli()) {
                throw new Error("The current version is latest, no need to update.");
            }

            tasks.archive = zip;

            monitor.message("Prepare to update.");
            if (tasks.origin.canUpdateJRE()) {
                tasks.origin.locateRoot().trackCopyingTo(tasks.updater.locateRoot(), o -> o.strip().glob("lib/**", "jre/**")).to(monitor);
            }

            monitor.message("Ready for update.");
        });
        Updater.tasks = prepare;

        Viewtify.dialog("Updater", Updater.class, ButtonType.APPLY, ButtonType.CLOSE).ifPresent(tasks -> {
            UpdateTask update = UpdateTask.create((x, monitor) -> {
                monitor.message("Installing the new version, please wait a minute.");
                tasks.archive.trackUnpackingTo(tasks.origin.locateRoot(), o -> o.replaceDifferent()).to(monitor);

                monitor.message("Clean up old files.");
                Map<String, File> files = new HashMap();
                tasks.origin.locateLibrary().walkFile("*.jar").to(file -> {
                    String name = name(file.name());
                    File old = files.put(name, file);
                    if (old != null) {
                        old.delete();
                    }
                });

                monitor.message("Update is completed, reboot.");
                tasks.origin.boot();

                Viewtify.application().deactivate();
            });

            tasks.updater.boot(Map.of("updater", store(update)));
            Viewtify.application().deactivate();
        });
    }

    private static String name(String value) {
        int index = value.indexOf('-');
        while (index != -1) {
            char c = value.charAt(index + 1);
            if (Character.isDigit(c)) {
                return value.substring(0, index);
            } else {
                index = value.indexOf('-', index + 1);
            }
        }
        return value;
    }

    /**
     * Store all tasks.
     * 
     * @return
     */
    public static String store(UpdateTask tasks) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(tasks);
            oos.close();
            byte[] bytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * Restore tasks.
     * 
     * @param tasks
     * @return
     */
    public static UpdateTask restore(String tasks) {
        try {
            byte[] bytes = Base64.getDecoder().decode(tasks);
            System.out.println(tasks);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (UpdateTask) ois.readObject();
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    public static UpdateTask create(Code task) {
        UpdateTask tasks = new UpdateTask(task);
        return tasks;
    }

    /**
     * Empty task.
     */
    private UpdateTask(Code code) {
        this.code = code;
    }

    /**
     * Internal tasks.
     */
    static interface Code extends WiseBiConsumer<UpdateTask, Monitor>, Serializable {
    }

    public static class Monitor implements WiseConsumer<Progress> {

        private final Consumer<String> messenger;

        private final WiseConsumer<Progress> progression;

        /**
         * @param messenger
         * @param progression
         */
        public Monitor(Consumer<String> messenger, WiseConsumer<Progress> progression) {
            this.messenger = messenger;
            this.progression = progression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void ACCEPT(Progress progress) throws Throwable {
            progression.accept(progress);
        }

        /**
         * Notify message.
         * 
         * @param message
         */
        public void message(String message) {
            messenger.accept(message);
        }
    }
}
