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

import kiss.Variable;

public class ToastMonitor {

    /** The current title. */
    final Variable<String> title = Variable.empty();

    /** The current message. */
    final Variable<String> message = Variable.empty();

    /** The current progression. */
    final Variable<Double> progress = Variable.of(0d);

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
}
