/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
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

    Dock base;

    int windowWidth;

    int windowHeight;

    boolean windowHeader;

    /**
     * Hide
     */
    DockRecommendedLocation() {
    }

    /**
     * Specify the recommended window.
     * 
     * @return
     */
    public DockRecommendedLocation window(int width, int height, boolean showHeader) {
        windowWidth = width;
        windowHeight = height;
        windowHeader = showHeader;
        return this;
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
    public DockRecommendedLocation top(Dock base) {
        recommendedArea = DockSystem.PositionTop;
        this.base = base;
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
    public DockRecommendedLocation bottom(Dock base) {
        recommendedArea = DockSystem.PositionBottom;
        this.base = base;
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
    public DockRecommendedLocation right(Dock base) {
        recommendedArea = DockSystem.PositionRight;
        this.base = base;
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
     * Specify the recommended area.
     * 
     * @return
     */
    public DockRecommendedLocation left(Dock base) {
        recommendedArea = DockSystem.PositionLeft;
        this.base = base;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "DockRecommendedLocation [recommendedArea=" + recommendedArea + ", recommendedRatio=" + recommendedRatio + ", base=" + base + "]";
    }

}