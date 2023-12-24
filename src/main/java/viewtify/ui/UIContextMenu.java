/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.List;
import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import kiss.Disposable;
import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.anime.Anime;
import viewtify.util.MonkeyPatch;

public class UIContextMenu {

    /** The actual ui. */
    private final List<MenuItem> menuProvider;

    /** The identifier of context menu. */
    private final Object id;

    /**
     * Enchanced view.
     */
    public UIContextMenu(Object id, List<MenuItem> menuProvider) {
        this.id = id;
        this.menuProvider = menuProvider;
    }

    /**
     * Declare checkbox menu.
     * 
     * @return Chainable API.
     */
    public UICheckMenuItem check() {
        return new UICheckMenuItem(register(new CheckMenuItem()));
    }

    /**
     * Declare checkbox menu with text.
     * 
     * @param text A label text.
     * @return Chainable API.
     */
    public UICheckMenuItem check(Object text) {
        return check().text(text);
    }

    /**
     * Declare checkbox menu with text.
     * 
     * @param text A label text.
     * @return Chainable API.
     */
    public UICheckMenuItem check(Variable text) {
        return check().text(text);
    }

    /**
     * Declare menu ui.
     * 
     * @param provider
     */
    public final void menu(UserInterfaceProvider<? extends Node> provider) {
        menu(provider, true);
    }

    /**
     * Declare menu ui.
     * 
     * @param provider
     */
    public final void menu(UserInterfaceProvider<? extends Node> provider, boolean hideOnClick) {
        CustomMenuItem menu = register(new CustomMenuItem(provider.ui()));
        menu.setHideOnClick(hideOnClick);
    }

    /**
     * Declare simple menu.
     * 
     * @return Chainable API.
     */
    public UIMenuItem<MenuItem> menu() {
        return new UIMenuItem(register(new MenuItem()));
    }

    /**
     * Declare simple menu with text.
     * 
     * @param text A label text.
     * @return Chainable API.
     */
    public UIMenuItem<MenuItem> menu(Object text) {
        return menu().text(text);
    }

    /**
     * Declare simple menu with text.
     * 
     * @param text A label text.
     * @return Chainable API.
     */
    public UIMenuItem<MenuItem> menu(Variable text) {
        return menu().text(text);
    }

    /**
     * Declare simple menu with text.
     * 
     * @param text A label text.
     */
    public void menu(Object text, Consumer<UIContextMenu> sub) {
        menu(Variable.of(String.valueOf(text)), sub);
    }

    /**
     * Declare simple menu with text.
     * 
     * @param text A label text.
     */
    public void menu(Variable<String> text, Consumer<UIContextMenu> sub) {
        UIMenuItem<Menu> menu = new UIMenuItem<>(register(new Menu())).text(text);
        menu.when(Menu.ON_SHOWN, () -> {
            ContextMenu context = MonkeyPatch.findContextMenu(menu.ui.getStyleableNode(), "submenu");
            MonkeyPatch.fix(context);

            int move = context.getAnchorX() < menu.ui.getParentPopup().getAnchorX() ? 3 : -3;
            Node node = context.getStyleableNode();
            node.setOpacity(0);
            node.setTranslateX(move);

            // When a sub menu is requested to be opened while the context menu is open, the Y-axis
            // positions of the main and sub menus are misaligned, which is being corrected
            // sequentially.
            double initialY = menu.ui.getParentPopup().getY();
            Disposable stop = Viewtify.observing(menu.ui.getParentPopup().yProperty()).to(currentY -> {
                node.setTranslateY(-5 + currentY - initialY);
            });

            Anime.define().opacity(node, 1).moveX(node, -move).run(stop::dispose);
        });

        sub.accept(new UIContextMenu(text, menu.ui.getItems()));
    }

    /**
     * Declare menu separator.
     */
    public void separator() {
        menuProvider.add(new SeparatorMenuItem());
    }

    /**
     * Register menu.
     * 
     * @return Chainable API.
     */
    private <M extends MenuItem> M register(M menu) {
        menu.getProperties().put(id, null);
        menuProvider.add(menu);

        return menu;
    }
}