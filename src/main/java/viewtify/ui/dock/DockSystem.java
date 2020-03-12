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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import viewtify.Viewtify;
import viewtify.ui.View;

/**
 * Handles the full window management with fully customizable layout and drag&drop into new not
 * existing windows.
 */
public final class DockSystem {

    /** Managed windows. */
    private static final List<RootArea> windows = new ArrayList<>();

    /** Managed views. */
    private static final Map<String, ViewStatus> views = new LinkedHashMap<>();

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
     * Get the root pane for this window manager.
     *
     * @return The root pane.
     */
    public static Parent getRootPane() {
        return root().node;
    }

    /**
     * Register a new view within this window manager.
     * <p/>
     * The Position will give an advice where this view should be placed.
     *
     * @param view The view to register.
     */
    public static void register(View view) {
        initializeLazy();

        Viewtify.inUI(() -> {
            ViewStatus current = new ViewStatus(view);
            if (views.containsKey(view.id())) {
                ViewStatus old = views.get(view.id());
                old.area.add(current, ViewPosition.CENTER);
                old.area.remove(old);
            } else {
                root().add(current, ViewPosition.CENTER);
            }
            views.put(view.id(), current);
        });
    }

    /**
     * Register a new root area as subwindow.
     *
     * @param area The new root area.
     */
    static void register(RootArea area) {
        windows.add(area);
    }

    /**
     * Unregister and close the given root area.
     *
     * @param area The root area to remove.
     */
    static void unregister(RootArea area) {
        // remove and close views on the specified area
        Iterator<Entry<String, ViewStatus>> iterator = views.entrySet().iterator();
        while (iterator.hasNext()) {
            ViewStatus status = iterator.next().getValue();
            if (status.area.getRootArea() == area) {
                status.area.remove(status);
                iterator.remove();
            }
        }

        // remove and close window of the specified area
        ((Stage) area.node.getScene().getWindow()).close();
        windows.remove(area);
    }

    /**
     * Bring all windows managed by this window manager to front.
     */
    static void bringToFront() {
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

            getRootPane().getScene().setOnDragExited(e -> {
                if (dropStage == null) {
                    dropStage = new DropStage();
                    dropStage.open();
                }
                e.consume();
            });
        }
    }

    // ==================================================================================
    // The drag&drop manager. The implementations handles the full dnd management of views.
    // ==================================================================================
    /** The specialized data format to handle the drag&drop gestures with managed tabs. */
    private static final DataFormat DATAFORMAT = new DataFormat("drag and drop manager");

    /** Temporal storage for the draged view */
    private static ViewStatus dragedViewStatus;

    /** The effect for the current drop zone. */
    private static final Blend effect = new Blend();

    /** The visible effect. */
    private static final ColorInput dropOverlay = new ColorInput();

    /** Handler for drag&drop outside a window. */
    private static DropStage dropStage;

    /** The current node where the effect is active. */
    private static Node effectTarget;

    /** Temp stage when the view was dropped outside a stagediver.fx window. */
    private static Stage droppedStage;

    /**
     * Initialize the drag&drop for a view.
     *
     * @param event The mouse event.
     */
    static void onDragDetected(MouseEvent event) {
        if (!(event.getSource() instanceof TabPane)) {
            return;
        }

        TabPane pane = (TabPane) event.getSource();
        ViewStatus view = (ViewStatus) pane.getSelectionModel().getSelectedItem().getUserData();
        dragedViewStatus = view;

        Dragboard db = pane.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.put(DATAFORMAT, view.view.id());

        db.setContent(content);
        if (dropStage == null) {
            dropStage = new DropStage();
            dropStage.open();
        }
        event.consume();
    }

    /**
     * Finish the drag&drop gesture.
     *
     * @param event The drag event
     */
    static void onDragDone(DragEvent event) {
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
            dragedViewStatus = null;
        }
        event.consume();
    }

    /**
     * Handle Drag&Drop to a invisible stage => opens a new window
     *
     * @param event The fired event.
     * @param dropStage The stage where the view was dropped.
     */
    static void onDragDroppedNewStage(DragEvent event, Stage dropStage) {
        if (isInvalidDragboard(event)) {
            return;
        }

        RootArea area = new RootArea(true);

        Node ui = dragedViewStatus.view.ui();
        Bounds bounds = ui.getBoundsInLocal();

        Scene scene = new Scene(area.node, bounds.getWidth(), bounds.getHeight());
        scene.getStylesheets().addAll(ui.getScene().getStylesheets());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setX(event.getX());
        stage.setY(event.getY());
        stage.setOnShown(e -> DockSystem.register(area));
        stage.setOnCloseRequest(e -> DockSystem.unregister(area));

        dragedViewStatus.area.remove(dragedViewStatus, false);
        area.add(dragedViewStatus, ViewPosition.CENTER);
        stage.show();
        droppedStage = stage;
        completeDropped(event, true);
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
        if (!(event.getGestureTarget() instanceof Control)) {
            return;
        }
        Control targetNode = (Control) event.getGestureTarget();
        // Add view to new area
        if (targetNode.getUserData() instanceof ViewArea) {
            ViewArea target = (ViewArea) targetNode.getUserData();
            dragedViewStatus.area.remove(dragedViewStatus, false);
            ViewPosition position = detectPosition(event, targetNode);
            target.add(dragedViewStatus, position);
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
    static void onDragOver(DragEvent event) {
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
    private static boolean isInvalidDragboard(DragEvent event) {
        // Check if dropped content is valid for dropping here
        Dragboard dragboard = event.getDragboard();
        return !dragboard.hasContent(DATAFORMAT) || !dragboard.getContent(DATAFORMAT).equals(dragedViewStatus.view.id());
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
     * Close all the invisible drop stages.
     */
    private static void closeDropStages() {
        if (dropStage != null) {
            dropStage.close();
            dropStage = null;
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

        /** The the primary stage containing the window manager. */
        private final Stage owner;

        /** A list with all stages (one per screen) which are used as drop areas. */
        private final List<Stage> stages = new ArrayList<>();

        /**
         * Initialize the drop stages for a new drag&drop gesture.
         */
        private DropStage() {
            this.owner = (Stage) DockSystem.getRootPane().getScene().getWindow();
        }

        /**
         * Open the drop stages.
         */
        private void open() {
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
                    if (e.getDragboard().hasContent(DATAFORMAT)) {
                        e.acceptTransferModes(TransferMode.MOVE);
                    }
                    e.consume();
                });
                scene.setOnDragDropped(e -> {
                    onDragDroppedNewStage(e, stage);
                });

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