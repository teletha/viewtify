/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.dock;

public final class DockRecommendedLocation {

    /** Default value is center. */
    int recommendedArea = DockSystem.PositionCenter;

    /** Default value is 0.5. */
    double recommendedRatio = 0.5;

    /**
     * Hide
     */
    DockRecommendedLocation() {
    }

    /**
     * Specify the recommended area.
     * 
     * @return
     */
    public DockRecommendedLocation center() {
        recommendedArea = DockSystem.PositionCenter;
        return this;
    }

    /**
     * Specify the recommended area.
     * 
     * @return
     */
    public DockRecommendedLocation top() {
        recommendedArea = DockSystem.PositionTop;
        return this;
    }

    /**
     * Specify the recommended area.
     * 
     * @return
     */
    public DockRecommendedLocation bottom() {
        recommendedArea = DockSystem.PositionBottom;
        return this;
    }

    /**
     * Specify the recommended area.
     * 
     * @return
     */
    public DockRecommendedLocation right() {
        recommendedArea = DockSystem.PositionRight;
        return this;
    }

    /**
     * Specify the recommended area.
     * 
     * @return
     */
    public DockRecommendedLocation left() {
        recommendedArea = DockSystem.PositionLeft;
        return this;
    }

    /**
     * Specify the recommended area division ratio.
     * 
     * @return
     */
    public DockRecommendedLocation ratio(double division) {
        recommendedRatio = division;
        return this;
    }
}