/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.edit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import kiss.I;
import kiss.Signal;
import kiss.Signaling;
import kiss.model.Model;
import viewtify.Viewtify;
import viewtify.ui.UITableView;
import viewtify.ui.UserInterface;

/**
 * The editing state manager.
 */
public class Edito {

    /** The stop notifier of edit management. */
    private final Signaling<Boolean> stop = new Signaling();

    /** The edited user interface. */
    private final ObservableMap<UserInterface, Snapshot> edited = FXCollections.observableHashMap();

    /** The edited event. */
    public final Signal<Boolean> editing = Viewtify.observe(edited).map(x -> !x.isEmpty());

    /**
     * Manage edit state of the specified UI.
     * 
     * @param <V>
     * @param table
     */
    public <V extends Serializable> void manage(UITableView<V> table) {
        Viewtify.observing(table.items())
                .subscribeOn(Viewtify.UIThread) // need
                .scan(x -> I.pair(x, snapshot(x, table::items)), (x, v) -> I.pair(v, x.ⅱ))
                .skip(1)
                .takeUntil(stop.expose)
                .to(x -> edited(table, x.ⅰ, x.ⅱ));
    }

    private void edited(UserInterface ui, Object value, Snapshot snapshot) {
        if (snapshot.match(value)) {
            Object removed = edited.remove(ui);
            if (removed != null) {
                ui.unstyle("edited");
            }
        } else {
            Object old = edited.put(ui, snapshot);
            if (old == null) {
                ui.style("edited");
            }
        }
    }

    /**
     * Revert all changes.
     */
    public void revert() {
        for (Entry<UserInterface, Snapshot> entry : edited.entrySet()) {
            entry.getValue().revert();
        }
    }

    /**
     * Stop edit management.
     */
    public void stop() {
        revert();

        stop.accept(Boolean.TRUE);
    }

    /**
     * Create snapshot of {@link List}.
     * 
     * @param <V>
     * @param value
     * @param revert
     * @return
     */
    private static <V extends Serializable> Snapshot<List<V>> snapshot(List<V> value, Consumer<List<V>> revert) {
        List copy = new ArrayList();
        for (Serializable serializable : value) {
            copy.add(clone(serializable));
        }

        return new Snapshot<List<V>>(copy, revert) {
            @Override
            public boolean match(Object target) {
                if (target instanceof List list) {
                    if (list.size() == copy.size()) {
                        for (int i = 0; i < list.size(); i++) {
                            if (!Objects.equals(list.get(i), copy.get(i))) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Create the cloned object if avaliable.
     * 
     * @param <V>
     * @param value
     * @return
     */
    private static <V> V clone(V value) {
        // skip immutable object
        if (value == null || value instanceof String || value instanceof Number || value instanceof Boolean) {
            return value;
        }

        // =================================
        // Serializable based clone
        // =================================
        if (value instanceof Serializable serializable) {
            try {
                ByteArrayOutputStream outArray = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(outArray);
                out.writeObject(serializable);
                out.close();

                ByteArrayInputStream inArray = new ByteArrayInputStream(outArray.toByteArray());
                ObjectInputStream in = new ObjectInputStream(inArray);
                V cloned = (V) in.readObject();
                in.close();

                return cloned;
            } catch (Throwable e) {
                throw I.quiet(e);
            }
        }

        // =================================
        // Cloneable based clone
        // =================================
        if (value instanceof Cloneable) {
            // return (V) value.clone();
        }

        // =================================
        // Property based clone
        // =================================
        Model<V> model = Model.of(value);
        if (!model.properties().isEmpty()) {
            return I.json(I.write(value)).as(model.type);
        }

        throw I.quiet(new CloneNotSupportedException(value.getClass() + " is not cloneable."));
    }

    /**
     * Snapshot object.
     */
    protected static class Snapshot<T> {

        /** The snapshoted value. This is immutable. */
        private final T value;

        /** The revert action. */
        private final Consumer<T> revert;

        /**
         * Hide constructor.
         * 
         * @param value
         */
        protected Snapshot(T value, Consumer<T> revert) {
            this.value = value;
            this.revert = Objects.requireNonNull(revert);
        }

        /**
         * Verify if the target object and this snapshotted object have the same value.
         * 
         * @param target
         * @return
         */
        protected boolean match(Object target) {
            return Objects.equals(value, target);
        }

        /**
         * Revet to the stored value.
         */
        protected void revert() {
            revert.accept(value);
        }
    }
}
