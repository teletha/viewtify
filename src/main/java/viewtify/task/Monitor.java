/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.task;

import java.util.Objects;
import java.util.function.Consumer;

import kiss.WiseBiConsumer;
import kiss.WiseConsumer;

public class Monitor<P> implements WiseConsumer<P> {

    /** The message handler. */
    private final Consumer<String> messenger;

    /** The persentage handler. */
    private final Consumer<Double> percentager;

    /** The progress listener. */
    private final WiseBiConsumer<Monitor<P>, P> progression;

    /** The state of progress. */
    private double completed = 0;

    /**
     * @param messenger
     * @param percentager
     * @param progression
     */
    public Monitor(Consumer<String> messenger, Consumer<Double> percentager, WiseBiConsumer<Monitor<P>, P> progression) {
        this.messenger = Objects.requireNonNull(messenger);
        this.percentager = Objects.requireNonNull(percentager);
        this.progression = Objects.requireNonNull(progression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ACCEPT(P progress) throws Throwable {
        progression.accept(this, progress);
    }

    /**
     * Notify message.
     * 
     * @param message
     */
    public void message(String message) {
        messenger.accept(message);
    }

    /**
     * Notify message with progress.
     * 
     * @param message
     */
    public void message(String message, double completed) {
        messenger.accept(message);
        complete(completed);
    }

    /**
     * Notify error message.
     * 
     * @param message
     * @throws Exception
     */
    public void error(String message) throws Exception {
        throw new Error(message);
    }

    /**
     * Configure the completed progress. (0 - 100)
     * 
     * @param completed
     */
    public void complete(double completed) {
        this.completed = Math.min(100, completed);

        percentager.accept(this.completed);
    }

    /**
     * Spawn sub monitor.
     * 
     * @param total
     * @return
     */
    public Monitor<P> spawn(double total) {
        return new Monitor<P>(messenger, p -> percentager.accept((p * total / 100d) + this.completed), progression);
    }
}