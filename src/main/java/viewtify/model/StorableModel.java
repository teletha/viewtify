/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.model;

public interface StorableModel {

    /**
     * The associated contianer for this item.
     * 
     * @return
     */
    private StorableList container() {
        return StorableList.of(getClass());
    }

    /**
     * Save this item.
     */
    default void save() {
        StorableList container = container();
        if (!container.contains(this)) container.add(this);
        container.store();
    }

    /**
     * Remove this item.
     */
    default void delete() {
        StorableList container = container();
        if (container.remove(this)) {
            container.store();
        }
    }
}