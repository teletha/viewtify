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

import org.controlsfx.control.decoration.Decoration;
import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.GraphicDecoration;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import kiss.I;
import viewtify.Viewtify;
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

        Viewtify.inUI(() -> {
            ObservableList<Decoration> decorations = Decorator.getDecorations(node);
            if (decorations == null || decorations.isEmpty()) {
                DecorationTooltip deco = DecorationTooltip.create(node, icon);
            }
        });

        return (Self) this;
    }

    /**
     * Undecorate all icons.
     */
    default Self undecorate() {
        I.signal(Decorator.getDecorations(ui())).take(GraphicDecoration.class::isInstance).take(1).on(Viewtify.UIThread).to(deco -> {
            Decorator.removeDecoration(ui(), deco);

            Object object = deco.getProperties().get("validation");
            if (object instanceof ChangeListener listener) {
                ui().focusedProperty().removeListener(listener);
            }
        });

        return (Self) this;
    }
}
