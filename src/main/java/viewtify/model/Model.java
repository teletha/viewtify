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
     * Create {@link Preference} with the default value.
     * 
     * @param defaultValue The default value.
     * @return A created new {@link Preference}.
     */
    protected final <C extends Comparable<? super C>> ComparablePreference<C> initialize(C defaultValue) {
        return new ComparablePreference(defaultValue);
    }

    /**
     * Create {@link Preference} with the default value.
     * 
     * @param defaultValue The default value.
     * @return A created new {@link Preference}.
     */
    protected final IntPreference initialize(int defaultValue) {
        return new IntPreference(defaultValue);
    }

    /**
     * Create {@link Preference} with the default value.
     * 
     * @param defaultValue The default value.
     * @return A created new {@link Preference}.
     */
    protected final LongPreference initialize(long defaultValue) {
        return new LongPreference(defaultValue);
    }

    /**
     * Create {@link Preference} with the default value.
     * 
     * @param defaultValue The default value.
     * @return A created new {@link Preference}.
     */
    protected final DoublePreference initialize(double defaultValue) {
        return new DoublePreference(defaultValue);
    }

    /**
     * Create {@link Preference} with the default value.
     * 
     * @param <V>
     * @param defaultValue The default value. (non-null)
     * @return A created new {@link Preference}.
     */
    protected final <V> Preference<V> initialize(Variable<V> defaultValue) {
        Preference<V> preference = new Preference(defaultValue.v);

        return preference;
    }

    /**
     * Preference value.
     */
    public class Preference<V> extends Variable<V> {

        /** Requirements. */
        protected final List<Function<V, V>> requirements = new ArrayList();

        /**
         * Hide constructor.
         */
        protected Preference(V defaultValue) {
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

    /**
     * Preference value for primitive int.
     */
    public class IntPreference extends Preference<Integer> {

        /**
         * Hide constructor.
         */
        private IntPreference(int defaultValue) {
            super(defaultValue);
        }

        /**
         * Add requirement on this preference.
         * 
         * @param requirement
         * @return Chainable API.
         */
        @Override
        public IntPreference require(Function<Integer, Integer> requirement) {
            if (requirement != null) {
                requirements.add(requirement);
            }
            return this;
        }

        /**
         * This is the maximum value that can be set. If it is greater than this value, this maximum
         * value will be set instead.
         * 
         * @param max A maximum value.
         * @return Chainable API.
         */
        public IntPreference requireMax(int max) {
            requirements.add(v -> Math.min(v, max));
            return this;
        }

        /**
         * This is the minimum value that can be set. If it is less than this value, this minimum
         * value will be set instead.
         * 
         * @param min A minimum value.
         * @return Chainable API.
         */
        public IntPreference requireMin(int min) {
            requirements.add(v -> Math.max(v, min));
            return this;
        }

        /**
         * The range of possible values. If a value outside this range is set, the closest value to
         * this range will be set instead.
         * 
         * @param min A minimum value.
         * @param max A maximum value.
         * @return Chainable API.
         */
        public IntPreference requireBetween(int min, int max) {
            if (max < min) {
                throw new IllegalArgumentException("The minimum value must be less than the maximum value.");
            }

            requirements.add(v -> Math.max(Math.min(v, max), min));
            return this;
        }
    }

    /**
     * Preference value for primitive long.
     */
    public class LongPreference extends Preference<Long> {

        /**
         * Hide constructor.
         */
        private LongPreference(long defaultValue) {
            super(defaultValue);
        }

        /**
         * Add requirement on this preference.
         * 
         * @param requirement
         * @return Chainable API.
         */
        @Override
        public LongPreference require(Function<Long, Long> requirement) {
            if (requirement != null) {
                requirements.add(requirement);
            }
            return this;
        }

        /**
         * This is the maximum value that can be set. If it is greater than this value, this maximum
         * value will be set instead.
         * 
         * @param max A maximum value.
         * @return Chainable API.
         */
        public LongPreference requireMax(long max) {
            requirements.add(v -> Math.min(v, max));
            return this;
        }

        /**
         * This is the minimum value that can be set. If it is less than this value, this minimum
         * value will be set instead.
         * 
         * @param min A minimum value.
         * @return Chainable API.
         */
        public LongPreference requireMin(long min) {
            requirements.add(v -> Math.max(v, min));
            return this;
        }

        /**
         * The range of possible values. If a value outside this range is set, the closest value to
         * this range will be set instead.
         * 
         * @param min A minimum value.
         * @param max A maximum value.
         * @return Chainable API.
         */
        public LongPreference requireBetween(long min, long max) {
            if (max < min) {
                throw new IllegalArgumentException("The minimum value must be less than the maximum value.");
            }

            requirements.add(v -> Math.max(Math.min(v, max), min));
            return this;
        }
    }

    /**
     * Preference value for primitive double.
     */
    public class DoublePreference extends Preference<Double> {

        /**
         * Hide constructor.
         */
        private DoublePreference(double defaultValue) {
            super(defaultValue);
        }

        /**
         * Add requirement on this preference.
         * 
         * @param requirement
         * @return Chainable API.
         */
        @Override
        public DoublePreference require(Function<Double, Double> requirement) {
            if (requirement != null) {
                requirements.add(requirement);
            }
            return this;
        }

        /**
         * This is the maximum value that can be set. If it is greater than this value, this maximum
         * value will be set instead.
         * 
         * @param max A maximum value.
         * @return Chainable API.
         */
        public DoublePreference requireMax(double max) {
            requirements.add(v -> Math.min(v, max));
            return this;
        }

        /**
         * This is the minimum value that can be set. If it is less than this value, this minimum
         * value will be set instead.
         * 
         * @param min A minimum value.
         * @return Chainable API.
         */
        public DoublePreference requireMin(double min) {
            requirements.add(v -> Math.max(v, min));
            return this;
        }

        /**
         * The range of possible values. If a value outside this range is set, the closest value to
         * this range will be set instead.
         * 
         * @param min A minimum value.
         * @param max A maximum value.
         * @return Chainable API.
         */
        public DoublePreference requireBetween(double min, double max) {
            if (max < min) {
                throw new IllegalArgumentException("The minimum value must be less than the maximum value.");
            }

            requirements.add(v -> Math.max(Math.min(v, max), min));
            return this;
        }
    }

    /**
     * Preference value for {@link Comparable}.
     */
    public class ComparablePreference<V extends Comparable<? super V>> extends Preference<V> {

        /**
         * Hide constructor.
         */
        protected ComparablePreference(V defaultValue) {
            super(defaultValue);
        }

        /**
         * Add requirement on this preference.
         * 
         * @param requirement
         * @return Chainable API.
         */
        @Override
        public ComparablePreference<V> require(Function<V, V> requirement) {
            if (requirement != null) {
                requirements.add(requirement);
            }
            return this;
        }

        /**
         * This is the maximum value that can be set. If it is greater than this value, this maximum
         * value will be set instead.
         * 
         * @param max A maximum value.
         * @return Chainable API.
         */
        public ComparablePreference<V> requireMax(V max) {
            Objects.requireNonNull(max);
            requirements.add(v -> v.compareTo(max) < 0 ? v : max);
            return this;
        }

        /**
         * This is the minimum value that can be set. If it is less than this value, this minimum
         * value will be set instead.
         * 
         * @param min A minimum value.
         * @return Chainable API.
         */
        public ComparablePreference<V> requireMin(V min) {
            Objects.requireNonNull(min);
            requirements.add(v -> min.compareTo(v) < 0 ? v : min);
            return this;
        }

        /**
         * The range of possible values. If a value outside this range is set, the closest value to
         * this range will be set instead.
         * 
         * @param min A minimum value.
         * @param max A maximum value.
         * @return Chainable API.
         */
        public ComparablePreference<V> requireBetween(V min, V max) {
            Objects.requireNonNull(min);
            Objects.requireNonNull(max);
            if (min.compareTo(max) > 0) {
                throw new IllegalArgumentException("The minimum value must be less than the maximum value.");
            }

            requirements.add(v -> min.compareTo(v) > 0 ? min : max.compareTo(v) < 0 ? max : v);
            return this;
        }
    }
}
