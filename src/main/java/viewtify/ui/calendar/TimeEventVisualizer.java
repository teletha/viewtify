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

import javafx.scene.Node;
import viewtify.ui.UserInterfaceProvider;

public interface TimeEventVisualizer<E extends TimeEvent> {

    /**
     * Build UI for each event.
     * 
     * @param event
     * @return
     */
    UserInterfaceProvider<? extends Node> visualize(E event);
}
