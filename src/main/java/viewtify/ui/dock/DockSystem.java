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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import viewtify.Viewtify;
import viewtify.ui.UserInterfaceProvider;
import viewtify.ui.View;

/**
 * Handles the full window management with fully customizable layout and drag&drop into new not
 * existing windows.
 */
public final class DockSystem {

    /** The root user interface for the docking system. */
    public static final UserInterfaceProvider<Parent> UI = () -> root().node;

    /** Managed windows. */
    private static final List<RootArea> windows = new ArrayList<>();

    /** FLAG */
    private static boolean initialized;

    /** The main root area. */
    private static RootArea root;

    /**
     * Hide.
     */
    private DockSystem() {
    }

    /**
     * Register a new view within this dock system.
     * <p/>
     * The Position will give an advice where this view should be placed.
     *
     * @param view The view to register.
     */
    public static void register(View view) {
        initializeLazy();

        Viewtify.inUI(() -> {
            Tab tab = new Tab(view.id());
            tab.setClosable(true);
            tab.setContent(view.ui());
            tab.setId(view.id());
            tab.setOnCloseRequest(event -> {
                TabArea.of(tab).remove(tab);
            });

            root().add(tab, ViewPosition.CENTER);
        });
    }

    /**
     * Bring all windows managed by this dock system to front.
     */
    private static void bringToFront() {
        for (RootArea area : windows) {
            if (area.node.getScene().getWindow() instanceof Stage) {
                ((Stage) area.node.getScene().getWindow()).toFront();
            }
        }
        ((Stage) root.node.getScene().getWindow()).toFront();
    }

    /**
     * Create the main root area lazy.
     *
     * @return The main area.
     */
    private static synchronized RootArea root() {
        if (root == null) {
            root = new RootArea(false);
        }
        return root;
    }

    /**
     * Called to initialize a controller after its root element has been completely processed.
     */
    private static synchronized void initializeLazy() {
        if (initialized == false) {
            initialized = true;

            root().node.getScene().setOnDragExited(e -> {
                dropStage.open();
                e.consume();
            });
        }
    }

    // ==================================================================================
    // The drag&drop manager. The implementations handles the full dnd management of views.
    // ==================================================================================
    /** The specialized data format to handle the drag&drop gestures with managed tabs. */
    private static final DataFormat DnD = new DataFormat("drag and drop manager");

    /** Temporal storage for the draged tab */
    private static Tab dragedTab;

    /** The effect for the current drop zone. */
    private static final Blend effect = new Blend();

    /** The visible effect. */
    private static final ColorInput dropOverlay = new ColorInput();

    /** Handler for drag&drop outside a window. */
    private static final DropStage dropStage = new DropStage();

    /** The current node where the effect is active. */
    private static Node effectTarget;

    /** Temp stage when the view was dropped outside a managed window. */
    private static Stage droppedStage;

    /**
     * Initialize the drag&drop for tab.
     *
     * @param event The mouse event.
     * @param tab The target tab.
     */
    static void onDragDetected(MouseEvent event, Tab tab) {
        event.consume(); // stop event propagation

        dragedTab = tab;

        ClipboardContent content = new ClipboardContent();
        content.put(DnD, DnD.toString());

        Dragboard board = tab.getTabPane().startDragAndDrop(TransferMode.MOVE);
        board.setContent(content);

        dropStage.open();
    }

    /**
     * Finish the drag&drop gesture.
     *
     * @param event The drag event
     */
    static void onDragDone(DragEvent event, TabArea area) {
        if (isValidDragboard(event)) {
            event.consume(); // stop event propagation

            Dragboard board = event.getDragboard();
            if (droppedStage != null) {
                droppedStage.setWidth(droppedStage.getWidth() - 1);
                droppedStage = null;
            }
            if (event.getTransferMode() == TransferMode.MOVE && board.hasContent(DnD)) {
                area.handleEmpty();
                dropStage.close();
                dragedTab = null;
            }
        }
    }

    /**
     * Handle Drag&Drop to a invisible stage => opens a new window
     *
     * @param event The fired event.
     */
    static void onDragDroppedNewStage(DragEvent event) {
        if (isValidDragboard(event)) {
            // create new window
            Node ui = dragedTab.getContent();
            Bounds bounds = ui.getBoundsInLocal();

            RootArea area = new RootArea(true);
            Scene scene = new Scene(area.node, bounds.getWidth(), bounds.getHeight());
            scene.getStylesheets().addAll(ui.getScene().getStylesheets());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setX(event.getX());
            stage.setY(event.getY());
            stage.setOnShown(e -> windows.add(area));
            stage.setOnCloseRequest(e -> windows.remove(area));

            TabArea.of(dragedTab).remove(dragedTab, false);
            area.add(dragedTab, ViewPosition.CENTER);
            stage.show();
            droppedStage = stage;
            completeDropped(event, true);
        }
    }

    /**
     * Handle the dropped event for panes. Mainly this event removes the view from the old position
     * and adds it at the new position.
     *
     * @param event The drag event.
     */
    static void onDragDropped(DragEvent event) {
        boolean success = false;
        if (isInvalidDragboard(event)) {
            return;
        }
        if (event.getGestureTarget() instanceof Control == false) {
            return;
        }
        Control targetNode = (Control) event.getGestureTarget();

        // Add view to new area
        if (targetNode.getUserData() instanceof ViewArea) {
            ViewArea target = (ViewArea) targetNode.getUserData();
            TabArea.of(dragedTab).remove(dragedTab, false);
            ViewPosition position = detectPosition(event, targetNode);
            target.add(dragedTab, position);
            success = true;
        }
        completeDropped(event, success);
    }

    /**
     * Handle the drag exited event for panes.
     *
     * @param event the drag event.
     */
    static void onDragExited(DragEvent event) {
        if (event.getSource() instanceof Node == false) {
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
    static void onDragOver(DragEvent event) {
        if (isInvalidDragboard(event)) {
            return;
        }

        if (event.getSource() instanceof Control == false) {
            return;
        }
        Control target = (Control) event.getSource();
        if (target != effectTarget) {
            if (effectTarget != null) {
                effectTarget.setEffect(null);
            }
            target.setEffect(effect);
        }
        effect.setMode(BlendMode.OVERLAY);
        dropOverlay.setPaint(Color.LIGHTBLUE);
        effect.setBottomInput(dropOverlay);
        ViewPosition position = detectPosition(event, target);

        ViewArea area = (ViewArea) target.getUserData();
        if (!area.canDropToCenter() && position == ViewPosition.CENTER) {
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
     * Complete the dropped event. This contains the cleaning the effects and other status.
     *
     * @param event The drag event
     * @param success Was the drop gesture successful
     */
    private static void completeDropped(DragEvent event, boolean success) {
        if (effectTarget != null) {
            effectTarget.setEffect(null);
        }
        effectTarget = null;

        event.setDropCompleted(success);
        event.consume();
    }

    /**
     * Validates the dragboard content.
     *
     * @param event The drag drop event.
     * @return False if the dragboard of the event contains a valid view id.
     */
    private static boolean isValidDragboard(DragEvent event) {
        Dragboard board = event.getDragboard();
        return board.hasContent(DnD) && board.getContent(DnD).equals(DnD.toString());
    }

    /**
     * Validates the dragboard content.
     *
     * @param event The drag drop event.
     * @return False if the dragboard of the event contains a valid view id.
     */
    private static boolean isInvalidDragboard(DragEvent event) {
        // Check if dropped content is valid for dropping here
        Dragboard dragboard = event.getDragboard();
        return !dragboard.hasContent(DnD) && !dragboard.getContent(DnD).equals(DnD.toString());
    }

    /**
     * Detect in witch sub area of the rootpane the given dragevent is rised.
     *
     * @param event The drag event
     * @return The position value for the detected sub area.
     */
    private static ViewPosition detectPosition(DragEvent event, Control source) {
        double areaX = event.getX() / source.getWidth();
        double areaY = event.getY() / source.getHeight();
        if (0.25 <= areaX && areaX < 0.75 && 0.25 <= areaY && areaY < 0.75) {
            return ViewPosition.CENTER;
        } else if (areaY < 0.25) {
            return ViewPosition.TOP;
        } else if (areaY >= 0.75) {
            return ViewPosition.BOTTOM;
        } else if (areaX < 0.25) {
            return ViewPosition.LEFT;
        } else {
            return ViewPosition.RIGHT;
        }
    }

    private static void adjustOverlay(Control target, ViewPosition position) {
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
     * The DropStage is a container which handles the drop events of views outside the the
     * application windows.
     * <p/>
     * This drop events are captured by one undecorated and transparent stage per screen. This
     * stages covers the whole screen.
     */
    private static final class DropStage {

        /** A list with all stages (one per screen) which are used as drop areas. */
        private final List<Stage> stages = new ArrayList<>();

        /**
         * Hide
         */
        private DropStage() {
        }

        /**
         * Open the drop stages.
         */
        private void open() {
            Stage owner = (Stage) root().node.getScene().getWindow();

            for (Screen screen : Screen.getScreens()) {
                // Initialize a drop stage for the given screen.
                Stage stage = new Stage();
                stage.initStyle(StageStyle.UTILITY);
                stage.setOpacity(0.01);

                // set Stage boundaries to visible bounds of the main screen
                Rectangle2D bounds = screen.getVisualBounds();
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());

                // root transparent pane as dummy
                Pane pane = new Pane();
                pane.setStyle("-fx-background-color: transparent");

                // create scene and apply event drag&drop handlers
                Scene scene = new Scene(pane, bounds.getWidth(), bounds.getHeight(), Color.TRANSPARENT);
                scene.setOnDragEntered(e -> {
                    owner.requestFocus();
                    DockSystem.bringToFront();
                    e.consume();
                });
                scene.setOnDragExited(e -> {
                    owner.requestFocus();
                    DockSystem.bringToFront();
                    e.consume();
                });
                scene.setOnDragOver(e -> {
                    if (e.getDragboard().hasContent(DnD)) {
                        e.acceptTransferModes(TransferMode.MOVE);
                    }
                    e.consume();
                });
                scene.setOnDragDropped(DockSystem::onDragDroppedNewStage);

                // show stage
                stage.setScene(scene);
                stage.show();

                // register
                stages.add(stage);
            }
        }

        /**
         * Close all open stages which are used for the drag&drop gesture.
         */
        private void close() {
            Iterator<Stage> iterator = stages.iterator();
            while (iterator.hasNext()) {
                iterator.next().close();
                iterator.remove();
            }
        }
    }
}