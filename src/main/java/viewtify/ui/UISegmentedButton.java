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

import org.controlsfx.control.SegmentedButton;

public class UISegmentedButton extends UserInterface<UISegmentedButton, SegmentedButton> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UISegmentedButton(View view) {
        super(new SegmentedButton(), view);
    }

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UISegmentedButton(SegmentedButton button, View view) {
        super(button, view);
    }
}