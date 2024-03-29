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

import java.io.InputStream;
import java.util.Objects;

import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.INamedCharacter;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import kiss.Disposable;
import kiss.I;
import kiss.Variable;
import stylist.Style;
import stylist.value.Color;
import viewtify.Viewtify;
import viewtify.ui.UILabel;
import viewtify.ui.UserInterfaceProvider;
import viewtify.util.FXUtils;

public interface LabelHelper<Self extends LabelHelper> extends PropertyAccessHelper {

    /**
     * Get text.
     * 
     * @return A current text.
     */
    default String text() {
        return property(Type.Text).getValue();
    }

    /**
     * Set text.
     * 
     * @param text A text to set.
     * @return Chainable API.
     */
    default Self text(Object text) {
        Viewtify.inUI(() -> {
            property(Type.Text).setValue(Objects.toString(text));
        });
        return (Self) this;
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Variable} to set.
     * @return Chainable API.
     */
    default Self text(Variable text) {
        Disposable disposable = text.observing().on(Viewtify.UIThread).to(v -> {
            property(Type.Text).setValue(I.transform(v, String.class));
        });

        FXUtils.replaceAssociation(ui(), Disposable.class, disposable);

        return (Self) this;
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Variable} to set.
     * @return Chainable API.
     */
    default Self text(Property text) {
        property(Type.Text).bindBidirectional(text);
        return (Self) this;
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Variable} to set.
     * @return Chainable API.
     */
    default Self text(INamedCharacter text) {
        return text(text, (Style[]) null);
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Variable} to set.
     * @return Chainable API.
     */
    default Self text(INamedCharacter text, Style... iconStyle) {
        if (text != null) {
            Glyph glyph = new Glyph("FontAwesome", text);
            StyleHelper.of(glyph).style(iconStyle);
            text(glyph);
        }
        return (Self) this;
    }

    /**
     * Set honrizontal styled text.
     * 
     * @param texts The text list.
     * @return Chainable API.
     */
    default Self text(UILabel... texts) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);

        for (int i = 0; i < texts.length; i++) {
            box.getChildren().add(texts[i].ui);
        }
        return text(box);
    }

    /**
     * Set vertical styled text.
     * 
     * @param texts The text list.
     * @return Chainable API.
     */
    default Self textV(UILabel... texts) {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);

        for (int i = 0; i < texts.length; i++) {
            box.getChildren().add(texts[i].ui);
        }
        return text(box);
    }

    /**
     * Set the specifie {@link Node} as literal component.
     * 
     * @param text A literal component to set.
     * @return Chainable API.
     */
    default Self text(UserInterfaceProvider text) {
        return text(text.ui().getStyleableNode());
    }

    /**
     * Set the specifie {@link Node} as literal component.
     * 
     * @param text A literal component to set.
     * @return Chainable API.
     */
    default Self text(Node text) {
        property(Type.Text).setValue(null);
        property(Type.Graphic).setValue(text);
        return (Self) this;
    }

    /**
     * Get font color.
     * 
     * @return A current font color.
     */
    default Paint color() {
        return property(Type.TextFill).getValue();
    }

    /**
     * Set font color.
     * 
     * @param color A color to set.
     * @return Chainable API.
     */
    default <P extends Paint> Self color(P color) {
        property(Type.TextFill).setValue(color);
        return (Self) this;
    }

    /**
     * Set font color.
     * 
     * @param color A color to set.
     * @return Chainable API.
     */
    default Self color(Color color) {
        return color(FXUtils.color(color));
    }

    /**
     * Set font color.
     * 
     * @param color A color to set.
     * @return Chainable API.
     */
    default Self color(Variable<? extends Paint> color) {
        property(Type.TextFill).bind(Viewtify.property(color));
        return (Self) this;
    }

    /**
     * Get font.
     * 
     * @return A current font.
     */
    default Font font() {
        return property(Type.Font).getValue();
    }

    /**
     * Set font.
     * 
     * @param font A font to set.
     * @return Chainable API.
     */
    default Self font(Font font) {
        property(Type.Font).setValue(font);
        return (Self) this;
    }

    /**
     * Set font by name.
     * 
     * @param name A font name to set.
     * @return Chainable API.
     */
    default Self font(String name) {
        return font(Font.font(name));
    }

    /**
     * Set font by size.
     * 
     * @param size A font size to set.
     * @return Chainable API.
     */
    default Self font(double size) {
        return font(Font.font(size));
    }

    /**
     * Set font by weight.
     * 
     * @param weight A font weight to set.
     * @return Chainable API.
     */
    default Self font(FontWeight weight) {
        return font(Font.font(null, weight, -1));
    }

    /**
     * Set font by size and weight.
     * 
     * @param size A font size to set.
     * @param weight A font weight to set.
     * @return Chainable API.
     */
    default Self font(double size, FontWeight weight) {
        return font(Font.font(null, weight, size));
    }

    /**
     * Set font by name, size and weight.
     * 
     * @param name A font name to set.
     * @param size A font size to set.
     * @param weight A font weight to set.
     * @return Chainable API.
     */
    default Self font(String name, double size, FontWeight weight) {
        return font(Font.font(name, weight, size));
    }

    /**
     * Set icon.
     * 
     * @param icon
     * @param styles
     * @return
     */
    default Self icon(INamedCharacter icon, Style... styles) {
        Glyph glyph = new Glyph("FontAwesome", icon);
        glyph.setPadding(new Insets(0, 2, 0, 2));

        ObservableList<String> classes = glyph.getStyleClass();
        classes.clear();
        for (Style style : styles) {
            classes.addAll(style.className());
        }

        property(Type.Graphic).setValue(glyph);
        return (Self) this;
    }

    /**
     * Set icon.
     * 
     * @param iconPath
     * @return
     */
    default Self icon(String iconPath) {
        return icon(iconPath, 0, 0);
    }

    /**
     * Set icon.
     * 
     * @param iconPath
     * @return
     */
    default Self icon(String iconPath, int size) {
        return icon(iconPath, size, size);
    }

    /**
     * Set icon.
     * 
     * @param iconPath
     * @return
     */
    default Self icon(String iconPath, int width, int height) {
        InputStream input = ClassLoader.getSystemResourceAsStream(iconPath);
        if (input != null) {
            Image image = new Image(input);
            ImageView view = new ImageView(image);
            if (0 < width) view.setFitWidth(width);
            if (0 < height) view.setFitHeight(height);

            property(Type.Graphic).setValue(view);
        }
        return (Self) this;
    }
}