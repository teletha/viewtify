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

import org.controlsfx.control.PopOver;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Duration;
import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.UserInterfaceProvider;

public interface TooltipHelper<Self extends TooltipHelper, W extends Node> extends StyleHelper<Self, W>, UserInterfaceProvider<W> {

    /**
     * Remove the tooltip from this user interface.
     * 
     * @return Chainable API.
     */
    default Self untooltip() {
        Tooltip.uninstall(ui(), null);
        return (Self) this;
    }

    /**
     * Set the text to be displayed as a tooltip.
     * 
     * @param text Tooltip text.
     * @return Chainable API.
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
     * Set the text to be displayed as a tooltip.
     * 
     * @param text A text {@link Variable} to set.
     * @return Chainable API.
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
    default Self popup(UserInterfaceProvider<Node> contents) {
        if (contents != null) {
            popup(() -> contents);
        }
        return (Self) this;
    }

    /**
     * Set the content to be displayed on popup.
     * 
     * @param builder Create the contents. This callback will be invoked every showing the popup.
     * @return Chainable API.
     */
    default Self popup(Supplier<UserInterfaceProvider<Node>> builder) {
        if (builder != null) {
            UserActionHelper<?> helper = UserActionHelper.of(ui());
            helper.when(User.LeftClick, event -> {
                PopOver p = ReferenceHolder.popover();

                if (p.isShowing()) {
                    if (p.getUserData() == ui()) {
                        p.hide();
                        p.setUserData(null);
                    } else {
                        p.setOnHidden(e -> show(builder, p, event.getScreenX() + 25, event.getScreenY()));
                    }
                } else {
                    show(builder, p, event.getScreenX() + 25, event.getScreenY());
                }
            });
        }
        return (Self) this;
    }

    /**
     * Show popup.
     * 
     * @param builder Create the contents. This callback will be invoked every showing the popup.
     * @param popup A singleton popup widget.
     */
    private void show(Supplier<UserInterfaceProvider<Node>> builder, PopOver popup, double x, double y) {
        Platform.runLater(() -> {
            Node ui = builder.get().ui();

            popup.setContentNode(ui);
            popup.show(ui(), x, y);
            System.out.println(ui.localToScreen(ui.getBoundsInLocal()));
            popup.setUserData(ui());
            popup.setOnHidden(e -> popup.setContentNode(null));
        });
    }

    /**
     * Close the current popup window.
     */
    static void unpopup() {
        ReferenceHolder.popover().hide();
    }

    /**
     * Set the content to be displayed on popup.
     * 
     * @param builder Create the contents. This callback will be invoked every showing the popup.
     * @return Chainable API.
     */
    default Self overlay(Supplier<UserInterfaceProvider<Node>> builder) {
        if (builder != null) {
            PopOver p = ReferenceHolder.popover();
            W node = ui();
            Bounds local = node.getBoundsInLocal();
            Bounds bounds = node.localToScreen(local);

            if (p.isShowing()) {
                if (p.getUserData() == ui()) {
                    p.hide();
                    p.setUserData(null);
                } else {
                    p.setOnHidden(e -> show(builder, p, bounds.getMinX(), bounds.getMinY()));
                }
            } else {
                show(builder, p, bounds.getMinX(), bounds.getMinY());
            }
        }
        return (Self) this;
    }
}