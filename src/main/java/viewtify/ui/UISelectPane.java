/*
 * Copyright (C) 2022 Nameless Production Committee
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

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import kiss.I;
import viewtify.ui.anime.SwapAnime;
import viewtify.ui.helper.User;

public class UISelectPane extends UserInterface<UISelectPane, HBox> {

    /** The label manager. */
    private final List<UILabel> labels = new ArrayList();

    /** The view manager. */
    private final List<UserInterfaceProvider> providers = new ArrayList();

    /** The left container. */
    private final VBox left = new VBox();

    /** The right container. */
    private final UIPane right = new UIPane(null);

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
        for (int i = 0; i < labels.size(); i++) {
            if (i == index) {
                labels.get(i).style("selected");
            } else {
                labels.get(i).unstyle("selected");
            }
        }
        right.content(providers.get(index), SwapAnime.FadeOutIn);

        return this;
    }
}
