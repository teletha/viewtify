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

import javafx.geometry.Orientation;
import javafx.geometry.Pos;

/**
 * An interface providing methods to set alignment, orientation, horizontal gap, and vertical gap
 * properties.
 *
 * @param <Self> The type of the implementing class, enabling method chaining.
 */
public interface AlignmentHelper<Self extends AlignmentHelper> extends PropertyAccessHelper {

    /**
     * Sets the alignment property.
     *
     * @param position The position to set for alignment.
     * @return The implementing class instance for method chaining.
     */
    default Self alignment(Pos position) {
        property(Type.Alignment).setValue(position);

        return (Self) this;
    }

    /**
     * Sets the orientation property.
     *
     * @param orientation The orientation to set.
     * @return The implementing class instance for method chaining.
     */
    default Self orientation(Orientation orientation) {
        property(Type.Orientation).setValue(orientation);

        return (Self) this;
    }

    /**
     * Sets the horizontal gap property.
     *
     * @param gap The horizontal gap to set.
     * @return The implementing class instance for method chaining.
     */
    default Self horizontalSpace(double gap) {
        property(Type.HGap).setValue(gap);

        return (Self) this;
    }

    /**
     * Sets the vertical gap property.
     *
     * @param gap The vertical gap to set.
     * @return The implementing class instance for method chaining.
     */
    default Self verticalSpace(double gap) {
        property(Type.VGap).setValue(gap);

        return (Self) this;
    }
}
