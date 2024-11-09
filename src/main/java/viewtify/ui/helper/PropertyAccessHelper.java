/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import org.controlsfx.control.IndexedCheckModel;

import kiss.I;

public interface PropertyAccessHelper {

    /**
     * Return the JavaFX's UI component.
     * 
     * @return A UI component
     */
    Object ui();

    /**
     * Retrieve the property by {@link Type}.
     * 
     * @param type A property type.
     * @return A property.
     */
    default <T> Property<T> property(Type<T> type) {
        try {
            Object ui = ui();
            return (Property<T>) ui.getClass().getMethod(type.name).invoke(ui);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * 
     */
    class Type<T> {
        /** The defined property type. */
        public static final Type<Pos> Alignment = new Type("alignment");

        /** The defined property type. */
        public static final Type<IndexedCheckModel> CheckModel = new Type("checkModel");

        /** The defined property type. */
        public static final Type<ContentDisplay> ContentDisplay = new Type("contentDisplay");

        /** The defined property type. */
        public static final Type<ContextMenu> ContextMenu = new Type("contextMenu");

        /** The defined property type. */
        public static final Type<Cursor> Cursor = new Type("cursor");

        /** The defined property type. */
        public static final Type<Boolean> Disable = new Type("disable");

        /** The defined property type. */
        public static final Type<javafx.scene.effect.Effect> Effect = new Type("effect");

        /** The defined property type. */
        public static final Type<String> EllipsisString = new Type("ellipsisString");

        /** The defined property type. */
        public static final Type<Font> Font = new Type("font");

        /** The defined property type. */
        public static final Type<Node> Graphic = new Type("graphic");

        /** The defined property type. */
        public static final Type<Double> GraphicTextGap = new Type("graphicTextGap");

        /** The defined property type. */
        public static final Type<Double> Height = new Type("prefHeight");

        /** The defined property type. */
        public static final Type<Double> HGap = new Type("hgap");

        /** The defined property type. */
        public static final Type<String> Id = new Type("id");

        /** The defined property type. */
        public static final Type<ObservableList> Items = new Type("items");

        /** The defined property type. */
        public static final Type<Double> LineSpacing = new Type("lineSpacing");

        /** The defined property type. */
        public static final Type<Boolean> Managed = new Type("managed");

        /** The defined property type. */
        public static final Type<Double> MaxHeight = new Type("maxHeight");

        /** The defined property type. */
        public static final Type<Double> MaxWidth = new Type("maxWidth");

        /** The defined property type. */
        public static final Type<Double> MinHeight = new Type("minHeight");

        /** The defined property type. */
        public static final Type<Double> MinWidth = new Type("minWidth");

        /** The defined property type. */
        public static final Type<Boolean> MnemonicParsing = new Type("mnemonicParsing");

        /** The defined property type. */
        public static final Type<Double> Opacity = new Type("opacity");

        /** The defined property type. */
        public static final Type Orientation = new Type("orientation");

        /** The defined property type. */
        public static final Type<Node> Placeholder = new Type("placeholder");

        /** The defined property type. */
        public static final Type<String> PromptText = new Type("promptText");

        /** The defined property type. */
        public static final Type<SelectionModel> SelectionModel = new Type("selectionModel");

        /** The defined property type. */
        public static final Type<String> Style = new Type("style");

        /** The defined property type. */
        public static final Type<String> Text = new Type("text");

        /** The defined property type. */
        public static final Type<TextAlignment> TextAlignment = new Type("textAlignment");

        /** The defined property type. */
        public static final Type<Paint> TextFill = new Type("textFill");

        /** The defined property type. */
        public static final Type<OverrunStyle> TextOverrun = new Type("textOverrun");

        /** The defined property type. */
        public static final Type<Tooltip> Tooltip = new Type("tooltip");

        /** The defined property type. */
        public static final Type<Boolean> Underline = new Type("underline");

        /** The defined property type. */
        public static final Type<Double> VGap = new Type("vgap");

        /** The defined property type. */
        public static final Type<Boolean> Visible = new Type("visible");

        /** The defined property type. */
        public static final Type<Double> Width = new Type("prefWidth");

        /** The defined property type. */
        public static final Type<Boolean> WrapText = new Type("wrapText");

        /** The property id counter. */
        private static int counter;

        /** The property name. */
        public final String name;

        /** The property id. */
        public final int id;

        /**
         * 
         */
        private Type(String name) {
            this.name = name + "Property";
            this.id = counter++;
        }
    }
}