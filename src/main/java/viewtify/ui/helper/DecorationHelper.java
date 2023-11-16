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

import org.controlsfx.control.decoration.Decorator;

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

        I.signal(Decorator.getDecorations(node))
                .as(ValidatableDecoration.class)
                .take(1)
                .on(Viewtify.UIThread)
                .or(() -> ValidatableDecoration.create(node, icon))
                .to(deco -> deco.message(message));

        return (Self) this;
    }

    /**
     * Undecorate all icons.
     */
    default Self undecorate() {
        I.signal(Decorator.getDecorations(ui()))
                .as(ValidatableDecoration.class)
                .take(1)
                .on(Viewtify.UIThread)
                .to(ValidatableDecoration::dispose);

        return (Self) this;
    }
}