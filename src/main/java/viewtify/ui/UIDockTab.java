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
import java.util.function.Function;

import com.panemu.tiwulfx.control.DetachableTab;

import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.StyleHelper;

public class UIDockTab extends DetachableTab
        implements StyleHelper<UIDockTab, DetachableTab>, LabelHelper<UIDockTab>, ContextMenuHelper<UIDockTab> {

    /** The parent view. */
    private final View parent;

    /** The actual view builder. */
    private final Function<UIDockTab, View> viewBuilder;

    private final AtomicBoolean loaded = new AtomicBoolean();

    /**
     * 
     */
    public UIDockTab(View parent, Function<UIDockTab, View> viewBuilder) {
        this.parent = Objects.requireNonNull(parent);
        this.viewBuilder = Objects.requireNonNull(viewBuilder);

        selectedProperty().addListener(change -> load());
        setDetachable(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DetachableTab ui() {
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
        }
    }
}
