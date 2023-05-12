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

import kiss.Managed;
import kiss.Observer;
import kiss.WiseConsumer;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import psychopath.Progress;

public class UpdateTask {

    /** The root directory of this application. */
    public final Directory root;

    /** The all tasks. */
    @Managed
    List<Task> tasks = new ArrayList();

    /** The intentional delay for ui notification. */
    @Managed
    int delay;

    /** The rebootable application name. */
    @Managed
    File rebootApp;

    /**
     * Build the update task for the specified new version.
     * 
     * @param location A locaiton of the new version.
     * @param definition
     */
    public static void run(String location, WiseConsumer<UpdateTask> definition) {
        if (location == null || location.isBlank()) {
            throw new Error("Unable to update because the location of the latest application is unknown.");
        }

        if (!location.endsWith(".zip")) {
            throw new Error("The application must be zipped.");
        }

        UpdateTask task = new UpdateTask();

        // check modification
        if (location.startsWith("http://") || location.startsWith("https://")) {
            throw new Error("Network access must be supported! FIXME");
        } else {
            File archive = Locator.file(location);

            if (archive.isAbsent()) {
                throw new Error("Archive [" + location + "] is not found.");
            }

            if (archive.lastModifiedMilli() <= task.root.lastModifiedMilli()) {
                throw new Error("The current version is latest, no need to update.");
            }

            definition.accept(task);
        }
    }

    /**
     * Hide constructor.
     */
    private UpdateTask() {
        this.root = Locator.directory("").absolutize();
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
     * Internal tasks.
     */
    public static abstract class Task implements WiseConsumer<Observer<? super Progress>> {

        /**
         * Task message.
         */
        public String message;
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
        public void ACCEPT(Observer<? super Progress> listener) throws Throwable {
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
        public void ACCEPT(Observer<? super Progress> listener) throws Throwable {
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
        public void ACCEPT(Observer<? super Progress> listener) throws Throwable {
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
        public void ACCEPT(Observer<? super Progress> listener) throws Throwable {
            target.trackDeleting(patterns).to(listener);
        }
    }
}
