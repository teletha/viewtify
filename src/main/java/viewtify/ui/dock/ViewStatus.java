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

import javafx.scene.control.Tab;

import viewtify.ui.View;

/**
 * Stores the current status and additional metadata of an window manager view.
 */
final class ViewStatus {

    /** The registered view */
    final View view;

    /** The tab which contains this view. */
    final Tab tab;

    /** The current position within the window */
    private ViewPosition position;

    private TabArea area;

    /**
     * Create a new view status.
     *
     * @param view The view to display.
     */
    ViewStatus(View view) {
        this.view = view;
        this.position = ViewPosition.CENTER;

        tab = new Tab(view.id());
        tab.setClosable(true);
        tab.setContent(view.ui());
        tab.setId(view.id());
        tab.setUserData(this);
        tab.setOnClosed(event -> {
            getArea().remove(this);
        });
    }

    public ViewPosition getPosition() {
        return position;
    }

    public void setPosition(ViewPosition position) {
        this.position = position;
    }

    public TabArea getArea() {
        return area;
    }

    public void setArea(TabArea area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "ViewStatus{" + "view=" + view + ", position=" + position + '}';
    }
}