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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import viewtify.ui.View;

/**
 * Handles the full window management with fully customizable layout and drag&drop into new not
 * existing windows.
 */
public class WindowManager {

    protected Pane rootPane = new HBox();

    private final DNDManager dragNDropManager = new DNDManager(this);

    private final List<RootArea> subWindows = new ArrayList<>();

    private Map<String, ViewStatus> views = new LinkedHashMap<>();

    private RootArea mainArea;

    private View focusedView;

    private View lastFocusedView;

    /**
     * Called to initialize a controller after its root element has been completely processed.
     */
    public void init() {
        dragNDropManager.init();
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                for (ViewStatus status : views.values()) {
                    status.setDeviderPositions();
                }
            }
        });
    }

    /**
     * Register a new view within this window manager.
     * <p/>
     * The Position will give an advice where this view should be placed.
     *
     * @param view The view to register.
     */
    public void register(final View view) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    register(view);
                }
            });
            return;
        }

        ViewStatus v = new ViewStatus(view);
        if (views.containsKey(view.id())) {
            ViewStatus oldView = views.get(view.id());
            TabArea area = oldView.getArea();
            area.add(v, Position.CENTER);
            area.remove(oldView);
        } else {
            getMainRootArea().add(v, v.getPosition());
        }
        views.put(view.id(), v);
    }

    /**
     * Register a new view within this window manager using a parent view to define the exact
     * position.
     *
     * @param view The view to register.
     * @param parent An already registered view which defines the exact position to insert the view.
     */
    public void register(final View view, final View parent) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    register(view, parent);
                }
            });
            return;
        }

        if (!views.containsKey(parent.id())) {
            throw new IllegalArgumentException("Can not find parent view");
        }
        ViewStatus parentStatus = views.get(parent.id());
        ViewStatus viewStatus = new ViewStatus(view, parentStatus);

        if (views.containsKey(view.id())) {
            ViewStatus oldView = views.get(view.id());
            oldView.getArea().remove(viewStatus);
        }
        parentStatus.getArea().add(viewStatus, viewStatus.getPosition());
        views.put(view.id(), viewStatus);
    }

    /**
     * Get the root pane for this window manager.
     *
     * @return The root pane.
     */
    public Parent getRootPane() {
        return getMainRootArea().getNode();
    }

    /**
     * Restore the layout to default.
     * <p/>
     * The layout is recreated in the same way as it was the first time initialized.
     */
    public void restoreDefaultLayout() {
        mainArea = null;
        List<RootArea> immuteAbleSubWindows = List.copyOf(subWindows);
        for (RootArea subWindow : immuteAbleSubWindows) {
            remove(subWindow);
        }
        rootPane.getChildren().clear();
        Map<String, ViewStatus> views = this.views;
        this.views = new LinkedHashMap<>();
        for (ViewStatus view : views.values()) {
            view.restoreDefault();
            if (view.getParent() == null) {
                register(view.getView());
            } else {
                register(view.getView(), view.getParent().getView());
            }
        }
    }

    /**
     * Close the specified view.
     * <p/>
     * The given view must be registered within the
     * {@link de.qaware.sdfx.windowmtg.api.WindowManager}. If it is not registered a
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param view That view that should be closed
     */
    public void closeView(View view) {
        if (!views.containsKey(view.id())) {
            throw new IllegalArgumentException(String.format("View with id '%s' is not registered", view.id()));
        }
        ViewStatus viewStatus = views.get(view.id());
        viewStatus.getArea().remove(viewStatus);
        viewStatus.setStatus(ViewStatus.Status.HIDDEN);
    }

    /**
     * Clone the specified view.
     * <p/>
     * The cloned view will be placed next to the given view in the same tab area.
     * <p/>
     * The given view must be registered within the
     * {@link de.qaware.sdfx.windowmtg.api.WindowManager}. If it is not registered a
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param view Clone the given view.
     * @return The cloned view object.
     */
    public View cloneView(View view) {
        return null;
    }

    /**
     * Show a closed view again. The view will be shown at the same position where it was on close.
     * The given view must be registered within the
     * {@link de.qaware.sdfx.windowmtg.api.WindowManager}. If it is not registered a
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param view The view to show.
     */
    public void showView(View view) {
        ViewStatus viewStatus = views.get(view.id());
        if (viewStatus == null || viewStatus.getView() != view) {
            throw new IllegalArgumentException(String.format("View with id '%s' is not registered", view.id()));
        }
        viewStatus.restoreDefault();

        ViewStatus parent = viewStatus.getParent();
        boolean added = false;
        views.remove(view.id());

        while (!added && parent != null) {
            if (parent.getStatus() == ViewStatus.Status.VISIBLE) {

                register(view, parent.getView());
                added = true;
            }
            parent = parent.getParent();
        }
        if (!added) {
            register(view);
        }
    }

    /**
     * Find a view with the assigned view id.
     * <p/>
     * This returns that view that has the given unique view id. If there is no view found it
     * returns null.
     *
     * @param viewID The view id to search.
     * @return The registered view or null if it was not found.
     */
    public View findView(String viewID) {
        if (!views.containsKey(viewID)) {
            return null;
        }
        return views.get(viewID).getView();
    }

    /**
     * Get that view that currently holds the focus within this window.
     *
     * @return That view that holds the focus.
     */
    public View getFocusedView() {
        return focusedView;
    }

    /**
     * Get that view that holds recently the focus within this window..
     *
     * @return That view that hodls recently the focus.
     */
    public View getLastFocusedView() {
        return lastFocusedView;
    }

    /**
     * Set the view that holds currently the focus and updates the last focused view.
     *
     * @param focusedView The view that should hold the focus.
     */
    public void setFocusedView(View focusedView) {
        this.lastFocusedView = this.focusedView;
        this.focusedView = focusedView;
    }

    /**
     * Register a new root area as subwindow.
     *
     * @param area The new root area.
     */
    public void register(RootArea area) {
        subWindows.add(area);
    }

    /**
     * Bring all windows managed by this window manager to front.
     */
    public void bringToFront() {

        for (RootArea area : subWindows) {
            if (area.getNode().getScene().getWindow() instanceof Stage) {
                ((Stage) area.getNode().getScene().getWindow()).toFront();
            }
        }
        ((Stage) mainArea.getNode().getScene().getWindow()).toFront();
    }

    /**
     * Remove and close the given root area.
     *
     * @param area The root area to remove.
     */
    public void remove(RootArea area) {
        List<ViewStatus> views = getForRootArea(area);
        for (ViewStatus view : views) {
            view.getArea().remove(view);
        }
        ((Stage) area.getNode().getScene().getWindow()).close();
        subWindows.remove(area);
    }

    /**
     * Get the main area. If it not exists it will be created.
     *
     * @return The main area.
     */
    public RootArea getMainRootArea() {
        if (mainArea == null) {
            mainArea = new RootArea(rootPane, dragNDropManager, false);
        }
        return mainArea;
    }

    /**
     * Request the redrawing of all areas.
     */
    public void redrawAreas() {
        getRootPane().requestLayout();
        for (RootArea area : subWindows) {
            area.getNode().requestLayout();
        }
    }

    /**
     * Get a list with all views which are registered under the given {@link RootArea}
     *
     * @param area The requested root area.
     * @return A list with all views which are registered under the given area.
     */
    private List<ViewStatus> getForRootArea(final RootArea area) {
        List<ViewStatus> areaViews = new ArrayList<>();
        for (ViewStatus view : views.values()) {
            if (view.getArea() != null && view.getArea().getRootArea() == area) {
                areaViews.add(view);
            }
        }
        return areaViews;
    }
}