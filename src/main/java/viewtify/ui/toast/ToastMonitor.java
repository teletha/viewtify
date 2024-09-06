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

import kiss.Variable;
import kiss.WiseRunnable;

public class ToastMonitor {

    /** The current title. */
    Variable<String> title = Variable.empty();

    /** The current message. */
    final Variable<String> message = Variable.empty();

    /** The current progression. */
    final Variable<Double> progress = Variable.of(0d);

    /** The action set at task canceled. */
    final List<WiseRunnable> cancels = new ArrayList();

    /** The action set at task completed. */
    final List<WiseRunnable> completes = new ArrayList();

    /** The total size of task. */
    private double total;

    /** The completed size of task. */
    private int current;

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
     * Set title.
     * 
     * @param title
     */
    public ToastMonitor title(Variable<String> title) {
        this.title = title;
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
     * Set total task size.
     * 
     * @param size
     * @return
     */
    public ToastMonitor totalProgress(int size) {
        this.total = size;
        return this;
    }

    /**
     * Increment progress.
     */
    public void incrementProgress() {
        if (current < total) {
            current++;
            calculateProgress();
        }
    }

    /**
     * Increment progress.
     */
    public void decrementProgress() {
        if (0 < current) {
            current--;
            calculateProgress();
        }
    }

    /**
     * Set progress
     * 
     * @param progress
     */
    public void setProgress(int progress) {
        if (0 <= progress && progress <= total) {
            current = progress;
            calculateProgress();
        }
    }

    /**
     * Reset progress.
     */
    public void resetProgress() {
        current = 0;
        calculateProgress();
    }

    /**
     * Complete progress
     */
    public void completeProgress() {
        current = (int) total;
        calculateProgress();
    }

    /**
     * Calculate the current progress.
     */
    private void calculateProgress() {
        progress.set(current / total);
    }

    /**
     * Register cancel action.
     * 
     * @param action
     * @return
     */
    public ToastMonitor whenCanceled(WiseRunnable action) {
        if (action != null) this.cancels.add(action);
        return this;
    }

    /**
     * Register complete action.
     * 
     * @param action
     * @return
     */
    public ToastMonitor whenCompleted(WiseRunnable action) {
        if (action != null) this.completes.add(action);
        return this;
    }
}
