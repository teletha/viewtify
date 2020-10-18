/*
 * Copyright (C) 2020 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.util;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;

import kiss.I;
import kiss.Variable;

public enum Corner implements Translatable {
    TopLeft(Pos.TOP_LEFT, I.translate("TopLeft")),

    TopRight(Pos.TOP_RIGHT, I.translate("TopRight")),

    BottomLeft(Pos.BOTTOM_LEFT, I.translate("BottomLeft")),

    BottomRight(Pos.BOTTOM_RIGHT, I.translate("BottomRight"));

    /** The actual position. */
    private final Pos position;

    /** The readable text. */
    private final Variable<String> text;

    /**
     * @param position
     */
    private Corner(Pos position, Variable<String> text) {
        this.position = position;
        this.text = text;
    }

    /**
     * Check horizontal location.
     * 
     * @return A result.
     */
    public boolean isLeftSide() {
        return position.getHpos() == HPos.LEFT;
    }

    /**
     * Check horizontal location.
     * 
     * @return A result.
     */
    public boolean isRightSide() {
        return position.getHpos() == HPos.RIGHT;
    }

    /**
     * Check vertical location.
     * 
     * @return A result.
     */
    public boolean isTopSide() {
        return position.getVpos() == VPos.TOP;
    }

    /**
     * Check vertical location.
     * 
     * @return A result.
     */
    public boolean isBottomSide() {
        return position.getVpos() == VPos.BOTTOM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return text.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Variable<String> toTraslated() {
        return text;
    }
}
