/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.anime;

import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.scene.Node;

public interface Slide {

    /** Built-in animation. */
    static ShowAnime PopX(int location) {
        return slide(location, Node::translateXProperty);
    }

    /** Built-in animation. */
    static ShowAnime PopY(int location) {
        return slide(location, Node::translateYProperty);
    }

    /** Built-in animation. */
    private static ShowAnime slide(int location, Function<Node, Property> extractor) {
        return (parent, before, action) -> {
            Anime.define().duration(0.8).interpolator(Interpolators.EASE_IN_OUT_BACK).effect(extractor.apply(before), location).run(action);
        };
    }
}