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

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Duration;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

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
        return popup(null, contents);
    }

    /**
     * Set the content to be displayed on popup.
     * 
     * @param contents The contents to display in popup.
     * @return Chainable API.
     */
    default Self popup(ArrowLocation arrow, UserInterfaceProvider<Node> contents) {
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
                PopOver p = ReferenceHolder.popover();
                double x, y;
                if (arrow == null) {
                    x = event.getScreenX() + 25;
                    y = event.getScreenY();
                } else {
                    int size = 6;
                    p.setArrowSize(size);
                    p.setArrowLocation(arrow);

                    Bounds bound = ui().localToScreen(ui().getBoundsInLocal());
                    x = switch (arrow) {
                    case TOP_CENTER, BOTTOM_CENTER -> bound.getCenterX() - bound.getWidth() / 4;
                    case TOP_LEFT, BOTTOM_LEFT -> bound.getCenterX();
                    case TOP_RIGHT, BOTTOM_RIGHT -> bound.getMinX();
                    case RIGHT_CENTER, RIGHT_BOTTOM, RIGHT_TOP -> bound.getMinX();
                    case LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM -> bound.getMaxX();
                    };
                    y = switch (arrow) {
                    case RIGHT_TOP, LEFT_TOP -> bound.getCenterY();
                    case RIGHT_CENTER, LEFT_CENTER -> bound.getCenterY() - bound.getHeight() / 4;
                    case RIGHT_BOTTOM, LEFT_BOTTOM -> bound.getMinY();
                    case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> bound.getMaxY();
                    case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> bound.getMinY();
                    };
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