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

import javafx.scene.layout.FlowPane;

public class UIFlowView<E> extends AbstractPane<E, FlowPane, UIFlowView<E>> {

    /**
     * @param view
     */
    public UIFlowView(View view) {
        super(new FlowPane(), view);
    }
}
