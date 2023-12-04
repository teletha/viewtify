/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import kiss.Extensible;
import kiss.I;
import kiss.Model;
import kiss.Property;
import kiss.Signal;
import kiss.Storable;
import kiss.Variable;
import kiss.WiseFunction;

public abstract class Preferences implements Storable<Preferences>, Extensible {

    /** The cache for preferences. */
    private static final Map<Class, PreferencesList> CACHE = new ConcurrentHashMap();

    /** The user defined name. */
    public final Preference<String> name = initialize("");

    /** The grouping container. */
    Storable container;

    /**
     * Hide constructor.
     */
    protected Preferences() {
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
     * {@inheritDoc}
     */
    @Override
    public Preferences store() {
        container.store();
        return this;
    }

    /**
     * Observe the value change events.
     * 
     * @return
     */
    public final Signal<Object> observe() {
        Model<Preferences> model = Model.of(this);
        return I.signal(model.properties()).flatMap(property -> model.observe(this, property));
    }

    /**
     * Reset all values to default.
     */
    public final void reset() {
        I.signal(Model.of(this).properties()).flatVariable(this::findBy).skipNull().to(Preference::reset);
    }

    /**
     * Find the actual value by property.
     * 
     * @param <V>
     * @param property
     * @return
     */
    private <V> Variable<Preference<V>> findBy(Property property) {
        Class clazz = getClass();

        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(property.name);
                if (Preference.class.isAssignableFrom(field.getType())) {
                    return Variable.of((Preference<V>) field.get(this));
                }
            } catch (Exception e) {
                // ignore
            }
            clazz = clazz.getSuperclass();
        }
        return Variable.empty();
    }

    /**
     * Search the user preference of the specified type.
     * 
     * @param <P>
     * @param type
     * @return
     */
    public static <P extends Preferences> P of(Class<P> type) {
        return of(type, null);
    }

    /**
     * Search the user preference of the specified type and name.
     * 
     * @param <P>
     * @param type
     * @return
     */
    public static <P extends Preferences> P of(Class<P> type, String name) {
        name = Objects.requireNonNullElse(name, "");

        PreferencesList<P> list = CACHE.computeIfAbsent(type, PreferencesList::new);
        for (P preferences : list) {
            if (preferences.name.is(name)) {
                return preferences;
            }
        }

        P named = I.make(type);
        named.name.set(name);
        named.container = list;
        named.auto();

        list.add(named);

        return named;
    }

    /**
     * Search all user preferences of the specified type.
     * 
     * @param <P>
     * @param type
     * @return
     */
    public static <P extends Preferences> List<P> all(Class<P> type) {
        return CACHE.computeIfAbsent(type, PreferencesList::new);
    }

    /**
     * Preference value.
     */
    public class Preference<V> extends Variable<V> {

        /** The default value. */
        private V defaultValue;

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

            require(v -> Objects.requireNonNullElse(v, this.defaultValue));
            this.defaultValue = defaultValue;
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
         * Synchronize value to the target sink.
         * 
         * @param target
         * @return
         */
        public Preference<V> syncTo(Consumer<V> target) {
            return syncTo(x -> x, target);
        }

        /**
         * Synchronize value to the target sink.
         * 
         * @param target
         * @return
         */
        public <R> Preference<V> syncTo(WiseFunction<V, R> converter, Consumer<R> target) {
            if (target != null) {
                observe().debounce(250, TimeUnit.MILLISECONDS).map(converter).diff().to(target);
            }
            return this;
        }

        /**
         * Reset to the default value.
         */
        public final void reset() {
            if (defaultValue != get()) {
                set(defaultValue);
            }
        }

        /**
         * Change the default value.
         * 
         * @param defaultValue
         */
        public final void setDefault(V defaultValue) {
            // update the current value by new default value if needed
            if (get() == this.defaultValue) {
                set(defaultValue);
            }

            // update the default value
            this.defaultValue = defaultValue;
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