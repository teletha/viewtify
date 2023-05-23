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
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.ButtonType;
import kiss.I;
import kiss.Observer;
import kiss.WiseBiConsumer;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import psychopath.Progress;
import viewtify.Viewtify;

public class UpdateTask implements Serializable {

    /** The all tasks. */
    List<Task> tasks = new ArrayList();

    /** The archive of new application. */
    private File archive;

    /** The origin platform. */
    private final ApplicationPlatform origin = ApplicationPlatform.current();

    /** The updater platform. */
    private ApplicationPlatform updater;

    /**
     * Build the update task for the specified new version.
     * 
     * @param archive A locaiton of the new version.
     */
    public static void run(String archive) {
        // prepare to update
        UpdateTask prepare = new UpdateTask();
        prepare.add("Prepare to update.", (tasks, p) -> {
            if (archive == null || archive.isBlank()) {
                throw new Error("Unable to update because the location of the new application is unknown.");
            }

            if (!archive.endsWith(".zip")) {
                throw new Error("The new application must be zipped.");
            }

            // check modification
            File zip = Locator.file(archive).absolutize();

            if (zip.isAbsent()) {
                throw new Error("Archive [" + archive + "] is not found.");
            }

            if (zip.lastModifiedMilli() <= tasks.origin.locateRoot().lastModifiedMilli()) {
                throw new Error("The current version is latest, no need to update.");
            }

            tasks.archive = zip;
            tasks.updater = tasks.origin.createUpdater(p);
        });
        prepare.add("Ready for update.", (tasks, p) -> {
        });
        Updater.tasks = prepare;

        Viewtify.dialog("Updater", Updater.class, ButtonType.APPLY, ButtonType.CLOSE).ifPresent(tasks -> {
            tasks.tasks.clear();
            tasks.unpack("Installing the new version, please wait a minute.", tasks.archive, tasks.origin.locateRoot());
            tasks.cleanup("Clean up old files.", tasks.origin.locateLibrary());
            tasks.reboot("Update is completed, reboot.");

            tasks.updater.boot(Map.of("updater", store(tasks)));
            Viewtify.application().deactivate();
        });
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

    /**
     * Empty task.
     */
    UpdateTask() {
    }

    public void add(String message, Code code) {
        Task task = new Task();
        task.message = message;
        task.code = code;

        tasks.add(task);
    }

    /**
     * @param departure
     * @param destination
     */
    public void move(String message, Directory departure, Directory destination) {
        add(message, (tasks, p) -> {
            departure.trackMovingTo(destination, o -> o.replaceDifferent().strip()).to(p);
        });
    }

    /**
     * @param departure
     * @param destination
     */
    public void copy(String message, Directory departure, Directory destination) {
        add(message, (tasks, p) -> {
            departure.trackCopyingTo(destination, o -> o.replaceDifferent().strip()).to(p);
        });
    }

    /**
     * @param departure
     * @param destination
     */
    public void unpack(String message, File departure, Directory destination) {
        add(message, (tasks, p) -> {
            departure.trackUnpackingTo(destination, o -> o.replaceDifferent()).to(p);
        });
    }

    /**
     * @param target
     */
    public void delete(String message, Directory target, String... patterns) {
        add(message, (tasks, p) -> {
            target.trackDeleting(patterns).to(p);
        });
    }

    public void cleanup(String message, Directory target) {
        add(message, (tasks, p) -> {
            Map<String, File> files = new HashMap();

            target.walkFile("*.jar").to(file -> {
                String name = name(file.name());
                File old = files.put(name, file);
                if (old != null) {
                    old.trackDeleting().to(p);
                }
            });
        });
    }

    private String name(String value) {
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
     * Reboot this application.
     * 
     * @param message
     */
    public void reboot(String message) {
        add(message, (tasks, p) -> {
            tasks.origin.boot();

            Viewtify.application().deactivate();
        });
    }

    /**
     * Internal tasks.
     */
    static class Task implements WiseBiConsumer<UpdateTask, Observer<? super Progress>>, Serializable {

        /**
         * Task message.
         */
        public String message;

        private Code code;

        public double weight() {
            return 20;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void ACCEPT(UpdateTask param1, Observer<? super Progress> param2) throws Throwable {
            code.accept(param1, param2);
        }
    }

    /**
     * Serializable task.
     */
    public interface Code extends WiseBiConsumer<UpdateTask, Observer<? super Progress>>, Serializable {
    }
}
