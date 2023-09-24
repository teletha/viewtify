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
import javafx.scene.control.ToggleButton;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.ValueHelper;

public class UIToggleButton extends UserInterface<UIToggleButton, ToggleButton>
        implements LabelHelper<UIToggleButton>, ContextMenuHelper<UIToggleButton>, ValueHelper<UIToggleButton, Boolean> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIToggleButton(View view) {
        super(new ToggleButton(), view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Boolean> valueProperty() {
        return ui.selectedProperty();
    }

    /**
     * Select this button.
     * 
     * @return
     */
    public UIToggleButton select() {
        ui.setSelected(true);
        return this;
    }

    /**
     * Unselect this button.
     * 
     * @return
     */
    public UIToggleButton unselect() {
        ui.setSelected(false);
        return this;
    }
}