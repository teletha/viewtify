/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import javafx.scene.control.SplitPane;

import viewtify.ui.helper.ContextMenuHelper;

public class UIWorkbench extends UserInterface<UIWorkbench, SplitPane> implements ContextMenuHelper<UIWorkbench> {

    /**
     * @param ui
     * @param view
     */
    public UIWorkbench(View view) {
        super(new SplitableArea(), view);
    }

    static class SplitableArea extends SplitPane {

        private SplitableArea() {

        }
    }

    static class AreaTree {

        /** The empty tree. */
        private static final AreaTree EMPTY = new AreaTree();

        public AreaTree left;

        public AreaTree right;

        /** Horizontal : 0 ~ 1, Vertical : 0 ~ -1 , None : 0 */
        private double ratio;

        /**
         * Get the ratio property of this {@link UIWorkbench.AreaTree}.
         * 
         * @return The ratio property.
         */
        private double getRatio() {
            return ratio;
        }

        /**
         * Set the ratio property of this {@link UIWorkbench.AreaTree}.
         * 
         * @param ratio The ratio value to set.
         */
        final void setRatio(double ratio) {
            this.ratio = ratio;
        }

        /**
         * Is this contents node or not.
         * 
         * @return
         */
        final boolean isContentNode() {
            return ratio == 0d;
        }

        /**
         * Is this layout node or not.
         * 
         * @return
         */
        final boolean isHorizontalLayoutNode() {
            return 0 < ratio && ratio <= 1;
        }

        /**
         * Is this layout node or not.
         * 
         * @return
         */
        final boolean isVerticalLayoutNode() {
            return -1 <= ratio && ratio < 0;
        }
    }
}
