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
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The DropStage is a container which handles the drop events of views outside the the application
 * windows.
 * <p/>
 * This drop events are captured by one undecorated and transparent stage per screen. This stages
 * covers the whole screen.
 */
class DropStage {
    /**
     * The drag&drop manager
     */
    private final DragNDropManager dndManager;

    /**
     * The the primary stage containing the window manager.
     */
    private final Stage owner;

    /**
     * A list with all stages (one per screen) which are used as drop areas.
     */
    private final List<Stage> stages = new ArrayList<>();

    /**
     * Initialize the drop stages for a new drag&drop gesture.
     *
     * @param dndManager The drag&drop manager which handles the events.
     */
    public DropStage(final DragNDropManager dndManager) {
        this.dndManager = dndManager;
        this.owner = (Stage) dndManager.getWindowManager().getRootPane().getScene().getWindow();
    }

    /**
     * Show the drop stages.
     */
    public void show() {
        List<Screen> screenList = Screen.getScreens();

        for (Screen screen : screenList) {
            Stage stage = initDropStage(screen);
            stages.add(stage);
        }
    }

    /**
     * Init a drop stage for the given screen.
     *
     * @param screen Init a drop stage for this screen.
     * @return The initialized drop stage.
     */
    private Stage initDropStage(Screen screen) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setOpacity(0.01);
        // stage.initOwner(owner);
        Rectangle2D screenBounds = screen.getVisualBounds();
        // set Stage boundaries to visible bounds of the main screen
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: null");
        Scene scene = new Scene(pane, screenBounds.getWidth(), screenBounds.getHeight(), Color.color(1, 1, 1, 0.01));

        stage.setScene(scene);
        stage.show();
        initSceneEvents(scene, stage);
        return stage;
    }

    /**
     * Initialize the events for a stage.
     *
     * @param scene The viewed scene.
     * @param stage The stage.
     */
    private void initSceneEvents(final Scene scene, final Stage stage) {
        scene.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                owner.requestFocus();
                dndManager.getWindowManager().bringToFront();
                event.consume();
            }
        });
        scene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard dragboard = event.getDragboard();
                if (!dragboard.hasContent(DragNDropManager.DATAFORMAT)) {
                    event.consume();
                    return;
                }
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });
        scene.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                owner.requestFocus();
                dndManager.getWindowManager().bringToFront();
                event.consume();
            }
        });
        scene.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                dndManager.onDragDroppedNewStage(event, stage);
            }
        });
    }

    /**
     * Close all open stages which are used for the drag&drop gesture.
     */
    public void close() {
        List<Stage> immuteAbleStages = List.copyOf(stages);
        for (Stage stage : immuteAbleStages) {
            stage.close();
            stages.remove(stage);
        }
    }
}
