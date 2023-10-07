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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.ColorPickerSkin;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.DnDAssistant;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.ValueHelper;

public class UIColorPicker extends UserInterface<UIColorPicker, ColorPicker>
        implements ValueHelper<UIColorPicker, Color>, EditableHelper<UIColorPicker>, ContextMenuHelper<UIColorPicker> {

    /** The color data transfer. */
    private static final DnDAssistant<Color> ColorDnD = new DnDAssistant();

    /**
     * Builde {@link ComboBox}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIColorPicker(View view) {
        super(new CustomColorPicker(), view);

        ColorDnD.source(this).target(this);
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
    public Property<Color> valueProperty() {
        return ui.valueProperty();
    }

    private static class CustomColorPicker extends ColorPicker {

        final static LinearGradient RED_LINE = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(0.45, Color.WHITE), new Stop(0.46, Color.RED), new Stop(0.54, Color.RED), new Stop(0.55, Color.WHITE), new Stop(1, Color.WHITE));

        @Override
        protected Skin<?> createDefaultSkin() {

            final CustomColorPickerSkin skin = new CustomColorPickerSkin(this);
            final Label lbl = (Label) skin.getDisplayNode();
            final StackPane pane = (StackPane) lbl.getGraphic();
            final Rectangle rect = (Rectangle) pane.getChildren().get(0);

            // set initial color to red line if transparent is shown
            if (getValue().equals(Color.TRANSPARENT)) rect.setFill(RED_LINE);

            // set color to red line when transparent is selected
            rect.fillProperty().addListener((o, oldVal, newVal) -> {
                if (newVal != null && newVal.equals(Color.TRANSPARENT)) rect.setFill(RED_LINE);
            });

            return skin;
        }

        private class CustomColorPickerSkin extends ColorPickerSkin {

            private boolean initialized = false;

            public CustomColorPickerSkin(ColorPicker colorPicker) {
                super(colorPicker);
            }

            @Override
            protected Node getPopupContent() {
                final Region popupContent = (Region) super.getPopupContent();

                // make sure listeners and geometry are only created once
                if (!initialized) {
                    final VBox paletteBox = (VBox) popupContent.getChildrenUnmodifiable().get(0);
                    final StackPane hoverSquare = (StackPane) popupContent.getChildrenUnmodifiable().get(1); // ColorSquare
                    final Rectangle hoverRect = (Rectangle) hoverSquare.getChildren().get(0); // ColorSquare
                    final GridPane grid = (GridPane) paletteBox.getChildren().get(0); // ColorPalette
                    final StackPane colorSquare = (StackPane) grid.getChildren().get(grid.getChildren().size() - 1); // ColorSquare
                    final Rectangle colorRect = (Rectangle) colorSquare.getChildren().get(0);

                    // set fill color of original color rectangle to transparent
                    // (can't be set to red line gradient because ComboBoxBase<Color> tries to cast
                    // it to Color)
                    colorRect.setFill(Color.TRANSPARENT);
                    // put another rectangle with red line on top of it
                    colorSquare.getChildren().add(new Rectangle(colorRect.getWidth(), colorRect.getHeight(), RED_LINE));
                    // show red line gradient also in hover rectangle when the transparent color is
                    // selected
                    hoverRect.fillProperty().addListener((o, oldVal, newVal) -> {
                        if (newVal.equals(Color.TRANSPARENT)) hoverRect.setFill(RED_LINE);
                    });

                    initialized = true;
                }

                return popupContent;
            }
        }
    }
}