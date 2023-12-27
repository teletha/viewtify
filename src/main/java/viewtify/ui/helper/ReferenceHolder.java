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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.Node;
import javafx.stage.Window;

public abstract class ReferenceHolder {

    /** The popup manager. */
    static final Map<Node, Window> popups = new ConcurrentHashMap();

    /** The reference holder. */
    volatile CollectableHelper.Ð collectable;
}