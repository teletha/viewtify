/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import viewtify.ui.anime.Anime;
import viewtify.util.MonkeyPatch;

public class EnhancedContextMenu extends ContextMenu {

    /** The reusable event consumer. */
    static final EventHandler<ContextMenuEvent> NOOP = ContextMenuEvent::consume;

    /** The disable state. */
    boolean disable;

    /** The last hidden time. */
    long lastHidden;

    /**
     * 
     */
    public EnhancedContextMenu() {
        addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> lastHidden = System.currentTimeMillis());
        addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<>() {

            @Override
            public void handle(WindowEvent x) {
                removeEventHandler(WindowEvent.WINDOW_SHOWING, this);

                MonkeyPatch.fix(EnhancedContextMenu.this);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void show() {
        if (canShow()) {
            super.show();

            Node node = getStyleableNode();
            node.setTranslateY(-10);
            node.setOpacity(0);

            Anime.define().opacity(node, 1).moveY(node, 0).run();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show(Node anchor, double screenX, double screenY) {
        if (canShow()) {
            super.show(anchor, screenX, screenY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show(Node anchor, Side side, double dx, double dy) {
        if (canShow()) {
            super.show(anchor, side, dx, dy);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show(Window owner) {
        if (canShow()) {
            super.show(owner);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show(Window ownerWindow, double anchorX, double anchorY) {
        if (canShow()) {
            super.show(ownerWindow, anchorX, anchorY);
        }
    }

    /**
     * Check showing state.
     * 
     * @return
     */
    private boolean canShow() {
        // check state
        if (isShowing()) {
            return false;
        }

        // availability
        if (disable) {
            return false;
        }

        // check squencial access
        if (System.currentTimeMillis() - lastHidden < 100) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hide() {
        Node node = getStyleableNode();

        Anime.define().opacity(node, 0).moveY(node, 10).run(super::hide);
    }
}