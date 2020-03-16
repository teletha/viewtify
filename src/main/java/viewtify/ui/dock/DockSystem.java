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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import kiss.I;
import kiss.Managed;
import kiss.Singleton;
import kiss.Storable;
import kiss.Variable;
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

    /** Layout Store */
    static DockLayout layout;

    /**
     * Place the view on the top side.
     */
    static final int TOP = 0;

    /**
     * Place the view on the left side.
     */
    static final int LEFT = 1;

    /**
     * Place the view within the center.
     */
    static final int CENTER = 2;

    /**
     * Place the window on the right side.
     */
    static final int RIGHT = 3;

    /**
     * Place the window on the bottom.
     */
    static final int BOTTOM = 4;

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
        Viewtify.inUI(() -> {
            String id = view.id();
            Tab tab = new Tab(id);
            tab.setClosable(true);
            tab.setContent(view.ui());
            tab.setId(id);

            layout.findAreaBy(id).or(root()).add(tab, CENTER);
        });
    }

    /**
     * Create the main root area lazy.
     *
     * @return The main area.
     */
    private static synchronized RootArea root() {
        if (root == null) {
            layout = I.make(DockLayout.class);

            if (layout.windows.isEmpty()) {
                root = new RootArea();
                layout.windows.add(root);
            } else {
                for (int i = 0; i < layout.windows.size(); i++) {
                    RootArea area = layout.windows.get(i);

                    if (i == 0) {
                        root = area;
                    } else {
                        openNewWindow(area, new BoundingBox(0, 0, 0, 0), null);
                    }
                }
            }
        }
        return root;
    }

    /**
     * Open new window with the specified {@link RootArea}.
     * 
     * @param area
     * @param bounds
     * @param shown
     */
    private static void openNewWindow(RootArea area, Bounds bounds, EventHandler<WindowEvent> shown) {
        Scene scene = new Scene(area.node, bounds.getWidth(), bounds.getHeight());
        Viewtify.applyApplicationStyle(scene);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setOnShown(shown);
        stage.setOnCloseRequest(e -> {
            layout.windows.remove(area);
            Viewtify.untrackLocation(area.id);
        });
        Viewtify.trackLocation(area.id, stage);
        stage.show();
    }

    /**
     * 
     */
    @Managed(Singleton.class)
    static class DockLayout implements Storable<DockLayout> {
        public List<RootArea> windows = new ArrayList();

        private DockLayout() {
            restore();
        }

        private Variable<ViewArea> findAreaBy(String id) {
            for (RootArea root : windows) {
                Variable<ViewArea> area = root.findAreaBy(id);

                if (area.isPresent()) {
                    return area;
                }
            }
            return Variable.empty();
        }
    }

    // ==================================================================================
    // The drag&drop manager. The implementations handles the full dnd management of views.
    // ==================================================================================
    /** The specialized data format to handle the drag&drop gestures with managed tabs. */
    private static final DataFormat DnD = new DataFormat("drag and drop manager");

    /** Temporal storage for the draged tab */
    private static Tab dragedTab;

    /** Temporal storage for the draged tab */
    private static TabArea dragedTabArea;

    /** Temp stage when the view was dropped outside a managed window. */
    private static final DropStage dropStage = new DropStage();

    /** The effect for the current drop zone. */
    private static final Blend effect = new Blend();

    /** The visible effect. */
    private static final ColorInput dropOverlay = new ColorInput();

    static {
        dropOverlay.setPaint(Color.LIGHTBLUE);
        effect.setMode(BlendMode.OVERLAY);
        effect.setBottomInput(dropOverlay);
    }

    /**
     * Initialize the drag&drop for tab.
     *
     * @param event The mouse event.
     * @param tab The target tab.
     */
    static void onDragDetected(MouseEvent event, TabArea area, Tab tab) {
        event.consume(); // stop event propagation

        dragedTab = tab;
        dragedTabArea = area;

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
            dropStage.close();

            if (event.getTransferMode() == TransferMode.MOVE && event.getDragboard().hasContent(DnD)) {
                area.handleEmpty();
                dragedTab = null;
                dragedTabArea = null;

                layout.store();
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
            Bounds contentsBound = ui.getBoundsInLocal();
            double titleBarHeight = ui.getScene().getWindow().getHeight() - ui.getScene().getHeight() - 8;

            RootArea area = new RootArea();
            area.sub = true;

            Bounds bounds = new BoundingBox(event.getScreenX(), event.getScreenY() - titleBarHeight, contentsBound
                    .getWidth(), contentsBound.getHeight() + titleBarHeight);

            openNewWindow(area, bounds, e -> {
                dragedTabArea.remove(dragedTab, false);
                area.add(dragedTab, CENTER);
                layout.windows.add(area);
            });

            event.setDropCompleted(true);
            event.consume();

            layout.store();
        }
    }

    /**
     * Handle the dropped event for panes. Mainly this event removes the view from the old position
     * and adds it at the new position.
     *
     * @param event The drag event.
     */
    static void onDragDropped(DragEvent event, TabArea area) {
        if (isValidDragboard(event)) {
            // Add view to new area
            dragedTabArea.remove(dragedTab, false);
            area.add(dragedTab, detectPosition(event, area.node));

            event.setDropCompleted(true);
            event.consume();
        }
    }

    /**
     * Handle the drag entered event for panes.
     *
     * @param event the drag event.
     */
    static void onDragEntered(DragEvent event, TabArea area) {
        if (isValidDragboard(event)) {
            event.consume();

            ((Stage) area.node.getScene().getWindow()).toFront();
        }
    }

    /**
     * Handle the drag exited event for panes.
     *
     * @param event the drag event.
     */
    static void onDragExited(DragEvent event, TabArea area) {
        if (isValidDragboard(event)) {
            event.consume();

            // erase overlay effect
            area.node.setEffect(null);
        }
    }

    /**
     * Handle the drag over event. It draws the drop position for the current cursor position.
     *
     * @param event The drag event.
     */
    static void onDragOver(DragEvent event, TabArea area) {
        if (isValidDragboard(event)) {
            event.consume();
            event.acceptTransferModes(TransferMode.MOVE);

            // apply overlay effect
            area.node.setEffect(effect);
            switch (detectPosition(event, area.node)) {
            case CENTER:
                dropOverlay.setX(0);
                dropOverlay.setY(0);
                dropOverlay.setWidth(area.node.getWidth());
                dropOverlay.setHeight(area.node.getHeight());
                break;
            case LEFT:
                dropOverlay.setX(0);
                dropOverlay.setY(0);
                dropOverlay.setWidth(area.node.getWidth() * 0.5);
                dropOverlay.setHeight(area.node.getHeight());
                break;
            case RIGHT:
                dropOverlay.setX(area.node.getWidth() * 0.5);
                dropOverlay.setY(0);
                dropOverlay.setWidth(area.node.getWidth() * 0.5);
                dropOverlay.setHeight(area.node.getHeight());
                break;
            case TOP:
                dropOverlay.setX(0);
                dropOverlay.setY(0);
                dropOverlay.setWidth(area.node.getWidth());
                dropOverlay.setHeight(area.node.getHeight() * 0.5);
                break;
            case BOTTOM:
                dropOverlay.setX(0);
                dropOverlay.setY(area.node.getHeight() * 0.5);
                dropOverlay.setWidth(area.node.getWidth());
                dropOverlay.setHeight(area.node.getHeight() * 0.5);
            }
        }
    }

    /**
     * Request the closing window event.
     */
    static void requestCloseWindow(RootArea area) {
        // Close the window only after all node related operations have been completed. If you close
        // the window immediately at this time, the window will disappear before the tab removal
        // process and a native error will occur. So here we have to ask only to close the window.
        Platform.runLater(() -> {
            ((Stage) area.node.getScene().getWindow()).close();

            layout.windows.remove(area);
            layout.store();
            Viewtify.untrackLocation(area.id);
        });
    }

    /**
     * Validates the dragboard content.
     *
     * @param event The drag drop event.
     * @return True if the dragboard of the event is valid.
     */
    private static boolean isValidDragboard(DragEvent event) {
        Dragboard board = event.getDragboard();
        return board.hasContent(DnD) && board.getContent(DnD).equals(DnD.toString());
    }

    /**
     * Detect in witch sub area of the rootpane the given dragevent is rised.
     *
     * @param event The drag event
     * @return The position value for the detected sub area.
     */
    private static int detectPosition(DragEvent event, Control source) {
        double areaX = event.getX() / source.getWidth();
        double areaY = event.getY() / source.getHeight();
        if (0.25 <= areaX && areaX < 0.75 && 0.25 <= areaY && areaY < 0.75) {
            return CENTER;
        } else if (areaY < 0.25) {
            return TOP;
        } else if (areaY >= 0.75) {
            return BOTTOM;
        } else if (areaX < 0.25) {
            return LEFT;
        } else {
            return RIGHT;
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
            Map<Stage, Boolean> temporaryOnTop = new HashMap();

            for (RootArea root : layout.windows) {
                Stage stage = root.getStage();
                temporaryOnTop.put(stage, stage.isAlwaysOnTop());
                stage.setAlwaysOnTop(true);
            }

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
                scene.setOnDragOver(e -> {
                    if (e.getDragboard().hasContent(DnD)) {
                        e.acceptTransferModes(TransferMode.MOVE);

                        if (borderBox != null && borderBox.isShowing()) {
                            borderBox.setX(e.getScreenX() + 4);
                            borderBox.setY(e.getScreenY() + 4);
                        }
                    }
                    e.consume();
                });
                scene.setOnDragEntered(e -> showBorderBox());
                scene.setOnDragExited(e -> hideBorderBox());
                scene.setOnDragDropped(DockSystem::onDragDroppedNewStage);

                // show stage
                stage.setScene(scene);
                stage.show();

                // register
                stages.add(stage);
            }

            for (RootArea root : layout.windows) {
                Stage stage = root.getStage();
                stage.setAlwaysOnTop(temporaryOnTop.remove(stage));
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

        /** A useful auxiliary line to predict the display position of a new window. */
        private Stage borderBox;

        /**
         * Displays a useful auxiliary line to predict the display position of the new window.
         */
        private void showBorderBox() {
            borderBox = new Stage();
            borderBox.initOwner(stages.get(0));
            borderBox.initStyle(StageStyle.TRANSPARENT);
            borderBox.setAlwaysOnTop(true);
            // The initial position is off the screen.
            borderBox.setX(-1000);
            borderBox.setY(-1000);

            Rectangle rect = new Rectangle(dragedTabArea.node.getWidth(), dragedTabArea.node.getHeight());
            rect.setStroke(dropOverlay.getPaint());
            rect.setStrokeWidth(5);
            rect.setFill(Color.TRANSPARENT);

            Group group = new Group();
            group.setStyle("-fx-background-color: rgba(0,0,0,0);");
            group.getChildren().add(rect);

            // show box
            borderBox.setScene(new Scene(group, rect.getWidth(), rect.getHeight(), Color.TRANSPARENT));
            borderBox.show();
        }

        /**
         * Erases a useful auxiliary line to predict the display position of a new window.
         */
        private void hideBorderBox() {
            borderBox.close();
            borderBox = null;
        }
    }
}