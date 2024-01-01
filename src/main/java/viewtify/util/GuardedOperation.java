/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import java.util.concurrent.atomic.AtomicBoolean;

import kiss.I;
import kiss.WiseRunnable;

public class GuardedOperation {

    /** The reusable no-operation. */
    public static final GuardedOperation NoOP = new NoOP();

    /** The sync state. */
    private final AtomicBoolean processing = new AtomicBoolean();

    /** The error handling. */
    private boolean ignoreError;

    /**
     * Guaranteed to be executed only once at a time. The difference with the synchronized block is
     * that it does not allow recursive execution by the same thread and if the process is rejected,
     * this operator will skip it instead of waiting.
     * 
     * @param process An atomic process.
     */
    public void guard(WiseRunnable process) {
        if (processing.compareAndSet(false, true)) {
            try {
                process.run();
            } catch (Throwable e) {
                if (!ignoreError) {
                    throw I.quiet(e);
                }
            } finally {
                processing.set(false);
            }
        }
    }

    /**
     * {@link #guard(WiseRunnable)} for {@link Runnable}.
     * <p>
     * Guaranteed to be executed only once at a time. The difference with the synchronized block is
     * that it does not allow recursive execution by the same thread and if the process is rejected,
     * this operator will skip it instead of waiting.
     * 
     * @param process An atomic process.
     */
    public void protect(Runnable process) {
        guard(I.wiseR(process));
    }

    /**
     * Ignore error while processing.
     * 
     * @return
     */
    public GuardedOperation ignoreError() {
        ignoreError = true;
        return this;
    }

    /**
     * 
     */
    private static class NoOP extends GuardedOperation {

        /**
         * {@inheritDoc}
         */
        @Override
        public void guard(WiseRunnable process) {
            process.run();
        }
    }
}