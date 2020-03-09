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

import java.time.Duration;

import javafx.geometry.Side;
import javafx.scene.Node;

import org.controlsfx.control.HiddenSidesPane;

import viewtify.ui.helper.ContextMenuHelper;

public class UISlidePane extends UserInterface<UISlidePane, HiddenSidesPane> implements ContextMenuHelper<UISlidePane> {

    /**
     * @param ui
     * @param view
     */
    public UISlidePane(View view) {
        super(new HiddenSidesPane(), view);
    }

    /**
     * Set slide contents.
     * 
     * @param side A side to slide.
     * @param contents A contents.
     * @return Chainable API.
     */
    public final UISlidePane on(Side side, UserInterfaceProvider<Node> contents) {
        return on(side, contents.ui());
    }

    /**
     * Set slide contents.
     * 
     * @param side A side to slide.
     * @param contents A contents.
     * @return Chainable API.
     */
    public final UISlidePane on(Side side, Node contents) {
        if (side != null && contents != null) {
            switch (side) {
            case BOTTOM:
                ui.setBottom(contents);
                break;

            case TOP:
                ui.setTop(contents);
                break;

            case RIGHT:
                ui.setRight(contents);
                break;

            case LEFT:
                ui.setLeft(contents);
                break;
            }
        }
        return this;
    }

    /**
     * Set the value of the trigger distance property. <br>
     * Setting the property to zero or a negative value will disable this functionality, so a hidden
     * side can only be made visible with {@link #setPinnedSide(Side)}.
     * 
     * @param distance the new value for the trigger distance property
     */
    public final UISlidePane animationDistance(double distance) {
        ui.setTriggerDistance(distance);

        return this;
    }

    /**
     * Set the animation delay
     * 
     * @param mills slide in animation delay
     */
    public final UISlidePane animationDelay(long mills) {
        return animationDelay(Duration.ofMillis(mills));
    }

    /**
     * Set the animation delay
     * 
     * @param delay slide in animation delay
     */
    public final UISlidePane animationDelay(Duration delay) {
        if (delay != null) {
            ui.setAnimationDelay(javafx.util.Duration.millis(delay.toMillis()));
        }
        return this;
    }

    /**
     * Set the animation delay
     * 
     * @param mills animation duration
     */
    public final UISlidePane animationDuration(long mills) {
        return animationDuration(Duration.ofMillis(mills));
    }

    /**
     * Set the animation delay
     * 
     * @param duration animation duration
     */
    public final UISlidePane animationDuration(Duration duration) {
        if (duration != null) {
            ui.setAnimationDuration(javafx.util.Duration.millis(duration.toMillis()));
        }
        return this;
    }
}
