package viewtify.ui.dock;

import java.lang.Class;
import java.lang.Override;
import java.lang.StringBuilder;
import java.lang.Throwable;
import java.lang.UnsupportedOperationException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.UnaryOperator;
import kiss.WiseConsumer;
import viewtify.ui.View;
import viewtify.ui.dock.Dock;

/**
 * Generated model for {@link DockModel}.
 * 
 * @see <a href="https://github.com/teletha/icymanipulator">Icy Manipulator (Code Generator)</a>
 */
public class Dock extends DockModel {

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
     * Create special method invoker.
     *
     * @param name A target method name.
     * @param parameterTypes A list of method parameter types.
     * @return A special method invoker.
     */
    private static final MethodHandle invoker(String name, Class... parameterTypes)  {
        try {
            Method method = viewtify.ui.dock.DockModel.class.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return MethodHandles.lookup().unreflect(method);
        } catch (Throwable e) {
            throw quiet(e);
        }
    }

    /** The overload or intercept method invoker. */
    private static final MethodHandle showOnInitial$1= invoker("showOnInitial");

    /**
     * Create special property updater.
     *
     * @param name A target property name.
     * @return A special property updater.
     */
    private static final MethodHandle updater(String name)  {
        try {
            Field field = Dock.class.getDeclaredField(name);
            field.setAccessible(true);
            return MethodHandles.lookup().unreflectSetter(field);
        } catch (Throwable e) {
            throw quiet(e);
        }
    }

    /** The final property updater. */
    private static final MethodHandle viewUpdater = updater("view");

    /** The final property updater. */
    private static final MethodHandle registrationUpdater = updater("registration");

    /** The final property updater. */
    private static final MethodHandle locationUpdater = updater("location");

    /** The final property updater. */
    private static final MethodHandle initialViewUpdater = updater("initialView");

    /** The exposed property. */
    public final Class<? extends View> view;

    /** The exposed property. */
    public final WiseConsumer<Dock> registration;

    /** The exposed property. */
    public final UnaryOperator<DockRecommendedLocation> location;

    /** The exposed property. */
    public final boolean initialView;

    /**
     * HIDE CONSTRUCTOR
     */
    protected Dock() {
        this.view = null;
        this.registration = super.registration();
        this.location = super.location();
        this.initialView = super.initialView();
    }

    /**
     * Set the View class to be displayed.
     *  
     *  @return
     */
    @Override
    public final Class<? extends View> view() {
        return this.view;
    }

    /**
     * Provide classic getter API.
     *
     * @return A value of view property.
     */
    @SuppressWarnings("unused")
    private final Class<? extends View> getView() {
        return this.view;
    }

    /**
     * Provide classic setter API.
     *
     * @paran value A new value of view property to assign.
     */
    private final void setView(Class<? extends View> value) {
        if (value == null) {
            throw new IllegalArgumentException("The view property requires non-null value.");
        }
        try {
            viewUpdater.invoke(this, value);
        } catch (UnsupportedOperationException e) {
        } catch (Throwable e) {
            throw quiet(e);
        }
    }

    /**
     * Sets the behaviour when actually displayed on the tab.
     *  
     *  @return
     */
    @Override
    public final WiseConsumer<Dock> registration() {
        return this.registration;
    }

    /**
     * Provide classic getter API.
     *
     * @return A value of registration property.
     */
    @SuppressWarnings("unused")
    private final WiseConsumer<Dock> getRegistration() {
        return this.registration;
    }

    /**
     * Provide classic setter API.
     *
     * @paran value A new value of registration property to assign.
     */
    private final void setRegistration(WiseConsumer<Dock> value) {
        if (value == null) {
            value = super.registration();
        }
        try {
            registrationUpdater.invoke(this, value);
        } catch (UnsupportedOperationException e) {
        } catch (Throwable e) {
            throw quiet(e);
        }
    }

    /**
     * Determines the area to be displayed when showing.
     *  
     *  @return
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
     * Sets whether or not to display this information during the initial layout.
     *  
     *  @return
     */
    @Override
    public final boolean initialView() {
        return this.initialView;
    }

    /**
     * Provide classic getter API.
     *
     * @return A value of initialView property.
     */
    @SuppressWarnings("unused")
    private final boolean getInitialView() {
        return this.initialView;
    }

    /**
     * Provide classic setter API.
     *
     * @paran value A new value of initialView property to assign.
     */
    private final void setInitialView(boolean value) {
        try {
            initialViewUpdater.invoke(this, value);
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
        StringBuilder builder = new StringBuilder("Dock [");
        builder.append("view=").append(view).append(", ");
        builder.append("registration=").append(registration).append(", ");
        builder.append("location=").append(location).append(", ");
        builder.append("initialView=").append(initialView).append("]");
        return builder.toString();
    }

    /**
     * Generates a hash code for a sequence of property values. The hash code is generated as if all the property values were placed into an array, and that array were hashed by calling Arrays.hashCode(Object[]). 
     *
     * @return A hash value of the sequence of property values.
     */
    @Override
    public int hashCode() {
        return Objects.hash(view, registration, location, initialView);
    }

    /**
     * Returns true if the all properties are equal to each other and false otherwise. Consequently, if both properties are null, true is returned and if exactly one property is null, false is returned. Otherwise, equality is determined by using the equals method of the base model. 
     *
     * @return true if the all properties are equal to each other and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Dock == false) {
            return false;
        }

        Dock other = (Dock) o;
        if (!Objects.equals(view, other.view)) return false;
        if (!Objects.equals(registration, other.registration)) return false;
        if (!Objects.equals(location, other.location)) return false;
        if (initialView != other.initialView) return false;
        return true;
    }

    /** The singleton builder. */
    public static final  Ìnstantiator<?> with = new Ìnstantiator();

    /**
     * Namespace for {@link Dock}  builder methods.
     */
    public static class Ìnstantiator<Self extends Dock & ÅssignableÅrbitrary<Self>> {

        /**
         * Create new {@link Dock} with the specified view property.
         * 
         * @return The next assignable model.
         */
        public Self view(Class<? extends View> view) {
            Åssignable o = new Åssignable();
            o.view(view);
            return (Self)o;
        }
    }

    /**
     * Property assignment API.
     */
    public static interface ÅssignableView<Next> {

        /**
         * Assign view property.
         * 
         * @param value A new value to assign.
         * @return The next assignable model.
         */
        default Next view(Class<? extends View> value) {
            ((Dock) this).setView((java.lang.Class)value);
            return (Next) this;
        }
    }

    /**
     * Property assignment API.
     */
    public static interface ÅssignableÅrbitrary<Next extends Dock> {

        /**
         * Assign registration property.
         * 
         * @param value A new value to assign.
         * @return The next assignable model.
         */
        default Next registration(WiseConsumer<? extends Dock> value) {
            ((Dock) this).setRegistration((kiss.WiseConsumer)value);
            return (Next) this;
        }

        /**
         * Assign location property.
         * 
         * @param value A new value to assign.
         * @return The next assignable model.
         */
        default Next location(UnaryOperator<? extends viewtify.ui.dock.DockRecommendedLocation> value) {
            ((Dock) this).setLocation((java.util.function.UnaryOperator)value);
            return (Next) this;
        }

        /**
         * Assign initialView property.
         * 
         * @param value A new value to assign.
         * @return The next assignable model.
         */
        default Next initialView(boolean value) {
            ((Dock) this).setInitialView(value);
            return (Next) this;
        }

        /**
         * Set as the View to be displayed during the initial layout.
         *  
         *  @return
         */
        default Next showOnInitial() {
            try {
                return initialView((boolean) showOnInitial$1.invoke(this));
            } catch (Throwable e) {
                throw quiet(e);
            }
        }
    }

    /**
     * Internal aggregated API.
     */
    protected static interface ÅssignableAll extends ÅssignableView {
    }

    /**
     * Mutable Model.
     */
    private static final class Åssignable extends Dock implements ÅssignableAll, ÅssignableÅrbitrary {
    }

    /**
     * The identifier for properties.
     */
    static final class My {
        static final String View = "view";
        static final String Registration = "registration";
        static final String Location = "location";
        static final String InitialView = "initialView";
    }
}
