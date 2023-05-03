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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.GraphicDecoration;

import kiss.I;
import viewtify.Viewtify;
import viewtify.style.FormStyles;
import viewtify.util.Icon;

public interface DecorationHelper<Self extends DecorationHelper<Self>> {

    /**
     * Find the decoratable widget.
     * 
     * @return
     */
    Node ui();

    /**
     * Decorate this ui by the specified icon.
     * 
     * @return
     */
    default Self decorateBy(Icon icon) {
        return decorateBy(icon, null);
    }

    /**
     * Decorate this ui by the specified icon.
     * 
     * @param icon
     * @param message
     */
    default Self decorateBy(Icon icon, String message) {
        I.signal(Decorator.getDecorations(ui()))
                .take(GraphicDecoration.class::isInstance)
                .take(1)
                .on(Viewtify.UIThread)
                .to(deco -> Decorator.removeDecoration(ui(), deco), e -> {

                }, () -> {
                    Label label = new Label();
                    label.setGraphic(icon.image());
                    label.setAlignment(Pos.CENTER);

                    if (message != null && !message.isEmpty()) {
                        Tooltip tooltip = new Tooltip(message);
                        tooltip.setAutoFix(true);
                        tooltip.setShowDelay(Duration.ZERO);
                        tooltip.setShowDuration(Duration.INDEFINITE);
                        StyleHelper.of(tooltip).style(FormStyles.ValidationToolTip);
                        label.setTooltip(tooltip);
                    }

                    Decorator.addDecoration(ui(), new GraphicDecoration(label, Pos.TOP_LEFT, 4, 4));
                });

        return (Self) this;
    }

    /**
     * Undecorate all icons.
     */
    default Self undecorate() {
        I.signal(Decorator.getDecorations(ui()))
                .take(GraphicDecoration.class::isInstance)
                .take(1)
                .on(Viewtify.UIThread)
                .to(deco -> Decorator.removeDecoration(ui(), deco));

        return (Self) this;
    }
}
