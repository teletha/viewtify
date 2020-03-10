/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.function.Consumer;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;

public class UIContextMenu {

    /** The actual ui. */
    private final ContextMenu ui;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    public UIContextMenu(ContextMenu ui) {
        this.ui = ui;
    }

    /**
     * Declare menu ui.
     * 
     * @param menu
     */
    public final void menu(UserInterfaceProvider<? extends Node> menu) {
        menu(menu.ui());
    }

    /**
     * Declare menu node.
     * 
     * @param menu
     */
    public final void menu(Node menu) {
        ui.getItems().add(new CustomMenuItem(menu));
    }

    /**
     * Declare simple menu.
     * 
     * @return
     */
    public UIMenuItem menu() {
        MenuItem menu = new MenuItem();
        ui.getItems().add(menu);

        return new UIMenuItem(menu);
    }

    /**
     * Declare checkbox menu.
     * 
     * @return
     */
    public UIMenuItem checkMenu() {
        CheckMenuItem menu = new CheckMenuItem();
        ui.getItems().add(menu);

        return new UIMenuItem(menu);
    }

    public static void declareOn(UserInterfaceProvider<? extends Node> node, Consumer<UIContextMenu> context) {
        declareOn(node.ui(), context);
    }

    public static void declareOn(Node node, Consumer<UIContextMenu> context) {
        node.setOnContextMenuRequested(e -> {
            e.consume();

            ContextMenu c = new ContextMenu();
            context.accept(new UIContextMenu(c));
            c.setAutoHide(true);
            c.setAutoFix(true);
            c.setConsumeAutoHidingEvents(true);
            c.setForceIntegerRenderScale(true);

            Point2D localToScreen = node.localToScreen(e.getX(), e.getY());
            c.show(node, localToScreen.getX(), localToScreen.getY());

            c.setOnCloseRequest(x -> {
                System.out.println("CloseRequest " + x);
            });
            c.setOnAutoHide(x -> {
                System.out.println("AutoHide " + x);
            });

            c.setOnHiding(x -> {
                System.out.println("Hidden " + x);
            });
            c.setOnHiding(x -> {
                System.out.println("Hiding " + x);
            });

            c.sceneProperty().addListener(invalid -> {
                System.out.println("invalid " + invalid);
            });
        });
    }
}
