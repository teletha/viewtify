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
import psychopath.Progress;
import viewtify.update.UpdateTask.Task.Copy;
import viewtify.update.UpdateTask.Task.Move;
import viewtify.update.UpdateTask.Task.Unpack;

public class UpdateTask {

    /** The all tasks. */
    @Managed
    private List<Task> tasks = new ArrayList();

    /**
     * Execute all tasks.
     * 
     * @param listener
     */
    void run(Observer<? super Progress> listener) {
        tasks.forEach(task -> {
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

        tasks.add(task);
    }

    /**
     * @param departure
     * @param destination
     */
    void copy(Directory departure, Directory destination) {
        Copy task = new Copy();
        task.departure = departure;
        task.destination = destination;

        tasks.add(task);
    }

    /**
     * @param departure
     * @param destination
     */
    void unpack(File departure, Directory destination) {
        Unpack task = new Unpack();
        task.departure = departure;
        task.destination = destination;

        tasks.add(task);
    }

    /**
     * Internal tasks.
     */
    public interface Task extends WiseConsumer<Observer<? super Progress>> {

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
            public void ACCEPT(Observer<? super Progress> listener) throws Throwable {
                departure.trackMovingTo(destination, o -> o.replaceExisting().strip()).to(listener);
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
            public void ACCEPT(Observer<? super Progress> listener) throws Throwable {
                departure.trackCopyingTo(destination, o -> o.replaceExisting().strip()).to(listener);
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
            public void ACCEPT(Observer<? super Progress> listener) throws Throwable {
                departure.trackUnpackingTo(destination, o -> o.replaceExisting()).to(listener);
            }
        }
    }
}
