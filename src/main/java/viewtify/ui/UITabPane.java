/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import kiss.WiseConsumer;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.property.SmartProperty;
import viewtify.ui.helper.Actions;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.SelectableHelper;
import viewtify.ui.helper.User;

public class UITabPane extends UserInterface<UITabPane, TabPane>
        implements ContextMenuHelper<UITabPane>, SelectableHelper<UITabPane, UITab>, CollectableHelper<UITabPane, UITab> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UITabPane(View view) {
        super(new TabPane(), view);

        // FUNCTIONALITY : wheel scroll will change selection.
        when(User.Scroll).take(Actions.inside(() -> ui.lookup(".tab-header-background"))).to(Actions.traverse(ui.getSelectionModel()));

        // dispose view automatically
        ui.getTabs().addListener((ListChangeListener<Tab>) change -> {
            while (change.next()) {
                for (Tab removed : change.getRemoved()) {
                    if (removed instanceof UITab tab && tab.supportAutomaticDispose()) {
                        tab.dispose();
                    }
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<UITab>> itemsProperty() {
        return new SmartProperty(ui.getTabs());
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

    /** The menu area. */
    private HBox menus;

    /**
     * @param builder
     */
    public final UITabPane registerIcon(WiseConsumer<UILabel> builder) {
        if (builder != null) {
            if (menus == null) {
                menus = new HBox();

                StackPane headerArea = (StackPane) ui.lookup(".tab-header-area");
                Viewtify.observing(headerArea.widthProperty()).to(width -> {
                    int size = menus.getChildren().size();

                    StackPane control = (StackPane) headerArea.lookup(".control-buttons-tab");
                    control.setPrefWidth(style.IconSize * (size + 1));
                    control.setPadding(new Insets(0, style.IconSize * size, 0, 0));

                    Pane button = (Pane) control.lookup(".tab-down-button");
                    button.setTranslateX(-style.IconSize * size);

                    menus.setLayoutX(width.doubleValue() - style.IconSize * Math.max(1, size));
                });
                headerArea.getChildren().add(menus);
            }

            UILabel icon = new UILabel(null).style(style.icon);
            builder.accept(icon);
            menus.getChildren().add(icon.ui());
        }
        return this;
    }

    private interface style extends StyleDSL {
        int IconSize = 32;

        Style icon = () -> {
            display.minHeight(IconSize, px).minWidth(IconSize, px);
            cursor.pointer();
            text.align.center().verticalAlign.middle();

            $.hover(() -> {
                background.color("-fx-accent");
            });
        };
    }
}