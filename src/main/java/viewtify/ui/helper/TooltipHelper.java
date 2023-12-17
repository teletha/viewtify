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
import org.controlsfx.control.PopOver.ArrowLocation;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
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
                PopOver p = ReferenceHolder.popover();
                double x, y;
                if (arrow == null) {
                    x = event.getScreenX() + 25;
                    y = event.getScreenY();
                } else {
                    p.setArrowSize(4);
                    p.setArrowLocation(arrow);

                    Bounds bound = ui().localToScreen(ui().getBoundsInLocal());
                    x = arrowX(arrow, event, bound);
                    y = arrowY(arrow, event, bound);
                }

                if (p.isShowing()) {
                    if (p.getUserData() == ui()) {
                        p.hide();
                        p.setUserData(null);
                    } else {
                        p.setOnHidden(e -> show(builder, p, x, y));
                    }
                } else {
                    show(builder, p, x, y);
                }
            });
        }
        return (Self) this;
    }

    /**
     * Build the arrow related configuration.
     * 
     * @param pop
     * @param arrow
     * @param bound
     */
    private double arrowX(ArrowLocation arrow, MouseEvent event, Bounds bound) {
        return switch (arrow) {
        case TOP_CENTER, BOTTOM_CENTER -> bound.getCenterX() - bound.getWidth() / 2;
        case LEFT_CENTER -> bound.getMaxX();
        case RIGHT_CENTER -> bound.getMinX();
        default -> 0;
        };
    }

    /**
     * Build the arrow related configuration.
     * 
     * @param pop
     * @param arrow
     * @param bound
     */
    private double arrowY(ArrowLocation arrow, MouseEvent event, Bounds bound) {
        return switch (arrow) {
        case TOP_CENTER -> bound.getMaxY();
        case BOTTOM_CENTER -> bound.getMinY();
        case LEFT_CENTER, RIGHT_CENTER -> bound.getCenterY();
        default -> 0;
        };
    }

    /**
     * Show popup.
     * 
     * @param builder Create the contents. This callback will be invoked every showing the popup.
     * @param popup A singleton popup widget.
     */
    private void show(Supplier<UserInterfaceProvider<? extends Node>> builder, PopOver popup, double x, double y) {
        Platform.runLater(() -> {
            Node ui = builder.get().ui();

            popup.setContentNode(ui);
            popup.show(ui(), x, y);
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
     * Sets the content to be displayed on an overlay popup using a {@link Supplier}.
     *
     * @param builder Create the contents. This callback will be invoked every time the overlay is
     *            shown.
     * @return The implementing class instance for method chaining.
     */
    default Self overlay(Supplier<UserInterfaceProvider<? extends Node>> builder) {
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