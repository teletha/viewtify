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

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.lang.StackWalker.Option;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import kiss.Decoder;
import kiss.Disposable;
import kiss.Encoder;
import kiss.I;
import kiss.Managed;
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
import transcript.Lang;
import viewtify.bind.Calculated;
import viewtify.bind.CalculatedList;
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

    /** The estimated application class. */
    private static Class applicatonEntryClass;

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
    private boolean tray = false;

    /** The configurable setting. */
    private Class<? extends View> trayView;

    /** The configurable setting. */
    private double width;

    /** The configurable setting. */
    private double height;

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
     * Configure the functionality of system tray.
     * 
     * @return
     */
    public Viewtify useSystemTray() {
        return useSystemTray(null);
    }

    /**
     * Configure the functionality of system tray.
     * 
     * @param trayView
     * @return
     */
    public Viewtify useSystemTray(Class<? extends View> trayView) {
        this.tray = true;
        this.trayView = trayView;
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
     * Configure application default language.
     * 
     * @param language A default language you want.
     * @return Chainable API.
     */
    public Viewtify language(Lang language) {
        if (language != null) {
            language.setDefault();
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
        activate(I.make(application));
    }

    /**
     * Activate the specified {@link Viewtify} application with {@link ActivationPolicy#Latest}.
     */
    public void activate(View application) {
        checkPolicy(application.getClass().getSimpleName());

        // load extensions in viewtify package
        I.load(Location.class);

        // load extensions in application package
        I.load(application.getClass());

        // build application stylesheet
        Path applicationStyles = CSSProcessor.pretty().formatTo(".preferences/application.css");

        // launch JavaFX UI
        Platform.startup(() -> {
            try {
                Stage stage = new Stage(stageStyle);
                stage.setWidth(width != 0 ? width : Screen.getPrimary().getBounds().getWidth() / 2);
                stage.setHeight(height != 0 ? height : Screen.getPrimary().getBounds().getHeight() / 2);

                // trace window size and position
                I.make(WindowLocator.class).restore().locate(application.getClass(), stage);

                View view = View.build(application);
                views.add(view);

                if (icon.length() != 0) {
                    stage.getIcons().add(loadImage(icon));
                    if (tray) buildSystemTray(view);
                }

                Scene scene = new Scene((Parent) view.ui());
                scene.getStylesheets().add(theme.url);
                scene.getStylesheets().add(applicationStyles.toUri().toURL().toExternalForm());

                // observe stylesheets
                observeStylesheet(scene.getStylesheets());
                observeStylesheet(scene.getRoot().getStylesheets());

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
     * Load the image resource which is located by the path.
     * 
     * @param path
     * @return
     */
    private java.awt.Image loadAWTImage(String path) {
        try {
            return ImageIO.read(loadResource(path));
        } catch (IOException e) {
            throw I.quiet(e);
        }
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
     * 
     * @param name A simple name of application.
     */
    private void checkPolicy(String applicationName) {
        if (policy != ActivationPolicy.Multiple) {
            // create application specified directory for lock
            Directory root = Locator.directory(".lock-" + applicationName.toLowerCase()).touch();

            root.lock()
                    .retryWhen(NullPointerException.class, e -> e.effect(() -> {
                        // another application is activated
                        if (policy == ActivationPolicy.Earliest) {
                            // make the window active
                            root.file("active").touch();

                            throw new Error("Application is running already.");
                        } else {
                            // close the window
                            root.file("close").touch();
                        }
                    }).delay(500, TimeUnit.MILLISECONDS).take(30))
                    .to();

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
    private void observeStylesheet(ObservableList<String> stylesheets) {
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
     * Create system tray.
     */
    private void buildSystemTray(View root) {
        TrayIcon tray = new TrayIcon(loadAWTImage(icon));
        tray.setImageAutoSize(true);
        tray.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                switch (e.getButton()) {
                case MouseEvent.BUTTON1: // left
                    // if the user double-clicks on the tray icon, show the main app stage.
                    inUI(root::show);
                    break;

                case MouseEvent.BUTTON2: // middle
                    break;

                case MouseEvent.BUTTON3: // right
                    break;
                }
            }
        });

        try {
            SystemTray system = SystemTray.getSystemTray();
            system.add(tray);
            onTerminating(() -> system.remove(tray));
        } catch (AWTException e) {
            throw I.quiet(e);
        }
    }

    /**
     * Gain application builder.
     * 
     * @return
     */
    public static final Viewtify application() {
        applicatonEntryClass = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).getCallerClass();
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
     * Reactivate the current application.
     */
    public static final void reactivate() {
        if (restartWithExewrap() || restartWithJava()) {
            deactivate();
        }
    }

    /**
     * Try to restart application in normal java environment.
     * 
     * @return
     */
    private static boolean restartWithJava() {
        ArrayList<String> commands = new ArrayList();

        // Java
        commands.add(System.getProperty("java.home") + java.io.File.separator + "bin" + java.io.File.separator + "java");
        commands.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());

        // classpath
        commands.add("-cp");
        commands.add(ManagementFactory.getRuntimeMXBean().getClassPath());

        // Class to be executed
        commands.add(applicatonEntryClass.getName());

        try {
            new ProcessBuilder(commands).start();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Try to restart application in exewrap environment.
     * 
     * @return
     */
    private static boolean restartWithExewrap() {
        String directory = System.getProperty("java.application.path");
        String name = System.getProperty("java.application.name");
        if (directory == null || name == null) {
            return false;
        }

        try {
            new ProcessBuilder().directory(new java.io.File(directory)).inheritIO().command(directory + "\\" + name).start();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Binding utility for {@link CalculatedList}.
     * 
     * @param list A {@link ObservableList} source to bind.
     * @return A binding builder.
     */
    public final static <E> CalculatedList<E> calculate(ObservableList<E> list) {
        return list instanceof CalculatedList ? (CalculatedList) list : new CalculatedList(list);
    }

    /**
     * Create {@link Calculated} from {@link Variable}.
     *
     * @param variable A {@link Variable}.
     * @return A new created {@link Calculated}.
     */
    public final static <E> Calculated<E> calculate(Variable<E> variable) {
        return calculate(variable, new InvalidationListener[0]);
    }

    /**
     * Create {@link Calculated} from {@link Variable}.
     * 
     * @param variable A {@link Variable}.
     * @return A new created {@link Calculated}.
     */
    public final static <E> Calculated<E> calculate(Variable<E> variable, InvalidationListener... listeners) {
        return new Calculated<E>(variable::get, null) {

            /** The binding disposer. */
            private final Disposable disposer = variable.observing().to(v -> {
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
     * Create {@link Calculated} from {@link Variable}.
     *
     * @param variables A list of {@link Variable}.
     * @return A new created {@link Calculated} list.
     */
    public final static <E> Calculated<E>[] calculate(Variable<E>... variables) {
        Calculated<E>[] calculations = new Calculated[variables.length];

        for (int i = 0; i < calculations.length; i++) {
            calculations[i] = calculate(variables[i]);
        }
        return calculations;
    }

    /**
     * Create {@link Calculated}.
     * 
     * @param o1 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <E> Calculated<E> calculate(Observable o1, WiseSupplier<E> calculation) {
        return calculate(o1, null, calculation);
    }

    /**
     * Create {@link Calculated}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <E> Calculated<E> calculate(Observable o1, Observable o2, WiseSupplier<E> calculation) {
        return calculate(o1, o2, null, calculation);
    }

    /**
     * Create {@link Calculated}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param o3 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <E> Calculated<E> calculate(Observable o1, Observable o2, Observable o3, WiseSupplier<E> calculation) {
        return calculate(o1, o2, o3, null, calculation);
    }

    /**
     * Create {@link Calculated}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param o3 A {@link Observable} target.
     * @param o4 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <E> Calculated<E> calculate(Observable o1, Observable o2, Observable o3, Observable o4, WiseSupplier<E> calculation) {
        return new Calculated<E>(calculation, null, o1, o2, o3, o4);
    }

    /**
     * Create {@link Calculated}.
     * 
     * @param o1 A {@link Observable} target.
     * @return A lazy evaluated calculation.
     */
    public static final <A> Calculated<A> calculate(A o1) {
        if (o1 instanceof ObservableValue) {
            return calculate((ObservableValue) o1);
        } else if (o1 instanceof Variable) {
            return calculate((Variable) o1);
        } else {
            return new Calculated(() -> o1, null);
        }
    }

    /**
     * Create {@link Calculated}.
     * 
     * @param o1 A {@link Observable} target.
     * @return A lazy evaluated calculation.
     */
    public static final <A> Calculated<A> calculate(ObservableValue<A> o1) {
        if (o1 instanceof Calculated) {
            return (Calculated<A>) o1;
        } else {
            return calculate(o1, () -> o1.getValue());
        }
    }

    /**
     * Create {@link Calculated}.
     * 
     * @param o1 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <A, R> Calculated<R> calculate(ObservableValue<A> o1, WiseFunction<A, R> calculator) {
        return calculate(o1, () -> calculator.apply(o1.getValue()));
    }

    /**
     * Create {@link Calculated}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <A, B, R> Calculated<R> calculate(ObservableValue<A> o1, ObservableValue<B> o2, WiseBiFunction<A, B, R> calculator) {
        return calculate(o1, o2, () -> calculator.apply(o1.getValue(), o2.getValue()));
    }

    /**
     * Create {@link Calculated}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param o3 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <A, B, C, R> Calculated<R> calculate(ObservableValue<A> o1, ObservableValue<B> o2, ObservableValue<C> o3, WiseTriFunction<A, B, C, R> calculator) {
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
     * Signal value changing.
     * 
     * @param values
     * @return
     */
    public static <T> Signal<T> observe(ObservableValue<T> value) {
        return observe(new ObservableValue[] {value});
    }

    /**
     * Signal value changing.
     * 
     * @param values
     * @return
     */
    public static <T> Signal<T> observe(ObservableValue<T>... values) {
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
    public static <T> Signal<T> observe(ObjectProperty<EventHandler<Event>> eventHandlerProperty, T eventValue) {
        return new Signal<>((observer, disposer) -> {
            EventHandler<Event> handler = e -> observer.accept(eventValue);
            eventHandlerProperty.set(handler);
            return disposer.add(() -> eventHandlerProperty.set(null));
        });
    }

    /**
     * Observe set change evnet.
     * 
     * @param set A set to observe its modification.
     * @return A modification stream.
     */
    public static <E> Signal<ObservableSet<E>> observe(ObservableSet<E> set) {
        return observeChange(set).mapTo(set);
    }

    /**
     * Observe list change evnet.
     * 
     * @param list A list to observe its modification.
     * @return A modification stream.
     */
    public static <E> Signal<ObservableList<E>> observe(ObservableList<E> list) {
        return observeChange(list).mapTo(list);
    }

    /**
     * Observe map change evnet.
     * 
     * @param map A map to observe its modification.
     * @return A modification stream.
     */
    public static <K, V> Signal<ObservableMap<K, V>> observe(ObservableMap<K, V> map) {
        return observeChange(map).mapTo(map);
    }

    /**
     * Observe set change evnet.
     * 
     * @param set A set to observe its modification.
     * @return A modification stream.
     */
    public static <E> Signal<ObservableSet<E>> observing(ObservableSet<E> set) {
        return observe(set).startWith(set);
    }

    /**
     * Observe list change evnet.
     * 
     * @param list A list to observe its modification.
     * @return A modification stream.
     */
    public static <E> Signal<ObservableList<E>> observing(ObservableList<E> list) {
        return observe(list).startWith(list);
    }

    /**
     * Observe map change evnet.
     * 
     * @param map A map to observe its modification.
     * @return A modification stream.
     */
    public static <K, V> Signal<ObservableMap<K, V>> observing(ObservableMap<K, V> map) {
        return observe(map).startWith(map);
    }

    /**
     * Signal value changing.
     * 
     * @param values
     * @return
     */
    public static <T> Signal<T> observing(ObservableValue<T> value) {
        return observe(value).startWith(value.getValue());
    }

    /**
     * Observe set change evnet.
     * 
     * @param map A set to observe its modification.
     * @return A modification event stream.
     */
    public static <E> Signal<SetChangeListener.Change<? extends E>> observeChange(ObservableSet<E> set) {
        return new Signal<>((observer, disposer) -> {
            SetChangeListener<E> listener = change -> observer.accept(change);

            set.addListener(listener);

            return disposer.add(() -> {
                set.removeListener(listener);
            });
        });
    }

    /**
     * Observe list change evnet.
     * 
     * @param list A set to observe its modification.
     * @return A modification event stream.
     */
    public static <E> Signal<ListChangeListener.Change<? extends E>> observeChange(ObservableList<E> list) {
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
     * Observe map change evnet.
     * 
     * @param map A map to observe its modification.
     * @return A modification event stream.
     */
    public static <K, V> Signal<MapChangeListener.Change<? extends K, ? extends V>> observeChange(ObservableMap<K, V> map) {
        return new Signal<>((observer, disposer) -> {
            MapChangeListener<K, V> listener = change -> observer.accept(change);

            map.addListener(listener);

            return disposer.add(() -> {
                map.removeListener(listener);
            });
        });
    }

    /**
     * Create the wrapped property of the specified {@link Variable}.
     * 
     * @param variable
     * @return
     */
    public static <T> Property<T> property(Variable<T> variable) {
        return new PropertyVariable(variable);
    }

    /**
     * Thin {@link Property} wrapper for {@link Variable}.
     */
    private static class PropertyVariable<V> implements Property<V> {

        /** The target. */
        private final Variable<V> variable;

        /** The listener cache. */
        private WeakHashMap<ChangeListener, Disposable> listeners;

        /**
         * @param variable
         */
        private PropertyVariable(Variable<V> variable) {
            this.variable = variable;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object getBean() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return "";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized void addListener(ChangeListener<? super V> listener) {
            Disposable disposer = variable.observe().to(v -> listener.changed(this, null, v));

            if (listeners == null) {
                listeners = new WeakHashMap();
            }
            listeners.put(listener, disposer);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized void removeListener(ChangeListener<? super V> listener) {
            if (listeners != null) {
                listeners.remove(listener);

                if (listeners.isEmpty()) {
                    listeners = null;
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V getValue() {
            return variable.v;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addListener(InvalidationListener listener) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void removeListener(InvalidationListener listener) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setValue(V value) {
            variable.set(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void bind(ObservableValue<? extends V> observable) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void unbind() {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isBound() {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void bindBidirectional(Property<V> other) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void unbindBidirectional(Property<V> other) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }
    }

    /**
     * @version 2017/11/25 23:59:20
     */
    @SuppressWarnings("serial")
    @Managed(value = Singleton.class)
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
            Signal<Boolean> windowState = Viewtify.observe(stage.maximizedProperty(), stage.iconifiedProperty());
            Signal<Number> windowLocation = Viewtify
                    .observe(stage.xProperty(), stage.yProperty(), stage.widthProperty(), stage.heightProperty());

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
