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
import java.util.function.Supplier;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import kiss.I;
import viewtify.Viewty;

/**
 * @version 2017/11/29 10:12:34
 */
public class UITabPane<T> extends UI<UITabPane, TabPane> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UITabPane(TabPane ui, Viewty view) {
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
    public <V extends Viewty> UITabPane load(String label, Class<V> loadingViewType) {
        return load(label, () -> I.make(loadingViewType));
    }

    /**
     * Load tab with the specified view.
     * 
     * @param label A tab label.
     * @param loadingViewType A view type to load.
     * @return
     */
    public <V extends Viewty> UITabPane load(String label, Supplier<V> view) {
        Tab tab = new Tab(label);
        AtomicBoolean loaded = new AtomicBoolean();

        tab.selectedProperty().addListener(change -> {
            if (loaded.getAndSet(true) == false) {
                tab.setContent(view.get().root());
            }
        });

        ui.getTabs().add(tab);
        return this;
    }
}
