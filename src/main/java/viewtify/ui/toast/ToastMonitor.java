/*
 * Copyright (C) 2024 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kiss.Observer;
import kiss.Signal;
import kiss.Variable;
import kiss.WiseRunnable;

public class ToastMonitor {

    /** The current title. */
    final Variable<String> title;

    /** The current message. */
    final Variable<String> message = Variable.empty();

    /** The current progression. */
    final Variable<Double> progress = Variable.of(0d);

    /** The action set at task canceled. */
    final List<WiseRunnable> cancels = new ArrayList();

    /** The action set at task completed. */
    final List<WiseRunnable> completes = new ArrayList();

    public ToastMonitor() {
        this(Variable.empty());
    }

    public ToastMonitor(Variable<String> title) {
        this.title = title;
    }

    /**
     * Set title.
     * 
     * @param title
     */
    public ToastMonitor title(String title) {
        this.title.set(title);
        return this;
    }

    /**
     * Set message.
     * 
     * @param message
     */
    public ToastMonitor message(String message) {
        this.message.set(message);
        return this;
    }

    /**
     * Set progress.
     * 
     * @param current
     */
    public void progress(double current) {
        this.progress.set(current);
    }

    /**
     * Register cancel action.
     * 
     * @param action
     * @return
     */
    public ToastMonitor whenCanceled(WiseRunnable action) {
        if (action != null) {
            this.cancels.add(action);
        }
        return this;
    }

    /**
     * Register complete action.
     * 
     * @param action
     * @return
     */
    public ToastMonitor whenCompleted(WiseRunnable action) {
        if (action != null) {
            this.completes.add(action);
        }
        return this;
    }

    public static void show(Variable<String> title, int estimatedTaskSize, Signal<?> task) {
        double weight = 1d / estimatedTaskSize;

        ToastMonitor monitor = new ToastMonitor(title);
        monitor.progress(0.01d);
        Toast.show(monitor);

        Accept accept = new Accept(weight, monitor);
        task.to(accept, () -> {
            System.out.println("Stopped");
        });
    }

    private static class Accept implements Observer {

        double weight;

        ToastMonitor monitor;

        /**
         * @param monitor
         */
        Accept(double weight, ToastMonitor monitor) {
            this.weight = weight;
            this.monitor = monitor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(Object value) {
            monitor.message(Objects.toString(value));
            monitor.progress.set(current -> current + weight);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void complete() {
            monitor.progress.set(1d);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void error(Throwable e) {
            monitor.message(e.getLocalizedMessage());
        }
    }
}
