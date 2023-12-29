/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.Objects;
import java.util.function.Supplier;

import org.controlsfx.control.PopOver.ArrowLocation;

import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Duration;
import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.UserInterfaceProvider;

/**
 * An interface providing methods for managing tooltips and popups for a UI element.
 *
 * @param <Self> The type of the implementing class, enabling method chaining.
 * @param <W> The type of the UI element to which the tooltip or popup is applied.
 */
public interface TooltipHelper<Self extends TooltipHelper, W extends Node> extends StyleHelper<Self, W>, UserInterfaceProvider<W> {

    /**
     * Removes the tooltip from this user interface.
     *
     * @return The implementing class instance for method chaining.
     */
    default Self untooltip() {
        Tooltip.uninstall(ui(), null);
        return (Self) this;
    }

    /**
     * Sets the text to be displayed as a tooltip.
     *
     * @param text Tooltip text.
     * @return The implementing class instance for method chaining.
     */
    default Self tooltip(Object text) {
        Tooltip tooltip = new Tooltip(Objects.toString(text));
        // WORKAROUND : When the anchor is no longer specified, the pop-up location shifts every
        // time, probably a bug.
        tooltip.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
        tooltip.setShowDelay(Duration.millis(333));
        tooltip.setShowDuration(Duration.INDEFINITE);
        tooltip.setFont(Font.font(12));
        tooltip.setAutoHide(true);
        tooltip.setWrapText(true);
        // When moving the focus to a control with a tooltip visible, an event is consumed to erase
        // the tooltip and prevents the focus from being moved incorrectly.
        tooltip.setConsumeAutoHidingEvents(false);
        tooltip.setOnShowing(e -> {
            Node node = ui();
            Bounds bounds = node.localToScreen(node.getBoundsInLocal());
            tooltip.setX(bounds.getMinX() - 8);
            tooltip.setY(bounds.getMaxY() - 2);
        });

        Tooltip.install(ui(), tooltip);
        return (Self) this;
    }

    /**
     * Sets the text to be displayed as a tooltip using a {@link Variable}.
     *
     * @param text A text {@link Variable} to set.
     * @return The implementing class instance for method chaining.
     */
    default Self tooltip(Variable text) {
        text.observing().on(Viewtify.UIThread).to(this::tooltip);
        return (Self) this;
    }

    /**
     * Set the content to be displayed on popup.
     * 
     * @param contents The contents to display in popup.
     * @return Chainable API.
     */
    default Self popup(UserInterfaceProvider<? extends Node> contents) {
        return popup(null, contents);
    }

    /**
     * Set the content to be displayed on popup.
     * 
     * @param contents The contents to display in popup.
     * @return Chainable API.
     */
    default Self popup(ArrowLocation arrow, UserInterfaceProvider<? extends Node> contents) {
        if (contents != null) {
            popup(arrow, () -> contents);
        }
        return (Self) this;
    }

    /**
     * Set the content to be displayed on popup.
     * 
     * @param builder Create the contents. This callback will be invoked every showing the popup.
     * @return Chainable API.
     */
    default Self popup(Supplier<UserInterfaceProvider<? extends Node>> builder) {
        return popup(null, builder);
    }

    /**
     * Set the content to be displayed on popup.
     * 
     * @param builder Create the contents. This callback will be invoked every showing the popup.
     * @return Chainable API.
     */
    default Self popup(ArrowLocation arrow, Supplier<UserInterfaceProvider<? extends Node>> builder) {
        if (builder != null) {
            UserActionHelper<?> helper = UserActionHelper.of(ui());
            helper.when(User.LeftClick, event -> {
                Viewtify.dialog()
                        .location(ui(), Objects.requireNonNullElse(arrow, ArrowLocation.TOP_CENTER))
                        .fadable(Side.TOP)
                        .showPopup(builder);
            });
        }
        return (Self) this;
    }
}