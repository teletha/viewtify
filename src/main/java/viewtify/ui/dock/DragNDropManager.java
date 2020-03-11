/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.dock;

import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * The drag&drop manager. The implementations handles the full dnd management of views.
 */
public interface DragNDropManager {

    /**
     * The specialized data format to handle the drag&drop gestures with managed tabs.
     */
    DataFormat DATAFORMAT = new DataFormat("de.qaware.sdfx.DragNDrop");

    /**
     * Called to initialize a controller after its root element has been completely processed.
     */
    void init();

    /**
     * Initialize the drag&drop for a view.
     *
     * @param event The mouse event.
     */
    void onDragDetected(MouseEvent event);

    /**
     * Finish the drag&drop gesture.
     *
     * @param event The drag event
     */
    void onDragDone(DragEvent event);

    /**
     * Handle Drag&Drop to a invisible stage => opens a new window
     *
     * @param event The fired event.
     * @param dropStage The stage where the view was dropped.
     */
    void onDragDroppedNewStage(DragEvent event, Stage dropStage);

    /**
     * Handle the dropped event for panes. Mainly this event removes the view from the old position
     * and adds it at the new position.
     *
     * @param event The drag event.
     */
    void onDragDropped(DragEvent event);

    /**
     * Handle the drag exited event for panes.
     *
     * @param event the drag event.
     */
    void onDragExited(DragEvent event);

    /**
     * Handle the drag over event. It draws the drop position for the current cursor position.
     *
     * @param event The drag event.
     */
    void onDragOver(DragEvent event);

    /**
     * Get the window manager instance.
     *
     * @return The window manager instance.
     */
    WindowManager getWindowManager();
}