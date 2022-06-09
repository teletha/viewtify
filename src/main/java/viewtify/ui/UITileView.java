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

import javafx.scene.layout.TilePane;

public class UITileView<E> extends AbstractPane<E, TilePane, UITileView<E>> {

    /**
     * @param view
     */
    public UITileView(View view) {
        super(new TilePane(), view);
    }
}
