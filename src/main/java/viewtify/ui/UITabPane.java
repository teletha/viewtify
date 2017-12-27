/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import kiss.I;
import viewtify.View;

/**
 * @version 2017/12/27 16:00:44
 */
public class UITabPane extends UserInterface<UITabPane, TabPane> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UITabPane(TabPane ui, View view) {
        super(ui, view);
    }

    /**
     * Set initial selected index.
     * 
     * @param initialSelectedIndex
     * @return
     */
    public UITabPane initial(int initialSelectedIndex) {
        restore(ui.getSelectionModel().selectedIndexProperty(), v -> ui.getSelectionModel().select((int) v), initialSelectedIndex);
        return this;
    }

    /**
     * Load tab with the specified view.
     * 
     * @param label A tab label.
     * @param loadingViewType A view type to load.
     * @return
     */
    public <V extends View> UITabPane load(String label, Class<V> loadingViewType) {
        return load(label, tab -> I.make(loadingViewType));
    }

    /**
     * Load tab with the specified view.
     * 
     * @param label A tab label.
     * @param loadingViewType A view type to load.
     * @return
     */
    public <V extends View> UITabPane load(String label, Function<UITab, V> view) {
        Tab tab = new Tab(label);
        AtomicBoolean loaded = new AtomicBoolean();

        tab.selectedProperty().addListener(change -> {
            if (loaded.getAndSet(true) == false) {
                tab.setContent(view.apply(new UITab(tab)).root());
            }
        });

        ui.getTabs().add(tab);
        return this;
    }
}
