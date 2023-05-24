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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.control.ButtonType;

import kiss.I;
import kiss.WiseBiConsumer;
import kiss.WiseConsumer;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import psychopath.Progress;
import viewtify.Viewtify;

@SuppressWarnings("serial")
public class UpdateTask implements Serializable {

    /** The all tasks. */
    Code code;

    /**
     * Empty task.
     */
    private UpdateTask(Code code) {
        this.code = code;
    }

    public static UpdateTask create(Code task) {
        return new UpdateTask(task);
    }

    /**
     * Build the update task for the specified new version.
     * 
     * @param archive A locaiton of the new version.
     */
    public static void update(String archive) {
        Directory updateDir = Locator.directory(".updater").absolutize();
        ApplicationPlatform origin = ApplicationPlatform.current();

        // prepare to update
        UpdateTask prepare = UpdateTask.create((tasks, monitor) -> {
            monitor.message("Verify the update.");

            // ====================================
            // check parameter
            // ====================================
            if (archive == null || archive.isBlank()) {
                monitor.message("Unable to update because the location of the new application is unknown.");
                return;
            }

            // ====================================
            // check archive
            // ====================================
            File file = Locator.file(archive).absolutize();

            if (file.isAbsent() || !file.extension().equals("zip")) {
                monitor.message("Zipped archive [" + archive + "] is not found.");
                return;
            }

            if (file.isBefore(origin.root)) {
                monitor.message("The current version is latest, no need to update.");
                return;
            }

            monitor.message("Prepare to update.");
            file.trackUnpackingTo(updateDir, option -> option.sync().replaceDifferent()).to(monitor);

            monitor.message("Ready for update.");
        });
        Updater.tasks = prepare;

        Viewtify.dialog("Updater", Updater.class, ButtonType.APPLY, ButtonType.CLOSE).ifPresent(tasks -> {

            UpdateTask update = UpdateTask.create((x, monitor) -> {
                monitor.message("Installing the new version, please wait a minute.");

                List<String> patterns = updateDir.children().map(c -> c.isFile() ? c.name() : c.name() + "/**").toList();
                updateDir.trackCopyingTo(origin.root, o -> o.strip().glob(patterns).replaceDifferent().sync()).to(monitor);

                monitor.message("Update is completed, reboot.");
                origin.boot();

                Viewtify.application().deactivate();
            });

            JREPlatform updater = new JREPlatform();
            updater.root = updateDir;
            updater.jre = updateDir.directory("jre");
            updater.application = Updater.class;
            updater.classPath = "lib/*";
            updater.boot(Map.of("updater", store(update)));

            Viewtify.application().deactivate();
        });
    }

    /**
     * Store all tasks.
     * 
     * @return
     */
    static String store(UpdateTask tasks) {
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
    static UpdateTask restore(String tasks) {
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
