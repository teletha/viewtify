/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

import kiss.Variable;

public class UISideTab extends UserInterface<UISideTab, HBox> {

    /** The label manager. */
    private final List<UILabel> labels = new ArrayList();

    /** The view manager. */
    private final List<Node> nodes = new ArrayList();

    /**
     * 
     */
    UISideTab(View view) {
        super(new HBox(), view);
    }

    /**
     * Add new tab.
     * 
     * @param title
     * @param view
     * @return
     */
    public UISideTab add(String title, Class<? extends View> view) {
        return add(Variable.of(title), view);
    }

    /**
     * Add new tab.
     * 
     * @param title
     * @param view
     * @return
     */
    public UISideTab add(Variable<String> title, Class<? extends View> view) {
        UILabel label = new UILabel(this.view);
        label.text(title);
        labels.add(label);

        return this;
    }
}
