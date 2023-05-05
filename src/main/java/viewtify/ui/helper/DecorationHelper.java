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

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
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
        Node node = ui();

        I.signal(Decorator.getDecorations(node))
                .take(GraphicDecoration.class::isInstance)
                .take(1)
                .on(Viewtify.UIThread)
                .to(deco -> Decorator.removeDecoration(node, deco), e -> {

                }, () -> {
                    Label label = new Label();
                    label.setGraphic(icon.image());
                    label.setScaleX(0.7);
                    label.setScaleY(0.7);
                    label.setAlignment(Pos.CENTER);

                    if (message != null && !message.isEmpty()) {
                        Tooltip tooltip = new Tooltip(message);
                        tooltip.setAutoFix(true);
                        tooltip.setShowDelay(Duration.ZERO);
                        tooltip.setShowDuration(Duration.INDEFINITE);
                        StyleHelper.of(tooltip).style(FormStyles.ValidationToolTip);
                        label.setTooltip(tooltip);

                        Decorator.addDecoration(node, new GraphicDecoration(label, Pos.TOP_LEFT, 0, 1));
                    }
                });

        return (Self) this;
    }

    private void visualize(Node node, Tooltip tooltip, boolean show) {
        if (show) {
            Bounds bounds = node.localToScreen(node.getBoundsInLocal());
            tooltip.show(node, bounds.getMinX(), bounds.getMinY());
        } else {
            tooltip.hide();
        }
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
