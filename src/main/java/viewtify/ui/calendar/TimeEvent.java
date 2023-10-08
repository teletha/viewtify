/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.calendar;

import java.time.LocalDate;
import java.time.LocalTime;

public interface TimeEvent extends Comparable<TimeEvent> {

    /**
     * Get the title.
     * 
     * @return
     */
    String title();

    /**
     * Get the description.
     * 
     * @return
     */
    default String description() {
        return "";
    }

    /**
     * Test whether this event is editable or not.
     * 
     * @return
     */
    default boolean isEditable() {
        return false;
    }

    /**
     * The event source.
     * 
     * @return
     */
    TimeEventSource source();

    /**
     * Get the starting date .
     * 
     * @return
     */
    LocalDate startDate();

    /**
     * Get the starting time .
     * 
     * @return
     */
    default LocalTime startTime() {
        return LocalTime.MIN;
    }

    /**
     * Get the ending date .
     * 
     * @return
     */
    default LocalDate endDate() {
        return startDate();
    }

    /**
     * Get the ending time .
     * 
     * @return
     */
    default LocalTime endTime() {
        return LocalTime.MAX;
    }

    /**
     * Get the location info.
     * 
     * @return
     */
    default String location() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default int compareTo(TimeEvent o) {
        return startTime().compareTo(o.startTime());
    }
}
