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

import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import kiss.Disposable;
import kiss.I;
import transcript.Transcript;
import viewtify.ui.helper.Actions;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.SelectableHelper;
import viewtify.ui.helper.User;

public class UITabPane extends UserInterface<UITabPane, SplitPane>
        implements ContextMenuHelper<UITabPane>, SelectableHelper<UITabPane, UITab>, CollectableHelper<UITabPane, UITab> {

    /** The model disposer. */
    private Disposable disposable = Disposable.empty();

    private final TabPane main = new TabPane();

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UITabPane(View view) {
        super(new SplitPane(), view);
        ui.setOrientation(Orientation.VERTICAL);
        ui.getItems().add(main);

        // FUNCTIONALITY : wheel scroll will change selection.
        when(User.Scroll).take(Actions.inside(() -> main.lookup(".tab-header-background"))).to(Actions.traverse(main.getSelectionModel()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Property<ObservableList<UITab>> itemsProperty() {
        return new SimpleObjectProperty(main.getTabs());
    }

    /**
     * Set initial selected index.
     * 
     * @param initialSelectedIndex
     * @return
     */
    public final UITabPane initial(int initialSelectedIndex) {
        restore(main.getSelectionModel().selectedIndexProperty(), v -> main.getSelectionModel().select(v.intValue()), initialSelectedIndex);
        return this;
    }

    /**
     * Load tab with the specified view.
     * 
     * @param label Specify the label of the tab. This is used as a temporary label until the
     *            contents of the tab are read, as tab loading is delayed until needed actually.
     * @param loadingViewType A view type to load.
     * @return
     */
    public final <V extends View> UITabPane load(String label, Class<V> loadingViewType) {
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
    public <V extends View> UITabPane load(String label, Function<UITab, View> viewBuilder) {
        UITab tab = new UITab(view, viewBuilder);
        tab.text(label).context(c -> {
            c.menu().text(Transcript.en("Tile")).when(User.Action, () -> tile(tab));
            c.menu().text(Transcript.en("Detach")).when(User.Action, () -> detach(tab));
        });

        main.getTabs().add(tab);
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
            main.setTabClosingPolicy(policy);
        }
        return this;
    }

    /**
     * detach The closing policy for the tabs.
     * 
     * @param policy The closing policy for the tabs.
     * @return Chainable API.
     */
    public final UITabPane policy(TabDragPolicy policy) {
        if (policy != null) {
            main.setTabDragPolicy(policy);
        }
        return this;
    }

    /**
     * Detach the specified tab.
     * 
     * @param tab
     */
    private void detach(UITab tab) {
        int originalIndex = main.getTabs().indexOf(tab);

        Pane content = (Pane) tab.getContent();
        tab.setContent(null);

        Scene scene = new Scene(content, content.getPrefWidth(), content.getPrefHeight());
        scene.getStylesheets().addAll(main.getScene().getStylesheets());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.getIcons().addAll(((Stage) main.getScene().getWindow()).getIcons());
        stage.setTitle(tab.getText());
        stage.setOnShown(e -> main.getTabs().remove(tab));
        stage.setOnCloseRequest(e -> {
            stage.close();
            tab.setContent(content);
            main.getTabs().add(originalIndex, tab);
        });
        stage.show();
    }

    /**
     * Tile the specified tab.
     * 
     * @param tab
     */
    private void tile(UITab tab) {
        int originalIndex = main.getTabs().indexOf(tab);

        main.getTabs().remove(tab);
        Node content = tab.getContent();

        ui.getItems().add(content);
        allocateEvenWidth();
    }

    /**
     * Move tab.
     * 
     * @param tab
     * @param from
     * @param to
     */
    private void move(Tab tab, TabPane from, TabPane to) {
        ObservableList<Tab> froms = from.getTabs();

        if (froms.remove(tab)) {
            to.getTabs().add(tab);

            if (froms.isEmpty()) {
                remove(from);
            }
        }
    }

    /**
     * Remove {@link TabPane}.
     * 
     * @param pane
     */
    private void remove(TabPane pane) {
        if (pane != null && ui.getItems().remove(pane)) {
            allocateEvenWidth();
        }
    }

    /**
     * Compute all positions for equal spacing.
     * 
     * @return
     */
    private void allocateEvenWidth() {
        int itemSize = ui.getItems().size();
        double equalSpacing = 1d / itemSize;
        double[] positions = new double[itemSize - 1];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = equalSpacing * (i + 1);
        }
        ui.setDividerPositions(positions);
    }
}
