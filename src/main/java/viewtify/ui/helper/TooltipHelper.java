/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.util.Duration;

import kiss.I;
import kiss.Signal;
import transcript.Lang;
import transcript.Transcript;
import viewtify.Viewtify;

public interface TooltipHelper<Self extends TooltipHelper, W extends Node> extends StyleHelper<Self, W> {

    /**
     * Set text.
     * 
     * @param text A text to set.
     */
    default Self tooltip(Object text) {
        Tooltip tooltip = new Tooltip(Objects.toString(text));
        tooltip.setShowDelay(Duration.millis(100));
        tooltip.setShowDuration(Duration.INDEFINITE);
        tooltip.setFont(Font.font(12));

        Tooltip.install(ui(), tooltip);
        return (Self) this;
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Supplier} to set.
     */
    default Self tooltip(Supplier text) {
        return tooltip(lang -> I.signal(text).map(String::valueOf));
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Supplier} to set.
     */
    default Self tooltip(Transcript text) {
        return tooltip(text::as);
    }

    /**
     * Set text.
     * 
     * @param text A text {@link Supplier} to set.
     */
    private Self tooltip(Function<Lang, Signal<String>> text) {
        Lang.observe().switchMap(I.wiseF(text)).on(Viewtify.UIThread).to(translated -> {
            tooltip(translated);
        });
        return (Self) this;
    }

}
