/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.concurrent.TimeUnit;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow.AnchorLocation;

import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.GraphicDecoration;

import kiss.Disposable;
import viewtify.Viewtify;
import viewtify.style.FormStyles;
import viewtify.ui.anime.HideAnime;
import viewtify.ui.anime.ShowAnime;
import viewtify.util.Icon;

class ValidatableDecoration extends GraphicDecoration implements Disposable {

    /** The decoration target. */
    private final Node node;

    /** The decration. */
    private final Label decoration;

    /** The tooltip. */
    private final Tooltip tooltip;

    /**
     * Create new decoration.
     * 
     * @param node
     * @param icon
     * @return
     */
    static ValidatableDecoration create(Node node, Icon icon) {
        Label label = new Label();
        label.setGraphic(icon.image());
        label.setScaleX(0.7);
        label.setScaleY(0.7);
        label.setAlignment(Pos.CENTER);

        return new ValidatableDecoration(node, icon, label);
    }

    /**
     * @param icon
     */
    ValidatableDecoration(Node node, Icon icon, Label decoration) {
        super(decoration, Pos.TOP_LEFT, 0, 1);
        this.node = node;
        this.decoration = decoration;
        this.tooltip = new Tooltip();
        this.tooltip.setAutoFix(true);
        this.tooltip.setAnchorLocation(AnchorLocation.CONTENT_BOTTOM_LEFT);
        StyleHelper.of(tooltip).style(FormStyles.ValidationToolTip);

        // show decoration
        Decorator.addDecoration(node, this);

        // focus management
        Viewtify.observing(node.focusedProperty()).debounce(250, TimeUnit.MILLISECONDS).on(Viewtify.UIThread).to(focused -> {
            if (focused) {
                show();
            } else {
                hide();
            }
        }, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vandalize() {
        Decorator.removeDecoration(node, this);
    }

    /**
     * Show tooltip.
     */
    public void show() {
        Bounds bounds = decoration.localToScreen(decoration.getBoundsInLocal());
        tooltip.show(decoration, bounds.getCenterX(), bounds.getMinY() - 7);

        ShowAnime.FadeIn(1).play(tooltip.getScene().getRoot());
    }

    /**
     * Hide tooltip.
     */
    public void hide() {
        HideAnime.FadeOut.play(tooltip.getScene().getRoot());
    }

    /**
     * Write message on tooltip.
     * 
     * @param message
     */
    public void message(String message) {
        tooltip.setText(message);
    }
}
