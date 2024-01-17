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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.scene.control.TextArea;
import viewtify.ui.helper.BlockHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.PlaceholderHelper;
import viewtify.ui.helper.ValueHelper;
import viewtify.util.MonkeyPatch;

public class UITextArea extends UserInterface<UITextArea, TextArea>
        implements ValueHelper<UITextArea, String>, BlockHelper<UITextArea>, ContextMenuHelper<UITextArea>, EditableHelper<UITextArea>,
        PlaceholderHelper<UITextArea> {

    /**
     * Enchanced view.
     */
    public UITextArea(View view) {
        super(new TextArea(), view);

        MonkeyPatch.fix(ui);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty edit() {
        return ui.editableProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<String> valueProperty() {
        return ui.textProperty();
    }

    /**
     * Configure the test wrap model.
     * 
     * @param wrap
     * @return
     */
    public UITextArea autoWrap(boolean wrap) {
        ui.setWrapText(wrap);
        return this;
    }
}