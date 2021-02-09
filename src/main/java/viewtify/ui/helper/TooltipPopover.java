/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.Objects;
import java.util.function.Supplier;

import org.controlsfx.control.PopOver;

import javafx.scene.Node;
import viewtify.ui.UserInterfaceProvider;

class TooltipPopover extends PopOver {

    static final TooltipPopover SINGLETON = new TooltipPopover();

    /**
     * 
     */
    public TooltipPopover() {
        setDetachable(false);
        setOnHidden(e -> setContentNode(null));
    }

    /**
     * Toggle visibility.
     * 
     * @param target
     */
    void toggleOn(Node target, Supplier<? extends UserInterfaceProvider<? extends Node>> builder) {
        Objects.requireNonNull(target);

        if (isShowing()) {
            hide();
        } else {
            setContentNode(builder.get().ui());
            show(target);
        }
    }
}
