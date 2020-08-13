/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.Parent;

import kiss.Signal;
import viewtify.Viewtify;

public final class LayoutAssistant implements InvalidationListener {

    /** The target node. */
    private final Parent node;

    /** The parent manager. maybe null */
    private final LayoutAssistant parent;

    /** Flag whether the node shoud layout on the next rendering phase or not. */
    private boolean shouldLayout;

    /** Flag whether the node is layoutable or not. */
    private boolean canLayout = true;

    /** The hideable nodes. */
    private Node[] invisible = new Node[0];

    /** The disable nodes. */
    private Node[] disable = new Node[0];

    /** The unmanageable nodes. */
    private Node[] unmanageable = new Node[0];

    /** The previous layout for relayout. */
    private Runnable previousLayout;

    /**
     * Create {@link LayoutAssistant}.
     * 
     * @param node
     */
    public LayoutAssistant(Parent node) {
        this(node, new LayoutAssistant(null, null, false), true);
    }

    /**
     * Create {@link LayoutAssistant}.
     * 
     * @param node
     * @param parent
     */
    private LayoutAssistant(Parent node, LayoutAssistant parent, boolean initial) {
        this.node = node;
        this.parent = parent;
        this.shouldLayout = initial;
    }

    /**
     * Request layouting ui in the next rendering phase.
     */
    public void requestLayout() {
        invalidated(null);
    }

    /**
     * Layout forcely if possible.
     */
    public void layoutForcely() {
        if (previousLayout != null) {
            Viewtify.inUI(() -> {
                shouldLayout = true;
                layout(previousLayout);
            });
        }
    }

    /**
     * Layout actually.
     * 
     * @param layout
     */
    public void layout(Runnable layout) {
        if (canLayout && (shouldLayout || parent.shouldLayout)) {
            layout.run();
            shouldLayout = false;
            previousLayout = layout;
        }
    }

    /**
     * Create sub {@link LayoutAssistant}.
     * 
     * @return
     */
    public LayoutAssistant sub() {
        return new LayoutAssistant(node, this, true);
    }

    /**
     * Register relayout action.
     * 
     * @param observables
     * @return Chainable API.
     */
    public LayoutAssistant layoutBy(Observable... observables) {
        for (Observable observable : observables) {
            observable.addListener(this);
        }
        return this;
    }

    /**
     * Register relayout action.
     * 
     * @param signals
     * @return Chainable API.
     */
    public LayoutAssistant layoutBy(Signal... signals) {
        for (Signal signal : signals) {
            signal.to(this::requestLayout);
        }
        return this;
    }

    /**
     * Register relayout condition.
     * 
     * @param timing A condition signal.
     * @return Chainable API.
     */
    public LayoutAssistant layoutWhile(Signal<Boolean> timing, Signal<Boolean>... timings) {
        timing.combineLatest(timings, (one, other) -> one && other).to(v -> {
            canLayout = v;

            for (Node node : invisible) {
                node.setVisible(canLayout);
            }
            for (Node node : disable) {
                node.setDisable(!canLayout);
            }
            for (Node node : unmanageable) {
                node.setManaged(!canLayout);
            }
        });
        return this;
    }

    /**
     * Specify the nodes to be hidden when cannot doing layout.
     */
    public LayoutAssistant invisible(Node... nodes) {
        if (nodes != null) {
            invisible = nodes;
        }
        return this;
    }

    /**
     * Specify the nodes to be disable when cannot doing layout.
     */
    public LayoutAssistant disable(Node... nodes) {
        if (nodes != null) {
            disable = nodes;
        }
        return this;
    }

    /**
     * Specify the nodes to be disable when cannot doing layout.
     */
    public LayoutAssistant unmanageable(Node... nodes) {
        if (nodes != null) {
            unmanageable = nodes;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invalidated(Observable observable) {
        if (shouldLayout == false) {
            shouldLayout = true;
            node.requestLayout();
        }
    }
}