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

import kiss.Observer;
import kiss.WiseConsumer;
import psychopath.Directory;
import psychopath.File;
import psychopath.Location;

public interface UpdateTask extends WiseConsumer<Observer<? super Location>> {

    /**
     * @param departure
     * @param destination
     * @return
     */
    static UpdateTask move(Directory departure, Directory destination) {
        Move task = new Move();
        task.departure = departure;
        task.destination = destination;
        return task;
    }

    /**
     * @param departure
     * @param destination
     * @return
     */
    static UpdateTask copy(Directory departure, Directory destination) {
        Copy task = new Copy();
        task.departure = departure;
        task.destination = destination;
        return task;
    }

    /**
     * @param departure
     * @param destination
     * @return
     */
    static UpdateTask unpack(File departure, Directory destination) {
        Unpack task = new Unpack();
        task.departure = departure;
        task.destination = destination;
        return task;
    }

    /**
     * Move
     */
    static class Move implements UpdateTask {
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
    static class Copy implements UpdateTask {
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
    static class Unpack implements UpdateTask {
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
