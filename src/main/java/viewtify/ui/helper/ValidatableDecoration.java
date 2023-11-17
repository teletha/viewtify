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

import java.util.concurrent.TimeUnit;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.stage.Window;

import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.GraphicDecoration;

import kiss.Disposable;
import kiss.WiseRunnable;
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

    /** The tooltip state. */
    private Disposable showing;

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
        label.setScaleX(0.8);
        label.setScaleY(0.8);
        label.setAlignment(Pos.CENTER);

        return new ValidatableDecoration(node, icon, label);
    }

    /**
     * @param icon
     */
    ValidatableDecoration(Node node, Icon icon, Label decoration) {
        super(decoration, Pos.TOP_LEFT, 1, 1);
        this.node = node;
        this.decoration = decoration;

        Tooltip tooltip = new Tooltip();
        tooltip.setAutoFix(true);
        tooltip.setAnchorLocation(AnchorLocation.CONTENT_BOTTOM_LEFT);
        StyleHelper.of(tooltip).style(FormStyles.ValidationToolTip);
        Viewtify.observe(tooltip.skinProperty()).first().to(n -> n.getNode().setOnMouseEntered(e -> hide()));

        this.tooltip = tooltip;

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
        hide(() -> Decorator.removeDecoration(node, this));
    }

    /**
     * Show tooltip.
     */
    public void show() {
        Bounds bounds = decoration.localToScreen(decoration.getBoundsInLocal());
        tooltip.show(decoration, bounds.getCenterX(), bounds.getMinY() - 7);

        ShowAnime.FadeIn(1).play(tooltip.getScene().getRoot());

        Window w = node.getScene().getWindow();
        showing = Viewtify.observe(w.xProperty(), w.yProperty(), w.widthProperty(), w.heightProperty()).take(1).to(() -> {
            hide();
            show();
        });
    }

    /**
     * Hide tooltip.
     */
    public void hide(WiseRunnable... complete) {
        HideAnime.FadeOut.play(tooltip.getScene().getRoot(), complete == null || complete.length != 1 ? null : complete[0]);

        if (showing != null) {
            showing.dispose();
            showing = null;
        }
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