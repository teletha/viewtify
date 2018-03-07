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
import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import toybox.SelectableModel;
import viewtify.View;
import viewtify.Viewtify;

/**
 * @version 2017/12/27 16:00:44
 */
public class UITabPane extends UserInterface<UITabPane, TabPane> {

    private SelectableModel model;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UITabPane(TabPane ui, View view) {
        super(ui, view);
    }

    public <T> UITabPane model(SelectableModel<T> model, BiFunction<UITab, T, View> view) {
        if (model != null) {
            this.model = model;
            System.out.println(model.getSelectedIndex());
            ui.getSelectionModel().select(model.getSelectedIndex());

            Viewtify.calculate(ui.selectionModelProperty())
                    .flatObservable(SingleSelectionModel<Tab>::selectedIndexProperty)
                    .as(Integer.class)
                    .to(model::select);

            for (T item : model.items) {
                load(item.toString(), tab -> view.apply(tab, item));
                last().v.closed.to(() -> model.items.remove(item));
            }
        }
        return this;

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

    /**
     * Retrieve all tabs.
     * 
     * @return
     */
    public Signal<UITab> tabs() {
        return I.signal(ui.getTabs()).map(tab -> new UITab(tab));
    }

    /**
     * Retrieve the first tab.
     * 
     * @return
     */
    public Variable<UITab> first() {
        return tabs().first().to();
    }

    /**
     * Retrieve the first tab.
     * 
     * @return
     */
    public Variable<UITab> last() {
        return tabs().last().to();
    }

    /**
     * The closing policy for the tabs.
     * 
     * @param policy The closing policy for the tabs.
     * @return Chainable API.
     */
    public UITabPane policy(TabClosingPolicy policy) {
        if (policy != null) {
            ui.tabClosingPolicyProperty().set(policy);
        }
        return this;
    }
}
