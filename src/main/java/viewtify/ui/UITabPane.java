/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.function.Consumer;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TabPane.TabDragPolicy;

import kiss.Disposable;
import viewtify.ui.helper.Actions;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.SelectableHelper;
import viewtify.ui.helper.User;

public class UITabPane extends UserInterface<UITabPane, TabPane>
        implements ContextMenuHelper<UITabPane>, SelectableHelper<UITabPane, UITab>, CollectableHelper<UITabPane, UITab> {

    /** The model disposer. */
    private Disposable disposable = Disposable.empty();

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UITabPane(View view) {
        super(new TabPane(), view);

        // FUNCTIONALITY : wheel scroll will change selection.
        when(User.Scroll).take(Actions.inside(() -> ui.lookup(".tab-header-background"))).to(Actions.traverse(ui.getSelectionModel()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<UITab>> itemsProperty() {
        return new SimpleObjectProperty(ui.getTabs());
    }

    /**
     * Set initial selected index.
     * 
     * @param initialSelectedIndex
     * @return
     */
    public UITabPane initial(int initialSelectedIndex) {
        restore(ui.getSelectionModel().selectedIndexProperty(), v -> ui.getSelectionModel().select(v.intValue()), initialSelectedIndex);
        return this;
    }

    /**
     * Add new tab at last with the specified contents.
     * 
     * @param builder A tab builder.
     * @return Chainable API.
     */
    public final UITabPane tab(Consumer<UITab> builder) {
        if (builder != null) {
            UITab tab = new UITab(view);
            builder.accept(tab);
            addItemAtLast(tab);
        }
        return this;
    }

    /**
     * Add new tab at the specified index with the specified contents.
     * 
     * @param index An index to insert tab.
     * @param builder A tab builder.
     * @return Chainable API.
     */
    public final UITabPane tab(int index, Consumer<UITab> builder) {
        if (builder != null) {
            UITab tab = new UITab(view);
            builder.accept(tab);
            addItemAt(index, tab);
        }
        return this;
    }

    /**
     * The closing policy for the tabs.
     * 
     * @param policy The closing policy for the tabs.
     * @return Chainable API.
     */
    public final UITabPane policy(TabClosingPolicy policy) {
        if (policy != null) {
            ui.setTabClosingPolicy(policy);
        }
        return this;
    }

    /**
     * The closing policy for the tabs.
     * 
     * @param policy The closing policy for the tabs.
     * @return Chainable API.
     */
    public final UITabPane policy(TabDragPolicy policy) {
        if (policy != null) {
            ui.setTabDragPolicy(policy);
        }
        return this;
    }

    /**
     * Check the visibility of tab header area.
     * 
     * @return Result.
     */
    public final boolean isHeaderShown() {
        return hasStyle("hide-header") == false;
    }

    /**
     * Switch the visibility of tab header area.
     * 
     * @param visible
     * @return Chainable API.
     */
    public final UITabPane showHeader(boolean visible) {
        if (visible) {
            unstyle("hide-header");
        } else {
            style("hide-header");
        }
        return this;
    }
}
