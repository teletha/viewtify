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

/**
 * An interface providing methods to set alignment, orientation, horizontal gap, and vertical gap
 * properties.
 *
 * @param <Self> The type of the implementing class, enabling method chaining.
 */
public interface BlockHelper<Self extends BlockHelper> extends PropertyAccessHelper {

    /**
     * Sets the height property.
     *
     * @return The implementing class instance for method chaining.
     */
    default Self height(double size) {
        property(Type.Height).setValue(size);

        return (Self) this;
    }

    /**
     * Sets the width property.
     *
     * @return The implementing class instance for method chaining.
     */
    default Self width(double size) {
        property(Type.Width).setValue(size);

        return (Self) this;
    }
}