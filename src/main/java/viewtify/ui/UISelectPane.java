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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

import javafx.beans.property.Property;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kiss.I;
import viewtify.property.SmartProperty;
import viewtify.ui.anime.SwapAnime;
import viewtify.ui.helper.User;
import viewtify.ui.helper.ValueHelper;

public class UISelectPane extends UserInterface<UISelectPane, HBox> implements ValueHelper<UISelectPane, UserInterfaceProvider> {

    /** The label manager. */
    private final List<UILabel> labels = new ArrayList();

    /** The view manager. */
    private final List<UserInterfaceProvider> providers = new ArrayList();

    /** The left container. */
    public final VBox left = new VBox();

    /** The right container. */
    public final UIScrollPane right = new UIScrollPane(null);

    /** The selection. */
    private final SmartProperty<UserInterfaceProvider> selection = new SmartProperty();

    /** The selectable confirmation. */
    private IntPredicate selectable;

    /**
     * 
     */
    UISelectPane(View view) {
        super(new HBox(), view);

        left.getStyleClass().add("select-buttons");
        right.ui.getStyleClass().add("select-content");
        ui.getStyleClass().add("select-pane");
        ui.getChildren().addAll(left, right.ui());
    }

    /**
     * Add new tab.
     * 
     * @param labelBuilder
     * @param view
     * @return
     */
    public UISelectPane add(Consumer<UILabel> labelBuilder, Class<? extends View> view) {
        return add(labelBuilder, I.make(view));
    }

    /**
     * Add new tab.
     * 
     * @param labelBuilder
     * @param provider
     * @return
     */
    public UISelectPane add(Consumer<UILabel> labelBuilder, UserInterfaceProvider provider) {
        if (labelBuilder != null && provider != null) {
            UILabel label = new UILabel(this.view);
            label.style("select-button").when(User.LeftClick, () -> selectAt(labels.indexOf(label)));
            labelBuilder.accept(label);
            labels.add(label);
            providers.add(provider);

            left.getChildren().add(label.ui());
        }
        return this;
    }

    /**
     * Select the specified tab.
     * 
     * @param index
     * @return
     */
    public UISelectPane selectAt(int index) {
        if (selectable != null && !selectable.test(index)) {
            return this;
        }

        for (int i = 0; i < labels.size(); i++) {
            if (i == index) {
                labels.get(i).style("selected");
            } else {
                labels.get(i).unstyle("selected");
            }
        }

        UserInterfaceProvider ui = providers.get(index);
        right.content(ui, SwapAnime.FadeOutIn);
        selection.set(ui);

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
    public Property<UserInterfaceProvider> valueProperty() {
        return selection;
    }
}