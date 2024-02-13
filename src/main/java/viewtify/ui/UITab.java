/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import kiss.WiseFunction;
import viewtify.Viewtify;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.DisableHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.StyleHelper;

public class UITab extends Tab
        implements StyleHelper<UITab, Tab>, LabelHelper<UITab>, ContextMenuHelper<UITab>, DisableHelper<UITab>, AutoDisposable {

    /** The translatable text. */
    private static final Variable<String> CloseThisTab = I.translate("Close this tab");

    /** The translatable text. */
    private static final Variable<String> CloseMultipleTabs = I.translate("Close multiple tabs");

    /** The translatable text. */
    private static final Variable<String> CloseRightTabs = I.translate("Close tabs to the right");

    /** The translatable text. */
    private static final Variable<String> CloseLeftTabs = I.translate("Close tabs to the left");

    /** The translatable text. */
    private static final Variable<String> CloseOtherTabs = I.translate("Close all other tabs");

    /** The translatable text. */
    private static final Variable<String> CloseAllTabs = I.translate("Close all tabs");

    /** Cache to find tab node. */
    private static final WiseFunction<Node, Object> findTab;

    static {
        try {
            Field field = Class.forName("javafx.scene.control.skin.TabPaneSkin$TabHeaderSkin").getDeclaredField("tab");
            field.setAccessible(true);

            findTab = field::get;
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /** The parent view. */
    private final View parent;

    /** The actual contents builder. */
    private Function<UITab, View> viewBuilder;

    /** Tab state. */
    private final AtomicBoolean loaded = new AtomicBoolean();

    /** The actual contents. */
    View contents;

    /** The cached reference for the styleable node . */
    private WeakReference<Node> styleable;

    /**
     * 
     */
    public UITab(View parent) {
        this.parent = parent;

        selectedProperty().addListener(change -> load());
        tabPaneProperty().addListener(invalidaed -> styleable = null);

        context(menus -> {
            ObservableList<Tab> tabs = getTabPane().getTabs();

            menus.menu().text(CloseThisTab).action(this::close);
            if (tabs.size() > 1) {
                menus.menu(CloseMultipleTabs, sub -> {
                    int index = tabs.indexOf(this);

                    if (index + 1 != tabs.size()) {
                        sub.menu(CloseRightTabs).action(() -> {
                            I.signal(tabs).skip(index + 1).buffer().flatIterable(x -> x).to(x -> tabs.remove(x));
                        });
                    }
                    if (index != 0) {
                        sub.menu(CloseLeftTabs).action(() -> {
                            I.signal(tabs).take(index).buffer().flatIterable(x -> x).to(x -> tabs.remove(x));
                        });
                    }
                    if (tabs.size() > 1) sub.menu(CloseOtherTabs).action(() -> {
                        I.signal(tabs).skip(this).buffer().flatIterable(x -> x).to(x -> tabs.remove(x));
                    });
                    sub.menu(CloseAllTabs).action(() -> {
                        tabs.clear();
                    });
                });
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tab ui() {
        return this;
    }

    /**
     * Set contents.
     * 
     * @param contents
     * @return
     */
    public final UITab contents(Class<? extends View> contents) {
        return contents(I.make(contents));
    }

    /**
     * Set contents.
     * 
     * @param contents
     * @return
     */
    public final UITab contents(View contents) {
        return contentsLazy(tab -> contents);
    }

    /**
     * Set contents.
     * 
     * @param contents
     * @return
     */
    public final UITab contentsLazy(Class<? extends View> contents) {
        return contentsLazy(tab -> I.make(contents));
    }

    /**
     * Set contents.
     * 
     * @param contents
     * @return
     */
    public final UITab contentsLazy(Function<UITab, View> contents) {
        viewBuilder = contents;

        if (isSelected()) {
            load();
        }
        return this;
    }

    /**
     * Make this tab closable.
     * 
     * @param enable
     * @return
     */
    public final UITab closable(boolean enable) {
        setClosable(enable);
        return this;
    }

    /**
     * Close this tab.
     * 
     * @return
     */
    public final UITab close() {
        getTabPane().getTabs().remove(this);

        return this;
    }

    /**
     * Select this tab.
     * 
     * @return
     */
    public final UITab select() {
        getTabPane().getSelectionModel().select(this);

        return this;
    }

    /**
     * Test if this tab has been already loaded.
     * 
     * @return
     */
    public final boolean isLoaded() {
        return loaded.get();
    }

    /**
     * Load tab contents explicitly.
     */
    public final void load() {
        if (viewBuilder != null && loaded.getAndSet(true) == false) {
            contents = viewBuilder.apply(this);
            contents.initializeLazy(parent);
            setContent(contents.ui());
        }
    }

    /**
     * Event stream of the tab selection state.
     * 
     * @return
     */
    public final Signal<Boolean> isSelecting() {
        return Viewtify.observing(selectedProperty());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized Node getStyleableNode() {
        if (styleable != null) {
            return styleable.get();
        }

        // When searching for elements with Node#lookupAll, it searches even within
        // the contents of tabs, so it is very inefficient.
        // Therefore, we will search the child node one by one in order.
        ObservableList<Node> children = getTabPane().getChildrenUnmodifiable();
        StackPane headerArea = (StackPane) children.get(children.size() - 1);
        StackPane headerRegion = (StackPane) headerArea.getChildren().get(1);
        for (Node node : headerRegion.getChildren()) {
            if (findTab.apply(node) == this) {
                styleable = new WeakReference(node);
                break;
            }
        }
        return styleable == null ? null : styleable.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vandalize() {
        if (contents != null) contents.dispose();
        if (styleable != null) styleable.clear();

        contents = null;
        viewBuilder = null;
        styleable = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportAutomaticDispose() {
        return AutoDisposable.supportAutomaticDispose(this) && (contents == null || contents.supportAutomaticDispose());
    }
}