/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.INamedCharacter;

import kiss.Disposable;
import kiss.WiseRunnable;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.property.SmartProperty;
import viewtify.ui.helper.Actions;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.SelectableHelper;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.User;
import viewtify.ui.helper.UserActionHelper;

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

    private HBox menus;

    /**
     * 
     */
    public final UITabPane addMenu(INamedCharacter icon, WiseRunnable action) {
        if (icon != null && action != null) {
            if (menus == null) {
                menus = new HBox();

                StackPane back = (StackPane) ui.lookup(".tab-header-background");
                back.setStyle("-fx-background-color: green;");

                StackPane region = (StackPane) ui.lookup(".headers-region");
                region.setStyle("-fx-background-color: blue;");

                StackPane control = (StackPane) ui.lookup(".control-buttons-tab");
                control.setStyle("-fx-background-color: yellow;");
                control.setAlignment(Pos.CENTER_LEFT);

                StackPane headerArea = (StackPane) ui.lookup(".tab-header-area");
                headerArea.setStyle("-fx-background-color: red;");
                headerArea.getChildren().add(menus);
                headerArea.widthProperty().addListener((x, o, n) -> {
                    menus.setLayoutX(n.doubleValue() - 30 * menus.getChildren().size());

                    control.setPrefWidth(30 * menus.getChildren().size() + 30);

                    double width = n.doubleValue() - 30 * menus.getChildren().size();
                    width = 500;

                    back.setMaxWidth(width);
                    back.setPrefWidth(width);
                    back.setMinWidth(width);

                    // region.setMaxWidth(width);
                    // region.setPrefWidth(width);
                    // region.setMinWidth(width);

                    headerArea.setMaxWidth(width);
                    headerArea.setPrefWidth(width);
                    headerArea.setMinWidth(width);

                    System.out.println(back.getWidth() + "  " + region.getWidth() + "   " + headerArea.getWidth() + "       " + width);
                });

                ui.getStyleClass().add("additional-menu");
            }

            Glyph glyph = new Glyph("FontAwesome", icon);
            StyleHelper.of(glyph).style(style.icon);
            UserActionHelper.of(glyph).when(User.LeftClick, action);

            menus.getChildren().add(glyph);

            Platform.runLater(() -> {
                System.out.println(menus.getBoundsInParent());
            });
        }
        return this;
    }

    private interface style extends StyleDSL {
        Style icon = () -> {
            display.minHeight(30, px).minWidth(30, px);
            cursor.pointer();
            text.align.center().verticalAlign.middle();

            $.hover(() -> {
                font.color("-fx-focus-color");
            });
        };
    }
}