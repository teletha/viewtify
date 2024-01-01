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

import javafx.scene.layout.FlowPane;

import viewtify.ui.helper.AlignmentHelper;

public class UIFlowView<E> extends AbstractPane<E, FlowPane, UIFlowView<E>> implements AlignmentHelper<UIFlowView<E>> {

    /**
     * @param view
     */
    public UIFlowView(View view) {
        super(new FlowPane(), view);
    }
}