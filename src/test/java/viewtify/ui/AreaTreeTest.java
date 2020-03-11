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

import org.junit.jupiter.api.Test;

import viewtify.JavaFXTester;
import viewtify.ui.UIWorkbench.AreaTree;

class AreaTreeTest extends JavaFXTester {

    @Test
    void isContentNode() {
        AreaTree tree = new AreaTree();
        assert tree.isContentNode();
    }

    @Test
    void isLayoutNode() {
        AreaTree tree = new AreaTree();
        tree.setRatio(0.5);
        assert tree.isContentNode() == false;
        assert tree.isHorizontalLayoutNode() == true;
        assert tree.isVerticalLayoutNode() == false;

        tree = new AreaTree();
        tree.setRatio(-0.5);
        assert tree.isContentNode() == false;
        assert tree.isHorizontalLayoutNode() == false;
        assert tree.isVerticalLayoutNode() == true;
    }

    @Test
    void subArea() {
        AreaTree tree = new AreaTree();
        tree.setRatio(0.5);
    }
}
