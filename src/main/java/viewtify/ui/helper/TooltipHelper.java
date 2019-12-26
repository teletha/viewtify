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

import org.controlsfx.control.PopOver;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Duration;
import kiss.I;
import kiss.Signal;
import transcript.Lang;
import transcript.Transcript;
import viewtify.Viewtify;
import viewtify.ui.View;

public interface TooltipHelper<Self extends TooltipHelper, W extends Node> extends StyleHelper<Self, W> {

    /**
     * Set the text to be displayed as a tooltip.
     * 
     * @param text Tooltip text.
     * @return Chainable API.
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
     * Set the text to be displayed as a tooltip.
     * 
     * @param text Tooltip text.
     * @return Chainable API.
     */
    default Self tooltip(Supplier text) {
        return tooltip(lang -> I.signal(text).map(String::valueOf));
    }

    /**
     * Set the text to be displayed as a tooltip.
     * 
     * @param text Tooltip text.
     * @return Chainable API.
     */
    default Self tooltip(Transcript text) {
        return tooltip(text::as);
    }

    /**
     * Set the text to be displayed as a tooltip.
     * 
     * @param text Tooltip text.
     * @return Chainable API.
     */
    private Self tooltip(Function<Lang, Signal<String>> text) {
        Lang.observing().switchMap(I.wiseF(text)).on(Viewtify.UIThread).to(translated -> {
            tooltip(translated);
        });
        return (Self) this;
    }

    /**
     * Set the content to be displayed as a popup.
     * 
     * @param contents Popup contents
     * @return Chainable API.
     */
    default Self popup(View contents) {
        return popup(AnchorLocation.WINDOW_TOP_LEFT, contents);
    }

    /**
     * Set the content to be displayed as a popup.
     * 
     * @param location Sets the position of the anchor used when popping up.
     * @param contents Popup contents
     * @return Chainable API.
     */
    default Self popup(AnchorLocation location, View contents) {
        if (contents != null) {
            ui().setOnMouseClicked(e -> {
                PopOver pop = (PopOver) ui().getProperties().computeIfAbsent("viewtify-popover", k -> {
                    PopOver p = new PopOver();
                    p.setDetachable(false);
                    p.setAnchorLocation(location);
                    p.setContentNode(contents.ui());
                    return p;
                });

                if (pop.isShowing()) {
                    pop.hide();
                } else {
                    pop.show(ui());
                }
            });
        }
        return (Self) this;
    }
}
