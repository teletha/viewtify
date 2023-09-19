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

public interface AlignmentHelper<Self extends AlignmentHelper> extends PropertyAccessHelper {

    /**
     * Set the alignment property.
     * 
     * @param position
     * @return
     */
    default Self alignment(Pos position) {
        property(Type.Alignment).setValue(position);

        return (Self) this;
    }

    /**
     * Set the oprientation property.
     * 
     * @param oprientation
     * @return
     */
    default Self orientation(Orientation oprientation) {
        property(Type.Orientation).setValue(oprientation);

        return (Self) this;
    }

    /**
     * Set the horizontal gap property.
     * 
     * @param gap
     * @return
     */
    default Self horizontalSpace(double gap) {
        property(Type.HGap).setValue(gap);

        return (Self) this;
    }

    /**
     * Set the vertical gap property.
     * 
     * @param gap
     * @return
     */
    default Self verticalSpace(double gap) {
        property(Type.VGap).setValue(gap);

        return (Self) this;
    }
}
