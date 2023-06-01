/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

public enum UpdatePolicy {

    Never(Long.MAX_VALUE), EveryTime(0), EveryDay(60 * 60 * 24), EveryWeek(60 * 60 * 24 * 7), EveryMonth(60 * 60 * 24 * 30);

    /** The interval time (second). */
    public final long interval;

    /**
     * Hide constructor.
     * 
     * @param intervalSeconds
     */
    private UpdatePolicy(long intervalSeconds) {
        this.interval = intervalSeconds;
    }
}
