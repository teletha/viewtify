/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.edit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import kiss.I;
import kiss.Signal;
import kiss.Signaling;
import kiss.WiseConsumer;
import kiss.model.Model;
import viewtify.Viewtify;
import viewtify.ui.UserInterface;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.ValueHelper;

/**
 * The editing state manager.
 */
public class Edito {

    /** The root edit context. */
    public static final Edito Root = new Edito();

    /** The stop notifier of edit management. */
    private final Signaling<Boolean> stop = new Signaling();

    /** The edit notifier of edit management. */
    private final Signaling<Map> edit = new Signaling();

    /** The edited user interface. */
    private final Map<StyleHelper, Snapshot> edited = new ConcurrentHashMap();

    /** The edited event. */
    public final Signal<Boolean> editing = edit.expose.map(x -> !x.isEmpty());

    /**
     * Manage edit state of the specified UI.
     * 
     * @param <V>
     * @param ui
     */
    public <V extends StyleHelper & ValueHelper<V, X>, X> void manageValue(V ui, WiseConsumer<X> save) {
        ui.observing()
                .scan(value -> snapshot(value, x -> ui.value(x), save), Snapshot::update)
                .takeUntil(stop.expose)
                .to(snapshot -> edited(ui, snapshot));
    }

    /**
     * Manage edit state of the specified UI.
     * 
     * @param <V>
     * @param ui
     */
    public <V extends UserInterface & CollectableHelper<V, X>, X> void manageList(V ui, WiseConsumer<List<X>> save) {
        Viewtify.observing(ui.items())
                .scan(value -> snapshot(value, x -> ui.items(x), save), Snapshot::update)
                .takeUntil(stop.expose)
                .to(snapshot -> edited(ui, snapshot));
    }

    private void edited(StyleHelper ui, Snapshot snapshot) {
        if (snapshot.match()) {
            Object removed = edited.remove(ui);
            if (removed != null) {
                ui.unstyle("edited");
                edit.accept(edited);
            }
        } else {
            Object old = edited.put(ui, snapshot);
            if (old == null) {
                ui.style("edited");
                edit.accept(edited);
            }
        }
    }

    /**
     * Test whether there is any editing state or not.
     * 
     * @return
     */
    public boolean isEditing() {
        return !edited.isEmpty();
    }

    /**
     * Count the number of editing state.
     * 
     * @return
     */
    public int countEditing() {
        return edited.size();
    }

    /**
     * Save all changes.
     */
    public void save() {
        for (Entry<StyleHelper, Snapshot> entry : edited.entrySet()) {
            StyleHelper ui = entry.getKey();
            Snapshot snap = entry.getValue();

            snap.save();
            Object removed = edited.remove(ui);
            if (removed != null) {
                ui.unstyle("edited");
                edit.accept(edited);
            }
        }
    }

    /**
     * Revert all changes.
     */
    public void revert() {
        for (Entry<StyleHelper, Snapshot> entry : edited.entrySet()) {
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
     * Create snapshot of some value.
     * 
     * @param <V>
     * @param value
     * @param revert
     * @return
     */
    private static <V> Snapshot<V> snapshot(V value, WiseConsumer<V> revert, WiseConsumer<V> save) {
        return new Snapshot(value, revert, save);
    }

    /**
     * Create snapshot of {@link List}.
     * 
     * @param <V>
     * @param value
     * @param revert
     * @return
     */
    private static <V> Snapshot<List<V>> snapshot(List<V> value, WiseConsumer<List<V>> revert, WiseConsumer<List<V>> save) {
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
        private final WiseConsumer<T> save;

        /**
         * Hide constructor.
         * 
         * @param initial
         */
        protected Snapshot(T initial, WiseConsumer<T> revert, WiseConsumer<T> save) {
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
            // special equality check for empty string
            if (initial instanceof String text && text.isEmpty() && latest == null) {
                return true;
            } else if (latest instanceof String text && text.isEmpty() && initial == null) {
                return true;
            }
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
            save.accept(latest);
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