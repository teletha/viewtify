/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;

import kiss.Disposable;
import kiss.I;
import kiss.Signal;
import kiss.Variable;
import viewtify.Viewtify;
import viewtify.model.Selectable;
import viewtify.ui.helper.User;

/**
 * @version 2018/09/09 12:03:35
 */
public class UITabPane extends UserInterface<UITabPane, TabPane> {

    /** The model disposer. */
    private Disposable disposable = Disposable.empty();

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    private UITabPane(View view) {
        super(new TabPane(), view);

        // FUNCTIONALITY : wheel scroll will change selection.
        when(User.Scroll).take(Action.inside(ui.lookup(".tab-header-background"))).to(Action.traverse(ui.getSelectionModel()));
    }

    public <T> UITabPane model(Selectable<T> model, BiFunction<UITab, T, View> view) {
        if (model != null) {
            disposable.dispose();
            disposable = Disposable.empty();

            model.add.startWith(model).to(v -> {
                load(v.toString(), tab -> view.apply(tab, v));
                last().v.closed.to(() -> model.remove(v));
            });
            model.remove.to(v -> {
            });

            disposable.add(model.selectionIndex.observeNow().to(ui.getSelectionModel()::select));
            disposable.add(Viewtify.calculate(ui.getSelectionModel())
                    .flatObservable(SelectionModel::selectedIndexProperty)
                    .as(Integer.class)
                    .observe()
                    .to(model::select));
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
        return load(label, tab -> View.build(loadingViewType));
    }

    /**
     * Load tab with the specified view.
     * 
     * @param label A tab label.
     * @param loadingViewType A view type to load.
     * @return
     */
    public <V extends View> UITabPane load(String label, Function<UITab, V> viewBuilder) {
        Tab tab = new Tab(label);
        AtomicBoolean loaded = new AtomicBoolean();

        tab.selectedProperty().addListener(change -> {
            if (loaded.getAndSet(true) == false) {
                V view = viewBuilder.apply(new UITab(tab));
                view.initializeLazy(this.view);

                tab.setContent(view.ui());
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
