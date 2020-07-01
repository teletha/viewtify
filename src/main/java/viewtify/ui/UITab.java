/*
 * Copyright (C) 2020 viewtify Development Team
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
import kiss.WiseFunction;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.StyleHelper;

public class UITab extends Tab implements StyleHelper<UITab, Tab>, LabelHelper<UITab>, ContextMenuHelper<UITab> {

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

    /** The cached reference for the styleable node . */
    private WeakReference<Node> styleable;

    /**
     * 
     */
    public UITab(View parent) {
        this.parent = parent;

        selectedProperty().addListener(change -> load());
        tabPaneProperty().addListener(invalidaed -> styleable = null);
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
        return contents(tab -> contents);
    }

    /**
     * Set contents.
     * 
     * @param contents
     * @return
     */
    public final UITab contents(Function<UITab, View> contents) {
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
            View view = viewBuilder.apply(this);
            view.initializeLazy(parent);
            setContent(view.ui());
        }
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
}