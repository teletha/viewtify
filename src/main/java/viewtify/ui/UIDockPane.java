/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.util.function.Function;

import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.node.DockNode.DockPosition;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.system.AnchorageSystem;

import kiss.I;

public class UIDockPane extends UserInterface<UIDockPane, DockStation> {

    /**
     * @param view
     */
    public UIDockPane(View view) {
        super(AnchorageSystem.createStation(), view);
    }

    /**
     * Load tab with the specified view.
     * 
     * @param label Specify the label of the tab. This is used as a temporary label until the
     *            contents of the tab are read, as tab loading is delayed until needed actually.
     * @param loadingViewType A view type to load.
     * @return
     */
    public <V extends View> UIDockPane load(String label, Class<V> loadingViewType) {
        return load(label, tab -> I.make(loadingViewType));
    }

    /**
     * Load tab with the specified view.
     * 
     * @param label Specify the label of the tab. This is used as a temporary label until the
     *            contents of the tab are read, as tab loading is delayed until needed actually.
     * @param loadingViewType A view type to load.
     * @return
     */
    public <V extends View> UIDockPane load(String label, Function<UITab, View> viewBuilder) {
        DockNode dock = AnchorageSystem.createDock(label, viewBuilder.apply(null).ui());
        dock.dock(ui, DockPosition.TOP);

        return this;
    }
}
