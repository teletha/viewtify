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

import kiss.Managed;
import kiss.Observer;
import kiss.Singleton;
import kiss.Storable;
import kiss.WiseConsumer;
import psychopath.Directory;
import psychopath.File;
import psychopath.Location;
import viewtify.update.UpdateTasks.Task;
import viewtify.update.UpdateTasks.Task.Copy;
import viewtify.update.UpdateTasks.Task.Move;
import viewtify.update.UpdateTasks.Task.Unpack;

@SuppressWarnings("serial")
@Managed(Singleton.class)
public class UpdateTasks extends ArrayList<Task> implements Storable<UpdateTasks> {

    /**
     * Hide constructor.
     */
    private UpdateTasks() {
    }

    /**
     * Execute all tasks.
     * 
     * @param listener
     */
    void run(Observer<? super Location> listener) {
        forEach(task -> {
            task.accept(listener);
        });
    }

    /**
     * @param departure
     * @param destination
     */
    void move(Directory departure, Directory destination) {
        Move task = new Move();
        task.departure = departure;
        task.destination = destination;

        add(task);
        store();
    }

    /**
     * @param departure
     * @param destination
     */
    void copy(Directory departure, Directory destination) {
        Copy task = new Copy();
        task.departure = departure;
        task.destination = destination;

        add(task);
        store();
    }

    /**
     * @param departure
     * @param destination
     */
    void unpack(File departure, Directory destination) {
        Unpack task = new Unpack();
        task.departure = departure;
        task.destination = destination;

        add(task);
        store();
    }

    /**
     * Internal tasks.
     */
    public interface Task extends WiseConsumer<Observer<? super Location>> {

        /**
         * Move
         */
        static class Move implements Task {
            public Directory departure;

            public Directory destination;

            /**
             * {@inheritDoc}
             */
            @Override
            public void ACCEPT(Observer<? super Location> listener) throws Throwable {
                departure.observeMovingTo(destination, o -> o.replaceExisting().strip()).to(listener);
            }
        }

        /**
         * Copy
         */
        static class Copy implements Task {
            public Directory departure;

            public Directory destination;

            /**
             * {@inheritDoc}
             */
            @Override
            public void ACCEPT(Observer<? super Location> listener) throws Throwable {
                departure.observeCopyingTo(destination, o -> o.replaceExisting().strip()).to(listener);
            }
        }

        /**
         * Unpack
         */
        static class Unpack implements Task {
            public File departure;

            public Directory destination;

            /**
             * {@inheritDoc}
             */
            @Override
            public void ACCEPT(Observer<? super Location> listener) throws Throwable {
                departure.observeUnpackingTo(destination, o -> o.replaceExisting()).to(listener);
            }
        }
    }
}
