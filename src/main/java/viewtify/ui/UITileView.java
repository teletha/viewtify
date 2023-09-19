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

import javafx.scene.layout.TilePane;

import viewtify.ui.helper.AlignmentHelper;

public class UITileView<E> extends AbstractPane<E, TilePane, UITileView<E>> implements AlignmentHelper<UITileView<E>> {

    /**
     * @param view
     */
    public UITileView(View view) {
        super(new TilePane(), view);

        ui.setPrefColumns(4);
    }
}