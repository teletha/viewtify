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
import java.util.Optional;

import kiss.Extensible;
import stylist.Style;

public interface CalendarMarker extends Extensible {

    /** The empty mark. */
    Mark None = new Mark(false, null, null);

    /**
     * Define the name of this marker.
     * 
     * @return
     */
    String name();

    /**
     * Test whether the specified date is marked or not.
     * 
     * @param date
     * @return
     */
    Optional<Mark> detect(LocalDate date);

    /**
     * Calendar mark.
     */
    record Mark(boolean marked, String description, Style style) {
    }
}
