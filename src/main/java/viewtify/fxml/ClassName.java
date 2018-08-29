/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.fxml;

import java.util.function.Consumer;

import viewtify.fxml.FXML.UINode;

/**
 * @version 2018/08/29 13:50:21
 */
public interface ClassName extends Consumer<UINode> {

    /**
     * {@inheritDoc}
     */
    @Override
    default void accept(UINode parent) {
        parent.classes.add(name());
    }

    String name();
}
