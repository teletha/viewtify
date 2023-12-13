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

import javafx.beans.property.Property;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.DisableHelper;
import viewtify.ui.helper.TooltipHelper;
import viewtify.ui.helper.UserActionHelper;
import viewtify.ui.helper.ValueHelper;
import viewtify.ui.helper.VisibleHelper;

public class UIImage extends UserInterface<UIImage, ImageView>
        implements ContextMenuHelper<UIImage>, DisableHelper<UIImage>, TooltipHelper<UIImage, ImageView>, UserActionHelper<UIImage>,
        VisibleHelper<UIImage>, ValueHelper<UIImage, Image> {

    /**
     * @param view
     */
    public UIImage(View view) {
        super(new ImageView(), view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Image> valueProperty() {
        return ui.imageProperty();
    }

    /**
     * Set image by path.
     * 
     * @param path
     * @return
     */
    public UIImage value(String path) {
        return value(new Image(path));
    }
}