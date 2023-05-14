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
import java.util.List;

import javafx.scene.control.ButtonType;
import kiss.I;
import kiss.Managed;
import kiss.Observer;
import kiss.WiseBiConsumer;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import psychopath.Progress;
import viewtify.Viewtify;

public class UpdateTask {

    /** The all tasks. */
    @Managed
    List<Task> tasks = new ArrayList();

    /** The root directory of this application. */
    @Managed
    private Directory root;

    /** The archive of new application. */
    @Managed
    private File archive;

    /** The origin platform. */
    @Managed
    private ApplicationPlatform origin = ApplicationPlatform.current();

    /** The updater platform. */
    @Managed
    private ApplicationPlatform updater;

    /**
     * Build the update task for the specified new version.
     * 
     * @param root An application root directory.
     * @param archive A locaiton of the new version.
     */
    public static void run(Directory root, String archive) {
        // prepare to update
        UpdateTask prepare = new UpdateTask(root, archive);
        prepare.verify("Verify new version.", archive);
        prepare.load("Download new version.");
        prepare.build("Prepare to update.");
        prepare.message("Ready for update.");
        UpdaterView.tasks = prepare;

        Viewtify.dialog("Updater", UpdaterView.class, ButtonType.APPLY, ButtonType.CLOSE).ifPresent(tasks -> {
            tasks.tasks.clear();
            tasks.unpack("Unpacking the new version.", tasks.archive, tasks.root);
            tasks.reboot("Update is completed, reboot.");

            I.write(tasks, tasks.updater.locateRoot().file("updater.json").newBufferedWriter());

            tasks.updater.boot();
            Viewtify.application().deactivate();
        });
    }

    /**
     * Empty task.
     */
    UpdateTask() {
    }

    /**
     * Hide constructor.
     */
    private UpdateTask(Directory root, String archive) {
        this.root = root.absolutize();

        if (archive.startsWith("http://") || archive.startsWith("https://")) {
            throw new Error("Network access must be supported! FIXME");
        }

        this.archive = Locator.file(archive);
    }

    /**
     * Verify archive.
     * 
     * @param message
     */
    public void verify(String message, String archive) {
        Verify task = new Verify();
        task.archive = archive;
        task.root = root;
        task.message = message;

        tasks.add(task);
    }

    /**
     * Load archive.
     * 
     * @param message
     */
    public void load(String message) {
        Load task = new Load();
        task.remote = archive.path();
        task.local = archive;
        task.message = message;

        tasks.add(task);
    }

    /**
     * @param departure
     * @param destination
     */
    public void move(String message, Directory departure, Directory destination) {
        Move task = new Move();
        task.departure = departure;
        task.destination = destination;
        task.message = message;

        tasks.add(task);
    }

    /**
     * @param departure
     * @param destination
     */
    public void copy(String message, Directory departure, Directory destination) {
        Copy task = new Copy();
        task.departure = departure;
        task.destination = destination;
        task.message = message;

        tasks.add(task);
    }

    /**
     * @param departure
     * @param destination
     */
    public void unpack(String message, File departure, Directory destination) {
        Unpack task = new Unpack();
        task.departure = departure;
        task.destination = destination;
        task.message = message;

        tasks.add(task);
    }

    /**
     * @param target
     */
    public void delete(String message, Directory target, String... patterns) {
        Delete task = new Delete();
        task.target = target;
        task.patterns = List.of(patterns);
        task.message = message;

        tasks.add(task);
    }

    /**
     * Reboot this application.
     * 
     * @param message
     */
    public void reboot(String message) {
        Reboot task = new Reboot();
        task.message = message;

        tasks.add(task);
    }

    /**
     * Notify message.
     * 
     * @param message
     */
    public void message(String message) {
        Message task = new Message();
        task.message = message;

        tasks.add(task);
    }

    /**
     * @param message
     */
    public void build(String message) {
        Build task = new Build();
        task.message = message;

        tasks.add(task);
    }

    /**
     * Internal tasks.
     */
    public static abstract class Task implements WiseBiConsumer<UpdateTask, Observer<? super Progress>> {

        /**
         * Task message.
         */
        public String message;
    }

    static class Build extends Task {

        /**
         * {@inheritDoc}
         */
        @Override
        public void ACCEPT(UpdateTask tasks, Observer<? super Progress> param) throws Throwable {
            tasks.updater = tasks.origin.updater();
        }
    }

    /**
     * Move
     */
    static class Move extends Task {
        public Directory departure;

        public Directory destination;

        /**
         * {@inheritDoc}
         */
        @Override
        public void ACCEPT(UpdateTask tasks, Observer<? super Progress> listener) throws Throwable {
            departure.trackMovingTo(destination, o -> o.replaceExisting().strip()).to(listener);
        }
    }

    /**
     * Copy
     */
    static class Copy extends Task {
        public Directory departure;

        public Directory destination;

        /**
         * {@inheritDoc}
         */
        @Override
        public void ACCEPT(UpdateTask tasks, Observer<? super Progress> listener) throws Throwable {
            departure.trackCopyingTo(destination, o -> o.replaceExisting().strip()).to(listener);
        }
    }

    /**
     * Unpack
     */
    static class Unpack extends Task {
        public File departure;

        public Directory destination;

        /**
         * {@inheritDoc}
         */
        @Override
        public void ACCEPT(UpdateTask tasks, Observer<? super Progress> listener) throws Throwable {
            departure.trackUnpackingTo(destination, o -> o.replaceExisting()).to(listener);
        }
    }

    /**
     * Delete
     */
    static class Delete extends Task {
        public Directory target;

        public List<String> patterns;

        /**
         * {@inheritDoc}
         */
        @Override
        public void ACCEPT(UpdateTask tasks, Observer<? super Progress> listener) throws Throwable {
            target.trackDeleting(patterns).to(listener);
        }
    }

    /**
     * Verify
     */
    static class Verify extends Task {

        public String archive;

        public Directory root;

        /**
         * {@inheritDoc}
         */
        @Override
        public void ACCEPT(UpdateTask tasks, Observer<? super Progress> listener) throws Throwable {
            if (archive == null || archive.isBlank()) {
                throw new Error("Unable to update because the location of the new application is unknown.");
            }

            if (!archive.endsWith(".zip")) {
                throw new Error("The new application must be zipped.");
            }

            // check modification
            File zip = Locator.file(archive);

            if (zip.isAbsent()) {
                throw new Error("Archive [" + archive + "] is not found.");
            }

            if (zip.lastModifiedMilli() <= root.lastModifiedMilli()) {
                throw new Error("The current version is latest, no need to update.");
            }
        }
    }

    static class Load extends Task {

        public String remote;

        public File local;

        /**
         * {@inheritDoc}
         */
        @Override
        public void ACCEPT(UpdateTask tasks, Observer<? super Progress> listener) throws Throwable {
            if (local.path().equals(remote)) {
                // do nothing
            } else {
                // download
            }
        }
    }

    static class Reboot extends Task {

        /**
         * {@inheritDoc}
         */
        @Override
        public void ACCEPT(UpdateTask tasks, Observer<? super Progress> listener) throws Throwable {
            tasks.origin.boot();

            Viewtify.application().deactivate();
        }
    }

    static class Message extends Task {

        /**
         * {@inheritDoc}
         */
        @Override
        public void ACCEPT(UpdateTask tasks, Observer<? super Progress> listener) throws Throwable {
            // do nothing
        }
    }
}
