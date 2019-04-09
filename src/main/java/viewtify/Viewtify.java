/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import static java.util.concurrent.TimeUnit.*;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import kiss.Decoder;
import kiss.Disposable;
import kiss.Encoder;
import kiss.I;
import kiss.Manageable;
import kiss.Signal;
import kiss.Singleton;
import kiss.Storable;
import kiss.Variable;
import kiss.WiseBiFunction;
import kiss.WiseFunction;
import kiss.WiseSupplier;
import kiss.WiseTriFunction;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import viewtify.bind.Calculation;
import viewtify.bind.CalculationList;
import viewtify.ui.UserInterface;
import viewtify.ui.View;
import viewtify.util.UIThreadSafeList;

/**
 * @version 2018/09/16 16:21:29
 */
public final class Viewtify {

    /** The runtime info. */
    private static final boolean inTest;

    /** The dispose on exit. */
    public static final Disposable Terminator = Disposable.empty();

    /** The thread pool. */
    private static final ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactory() {

        /**
         * {@inheritDoc}
         */
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        }
    });

    /** Executor for UI Thread. */
    public static final Consumer<Runnable> UIThread = Viewtify::inUI;

    /** Executor for Worker Thread. */
    public static final Consumer<Runnable> WorkerThread = pool::submit;

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

        // For Test
        inTest = I.signal(new Error().getStackTrace())
                .take(e -> e.getClassName().startsWith("org.junit."))
                .take(1)
                .mapTo(true)
                .startWith(false)
                .to()
                .get();

        CSS.enhance();
    }

    /** All managed views. */
    private static List<View> views = new ArrayList();

    /** The configurable setting. */
    private ActivationPolicy policy = ActivationPolicy.Latest;

    /** The configurable setting. */
    private StageStyle stageStyle = StageStyle.DECORATED;

    /** The configurable setting. */
    private Theme theme = Theme.Light;

    /** The configurable setting. */
    private String icon = "";

    /** The configurable setting. */
    private double width;

    /** The configurable setting. */
    private double height;

    /** The application class. */
    private Class<? extends View> applicationClass;

    /**
     * Hide constructor.
     */
    private Viewtify() {
    }

    /**
     * Add termination action.
     * 
     * @param termination
     * @return
     */
    public Viewtify onTerminating(Runnable termination) {
        if (termination != null) {
            Terminator.add(termination::run);
        }
        return this;
    }

    /**
     * Configure application {@link ActivationPolicy}.
     * 
     * @param policy
     * @return
     */
    public Viewtify use(ActivationPolicy policy) {
        if (policy != null) {
            this.policy = policy;
        }
        return this;
    }

    /**
     * Configure {@link StageStyle}.
     * 
     * @param style
     * @return
     */
    public Viewtify use(StageStyle style) {
        if (style != null) {
            this.stageStyle = style;
        }
        return this;
    }

    /**
     * Configure application {@link Theme}.
     * 
     * @param theme
     * @return
     */
    public Viewtify use(Theme theme) {
        if (theme != null) {
            this.theme = theme;
        }
        return this;
    }

    /**
     * Configure application icon.
     * 
     * @return A relative path to icon.
     */
    public Viewtify icon(String pathToIcon) {
        if (pathToIcon != null) {
            this.icon = pathToIcon;
        }
        return this;
    }

    /**
     * Configure application initial size
     * 
     * @param width
     * @param height
     * @return
     */
    public Viewtify size(double width, double height) {
        if (0 < width) {
            this.width = width;
        }

        if (0 < height) {
            this.height = height;
        }
        return this;
    }

    /**
     * Activate the specified {@link Viewtify} application with {@link ActivationPolicy#Latest}.
     */
    public void activate(Class<? extends View> application) {
        this.applicationClass = application;

        checkPolicy();

        // load extensions in viewtify package
        I.load(Location.class);

        // load extensions in application package
        I.load(application);

        // build application stylesheet
        Path applicationStyles = CSSProcessor.pretty().formatTo(".preferences/application.css");

        // launch JavaFX UI
        Platform.startup(() -> {
            try {
                Stage stage = new Stage(stageStyle);
                stage.setWidth(width != 0 ? width : Screen.getPrimary().getBounds().getWidth() / 2);
                stage.setHeight(height != 0 ? height : Screen.getPrimary().getBounds().getHeight() / 2);
                stage.getIcons().add(loadImage(icon));

                // trace window size and position
                I.make(WindowLocator.class).restore().locate(application, stage);

                View view = View.build(application);
                views.add(view);

                Scene scene = new Scene((Parent) view.ui());
                scene.getStylesheets().add(theme.url);
                scene.getStylesheets().add(applicationStyles.toUri().toURL().toExternalForm());

                // observe stylesheets
                observe(scene.getStylesheets());
                observe(scene.getRoot().getStylesheets());

                stage.showingProperty().addListener((observable, oldValue, newValue) -> {
                    if (oldValue == true && newValue == false) {
                        deactivate();
                    }
                });

                stage.setScene(scene);
                stage.show();
            } catch (MalformedURLException e) {
                throw I.quiet(e);
            }
        });
    }

    /**
     * Load the image resource which is located by the path.
     * 
     * @param path
     * @return
     */
    private Image loadImage(String path) {
        return new Image(loadResource(path));
    }

    /**
     * Load the resource which is located by the path.
     * 
     * @param path
     * @return
     */
    private InputStream loadResource(String path) {
        File file = Locator.file(path);

        if (file.isPresent()) {
            return file.newInputStream();
        } else {
            return ClassLoader.getSystemResourceAsStream(path);
        }
    }

    /**
     * Check {@link ActivationPolicy}.
     */
    private void checkPolicy() {
        if (policy != ActivationPolicy.Multiple) {
            // create application specified directory for lock
            Directory root = Locator.directory(".lock").touch();

            root.lock(() -> {
                // another application is activated
                if (policy == ActivationPolicy.Earliest) {
                    // make the window active
                    root.file("active").touch();

                    throw new Error("Application is running already.");
                } else {
                    // close the window
                    root.file("close").touch();
                }
            });

            // observe lock directory for next application
            root.observe().map(WatchEvent::context).to(path -> {
                switch (path.name()) {
                case "active":
                    for (View view : views) {
                        view.show();
                    }
                    break;

                case "close":
                    deactivate();
                    break;
                }
            });
        }
    }

    /**
     * Observe stylesheet.
     * 
     * @param stylesheets
     */
    private void observe(ObservableList<String> stylesheets) {
        for (String stylesheet : stylesheets) {
            if (stylesheet.startsWith("file:/")) {
                File file = Locator.file(stylesheet.substring(6));

                if (file.isPresent()) {
                    file.observe().debounce(1, SECONDS).to(e -> {
                        AtomicInteger index = new AtomicInteger();

                        // remove
                        Viewtify.inUI(() -> {
                            index.set(stylesheets.indexOf(stylesheet));

                            if (index.get() != -1) {
                                stylesheets.remove(index.get());
                            }
                        });

                        // reload
                        Viewtify.inUI(() -> {
                            if (index.get() == -1) {
                                stylesheets.add(stylesheet);
                            } else {
                                stylesheets.add(index.get(), stylesheet);
                            }
                        });
                    });
                }
            }
        }
    }

    /**
     * Gain application builder.
     * 
     * @return
     */
    public static final Viewtify application() {
        return new Viewtify();
    }

    /**
     * Deactivate the current application.
     */
    public static final void deactivate() {
        Platform.exit();

        Terminator.dispose();
    }

    /**
     * Binding utility for {@link CalculationList}.
     * 
     * @param list A {@link ObservableList} source to bind.
     * @return A binding builder.
     */
    public final static <E> CalculationList<E> calculate(ObservableList<E> list) {
        return list instanceof CalculationList ? (CalculationList) list : new CalculationList(list);
    }

    /**
     * Create {@link Calculation} from {@link Variable}.
     *
     * @param variable A {@link Variable}.
     * @return A new created {@link Calculation}.
     */
    public final static <E> Calculation<E> calculate(Variable<E> variable) {
        return calculate(variable, new InvalidationListener[0]);
    }

    /**
     * Create {@link Calculation} from {@link Variable}.
     * 
     * @param variable A {@link Variable}.
     * @return A new created {@link Calculation}.
     */
    public final static <E> Calculation<E> calculate(Variable<E> variable, InvalidationListener... listeners) {
        return new Calculation<E>(variable::get, null) {

            /** The binding disposer. */
            private final Disposable disposer = variable.observeNow().to(v -> {
                invalidate();
                for (InvalidationListener listener : listeners) {
                    listener.invalidated(this);
                }
            });

            /**
             * {@inheritDoc}
             */
            @Override
            public void dispose() {
                super.dispose();

                disposer.dispose();
            }
        };
    }

    /**
     * Create {@link Calculation} from {@link Variable}.
     *
     * @param variables A list of {@link Variable}.
     * @return A new created {@link Calculation} list.
     */
    public final static <E> Calculation<E>[] calculate(Variable<E>... variables) {
        Calculation<E>[] calculations = new Calculation[variables.length];

        for (int i = 0; i < calculations.length; i++) {
            calculations[i] = calculate(variables[i]);
        }
        return calculations;
    }

    /**
     * Create {@link Calculation}.
     * 
     * @param o1 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <E> Calculation<E> calculate(Observable o1, WiseSupplier<E> calculation) {
        return calculate(o1, null, calculation);
    }

    /**
     * Create {@link Calculation}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <E> Calculation<E> calculate(Observable o1, Observable o2, WiseSupplier<E> calculation) {
        return calculate(o1, o2, null, calculation);
    }

    /**
     * Create {@link Calculation}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param o3 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <E> Calculation<E> calculate(Observable o1, Observable o2, Observable o3, WiseSupplier<E> calculation) {
        return calculate(o1, o2, o3, null, calculation);
    }

    /**
     * Create {@link Calculation}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param o3 A {@link Observable} target.
     * @param o4 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <E> Calculation<E> calculate(Observable o1, Observable o2, Observable o3, Observable o4, WiseSupplier<E> calculation) {
        return new Calculation<E>(calculation, null, o1, o2, o3, o4);
    }

    /**
     * Create {@link Calculation}.
     * 
     * @param o1 A {@link Observable} target.
     * @return A lazy evaluated calculation.
     */
    public static final <A> Calculation<A> calculate(A o1) {
        if (o1 instanceof ObservableValue) {
            return calculate((ObservableValue) o1);
        } else if (o1 instanceof Variable) {
            return calculate((Variable) o1);
        } else {
            return new Calculation(() -> o1, null);
        }
    }

    /**
     * Create {@link Calculation}.
     * 
     * @param o1 A {@link Observable} target.
     * @return A lazy evaluated calculation.
     */
    public static final <A> Calculation<A> calculate(ObservableValue<A> o1) {
        if (o1 instanceof Calculation) {
            return (Calculation<A>) o1;
        } else {
            return calculate(o1, () -> o1.getValue());
        }
    }

    /**
     * Create {@link Calculation}.
     * 
     * @param o1 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <A, R> Calculation<R> calculate(ObservableValue<A> o1, WiseFunction<A, R> calculator) {
        return calculate(o1, () -> calculator.apply(o1.getValue()));
    }

    /**
     * Create {@link Calculation}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <A, B, R> Calculation<R> calculate(ObservableValue<A> o1, ObservableValue<B> o2, WiseBiFunction<A, B, R> calculator) {
        return calculate(o1, o2, () -> calculator.apply(o1.getValue(), o2.getValue()));
    }

    /**
     * Create {@link Calculation}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param o3 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <A, B, C, R> Calculation<R> calculate(ObservableValue<A> o1, ObservableValue<B> o2, ObservableValue<C> o3, WiseTriFunction<A, B, C, R> calculator) {
        return calculate(o1, o2, o3, () -> calculator.apply(o1.getValue(), o2.getValue(), o3.getValue()));
    }

    /**
     * Execute task in pooled-background-worker thread.
     * 
     * @param process
     */
    public final static void inWorker(Runnable process) {
        pool.submit(process);
    }

    /**
     * Execute task in pooled-background-worker thread.
     * 
     * @param process
     */
    public final static void inWorker(Supplier<Disposable> process) {
        pool.submit(() -> {
            Terminator.add(process.get());
        });
    }

    /**
     * Execute task in UI thread.
     * 
     * @param process
     */
    public final static void inUI(Runnable process) {
        if (Platform.isFxApplicationThread() || inTest) {
            process.run();
        } else {
            Platform.runLater(process::run);
        }
    }

    /**
     * Execute task in UI thread.
     * 
     * @param process
     */
    public final static void inUI(Supplier<Disposable> process) {
        if (Platform.isFxApplicationThread()) {
            Terminator.add(process.get());
        } else {
            Platform.runLater(() -> {
                Terminator.add(process.get());
            });
        }
    }

    /**
     * Create {@link ObservableValue} which notify value change event in UI thread.
     * 
     * @param value
     * @return
     */
    public final static <E> ObservableValue<E> inUI(ObservableValue<E> value) {
        Objects.requireNonNull(value);

        return new ObservableValue<E>() {

            /** The wrapper manager. */
            private WeakHashMap wrappers;

            /**
             * {@inheritDoc}
             */
            @Override
            public synchronized void addListener(InvalidationListener listener) {
                // propagate change event on UI thread
                InvalidationListener wrapper = o -> {
                    inUI(() -> listener.invalidated(o));
                };
                value.addListener(wrapper);

                // manage the wrapped listener
                if (wrappers == null) {
                    wrappers = new WeakHashMap();
                }
                wrappers.put(listener, wrapper);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public synchronized void removeListener(InvalidationListener listener) {
                if (wrappers != null) {
                    InvalidationListener wrapper = (InvalidationListener) wrappers.remove(listener);

                    if (wrapper != null) {
                        value.removeListener(wrapper);

                        if (wrappers.isEmpty()) {
                            wrappers = null;
                        }
                    }
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public synchronized void addListener(ChangeListener<? super E> listener) {
                // propagate change event on UI thread
                ChangeListener<? super E> wrapper = (s, o, n) -> {
                    inUI(() -> listener.changed(s, o, n));
                };
                value.addListener(wrapper);

                // manage the wrapped listener
                if (wrappers == null) {
                    wrappers = new WeakHashMap();
                }
                wrappers.put(listener, wrapper);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public synchronized void removeListener(ChangeListener<? super E> listener) {
                if (wrappers != null) {
                    ChangeListener<? super E> wrapper = (ChangeListener<? super E>) wrappers.remove(listener);

                    if (wrapper != null) {
                        value.removeListener(wrapper);

                        if (wrappers.isEmpty()) {
                            wrappers = null;
                        }
                    }
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public E getValue() {
                return value.getValue();
            }
        };
    }

    /**
     * Create {@link ObservableList} which notify value change event in UI thread.
     * 
     * @param list A target {@link ObservableList}.
     * @return A created ui thread-safe {@link ObservableList}.
     */
    public static final <E> ObservableList<E> inUI(ObservableList<E> list) {
        return list instanceof UIThreadSafeList ? list : new UIThreadSafeList<>(list);
    }

    /**
     * Observe list change evnet.
     * 
     * @param list
     * @return
     */
    public static <E> Signal<Change<? extends E>> signal(ObservableList<E> list) {
        return new Signal<>((observer, disposer) -> {
            ListChangeListener<E> listener = change -> {
                while (change.next()) {
                    observer.accept(change);
                }
            };

            list.addListener(listener);

            return disposer.add(() -> {
                list.removeListener(listener);
            });
        });
    }

    /**
     * Signal value changing.
     * 
     * @param values
     * @return
     */
    public static <T> Signal<T> signal(ObservableValue<T>... values) {
        return new Signal<>((observer, disposer) -> {
            ChangeListener<T> listener = (s, o, n) -> {
                observer.accept(n);
            };

            for (ObservableValue<T> value : values) {
                value.addListener(listener);
            }

            return disposer.add(() -> {
                for (ObservableValue<T> value : values) {
                    value.removeListener(listener);
                }
            });
        });
    }

    /**
     * Build event signal.
     * 
     * @param eventHandlerProperty
     * @param eventValue
     * @return
     */
    public static <T> Signal<T> signal(ObjectProperty<EventHandler<Event>> eventHandlerProperty, T eventValue) {
        return new Signal<>((observer, disposer) -> {
            EventHandler<Event> handler = e -> observer.accept(eventValue);
            eventHandlerProperty.set(handler);
            return disposer.add(() -> eventHandlerProperty.set(null));
        });
    }

    /**
     * Signal value changing.
     * 
     * @param values
     * @return
     */
    public static <T> Signal<T> signalNow(ObservableValue<T> value) {
        return signal(value).startWith(value.getValue());
    }

    public static UserInterface wrap(Control ui, View view) {
        return new UserInterface(ui, view);
    }

    /**
     * Clip the specified node by its parent node.
     * 
     * @param node A target node to clip.
     */
    public static final void clip(Node node) {
        if (node != null) {
            clip(node, node.parentProperty());
        }
    }

    /**
     * Clip the specified node by the specified clipper.
     * 
     * @param node A target node to clip.
     * @param clipper A clip area.
     */
    public static final void clip(Node node, Parent clipper) {
        if (node != null && clipper != null) {
            clip(node, new SimpleObjectProperty(clipper));
        }
    }

    /**
     * Clip the specified node by the specified clipper.
     * 
     * @param node A target node to clip.
     * @param clipper A clip area.
     */
    public static final void clip(Node node, ObservableValue<? extends Parent> clipper) {
        if (node != null && clipper != null) {
            Calculation<Double> width = calculate(clipper).as(Region.class).flatDouble(Region::widthProperty);
            Calculation<Double> height = calculate(clipper).as(Region.class).flatDouble(Region::heightProperty);

            node.clipProperty().bind(calculate(width, height, () -> new Rectangle(width.get(), height.get())));
        }
    }

    /**
     * Build {@link ObservableList}.
     * 
     * @param list
     * @return
     */
    public static final <E> ObservableList<E> observe(List<E> list) {
        return new ObservableWrapList<E>(list);
    }

    /**
     * Build {@link ObservableList}.
     * 
     * @param list
     * @return
     */
    public static final <E> ObservableList<E> observe(List<E> list, Signal<E> add, Signal<E> remove) {
        return new ObservableWrapList(list, add, remove);
    }

    /**
     * @version 2018/04/12 12:59:14
     */
    private static class ObservableWrapList<E> extends ModifiableObservableListBase<E> implements ObservableList<E> {

        /** The delegation. */
        private final List<E> list;

        /**
         * @param list
         */
        private ObservableWrapList(List<E> list) {
            this(list, null, null);
        }

        /**
         * @param list
         * @param addLast
         * @param removeFirst
         */
        private ObservableWrapList(List<E> list, Signal<E> addLast, Signal<E> removeFirst) {
            this.list = Objects.requireNonNull(list);

            if (addLast != null) {
                addLast.to(v -> {
                    beginChange();
                    try {
                        int size = list.size();
                        nextAdd(size - 1, size);
                    } finally {
                        endChange();
                    }
                });
            }
            if (removeFirst != null) {
                removeFirst.to(v -> {
                    beginChange();
                    try {
                        nextRemove(0, v);
                    } finally {
                        endChange();
                    }
                });
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return list.size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public E get(int index) {
            return list.get(index);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void doAdd(int index, E element) {
            list.add(index, element);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected E doSet(int index, E element) {
            return list.set(index, element);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected E doRemove(int index) {
            return list.remove(index);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<E> iterator() {
            return list.iterator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListIterator<E> listIterator() {
            return list.listIterator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListIterator<E> listIterator(int index) {
            return list.listIterator(index);
        }
    }

    public static final <E> ObservableValue<E> observe(Variable<E> value) {
        return new ObservableVariable(value);
    }

    public static final Object[] observe(Object... values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof Variable) {
                values[i] = observe((Variable) values[i]);
            }
        }
        return values;
    }

    /**
     * @version 2018/04/27 12:51:37
     */
    private static class ObservableVariable<E> extends ObservableValueBase<E> {

        /** The actual value holder. */
        private final Variable<E> var;

        /**
         * @param var
         */
        private ObservableVariable(Variable<E> var) {
            this.var = Objects.requireNonNull(var);
            this.var.observe().to(this::fireValueChangedEvent);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public E getValue() {
            return var.get();
        }
    }

    /**
     * @version 2017/11/25 23:59:20
     */
    @SuppressWarnings("serial")
    @Manageable(lifestyle = Singleton.class)
    private static class WindowLocator extends HashMap<Class, Location> implements Storable<WindowLocator> {

        /** Magic Number for window state. */
        private static final int Normal = 0;

        /** Magic Number for window state. */
        private static final int Max = 1;

        /** Magic Number for window state. */
        private static final int Min = 2;

        /**
         * <p>
         * Apply window size and location setting.
         * </p>
         * 
         * @param stage A target to apply.
         */
        void locate(Class view, Stage stage) {
            Location location = get(view);

            if (location != null) {
                // restore window location
                if (location.w != 0) {
                    stage.setX(location.x);
                    stage.setY(location.y);
                    stage.setWidth(location.w);
                    stage.setHeight(location.h);
                }

                // restore window state
                switch (location.state) {
                case Max:
                    stage.setMaximized(true);
                    break;

                case Min:
                    stage.setIconified(true);
                    break;
                }
            }

            // observe window location and state
            Signal<Boolean> windowState = Viewtify.signal(stage.maximizedProperty(), stage.iconifiedProperty());
            Signal<Number> windowLocation = Viewtify
                    .signal(stage.xProperty(), stage.yProperty(), stage.widthProperty(), stage.heightProperty());

            windowState.merge(windowLocation.mapTo(true)).debounce(500, MILLISECONDS).to(() -> {
                Location store = computeIfAbsent(view, key -> new Location());

                if (stage.isMaximized()) {
                    store.state = Max;
                } else if (stage.isIconified()) {
                    store.state = Min;
                } else {
                    store.state = Normal;
                    store.x = stage.getX();
                    store.y = stage.getY();
                    store.w = stage.getWidth();
                    store.h = stage.getHeight();
                }
                store();
            });
        }
    }

    /**
     * @version 2017/11/25 23:31:06
     */
    static class Location implements Decoder<Location>, Encoder<Location> {

        /** The window location. */
        public double x;

        /** The window location. */
        public double y;

        /** The window location. */
        public double w;

        /** The window location. */
        public double h;

        /** The window location. */
        public int state;

        /**
         * {@inheritDoc}
         */
        @Override
        public String encode(Location value) {
            return value.x + " " + value.y + " " + value.w + " " + value.h + " " + value.state;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Location decode(String value) {
            String[] values = value.split(" ");

            Location locator = new Location();
            locator.x = Double.parseDouble(values[0]);
            locator.y = Double.parseDouble(values[1]);
            locator.w = Double.parseDouble(values[2]);
            locator.h = Double.parseDouble(values[3]);
            locator.state = Integer.parseInt(values[4]);

            return locator;
        }
    }
}
