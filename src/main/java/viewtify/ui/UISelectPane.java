/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kiss.I;
import kiss.WiseSupplier;
import viewtify.Viewtify;
import viewtify.ui.anime.SwapAnime;
import viewtify.ui.helper.SelectableHelper;
import viewtify.ui.helper.User;

public class UISelectPane extends UserInterface<UISelectPane, HBox> implements SelectableHelper<UISelectPane, View> {

    /** The label manager. */
    private final List<UILabel> labels = new ArrayList();

    /** The menu container. */
    public final VBox menus = new VBox();

    /** The contents container. */
    public final UIScrollPane contents = new UIScrollPane(null);

    /** The selectable confirmation. */
    private IntPredicate selectable;

    /** The actual contents set. */
    private final List<Builder> builders = new ArrayList();

    /** The selection model. */
    private final SingleSelectionModel<View> model = new SingleSelectionModel() {

        /**
         * {@inheritDoc}
         */
        @Override
        protected View getModelItem(int index) {
            return builders.get(index).view();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected int getItemCount() {
            return builders.size();
        }
    };

    /**
     * Hide constructor.
     * 
     * @param view
     */
    UISelectPane(View view) {
        super(new HBox(), view);

        menus.getStyleClass().add("select-buttons");
        contents.ui.getStyleClass().add("select-content");
        ui.getStyleClass().add("select-pane");
        ui.getChildren().addAll(menus, contents.ui());

        Viewtify.observe(model.selectedIndexProperty()).to(index -> {
            if (selectable != null && !selectable.test(index)) {
                return;
            }

            for (int i = 0; i < labels.size(); i++) {
                if (i == index) {
                    labels.get(i).style("selected");
                } else {
                    labels.get(i).unstyle("selected");
                }
            }

            View ui = builders.get(index).view();
            contents.content(ui, SwapAnime.FadeOutIn);
        });
    }

    /**
     * Add new tab.
     * 
     * @param labelBuilder
     * @param view
     * @return
     */
    public UISelectPane add(Consumer<UILabel> labelBuilder, Class<? extends View> view) {
        return add(labelBuilder, () -> I.make(view));
    }

    /**
     * Add new tab.
     * 
     * @param labelBuilder
     * @param provider
     * @return
     */
    public UISelectPane add(Consumer<UILabel> labelBuilder, WiseSupplier<View> provider) {
        if (labelBuilder != null && provider != null) {
            UILabel label = new UILabel(this.view);
            label.style("select-button").when(User.LeftClick, () -> selectAt(labels.indexOf(label)));
            labelBuilder.accept(label);
            labels.add(label);
            builders.add(new Builder(provider));

            menus.getChildren().add(label.ui());
        }
        return this;
    }

    /**
     * Configure the selectable tab.
     * 
     * @param selectable
     * @return
     */
    public UISelectPane selectable(IntPredicate selectable) {
        this.selectable = selectable;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectionModel<View> selectionModelProperty() {
        return model;
    }

    private class Builder {

        /** The view builder. */
        private final WiseSupplier<View> builder;

        /** Contents */
        private View view;

        private Builder(WiseSupplier<View> builder) {
            this.builder = builder;
        }

        private View view() {
            if (view == null || view.isDisposed()) {
                view = builder.get();
            }
            return view;
        }
    }
}
