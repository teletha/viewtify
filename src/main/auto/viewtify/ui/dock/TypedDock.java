package viewtify.ui.dock;

import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.Throwable;
import java.lang.UnsupportedOperationException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import kiss.WiseBiConsumer;
import viewtify.ui.UITab;
import viewtify.ui.dock.TypedDock;

/**
 * Generated model for {@link TypedDockModel}.
 * 
 * @see <a href="https://github.com/teletha/icymanipulator">Icy Manipulator (Code Generator)</a>
 */
public class TypedDock<T> extends TypedDockModel<T> {

     /** Determines if the execution environment is a Native Image of GraalVM. */
    private static final boolean NATIVE = "runtime".equals(System.getProperty("org.graalvm.nativeimage.imagecode"));

    /**
     * Deceive complier that the specified checked exception is unchecked exception.
     *
     * @param <T> A dummy type for {@link RuntimeException}.
     * @param throwable Any error.
     * @return A runtime error.
     * @throws T Dummy error to deceive compiler.
     */
    private static final <T extends Throwable> T quiet(Throwable throwable) throws T {
        throw (T) throwable;
    }

    /**
     * Create special property updater.
     *
     * @param name A target property name.
     * @return A special property updater.
     */
    private static final Field updater(String name)  {
        try {
            Field field = TypedDock.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Throwable e) {
            throw quiet(e);
        }
    }

    /**
     * Create fast property updater.
     *
     * @param field A target field.
     * @return A fast property updater.
     */
    private static final MethodHandle handler(Field field)  {
        try {
            return MethodHandles.lookup().unreflectSetter(field);
        } catch (Throwable e) {
            throw quiet(e);
        }
    }

    /** The final property updater. */
    private static final Field idField = updater("id");

    /** The fast final property updater. */
    private static final MethodHandle idUpdater = handler(idField);

    /** The final property updater. */
    private static final Field registrationField = updater("registration");

    /** The fast final property updater. */
    private static final MethodHandle registrationUpdater = handler(registrationField);

    /** The final property updater. */
    private static final Field locationField = updater("location");

    /** The fast final property updater. */
    private static final MethodHandle locationUpdater = handler(locationField);

    /** The final property updater. */
    private static final Field showOnInitialField = updater("showOnInitial");

    /** The fast final property updater. */
    private static final MethodHandle showOnInitialUpdater = handler(showOnInitialField);

    /** The exposed property. */
    public final String id;

    /** The exposed property. */
    public final WiseBiConsumer<UITab, T> registration;

    /** The exposed property. */
    public final UnaryOperator<DockRecommendedLocation> location;

    /** The exposed property. */
    public final List<T> showOnInitial;

    /**
     * HIDE CONSTRUCTOR
     */
    protected TypedDock() {
        this.id = null;
        this.registration = null;
        this.location = super.location();
        this.showOnInitial = super.showOnInitial();
    }

    /**
     * Return the id property.
     *
     * @return A value of id property.
     */
    @Override
    public final String id() {
        return this.id;
    }

    /**
     * Provide classic getter API.
     *
     * @return A value of id property.
     */
    @SuppressWarnings("unused")
    private final String getId() {
        return this.id;
    }

    /**
     * Provide classic setter API.
     *
     * @paran value A new value of id property to assign.
     */
    private final void setId(String value) {
        if (value == null) {
            throw new IllegalArgumentException("The id property requires non-null value.");
        }
        try {
            idUpdater.invoke(this, value);
        } catch (UnsupportedOperationException e) {
        } catch (Throwable e) {
            throw quiet(e);
        }
    }

    /**
     * Return the registration property.
     *
     * @return A value of registration property.
     */
    @Override
    public final WiseBiConsumer<UITab, T> registration() {
        return this.registration;
    }

    /**
     * Provide classic getter API.
     *
     * @return A value of registration property.
     */
    @SuppressWarnings("unused")
    private final WiseBiConsumer<UITab, T> getRegistration() {
        return this.registration;
    }

    /**
     * Provide classic setter API.
     *
     * @paran value A new value of registration property to assign.
     */
    private final void setRegistration(WiseBiConsumer<UITab, T> value) {
        if (value == null) {
            throw new IllegalArgumentException("The registration property requires non-null value.");
        }
        try {
            registrationUpdater.invoke(this, value);
        } catch (UnsupportedOperationException e) {
        } catch (Throwable e) {
            throw quiet(e);
        }
    }

    /**
     * Return the location property.
     *
     * @return A value of location property.
     */
    @Override
    public final UnaryOperator<viewtify.ui.dock.DockRecommendedLocation> location() {
        return this.location;
    }

    /**
     * Provide classic getter API.
     *
     * @return A value of location property.
     */
    @SuppressWarnings("unused")
    private final UnaryOperator<viewtify.ui.dock.DockRecommendedLocation> getLocation() {
        return this.location;
    }

    /**
     * Provide classic setter API.
     *
     * @paran value A new value of location property to assign.
     */
    private final void setLocation(UnaryOperator<viewtify.ui.dock.DockRecommendedLocation> value) {
        if (value == null) {
            value = super.location();
        }
        try {
            locationUpdater.invoke(this, value);
        } catch (UnsupportedOperationException e) {
        } catch (Throwable e) {
            throw quiet(e);
        }
    }

    /**
     * Set as the View to be displayed during the initial layout.
     *  
     *  @return
     */
    @Override
    public final List<T> showOnInitial() {
        return this.showOnInitial;
    }

    /**
     * Provide classic getter API.
     *
     * @return A value of showOnInitial property.
     */
    @SuppressWarnings("unused")
    private final List<T> getShowOnInitial() {
        return this.showOnInitial;
    }

    /**
     * Provide classic setter API.
     *
     * @paran value A new value of showOnInitial property to assign.
     */
    private final void setShowOnInitial(List<T> value) {
        if (value == null) {
            value = super.showOnInitial();
        }
        try {
            showOnInitialUpdater.invoke(this, value);
        } catch (UnsupportedOperationException e) {
        } catch (Throwable e) {
            throw quiet(e);
        }
    }

    /**
     * Show all property values.
     *
     * @return All property values.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TypedDock<T> [");
        builder.append("id=").append(id).append(", ");
        builder.append("registration=").append(registration).append(", ");
        builder.append("location=").append(location).append(", ");
        builder.append("showOnInitial=").append(showOnInitial).append("]");
        return builder.toString();
    }

    /**
     * Generates a hash code for a sequence of property values. The hash code is generated as if all the property values were placed into an array, and that array were hashed by calling Arrays.hashCode(Object[]). 
     *
     * @return A hash value of the sequence of property values.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, registration, location, showOnInitial);
    }

    /**
     * Returns true if the all properties are equal to each other and false otherwise. Consequently, if both properties are null, true is returned and if exactly one property is null, false is returned. Otherwise, equality is determined by using the equals method of the base model. 
     *
     * @return true if the all properties are equal to each other and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof TypedDock == false) {
            return false;
        }

        TypedDock<T> other = (TypedDock<T>) o;
        if (!Objects.equals(id, other.id)) return false;
        if (!Objects.equals(registration, other.registration)) return false;
        if (!Objects.equals(location, other.location)) return false;
        if (!Objects.equals(showOnInitial, other.showOnInitial)) return false;
        return true;
    }

    public static <T> Ìnstantiator<?, T> with() {
        return new Ìnstantiator();
    }

    /**
     * Namespace for {@link TypedDock}  builder methods.
     */
    public static class Ìnstantiator<Self extends TypedDock<T> & ÅssignableÅrbitrary<Self, T>, T> {

        /**
         * Create new {@link TypedDock} with the specified id property.
         * 
         * @return The next assignable model.
         */
        public ÅssignableRegistration<Self, T> id(String id) {
            Åssignable o = new Åssignable();
            o.id(id);
            return o;
        }
    }

    /**
     * Property assignment API.
     */
    public static interface ÅssignableId<Next, T> {

        /**
         * Assign id property.
         * 
         * @param value A new value to assign.
         * @return The next assignable model.
         */
        default Next id(String value) {
            ((TypedDock<T>) this).setId(value);
            return (Next) this;
        }
    }

    /**
     * Property assignment API.
     */
    public static interface ÅssignableRegistration<Next, T> {

        /**
         * Assign registration property.
         * 
         * @param value A new value to assign.
         * @return The next assignable model.
         */
        default Next registration(WiseBiConsumer<? extends UITab, T> value) {
            ((TypedDock<T>) this).setRegistration((kiss.WiseBiConsumer)value);
            return (Next) this;
        }
    }

    /**
     * Property assignment API.
     */
    public static interface ÅssignableÅrbitrary<Next extends TypedDock<T>, T> {

        /**
         * Assign location property.
         * 
         * @param value A new value to assign.
         * @return The next assignable model.
         */
        default Next location(UnaryOperator<? extends viewtify.ui.dock.DockRecommendedLocation> value) {
            ((TypedDock<T>) this).setLocation((java.util.function.UnaryOperator)value);
            return (Next) this;
        }

        /**
         * Assign showOnInitial property.
         * 
         * @param value A new value to assign.
         * @return The next assignable model.
         */
        default Next showOnInitial(List<T> value) {
            ((TypedDock<T>) this).setShowOnInitial((java.util.List)value);
            return (Next) this;
        }

        /**
         * Assign showOnInitial property.
         * 
         * @return The next assignable model.
         */
        default Next showOnInitial(T... values) {
            return showOnInitial(List.of(values));
        }
    }

    /**
     * Internal aggregated API.
     */
    protected static interface ÅssignableAll extends ÅssignableId, ÅssignableRegistration {
    }

    /**
     * Mutable Model.
     */
    private static final class Åssignable<T> extends TypedDock<T> implements ÅssignableAll, ÅssignableÅrbitrary {
    }

    /**
     * The identifier for properties.
     */
    static final class My {
        static final String Id = "id";
        static final String Registration = "registration";
        static final String Location = "location";
        static final String ShowOnInitial = "showOnInitial";
    }
}
