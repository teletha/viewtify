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

import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.GraphicDecoration;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow.AnchorLocation;
import viewtify.style.FormStyles;
import viewtify.ui.anime.HideAnime;
import viewtify.ui.anime.ShowAnime;
import viewtify.util.Icon;

class DecorationTooltip extends GraphicDecoration implements ChangeListener<Boolean> {

    private final Tooltip tooltip;

    private final Label label;

    static DecorationTooltip create(Node node, Icon icon) {
        Label label = new Label();
        label.setGraphic(icon.image());
        label.setScaleX(0.7);
        label.setScaleY(0.7);
        label.setAlignment(Pos.CENTER);

        return new DecorationTooltip(node, icon, label);
    }

    /**
     * @param icon
     */
    DecorationTooltip(Node node, Icon icon, Label label) {
        super(label, Pos.TOP_LEFT, 0, 1);
        this.label = label;
        this.tooltip = new Tooltip();
        this.tooltip.setAutoFix(true);
        this.tooltip.setAnchorLocation(AnchorLocation.CONTENT_BOTTOM_LEFT);
        StyleHelper.of(tooltip).style(FormStyles.ValidationToolTip);

        Decorator.addDecoration(node, this);

        node.focusedProperty().addListener(this);
        if (node.isFocused()) {
            show();
        }

        node.focusedProperty().addListener(this);
        if (node.isFocused()) {
            show();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            show();
        } else {
            hide();
        }
    }

    public void show() {
        Bounds bounds = label.localToScreen(label.getBoundsInLocal());
        tooltip.show(label, bounds.getCenterX(), bounds.getMinY() - 7);

        ShowAnime.FadeIn(1).play(tooltip.getScene().getRoot());
    }

    public void hide() {
        HideAnime.FadeOut.play(tooltip.getScene().getRoot());
    }
}
