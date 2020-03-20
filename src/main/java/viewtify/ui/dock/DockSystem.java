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
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import kiss.Signaling;
import kiss.Storable;
import kiss.Variable;
import kiss.WiseFunction;
import viewtify.Viewtify;
import viewtify.ui.UITab;
import viewtify.ui.UserInterfaceProvider;
import viewtify.ui.View;

/**
 * Handles the full window management with fully customizable layout and drag&drop into new not
 * existing windows.
 */
public final class DockSystem {

    /** The root user interface for the docking system. */
    public static final UserInterfaceProvider<Parent> UI = () -> root().node.ui;

    /**
     * Place the view within the center.
     */
    static final int PositionCenter = -1;

    /**
     * Place the view on the top side.
     */
    static final int PositionTop = -2;

    /**
     * Place the view on the left side.
     */
    static final int PositionLeft = -3;

    /**
     * Place the window on the right side.
     */
    static final int PositionRight = -4;

    /**
     * Place the window on the bottom.
     */
    static final int PositionBottom = -5;

    /**
     * Place the view on the restored order.
     */
    static final int PositionRestore = -6;

    /** Configuration store. */
    private static boolean tabMode = true;

    /** Layout Store */
    private static DockLayout layout;

    /** The main root area. */
    private static RootArea root;

    /**
     * Hide.
     */
    private DockSystem() {
    }

    /**
     * Configuration.
     * 
     * @param tab
     */
    public static void configDefaultMode(boolean tab) {
        tabMode = tab;
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
            UITab tab = new UITab();
            tab.text(id);
            tab.setClosable(true);
            tab.setContent(view.ui());
            tab.setId(id);

            layout.findAreaBy(id).or(root()).add(tab, null, PositionRestore, true);
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
     * Save the current layout info.
     */
    static void saveLayout() {
        if (layout != null) {
            layout.requestSave.accept(true);
        }
    }

    static TabArea mainTabArea() {
        WiseFunction<ViewArea<?>, TabArea> finder = I.recurse((self, area) -> {
            if (area instanceof TabArea) {
                return (TabArea) area;
            }

            for (ViewArea child : area.children) {
                TabArea c = self.apply(child);

                if (c != null) {
                    return c;
                }
            }
            return null;
        });
        return finder.apply(root());
    }

    /**
     * Open new window with the specified {@link RootArea}.
     * 
     * @param area
     * @param bounds
     * @param shown
     */
    private static void openNewWindow(RootArea area, Bounds bounds, EventHandler<WindowEvent> shown) {
        Scene scene = new Scene(area.node.ui, bounds.getWidth(), bounds.getHeight());
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
    private static class DockLayout implements Storable<DockLayout> {
        public List<RootArea> windows = new ArrayList();

        private final Signaling<Boolean> requestSave = new Signaling();

        private DockLayout() {
            restore();
            requestSave.expose.debounce(1000, TimeUnit.MILLISECONDS).to(this::store);
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
    private static UITab dragedTab;

    /** Temporal storage for the draged tab */
    private static TabArea dragedTabArea;

    /** The Doppelganger of the tab being dragged. */
    private static final UITab dragedDoppelganger = new UITab();

    /** Temporal storage for the draged tab */
    private static boolean dragedMode;

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
    static void onDragDetected(MouseEvent event, TabArea area, UITab tab) {
        event.consume(); // stop event propagation

        dragedTab = tab;
        dragedTabArea = area;
        dragedDoppelganger.setText(tab.getText());
        dragedMode = event.isControlDown() || event.isShiftDown() || event.isMetaDown() || event.isAltDown() ? !tabMode : tabMode;

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
     * Handle the drag entered event for panes.
     *
     * @param event the drag event.
     */
    static void onDragEntered(DragEvent event, ViewArea area) {
        if (isValidDragboard(event)) {
            event.consume();

            ((Stage) area.node.ui.getScene().getWindow()).toFront();
        }
    }

    /**
     * Handle the drag exited event for panes.
     *
     * @param event the drag event.
     */
    static void onDragExited(DragEvent event, ViewArea area) {
        if (isValidDragboard(event)) {
            event.consume();

            area.node.ui.setEffect(null);
        }
    }

    /**
     * Handle the drag over event. It draws the drop position for the current cursor position.
     *
     * @param event The drag event.
     */
    static void onDragOver(DragEvent event, ViewArea area) {
        if (isValidDragboard(event)) {
            event.consume();

            int position = detectPosition(event, area.node.ui);
            if (position == PositionCenter && area == dragedTabArea) {
                area.node.ui.setEffect(null);
            } else {
                applyOverlay(area.node.ui, position);
                event.acceptTransferModes(TransferMode.MOVE);
            }
        }
    }

    /**
     * Handle the dropped event for panes. Mainly this event removes the view from the old position
     * and adds it at the new position.
     *
     * @param event The drag event.
     */
    static void onDragDropped(DragEvent event, ViewArea area) {
        if (isValidDragboard(event)) {
            // The insertion point is determined from the position of the pointer, but at that time
            // it is necessary to calculate the actual tab size, and if the tab is removed, the size
            // cannot be calculated.
            // Therefore, it is necessary to calculate it first.
            int position = detectPosition(event, area.node.ui);
            if (position != PositionCenter || area != dragedTabArea) {
                if (dragedMode) {
                    dragedTabArea.remove(dragedTab, false);
                    area.add(dragedTab, dragedTabArea, position, dragedMode);
                } else {
                    dragedTabArea.remove(dragedTab, false);
                    area.add(dragedTab, dragedTabArea, position, dragedMode);
                }
            }

            event.setDropCompleted(true);
            event.consume();
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
                area.add(dragedTab, dragedTabArea, PositionCenter, true);
                layout.windows.add(area);
            });

            event.setDropCompleted(true);
            event.consume();

            layout.store();
        }
    }

    /**
     * Apply overlay effect to the specified node.
     * 
     * @param node
     * @param position
     */
    private static void applyOverlay(Node node, int position) {
        node.setEffect(effect);

        Bounds bounds = node.getBoundsInLocal();
        if (position == 0) {
            position = PositionCenter;
            bounds = dragedTab.getStyleableNode().getBoundsInLocal();
        }
        switch (position) {
        case PositionCenter:
            dropOverlay.setX(0);
            dropOverlay.setY(0);
            dropOverlay.setWidth(bounds.getWidth());
            dropOverlay.setHeight(bounds.getHeight());
            break;
        case PositionLeft:
            dropOverlay.setX(0);
            dropOverlay.setY(0);
            dropOverlay.setWidth(bounds.getWidth() * 0.5);
            dropOverlay.setHeight(bounds.getHeight());
            break;
        case PositionRight:
            dropOverlay.setX(bounds.getWidth() * 0.5);
            dropOverlay.setY(0);
            dropOverlay.setWidth(bounds.getWidth() * 0.5);
            dropOverlay.setHeight(bounds.getHeight());
            break;
        case PositionTop:
            dropOverlay.setX(0);
            dropOverlay.setY(0);
            dropOverlay.setWidth(bounds.getWidth());
            dropOverlay.setHeight(bounds.getHeight() * 0.5);
            break;
        case PositionBottom:
            dropOverlay.setX(0);
            dropOverlay.setY(bounds.getHeight() * 0.5);
            dropOverlay.setWidth(bounds.getWidth());
            dropOverlay.setHeight(bounds.getHeight() * 0.5);
        }
    }

    /**
     * Handle the drag entered event for panes.
     *
     * @param event the drag event.
     */
    static void onHeaderDragEntered(DragEvent event, TabArea area) {
        if (isValidDragboard(event)) {
            event.consume();

            area.node.ui.setEffect(null);

            if (area == dragedTabArea) {
                applyOverlay(dragedTab.getStyleableNode(), PositionCenter);
            } else {
                area.add(dragedDoppelganger, dragedTabArea, PositionCenter, true);
                applyOverlay(dragedDoppelganger.getStyleableNode(), 0);
            }
        }
    }

    /**
     * Handle the drag exited event for panes.
     *
     * @param event the drag event.
     */
    static void onHeaderDragExited(DragEvent event, TabArea area) {
        if (isValidDragboard(event)) {
            event.consume();

            revert(area);

            if (area == dragedTabArea) {
                dragedTab.getStyleableNode().setEffect(null);
            }
        }
    }

    /**
     * Handle the drag over event. It draws the drop position for the current cursor position.
     *
     * @param event The drag event.
     */
    static void onHeaderDragOver(DragEvent event, TabArea area) {
        if (isValidDragboard(event)) {
            event.consume();
            event.acceptTransferModes(TransferMode.MOVE);

            int[] values = calculate(area, event);
            int actualIndex = values[0];
            int pointerIndex = values[1];
            int width = values[2];

            ObservableList<Tab> tabs = area.node.ui.getTabs();
            for (int i = 0; i < tabs.size(); i++) {
                Node node = tabs.get(i).getStyleableNode();

                if (i == actualIndex) {
                    double lowerBound = -actualIndex * width;
                    double upperBound = (tabs.size() - actualIndex - 1) * width;
                    node.setTranslateX(Math.max(lowerBound, Math.min(event.getX() - width * (i + 0.5), upperBound)));
                    node.setViewOrder(-100);
                } else if (i < actualIndex) {
                    if (i < pointerIndex) {
                        node.setTranslateX(0);
                        node.setViewOrder(0);
                    } else {
                        node.setTranslateX(width);
                        node.setViewOrder(0);
                    }
                } else {
                    if (i <= pointerIndex) {
                        node.setTranslateX(-width);
                        node.setViewOrder(0);
                    } else {
                        node.setTranslateX(0);
                        node.setViewOrder(0);
                    }
                }
            }
        }
    }

    /**
     * Handle the dropped event for panes. Mainly this event removes the view from the old position
     * and adds it at the new position.
     *
     * @param event The drag event.
     */
    static void onHeaderDragDropped(DragEvent event, TabArea area) {
        if (isValidDragboard(event)) {
            revert(area);

            int[] values = calculate(area, event);

            dragedTabArea.remove(dragedTab, false);
            area.add(dragedTab, dragedTabArea, values[1], true);
            area.node.ui.getSelectionModel().select(values[1]);

            event.setDropCompleted(true);
            event.consume();
        }
    }

    /**
     * Calculate values (current dragging tab index, tab index on pointer and tab width)
     * 
     * @param area
     * @param event
     * @return
     */
    private static int[] calculate(TabArea area, DragEvent event) {
        ObservableList<Tab> tabs = area.node.ui.getTabs();
        int tabWidth = (int) tabs.get(0).getStyleableNode().prefWidth(-1);
        int actualIndex = tabs.indexOf(area == dragedTabArea ? dragedTab : dragedDoppelganger);
        int expectedIndex = Math.min((int) ((event.getX() + tabWidth / 8) / tabWidth), tabs.size() + (actualIndex == -1 ? 0 : -1));

        return new int[] {actualIndex, expectedIndex, tabWidth};
    }

    /**
     * Revert tab's location and remove doppelganger tab.
     * 
     * @param area
     */
    private static void revert(TabArea area) {
        area.remove(dragedDoppelganger, false);

        for (Tab tab : area.node.ui.getTabs()) {
            Node node = tab.getStyleableNode();
            node.setTranslateX(0);
            node.setViewOrder(0);
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
            ((Stage) area.node.ui.getScene().getWindow()).close();

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
    private static int detectPosition(DragEvent event, Node source) {
        Side side = dragedTabArea.node.ui.getSide();
        Bounds bound = source.getBoundsInLocal();
        double horizontalPadding = side.isHorizontal() ? dragedTab.getStyleableNode().prefHeight(-1) : 0;
        double verticalPadding = side.isVertical() ? dragedTab.getStyleableNode().prefWidth(-1) : 0;
        double x = event.getX() - verticalPadding;
        double y = event.getY() - horizontalPadding;
        double width = bound.getWidth() - verticalPadding;
        double height = bound.getHeight() - horizontalPadding;

        double min = 0.3;
        double max = 1 - min;
        double areaX = x / width;
        double areaY = y / height;
        if (min <= areaX && areaX < max && min <= areaY && areaY < max) {
            return PositionCenter;
        }

        boolean bottom = event.getX() * height / width < y;
        boolean right = (height - event.getX() * height / width) < y;

        if (bottom) {
            if (right) {
                return PositionBottom;
            } else {
                return PositionLeft;
            }
        } else {
            if (right) {
                return PositionRight;
            } else {
                return PositionTop;
            }
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

            bringAllWindowsToFront();
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

            Rectangle rect = new Rectangle(dragedTabArea.node.ui.getWidth(), dragedTabArea.node.ui.getHeight());
            rect.setStroke(dropOverlay.getPaint());
            rect.setStrokeWidth(5);
            rect.setFill(Color.WHITE.deriveColor(0, 0, 0, 0.3));

            Group group = new Group();
            group.setStyle("-fx-background-color: rgba(0,0,0,0);");
            group.getChildren().add(rect);

            // show box
            borderBox.setScene(new Scene(group, rect.getWidth(), rect.getHeight(), Color.TRANSPARENT));
            borderBox.show();

            bringAllWindowsToFront();
        }

        /**
         * Erases a useful auxiliary line to predict the display position of a new window.
         */
        private void hideBorderBox() {
            borderBox.close();
            borderBox = null;
        }

        /**
         * Brings all windows to the front.
         */
        private void bringAllWindowsToFront() {
            for (RootArea root : layout.windows) {
                root.node.stage().ifPresent(stage -> {
                    boolean state = stage.isAlwaysOnTop();
                    stage.setAlwaysOnTop(true);
                    stage.setAlwaysOnTop(state);
                });
            }
        }
    }
}