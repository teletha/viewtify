/*
 * Copyright (C) 2020 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import kiss.Managed;
import kiss.Singleton;
import kiss.Storable;
import kiss.Variable;

@Managed(Singleton.class)
public abstract class Model<Self extends Model> implements Storable<Self> {

    /**
     * Restore data.
     */
    protected Model() {
        restore().auto();
    }

    /**
     * Create {@link Preference} with the default value.
     * 
     * @param <V>
     * @param defaultValue The default value. (non-null)
     * @return A created new {@link Preference}.
     */
    protected final <V> Preference<V> initialize(V defaultValue) {
        return new Preference(defaultValue);
    }

    /**
     * Preference Value.
     */
    public class Preference<V> extends Variable<V> {

        /** Requirements. */
        private final List<Function<V, V>> requirements = new ArrayList();

        /**
         * Hide constructor.
         */
        private Preference(V defaultValue) {
            super(Objects.requireNonNull(defaultValue, "Be sure to specify a non-null default value."));

            intercept((o, n) -> {
                for (Function<V, V> req : requirements) {
                    n = req.apply(n);
                }
                return n;
            });

            require(v -> Objects.requireNonNullElse(v, defaultValue));
        }

        /**
         * Add requirement on this preference.
         * 
         * @param requirement
         * @return Chainable API.
         */
        public Preference<V> require(Function<V, V> requirement) {
            if (requirement != null) {
                requirements.add(requirement);
            }
            return this;
        }

        /**
         * Chainable value setter.
         * 
         * @param value A new value to set on this preference.
         * @return A base model.
         */
        public Self with(V value) {
            set(value);
            return (Self) Model.this;
        }
    }
}
