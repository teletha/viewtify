/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.model;

import kiss.Signal;
import kiss.Variable;
import viewtify.Switch;

/**
 * @version 2018/03/13 11:19:15
 */
public abstract class Selectable<T> extends Sequential<T> {

    /** The event signal. */
    private final Switch<T> selects = new Switch();

    /** The event signal. */
    public transient final Signal<T> select = selects.expose;

    /** The event signal. */
    private final Switch<T> deselects = new Switch();

    /** The event signal. */
    public transient final Signal<T> deselect = deselects.expose;

    /** The selected item index. */
    public final Variable<Integer> selectionIndex = Variable.of(-1);

    /**
     * Current select item.
     * 
     * @return
     */
    public final T selection() {
        return get(selectionIndex.v);
    }

    /**
     * Current select index.
     * 
     * @return
     */
    public final int selectionIndex() {
        return selectionIndex.v;
    }

    /**
     * Current selection state.
     * 
     * @return
     */
    public final boolean hasSelection() {
        return 0 <= selectionIndex.v;
    }

    /**
     * <p>
     * Deselect item.
     * </p>
     */
    public final void deselect() {
        select(-1);
    }

    /**
     * Select by index.
     * 
     * @param index The item index to select.
     */
    public final T select(int index) {
        selectionIndex.set(index);
        return selection();
    }

    /**
     * Select by item.
     * 
     * @param item
     * @return
     */
    public final T select(T item) {
        return select(indexOf(item));
    }

    /**
     * <p>
     * Select next item.
     * </p>
     * 
     * @return
     */
    public final T selectNext() {
        return select(selectionIndex.v + 1);
    }

    /**
     * <p>
     * Select previous item.
     * </p>
     * 
     * @return
     */
    public final T selectPrevious() {
        return select(selectionIndex.v - 1);
    }

    /**
     * <p>
     * Select first item.
     * </p>
     * 
     * @return
     */
    public final T selectFirst() {
        return select(0);
    }

    /**
     * <p>
     * Select last item.
     * </p>
     * 
     * @return
     */
    public final T selectLast() {
        return select(size() - 1);
    }
}
