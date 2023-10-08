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

import static javafx.scene.paint.Color.TRANSPARENT;
import static javafx.scene.paint.Color.web;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
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
import viewtify.Viewtify;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.DnDAssistant;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.ValueHelper;
import viewtify.util.FXUtils;

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

    public stylist.value.Color getCSSColor() {
        return FXUtils.color(ui.getValue());
    }

    /**
     * Set the custom color palette.
     * 
     * @param colors
     * @return
     */
    public UIColorPicker customPalette(List<Color> colors) {
        ((CustomColorPicker) ui).palette = colors;

        return this;
    }

    /** The transparent effect. */
    private static final LinearGradient LINE = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(0.45, Color.WHITE), new Stop(0.46, Color.DARKRED), new Stop(0.54, Color.DARKRED), new Stop(0.55, Color.WHITE), new Stop(1, Color.WHITE));

    /**
     * Custom color picker.
     */
    private static class CustomColorPicker extends ColorPicker {

        /** The pretty palette. */
        private List<Color> palette = List
                .of(TRANSPARENT, web("#0079bf"), web("#70b500"), web("#ff9f1a"), web("#eb5a46"), web("#f2d600"), web("#c377e0"), web("#ff78cb"), web("#f9ebdf"), web("#c4c9cc"), web("#84754e"), web("#47494a"));

        /**
         * {@inheritDoc}
         */
        @Override
        protected Skin<?> createDefaultSkin() {
            CustomColorPickerSkin skin = new CustomColorPickerSkin(this);
            Label label = (Label) skin.getDisplayNode();
            StackPane pane = (StackPane) label.getGraphic();
            Rectangle colorBox = (Rectangle) pane.getChildren().get(0);

            Viewtify.observing(colorBox.fillProperty()).take(Color.TRANSPARENT::equals).to(() -> colorBox.setFill(LINE));

            return skin;
        }
    }

    /**
     * Custom color picker.
     */
    private static class CustomColorPickerSkin extends ColorPickerSkin {

        private final CustomColorPicker picker;

        private boolean initialized = false;

        /**
         * @param picker
         */
        private CustomColorPickerSkin(CustomColorPicker picker) {
            super(picker);
            this.picker = picker;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Node getPopupContent() {
            Region popupContent = (Region) super.getPopupContent();

            // make sure listeners and geometry are only created once
            if (!initialized) {
                initialized = true;

                VBox root = (VBox) popupContent.getChildrenUnmodifiable().get(0);
                StackPane hoverSquare = (StackPane) popupContent.getChildrenUnmodifiable().get(1);
                Rectangle hoverColorBox = (Rectangle) hoverSquare.getChildren().get(0);
                Viewtify.observe(hoverColorBox.fillProperty()).take(Color.TRANSPARENT::equals).to(() -> hoverColorBox.setFill(LINE));

                GridPane palette = (GridPane) root.getChildren().get(0);
                ObservableList<Node> children = palette.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    StackPane stack = (StackPane) children.get(i);
                    Rectangle box = (Rectangle) stack.getChildren().get(0);

                    if (i < picker.palette.size()) {
                        box.setFill(picker.palette.get(i));

                        if (picker.palette.get(i) == Color.TRANSPARENT) {
                            stack.getChildren().add(new Rectangle(box.getWidth(), box.getHeight(), LINE));
                        }
                        Tooltip.install(stack, new Tooltip(toRGBCode(picker.palette.get(i))));
                    } else {
                        box.setManaged(false);
                        box.setVisible(false);
                    }
                }
            }

            return popupContent;
        }
    }

    private static String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }
}