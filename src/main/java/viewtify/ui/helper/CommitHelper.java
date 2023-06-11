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

import kiss.WiseConsumer;
import viewtify.edit.Edito;

public interface CommitHelper<Self extends CommitHelper<Self, V>, V> {

    default Self comittable(WiseConsumer<V> save) {
        return comittable(save, null);
    }

    Self comittable(WiseConsumer<V> save, Edito context);
}
