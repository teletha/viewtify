/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Application;
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
import javafx.scene.control.Control;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import filer.Filer;
import kiss.Disposable;
import kiss.I;
import kiss.Signal;
import kiss.Variable;
import kiss.WiseBiFunction;
import kiss.WiseFunction;
import kiss.WiseSupplier;
import kiss.WiseTriFunction;
import stylist.Stylist;
import stylist.util.HierarchicalNaming;
import viewtify.bind.Calculation;
import viewtify.bind.CalculationList;
import viewtify.ui.UserInterface;
import viewtify.util.UIThreadSafeList;

/**
 * @version 2017/12/01 18:25:44
 */
public final class Viewtify {

    /** The runtime info. */
    private static final boolean inTest;

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

        // Error Handling
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
            e.printStackTrace();
        });

        Stylist.setNamingStrategy(new HierarchicalNaming("_"));

        // For Test
        inTest = I.signal(new Error().getStackTrace())
                .take(e -> e.getClassName().startsWith("org.junit."))
                .take(1)
                .mapTo(true)
                .startWith(false)
                .to()
                .get();
    }

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

    /** The root view class. */
    private static Class<? extends View> rootViewClass;

    /** The root view instance for cache. */
    private static View rootView;

    /** The singleton FX stage. */
    static Stage stage;

    /** The application initializer, plz call me on {@link Application#start(Stage)}. */
    static Consumer<Stage> initializer;

    /**
     * Activate the specified {@link Viewtify} application with {@link ActivationPolicy#Latest}.
     */
    public static final void activate(Class<? extends View> application) {
        activate(application, null);
    }

    /**
     * Activate the specified {@link Viewtify} application with {@link StageStyle#DECORATED}.
     */
    public static final void activate(Class<? extends View> application, ActivationPolicy policy) {
        activate(application, policy, null);
    }

    /**
     * Activate the specified {@link Viewtify} application.
     */
    public static final synchronized void activate(Class<? extends View> application, ActivationPolicy policy, StageStyle style) {
        if (rootViewClass != null) {
            return; // ignore duplicated call
        }

        rootViewClass = application;

        if (policy == null) {
            policy = ActivationPolicy.Latest;
        }

        // =====================================================================
        // Validate ActivationPolicy
        // =====================================================================
        if (policy != ActivationPolicy.Multiple) {
            try {
                // create application specified directory for lock
                Path root = Filer.locate(System.getProperty("java.io.tmpdir")).resolve(application.getName());

                if (Files.notExists(root)) {
                    Files.createDirectory(root);
                }
                Path file = root.resolve("lock");

                // try to retrieve lock to validate
                AsynchronousFileChannel channel = AsynchronousFileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                FileLock lock = channel.tryLock();

                if (lock == null) {
                    // another application is activated
                    if (policy == ActivationPolicy.Earliest) {
                        // make the window active
                        touch(root.resolve("active"));

                        throw new RuntimeException("Application is running already.");
                    } else {
                        // close the window
                        touch(root.resolve("close"));

                        // wait for shutdown previous application
                        channel.lock();
                    }
                }

                // observe lock directory for next application
                Filer.observe(root).map(WatchEvent::context).to(path -> {
                    switch (path.getFileName().toString()) {
                    case "active":
                        show();
                        break;

                    case "close":
                        deactivate();
                        break;
                    }
                });
            } catch (Exception e) {
                throw I.quiet(e);
            }
        }

        // load extensions in viewtify package
        I.load(ViewtifyApplication.Location.class, false);

        // load extensions in application package
        I.load(application, false);

        // build application initializer
        initializer = s -> {
            stage = s;
            stage.initStyle(style != null ? style : StageStyle.DECORATED);
        };

        // launch JavaFX UI
        Application.launch(ViewtifyApplication.class);
    }

    /**
     * <p>
     * Implements the same behaviour as the "touch" utility on Unix. It creates a new file with size
     * 0 or, if the file exists already, it is opened and closed without modifying it, but updating
     * the file date and time.
     * </p>
     * 
     * @param path
     */
    private static void touch(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.setLastModifiedTime(path, FileTime.fromMillis(System.currentTimeMillis()));
        } else {
            Files.createFile(path);
        }
    }

    /**
     * Deactivate the current application.
     */
    public static final void deactivate() {
        Terminator.dispose();

        Platform.exit();
    }

    /**
     * Force to show the current application on top of display.
     */
    public static final void show() {
        inUI(() -> {
            if (stage != null && !stage.isAlwaysOnTop()) {
                stage.setAlwaysOnTop(true);
                stage.setAlwaysOnTop(false);
            }
        });
    }

    /**
     * Force to blink the current application.
     */
    public static final void blink() {
        inUI(() -> {
            if (stage != null) {
                stage.toFront();
            }
        });
    }

    /**
     * Retrieve the root view.
     * 
     * @return
     */
    public static final <V extends View> V root() {
        if (rootView == null) {
            rootView = View.build(rootViewClass);
        }
        return (V) rootView;
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
     * Retrieve the {@link Screen} that {@link ViewtifyApplication} is displayed.
     * 
     * @return
     */
    public static Screen screen() {
        Window window = root().root().getScene().getWindow();

        for (Screen screen : Screen.getScreens()) {
            if (screen.getBounds().contains(window.getX(), window.getY())) {
                return screen;
            }
        }
        return Screen.getPrimary();
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
}
