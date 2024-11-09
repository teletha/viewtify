/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.WeakHashMap;

import javafx.collections.ObservableMap;

public abstract class ReferenceHolder {

    /** The weakable KVS. */
    static final WeakHashMap<Object, ObservableMap> ASSOCIATIVE = new WeakHashMap();

    /** The reference holder. */
    volatile CollectableHelper.Ð collectable;
}