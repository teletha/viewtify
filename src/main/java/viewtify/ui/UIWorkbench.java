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

import java.util.Arrays;

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

    static class StructureTree {

        /** The empty tree. */
        private static final StructureTree EMPTY = new StructureTree();

        public StructureTree left;

        public StructureTree right;

        /** Horizontal : 0 ~ 1, Vertical : 0 ~ -1 , None : 0 */
        public double ratio;

        SplitableArea build() {
            SplitableArea area = new SplitableArea();

            if (ratio == 0) {
                return area;
            } else {
                area.setDividerPositions(ratio);
                System.out.println(Arrays.toString(area.getDividerPositions()) + "  " + ratio);
                return area;
            }

        }

        /**
         * 
         */
        private void SplitableArea() {
        }
    }
}
