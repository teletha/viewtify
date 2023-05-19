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

import javafx.scene.layout.StackPane;
import kiss.I;
import viewtify.ui.helper.ContainerHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.DisableHelper;
import viewtify.ui.helper.UserActionHelper;

public class UIStackPane extends UserInterface<UIStackPane, StackPane>
        implements ContainerHelper<UIStackPane, StackPane>, ContextMenuHelper<UIStackPane>, DisableHelper<UIStackPane>,
        UserActionHelper<UIStackPane> {

    /**
     * @param view
     */
    public UIStackPane(View view) {
        super(new StackPane(), view);
    }

    /**
     * Add new view.
     * 
     * @param <V>
     * @param view
     * @return
     */
    public <V extends View> UIStackPane add(Class<V> view) {
        return add(I.make(view));
    }

    /**
     * Add new view.
     * 
     * @param <V>
     * @param view
     * @return
     */
    public <V extends View> UIStackPane add(V view) {
        if (view != null) {
            ui.getChildren().add(view.ui());
        }
        return this;
    }

    /**
     * Remove the specified view.
     * 
     * @param <V>
     * @param view
     * @return
     */
    public <V extends View> UIStackPane remove(V view) {
        if (view != null) {
            ui.getChildren().remove(view.ui());
        }
        return this;
    }
}