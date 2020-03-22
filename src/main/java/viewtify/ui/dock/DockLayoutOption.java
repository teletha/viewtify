/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.dock;

public final class DockLayoutOption {

    /** Default value is center. */
    int recommendedArea = DockSystem.PositionCenter;

    /** Default value is 0.5. */
    double recommendedRatio = 0.5;

    /**
     * Hide
     */
    DockLayoutOption() {
    }

    /**
     * Specify the recommended area.
     * 
     * @return
     */
    public DockLayoutOption center() {
        recommendedArea = DockSystem.PositionCenter;
        return this;
    }

    /**
     * Specify the recommended area.
     * 
     * @return
     */
    public DockLayoutOption top() {
        recommendedArea = DockSystem.PositionTop;
        return this;
    }

    /**
     * Specify the recommended area.
     * 
     * @return
     */
    public DockLayoutOption bottom() {
        recommendedArea = DockSystem.PositionBottom;
        return this;
    }

    /**
     * Specify the recommended area.
     * 
     * @return
     */
    public DockLayoutOption right() {
        recommendedArea = DockSystem.PositionRight;
        return this;
    }

    /**
     * Specify the recommended area.
     * 
     * @return
     */
    public DockLayoutOption left() {
        recommendedArea = DockSystem.PositionLeft;
        return this;
    }

    /**
     * Specify the recommended area division ratio.
     * 
     * @return
     */
    public DockLayoutOption ratio(double division) {
        recommendedRatio = division;
        return this;
    }
}
