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

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import kiss.I;
import kiss.Signal;
import kiss.Signaling;
import kiss.WiseConsumer;
import kiss.WiseRunnable;
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
    public <V> void manage(UITableView<V> table, WiseRunnable save) {
        Viewtify.observing(table.items())
                .subscribeOn(Viewtify.UIThread) // need
                .scan(value -> snapshot(value, table::items, save), Snapshot::update)
                .skip(1)
                .takeUntil(stop.expose)
                .to(snapshot -> edited(table, snapshot));
    }

    private void edited(UserInterface ui, Snapshot snapshot) {
        if (snapshot.match()) {
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
     * Save all changes.
     */
    public void save() {
        for (Entry<UserInterface, Snapshot> entry : edited.entrySet()) {
            UserInterface ui = entry.getKey();
            Snapshot snap = entry.getValue();

            snap.save();
            Object removed = edited.remove(ui);
            if (removed != null) {
                ui.unstyle("edited");
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
    private static <V> Snapshot<List<V>> snapshot(List<V> value, WiseConsumer<List<V>> revert, WiseRunnable save) {
        return new Snapshot<List<V>>(value, revert, save) {
            @Override
            public boolean match() {
                if (latest.size() == initial.size()) {
                    for (int i = 0; i < latest.size(); i++) {
                        if (!Objects.equals(latest.get(i), initial.get(i))) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }

            @Override
            protected List<V> clone(List<V> value) {
                List copy = new ArrayList();
                for (Object v : value) {
                    copy.add(Edito.clone(v));
                }
                System.out.println("Create snapshot " + copy);
                return copy;
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
        protected T initial;

        /** The latest value. */
        protected T latest;

        /** The revert action. */
        private final WiseConsumer<T> revert;

        /** The save action. */
        private final WiseRunnable save;

        /**
         * Hide constructor.
         * 
         * @param initial
         */
        protected Snapshot(T initial, WiseConsumer<T> revert, WiseRunnable save) {
            this.initial = clone(initial);
            this.latest = initial;
            this.revert = Objects.requireNonNull(revert);
            this.save = Objects.requireNonNull(save);
        }

        /**
         * Update the latest value.
         * 
         * @param value
         * @return
         */
        protected Snapshot<T> update(T value) {
            this.latest = value;
            return this;
        }

        /**
         * Verify if the target object and this snapshotted object have the same value.
         * 
         * @return
         */
        protected boolean match() {
            return Objects.equals(initial, latest);
        }

        /**
         * Revet to the stored value.
         */
        protected void revert() {
            revert.accept(initial);
        }

        /**
         * Save data to the backend storage.
         */
        protected void save() {
            save.run();
            initial = clone(latest);
        }

        /**
         * Clone object.
         * 
         * @param value
         * @return
         */
        protected T clone(T value) {
            return Edito.clone(value);
        }
    }
}
