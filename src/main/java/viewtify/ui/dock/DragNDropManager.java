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

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TabPane;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The drag&drop manager. The implementations handles the full dnd management of views.
 */
public class DragNDropManager {

    /**
     * The specialized data format to handle the drag&drop gestures with managed tabs.
     */
    static final DataFormat DATAFORMAT = new DataFormat("de.qaware.sdfx.DragNDrop");

    /**
     * Temporal storage for the draged view
     */
    private static ViewStatus dragedViewStatus;

    /**
     * The window manager.
     */
    private final WindowManager windowManager;

    /**
     * The effect for the current drop zone.
     */
    private final Blend effect = new Blend();

    /**
     * The visible effect.
     */
    private final ColorInput dropOverlay = new ColorInput();

    /**
     * Handler for drag&drop outside a window
     */
    private DropStage dropStage;

    /**
     * The current node where the effect is active.
     */
    private Node effectTarget;

    /**
     * Temp stage when the view was dropped outside a stagediver.fx window.
     */
    private Stage droppedStage;

    /**
     * Create a new drag&drop manager instance.
     *
     * @param windowManager The window manager which handles the views and sub windows.
     */
    public DragNDropManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public static ViewStatus getDragedViewStatus() {
        return dragedViewStatus;
    }

    public static void setDragedViewStatus(ViewStatus dragedViewStatus) {
        DragNDropManager.dragedViewStatus = dragedViewStatus;
    }

    /**
     * Called to initialize a controller after its root element has been completely processed.
     */
    public void init() {
        windowManager.getRootPane().getScene().setOnDragExited(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                System.out.println("exit " + dropStage);
                if (dropStage == null) {
                    dropStage = new DropStage(DragNDropManager.this);
                    dropStage.show();
                }
                event.consume();
            }
        });
    }

    /**
     * Initialize the drag&drop for a view.
     *
     * @param event The mouse event.
     */
    public void onDragDetected(MouseEvent event) {
        if (!(event.getSource() instanceof TabPane)) {
            return;
        }

        TabPane pane = (TabPane) event.getSource();
        ViewStatus view = (ViewStatus) pane.getSelectionModel().getSelectedItem().getUserData();
        setDragedViewStatus(view);

        Dragboard db = pane.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.put(DATAFORMAT, view.getView().id());

        db.setContent(content);
        if (dropStage == null) {
            dropStage = new DropStage(DragNDropManager.this);
            dropStage.show();
        }
        event.consume();
    }

    /**
     * Finish the drag&drop gesture.
     *
     * @param event The drag event
     */

    public void onDragDone(DragEvent event) {
        if (!(event.getSource() instanceof TabPane) && ((TabPane) event.getSource()).getUserData() instanceof TabArea) {
            return;
        }
        TabPane source = (TabPane) event.getSource();
        TabArea area = (TabArea) source.getUserData();
        Dragboard db = event.getDragboard();
        if (droppedStage != null) {
            droppedStage.setWidth(droppedStage.getWidth() - 1);
            droppedStage = null;
        }
        if (event.getTransferMode() == TransferMode.MOVE && db.hasContent(DATAFORMAT)) {
            area.handleEmpty();
            closeDropStages();
            getDragedViewStatus().setDeviderPositions();
            setDragedViewStatus(null);
        }
        windowManager.redrawAreas();
        event.consume();
    }

    /**
     * Handle Drag&Drop to a invisible stage => opens a new window
     *
     * @param event The fired event.
     * @param dropStage The stage where the view was dropped.
     */

    public void onDragDroppedNewStage(DragEvent event, Stage dropStage) {

        if (isInvalidDragboard(event)) {
            return;
        }

        RootArea area = new RootArea(this, true);
        Stage stage = initManagedWindow(dropStage, area);

        getDragedViewStatus().getArea().remove(getDragedViewStatus(), false);
        getDragedViewStatus().setPosition(Position.CENTER);
        area.add(getDragedViewStatus(), Position.CENTER);
        stage.setTitle(getDragedViewStatus().getView().id());
        stage.show();
        droppedStage = stage;
        windowManager.register(area);
        completeDropped(event, true);
    }

    /**
     * Handle the dropped event for panes. Mainly this event removes the view from the old position
     * and adds it at the new position.
     *
     * @param event The drag event.
     */

    public void onDragDropped(DragEvent event) {
        boolean success = false;
        if (isInvalidDragboard(event)) {
            return;
        }
        if (!(event.getGestureTarget() instanceof Control)) {
            return;
        }
        Control targetNode = (Control) event.getGestureTarget();
        // Add view to new area
        if (targetNode.getUserData() instanceof ViewArea) {
            ViewArea target = (ViewArea) targetNode.getUserData();
            getDragedViewStatus().getArea().remove(getDragedViewStatus(), false);
            getDragedViewStatus().setPosition(detectPosition(event, targetNode));
            Position position = detectPosition(event, targetNode);
            target.add(getDragedViewStatus(), position);
            success = true;
        }
        completeDropped(event, success);
    }

    /**
     * Handle the drag exited event for panes.
     *
     * @param event the drag event.
     */

    public void onDragExited(DragEvent event) {
        if (!(event.getSource() instanceof Node)) {
            return;
        }
        Node target = (Node) event.getSource();
        target.setEffect(null);
        event.consume();
    }

    /**
     * Handle the drag over event. It draws the drop position for the current cursor position.
     *
     * @param event The drag event.
     */

    public void onDragOver(DragEvent event) {
        if (!(event.getSource() instanceof Control)) {
            return;
        }
        Control target = (Control) event.getSource();
        if (target != effectTarget) {
            if (effectTarget != null) {
                effectTarget.setEffect(null);
            }
            target.setEffect(effect);
        }
        effect.setMode(BlendMode.COLOR_BURN);
        dropOverlay.setPaint(Color.LIGHTSTEELBLUE);
        effect.setBottomInput(dropOverlay);
        Position position = detectPosition(event, target);

        ViewArea area = (ViewArea) target.getUserData();
        if (!area.dropToCenter() && position == Position.CENTER) {
            event.consume();
            target.setEffect(null);
            effectTarget = null;
            return;
        }
        adjustOverlay(target, position);
        effectTarget = target;
        event.acceptTransferModes(TransferMode.MOVE);
        event.consume();
    }

    /**
     * Get the window manager instance.
     *
     * @return The window manager instance.
     */

    public WindowManager getWindowManager() {
        return windowManager;
    }

    /**
     * Complete the dropped event. This contains the cleaning the effects and other status.
     *
     * @param event The drag event
     * @param success Was the drop gesture successful
     */
    private void completeDropped(DragEvent event, boolean success) {
        if (effectTarget != null) {
            effectTarget.setEffect(null);
        }
        effectTarget = null;

        event.setDropCompleted(success);
        // closeDropStages();
        event.consume();
    }

    /**
     * Validates the dragboard content.
     *
     * @param event The drag drop event.
     * @return False if the dragboard of the event contains a valid view id.
     */
    private boolean isInvalidDragboard(DragEvent event) {
        // Check if dropped content is valid for dropping here
        Dragboard dragboard = event.getDragboard();
        return !dragboard.hasContent(DATAFORMAT) || !dragboard.getContent(DATAFORMAT).equals(getDragedViewStatus().getView().id());
    }

    /**
     * Detect in witch sub area of the rootpane the given dragevent is rised.
     *
     * @param event The drag event
     * @return The position value for the detected sub area.
     */
    private Position detectPosition(DragEvent event, Control source) {
        double areaX = event.getX() / source.getWidth();
        double areaY = event.getY() / source.getHeight();
        if (0.25 <= areaX && areaX < 0.75 && 0.25 <= areaY && areaY < 0.75) {
            return Position.CENTER;
        } else if (areaY < 0.25) {
            return Position.TOP;
        } else if (areaY >= 0.75) {
            return Position.BOTTOM;
        } else if (areaX < 0.25) {
            return Position.LEFT;
        } else {
            return Position.RIGHT;
        }
    }

    private void adjustOverlay(Control target, Position position) {
        switch (position) {
        case CENTER:
            dropOverlay.setX(0);
            dropOverlay.setY(0);
            dropOverlay.setWidth(target.getWidth());
            dropOverlay.setHeight(target.getHeight());
            break;
        case LEFT:
            dropOverlay.setX(0);
            dropOverlay.setY(0);
            dropOverlay.setWidth(target.getWidth() * 0.5);
            dropOverlay.setHeight(target.getHeight());
            break;
        case RIGHT:
            dropOverlay.setX(target.getWidth() * 0.5);
            dropOverlay.setY(0);
            dropOverlay.setWidth(target.getWidth() * 0.5);
            dropOverlay.setHeight(target.getHeight());
            break;
        case TOP:
            dropOverlay.setX(0);
            dropOverlay.setY(0);
            dropOverlay.setWidth(target.getWidth());
            dropOverlay.setHeight(target.getHeight() * 0.5);
            break;
        case BOTTOM:
            dropOverlay.setX(0);
            dropOverlay.setY(target.getHeight() * 0.5);
            dropOverlay.setWidth(target.getWidth());
            dropOverlay.setHeight(target.getHeight() * 0.5);
        }
    }

    /**
     * Initialize a new managed window.
     *
     * @param dropStage The stage where the view was dropped.
     * @param area The new root area which should be the new root node for the new stage.
     * @return The new created stage.
     */
    private Stage initManagedWindow(Stage dropStage, final RootArea area) {
        Scene scene = new Scene(area.getNode(), dropStage.getWidth(), dropStage.getHeight());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setWidth(dropStage.getWidth());
        stage.setHeight(dropStage.getHeight());
        stage.setX(dropStage.getX());
        stage.setY(dropStage.getY());
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                windowManager.remove(area);
            }
        });
        return stage;
    }

    /**
     * Close all the invisible drop stages.
     */
    private void closeDropStages() {

        if (dropStage != null) {
            dropStage.close();
            dropStage = null;
        }
    }
}