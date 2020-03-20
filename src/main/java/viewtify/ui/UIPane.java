/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import javafx.scene.layout.Pane;

public class UIPane extends UserInterface<UIPane, Pane> {

    /**
     * @param view
     */
    public UIPane(View view) {
        super(new Pane(), view);
    }

    /**
     * @param pane
     * @param view
     */
    public UIPane(Pane pane, View view) {
        super(pane, view);
    }
}
