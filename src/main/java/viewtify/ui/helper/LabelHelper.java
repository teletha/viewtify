/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import transcript.Lang;
import transcript.Transcript;
import viewtify.Viewtify;
import viewtify.ui.UILabel;
import viewtify.ui.UserInterfaceProvider;

public interface LabelHelper<Self extends LabelHelper> extends PropertyHelper {

    /**
     * Get text.
     * 
     * @param text
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
        property(Type.Text).setValue(Objects.toString(text));
        return (Self) this;
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Supplier} to set.
     * @return Chainable API.
     */
    default Self text(Supplier text) {
        return text(lang -> I.signal(text).map(String::valueOf));
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Supplier} to set.
     * @return Chainable API.
     */
    default Self text(Transcript text) {
        return text(text::as);
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Supplier} to set.
     * @return Chainable API.
     */
    private Self text(Function<Lang, Signal<String>> text) {
        Lang.observe().switchMap(I.wiseF(text)).on(Viewtify.UIThread).to(translated -> {
            text(translated);
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
        text.observeNow().on(Viewtify.UIThread).to((Consumer) this::text);
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
}
