/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.function.Consumer;

import javafx.beans.property.Property;
import javafx.scene.control.ContextMenu;

import viewtify.ui.UIContextMenu;

public interface ContextMenuHelper<Self extends ContextMenuHelper> extends PropertyAccessHelper {

    /**
     * Create context menu.
     * 
     * @param builder
     * @return
     */
    default Self context(Consumer<UIContextMenu> builder) {
        Property<ContextMenu> context = property(Type.ContextMenu);

        if (context.getValue() == null) {
            ContextMenu root = new ContextMenu();
            builder.accept(new UIContextMenu(root));
            context.setValue(root);
        }
        return (Self) this;
    }
}
