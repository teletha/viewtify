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

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

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
            fix(c, true);

            Point2D localToScreen = node.localToScreen(e.getX(), e.getY());
            c.show(node, localToScreen.getX(), localToScreen.getY());
        });
    }

    private static void fix(ContextMenu contextMenu, boolean hideOnMouseReleased) {
        contextMenu.addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Node contextMenuNode = contextMenu.getStyleableNode();
                for (MenuItem menuItem : contextMenu.getItems()) {
                    Node menuItemNode = menuItem.getStyleableNode();
                    if (menuItemNode == null) {
                        continue;
                    }

                    // マウスカーソルがメニューアイテムの外側に出てとき、
                    // 他ノードにフォーカス要求を出すことでメニューアイテムからフォーカスを外します。
                    // ここでは他ノードとして親であるcontextMenuNodeを指定してます。（他の適当なノードでもOK）
                    menuItemNode.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
                        contextMenuNode.requestFocus();
                    });

                    // メニューアイテムで押下されたマウスボタンが離されたとき、
                    // メニューアイテムがフォーカスされていなければ、マウスイベントを消費します。
                    // これによってメニューアイテムのアクション発動を抑止することができます。
                    // また、hideOnMouseReleased の指定に従ってコンテキスト・メニューを非表示にします。
                    menuItemNode.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
                        if (!menuItemNode.isFocused()) {
                            mouseEvent.consume();
                            if (hideOnMouseReleased) {
                                contextMenu.hide();
                            }
                        }
                    });
                }
                contextMenu.removeEventHandler(WindowEvent.WINDOW_SHOWING, this);
            }
        });
    }
}
