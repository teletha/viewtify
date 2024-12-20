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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import kiss.Disposable;
import kiss.I;
import kiss.Variable;
import stylist.Style;
import stylist.value.Color;
import viewtify.StyleManipulator;
import viewtify.Viewtify;
import viewtify.ui.UserInterfaceProvider;
import viewtify.util.FXUtils;

public interface LabelHelper<Self extends LabelHelper> extends PropertyAccessHelper, AssociativeHelper {

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
        return text(Variable.of(text));
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Variable} to set.
     * @return Chainable API.
     */
    default Self text(Variable text) {
        bind(property(Type.Text), text, x -> I.transform(x, String.class));
        return (Self) this;
    }

    /**
     * Set the specified {@link Node} as literal component.
     * 
     * @param items A literal component to set.
     * @return Chainable API.
     */
    default Self text(UserInterfaceProvider<? extends Node>... items) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);

        for (UserInterfaceProvider<? extends Node> item : items) {
            box.getChildren().add(item.ui().getStyleableNode());
        }
        return text(box);
    }

    /**
     * Set the specified {@link Node} as literal component.
     * 
     * @param items A literal component to set.
     * @return Chainable API.
     */
    default Self textV(UserInterfaceProvider<? extends Node>... items) {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);

        for (UserInterfaceProvider<? extends Node> item : items) {
            box.getChildren().add(item.ui());
        }
        return text(box);
    }

    /**
     * Set the specified {@link Node} as literal component.
     * 
     * @param text A literal component to set.
     * @return Chainable API.
     */
    default Self text(Node text) {
        dispose(Disposable.class);

        property(Type.Text).setValue(null);
        property(Type.Graphic).setValue(text);
        return (Self) this;
    }

    /**
     * Get the graphic of this component.
     * 
     * @return
     */
    default Node graphic() {
        return property(Type.Graphic).getValue();
    }

    /**
     * Get font color.
     * 
     * @return A current font color.
     */
    default Paint color() {
        try {
            return property(Type.TextFill).getValue();
        } catch (Exception e) {
            String value = StyleManipulator.get(this, "-fx-text-fill");
            if (value != null) {
                return javafx.scene.paint.Color.web(value);
            }
            return null;
        }
    }

    /**
     * Set font color.
     * 
     * @param color A color to set.
     * @return Chainable API.
     */
    default Self color(javafx.scene.paint.Color color) {
        try {
            property(Type.TextFill).setValue(color);
        } catch (Exception e) {
            StyleManipulator.set(this, "-fx-text-fill", FXUtils.color(color).toRGB());
        }
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
        try {
            return property(Type.Font).getValue();
        } catch (Exception e) {
            String css = StyleManipulator.get(this, "-fx-font");
            if (css != null) {
                int i = css.indexOf(' ');
                return Font.font(css.substring(i + 2, css.length() - 1), Double.parseDouble(css.substring(0, i)));
            }
            return null;
        }
    }

    /**
     * Set font.
     * 
     * @param font A font to set.
     * @return Chainable API.
     */
    default Self font(Font font) {
        try {
            property(Type.Font).setValue(font);
        } catch (Exception e) {
            StyleManipulator.set(this, "-fx-font", font.getSize() + " \"" + font.getFamily() + "\"");
        }
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
     * Set the specified icon using icon font.
     * 
     * @param icon An icon font.
     * @return Chainable API.
     */
    default Self icon(Ikon icon, Style... styles) {
        if (icon != null) {
            FontIcon font = new FontIcon(icon);
            if (styles == null || styles.length == 0) {
                font.iconColorProperty().bind(property(Type.TextFill));
            } else {
                StyleHelper.of(font).style(styles);
            }
            property(Type.Graphic).setValue(font);
        }
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