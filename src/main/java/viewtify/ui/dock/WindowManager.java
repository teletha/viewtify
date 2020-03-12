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

import javafx.scene.Parent;
import javafx.stage.Stage;

import viewtify.Viewtify;
import viewtify.ui.View;

/**
 * Handles the full window management with fully customizable layout and drag&drop into new not
 * existing windows.
 */
public class WindowManager {

    /** Drag & Drop manager. */
    private final DNDManager dndManager = new DNDManager(this);

    /** Managed windows. */
    final List<RootArea> windows = new ArrayList<>();

    /** Managed views. */
    private final Map<String, ViewStatus> views = new LinkedHashMap<>();

    /** FLAG */
    private boolean initialized;

    /** The main root area. */
    private RootArea root;

    /**
     * Get the root pane for this window manager.
     *
     * @return The root pane.
     */
    public final Parent getRootPane() {
        return root().getNode();
    }

    /**
     * Register a new view within this window manager.
     * <p/>
     * The Position will give an advice where this view should be placed.
     *
     * @param view The view to register.
     */
    public void register(View view) {
        initializeLazy();

        Viewtify.inUI(() -> {
            ViewStatus current = new ViewStatus(view);
            if (views.containsKey(view.id())) {
                ViewStatus old = views.get(view.id());
                TabArea area = old.getArea();
                area.add(current, ViewPosition.CENTER);
                area.remove(old);
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
    final void register(RootArea area) {
        windows.add(area);
    }

    /**
     * Unregister and close the given root area.
     *
     * @param area The root area to remove.
     */
    final void unregister(RootArea area) {
        // remove and close views on the specified area
        Iterator<Entry<String, ViewStatus>> iterator = views.entrySet().iterator();
        while (iterator.hasNext()) {
            ViewStatus status = iterator.next().getValue();
            TabArea tabArea = status.getArea();
            if (tabArea.getRootArea() == area) {
                tabArea.remove(status);
                iterator.remove();
            }
        }

        // remove and close window of the specified area
        ((Stage) area.getNode().getScene().getWindow()).close();
        windows.remove(area);
    }

    /**
     * Bring all windows managed by this window manager to front.
     */
    final void bringToFront() {
        for (RootArea area : windows) {
            if (area.getNode().getScene().getWindow() instanceof Stage) {
                ((Stage) area.getNode().getScene().getWindow()).toFront();
            }
        }
        ((Stage) root.getNode().getScene().getWindow()).toFront();
    }

    /**
     * Create the main root area lazy.
     *
     * @return The main area.
     */
    private synchronized RootArea root() {
        if (root == null) {
            root = new RootArea(dndManager, false);
        }
        return root;
    }

    /**
     * Called to initialize a controller after its root element has been completely processed.
     */
    private synchronized void initializeLazy() {
        if (initialized == false) {
            initialized = true;
            dndManager.init();
        }
    }
}