/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.util.Arrays;

import javafx.geometry.Orientation;

import org.junit.jupiter.api.Test;

import viewtify.JavaFXTester;
import viewtify.ui.UIWorkbench.SplitableArea;
import viewtify.ui.UIWorkbench.StructureTree;

class UIWorkbenchTest extends JavaFXTester {

    @Test
    void empty() {
        StructureTree tree = new StructureTree();

        SplitableArea area = tree.build();
        assert area.getOrientation() == Orientation.HORIZONTAL;
        assert Arrays.equals(area.getDividerPositions(), new double[] {});
    }

    @Test
    void divideHorizontal() {
        StructureTree tree = new StructureTree();
        tree.ratio = 0.5;

        SplitableArea area = tree.build();
        assert area.getOrientation() == Orientation.HORIZONTAL;
        assert Arrays.equals(area.getDividerPositions(), new double[] {0.5});
    }
}
