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

import javafx.scene.control.Labeled;
import kiss.I;
import kiss.Signal;
import kiss.Variable;
import transcript.Lang;
import transcript.Transcript;
import viewtify.Viewtify;

/**
 * @version 2018/08/27 21:27:30
 */
public interface LabelHelper<Self extends LabelHelper, W extends Labeled> extends StyleHelper<Self, W> {

    /**
     * Get text.
     * 
     * @param text
     */
    default String text() {
        return ui().getText();
    }

    /**
     * Set text.
     * 
     * @param text A text to set.
     */
    default Self text(Object text) {
        ui().setText(Objects.toString(text));
        return (Self) this;
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Supplier} to set.
     */
    default Self text(Supplier<?> text) {
        return text(lang -> I.signal(text).map(String::valueOf));
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Supplier} to set.
     */
    default Self text(Transcript text) {
        return text(text::as);
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Supplier} to set.
     */
    private Self text(Function<Lang, Signal<String>> text) {
        Lang.observe().switchMap(I.wiseF(text)).on(Viewtify.UIThread).to(translated -> {
            text(translated);
        });
        return (Self) this;
    }

    default Self text(Variable text) {
        text.observeNow().on(Viewtify.UIThread).to((Consumer) this::text);
        return (Self) this;
    }
}
