/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.model;

import kiss.I;
import kiss.model.Model;

public interface StorableModel<C extends StorableList> {

    /**
     * The associated contianer for this item.
     * 
     * @return
     */
    private C container() {
        return I.make((Class<C>) Model.collectParameters(getClass(), StorableModel.class)[0]);
    }

    /**
     * Save this item.
     */
    default void save() {
        C container = container();
        if (!container.contains(this)) container.add(this);
        container.store();
    }

    /**
     * Remove this item.
     */
    default void delete() {
        C container = container();
        if (container.remove(this)) {
            container.store();
        }
    }
}
