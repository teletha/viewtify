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

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.scene.control.Tab;

import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.StyleHelper;

public class UITab extends Tab implements StyleHelper<UITab, Tab>, LabelHelper<UITab>, ContextMenuHelper<UITab> {

    /** The parent view. */
    private final View parent;

    /** The actual view builder. */
    private final Function<UITab, View> viewBuilder;

    /** The post-building event handler. */
    private final Consumer<UITab> viewBuilt;

    /** Tab state. */
    private final AtomicBoolean loaded = new AtomicBoolean();

    /**
     * 
     */
    public UITab(View parent, Function<UITab, View> viewBuilder, Consumer<UITab> viewBuilt) {
        this.parent = Objects.requireNonNull(parent);
        this.viewBuilder = Objects.requireNonNull(viewBuilder);
        this.viewBuilt = Objects.requireNonNullElse(viewBuilt, ui -> {
        });

        selectedProperty().addListener(change -> load());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tab ui() {
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
        if (loaded.getAndSet(true) == false) {
            View view = viewBuilder.apply(this);
            view.initializeLazy(parent);
            setContent(view.ui());
            viewBuilt.accept(this);
        }
    }
}
