/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import static java.util.concurrent.TimeUnit.*;
import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.StackWalker.Option;
import java.lang.management.ManagementFactory;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.FloatExpression;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.binding.LongExpression;
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
import javafx.stage.WindowEvent;

import com.sun.javafx.application.PlatformImpl;

import kiss.Decoder;
import kiss.Disposable;
import kiss.Encoder;
import kiss.I;
import kiss.Managed;
import kiss.Signal;
import kiss.Singleton;
import kiss.Storable;
import kiss.Variable;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import viewtify.ui.UIWeb;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.User;
import viewtify.ui.helper.UserActionHelper;

/**
 * @version 2018/09/16 16:21:29
 */
public final class Viewtify {

    /** Command Repository */
    static final Map<Command, Deque<Runnable>> commands = new ConcurrentHashMap();

    private static final ShortcutManager shortcut = I.make(ShortcutManager.class);

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

    /** The application configurator. */
    private static final Viewtify viewtify = new Viewtify();

    /** All managed views. */
    private static final List<View> views = new ArrayList();

    /** The managed application stylesheets. */
    private static final CopyOnWriteArrayList<String> stylesheets = new CopyOnWriteArrayList();

    /** The estimated application class. */
    private static volatile Class applicationLaunchingClass;

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

    /**
     * Hide.
     */
    private Viewtify() {
    }

    /**
     * Gain application builder.
     * 
     * @return
     */
    public static synchronized Viewtify application() {
        if (applicationLaunchingClass == null) {
            applicationLaunchingClass = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        }
        return viewtify;
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
     * Configure application default language.
     * 
     * @param language A default language you want.
     * @return Chainable API.
     */
    public Viewtify language(String language) {
        if (language != null) {
            I.Lang.set(language);
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
     * Configure error logging.
     * 
     * @param errorHandler
     * @return
     */
    public Viewtify logging(BiConsumer<String, Throwable> errorHandler) {
        if (errorHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
                errorHandler.accept("Error in " + thread.getName() + " : " + error.getLocalizedMessage(), error);
            });
        }
        return this;
    }

    /**
     * Activate the specified application. You can call this method as many times as you like.
     * 
     * @param applicationClass The application {@link View} to activate.
     */
    public void activate(Class<? extends View> applicationClass) {
        activate(applicationClass, null);
    }

    /**
     * Activate the specified application. You can call this method as many times as you like.
     * 
     * @param applicationClass The application {@link View} to activate.
     * @param view Callback to be called after the application is activated.
     */
    public <V extends View> void activate(Class<? extends V> applicationClass, Consumer<V> view) {
        // Execute a configuration for an application that should be processed only once throughout
        // the entire life cycle of the application. If you run it more than once, nothing happens.
        initializeOnlyOnce(applicationClass);

        // launch application
        PlatformImpl.startup(() -> {
            V application = I.make(applicationClass);
            Stage stage = new Stage(stageStyle);
            stage.setWidth(width != 0 ? width : Screen.getPrimary().getBounds().getWidth() / 2);
            stage.setHeight(height != 0 ? height : Screen.getPrimary().getBounds().getHeight() / 2);

            Scene scene = new Scene((Parent) application.ui());
            manage(application.getClass().getName(), scene, stage, false);

            // root stage management
            views.add(application);
            stage.showingProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == true && newValue == false) {
                    views.remove(application);

                    // If the last window has been closed, deactivate this application.
                    if (views.isEmpty()) deactivate();
                }
            });

            stage.setScene(scene);
            stage.show();

            if (view != null) {
                view.accept(application);
            }
        }, false);
    }

    /**
     * Execute a configuration for an application that should be processed only once throughout the
     * entire life cycle of the application. If you run it more than once, nothing happens.
     * 
     * @param applicationClass An application class.
     */
    private synchronized void initializeOnlyOnce(Class applicationClass) {
        if (stylesheets.size() == 0) {
            String prefs = ".preferences for " + applicationClass.getSimpleName().toLowerCase();

            // How to handle simultaneous application startup
            checkActivationPolicy(prefs);

            // Separate settings for each application
            I.envy("PreferenceDirectory", prefs);

            // load extensions in viewtify package
            I.load(Location.class);

            // load extensions in application package
            I.load(applicationClass);

            // collect stylesheets for application
            stylesheets.add(Theme.locate("viewtify/ui.css"));
            stylesheets.add(viewtify.theme.location);
            stylesheets.add(Locator.file(CSSProcessor.pretty().formatTo(prefs + "/application.css")).externalForm());

            // observe stylesheet's modification
            I.signal(stylesheets)
                    .take(uri -> uri.startsWith("file:/"))
                    .map(uri -> Locator.file(uri.substring(6).replace("%20", " ")))
                    .scan(groupingBy(File::parent, mapping(File::name, toList())))
                    .last()
                    .flatIterable(m -> m.entrySet())
                    .flatMap(e -> e.getKey().observe(e.getValue()))
                    .debounce(1, SECONDS)
                    .map(change -> change.context().externalForm())
                    .to(this::reloadStylesheet);
        }
    }

    /**
     * Check {@link ActivationPolicy}.
     * 
     * @param prefs An application preference root directory.
     */
    private void checkActivationPolicy(String prefs) {
        if (policy != ActivationPolicy.Multiple) {
            // create application specified directory for lock
            Directory root = Locator.directory(prefs + "/lock").touch();

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
                    }).wait(500, TimeUnit.MILLISECONDS).take(50))
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
     * Reload the specified stylesheet.
     * 
     * @param changed A target shtylesheet's location.
     */
    private void reloadStylesheet(String changed) {
        for (View view : views) {
            ObservableList<String> stylesheets = view.ui().getScene().getStylesheets();
            int[] index = {-1};

            // Note that reapplying the style will only take effect if you delete the stylesheet
            // once and then add it again after a period of time.
            Viewtify.inUI(() -> {
                index[0] = stylesheets.indexOf(changed);
                if (index[0] != -1) stylesheets.remove(index[0]);
            });
            Viewtify.inUI(() -> {
                stylesheets.add(index[0], changed);
            });
        }
    }

    /**
     * Deactivate the current application.
     */
    public void deactivate() {
        Platform.exit();

        Terminator.dispose();
    }

    /**
     * Reactivate the current application.
     */
    public void reactivate() {
        if (restartWithExewrap() || restartWithJava()) {
            deactivate();
        }
    }

    /**
     * Try to restart application in normal java environment.
     * 
     * @return
     */
    private boolean restartWithJava() {
        ArrayList<String> commands = new ArrayList();

        // Java
        commands.add(System.getProperty("java.home") + java.io.File.separator + "bin" + java.io.File.separator + "java");
        commands.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());

        // classpath
        commands.add("-cp");
        commands.add(ManagementFactory.getRuntimeMXBean().getClassPath());

        // Class to be executed
        commands.add(applicationLaunchingClass.getName());

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
    private boolean restartWithExewrap() {
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
     * Load the image resource which is located by the path.
     * 
     * @param path
     * @return
     */
    private static Image loadImage(String path) {
        return new Image(loadResource(path));
    }

    /**
     * Load the resource which is located by the path.
     * 
     * @param path
     * @return
     */
    private static InputStream loadResource(String path) {
        File file = Locator.file(path);

        if (file.isPresent()) {
            return file.newInputStream();
        } else {
            return ClassLoader.getSystemResourceAsStream(path);
        }
    }

    /**
     * Generates a separate window with only {@link UIWeb}. If the application is not running, it
     * will automatically launch an anonymous application.
     */
    public static synchronized void browser(Consumer<UIWeb> browser) {
        application().activate(AnonyBrowser.class, view -> browser.accept(view.web));
    }

    /**
     * 
     */
    @SuppressWarnings("unused")
    private static class AnonyBrowser extends View {

        UIWeb web;

        class view extends ViewDSL {
            {
                $(web);
            }
        }

        @Override
        protected void initialize() {
        }
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
     * Manage as viewtify application window. Apply window size and location setting and track the
     * upcoming modification. Apply the application styles (design, icon etc) to the specified
     * window.
     * 
     * @param id An identical name of the window.
     * @param scene A target window to manage.
     */
    public static void manage(String id, Scene scene) {
        if (scene != null) {
            manage(id, scene, (Stage) scene.getWindow(), true);
        }
    }

    /**
     * Manage as viewtify application window. Apply window size and location setting and track the
     * upcoming modification. Apply the application styles (design, icon etc) to the specified
     * window.
     * 
     * @param id An identical name of the window. (required)
     * @param scene A target window to manage. (required)
     * @param untrackable
     */
    private static void manage(String id, Scene scene, Stage stage, boolean untrackable) {
        if (scene == null || stage == null) {
            return;
        }

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Require window identifier.");
        }

        // ================================================================
        // Application Styling System
        //
        // Applies all stylesheets collected at startup. Any changes made after startup
        // are automatically detected and reapplied at any time.
        // ================================================================
        scene.getStylesheets().addAll(stylesheets);

        // apply icon
        if (viewtify.icon.length() != 0) {
            stage.getIcons().add(loadImage(viewtify.icon));
        }

        // ================================================================
        // Keyboard Binding System
        //
        // Monitors the shortcut keys and invokes the corresponding commands.
        // Bug Fix: Prevent the KeyPress event from occurring continuously if you hold down a key.
        // ================================================================
        UserActionHelper<?> helper = () -> scene;
        helper.when(User.KeyPress).first().repeatWhen(e -> helper.when(User.KeyRelease)).to(shortcut::activate);

        // ================================================================
        // Window Tracking System
        //
        // Restores the position and size of the window from its previous state.
        // It constantly monitors the status and saves any changes.
        // ================================================================
        I.make(WindowLocator.class).locate(id, stage);
        if (untrackable) {
            stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
                WindowLocator locator = I.make(WindowLocator.class);
                if (locator.remove(id) != null) {
                    locator.store();
                }
            });
        }
    }

    /**
     * Signal value changing.
     * 
     * @param values
     * @return
     */
    public static Signal<Integer> observe(IntegerExpression value) {
        return observe(new ObservableValue[] {value});
    }

    /**
     * Signal value changing.
     * 
     * @param values
     * @return
     */
    public static Signal<Long> observe(LongExpression value) {
        return observe(new ObservableValue[] {value});
    }

    /**
     * Signal value changing.
     * 
     * @param values
     * @return
     */
    public static Signal<Float> observe(FloatExpression value) {
        return observe(new ObservableValue[] {value});
    }

    /**
     * Signal value changing.
     * 
     * @param values
     * @return
     */
    public static Signal<Double> observe(DoubleExpression value) {
        return observe(new ObservableValue[] {value});
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
    public static Signal<Integer> observing(IntegerExpression value) {
        return observe(value).startWith(value.getValue());
    }

    /**
     * Signal value changing.
     * 
     * @param values
     * @return
     */
    public static Signal<Long> observing(LongExpression value) {
        return observe(value).startWith(value.getValue());
    }

    /**
     * Signal value changing.
     * 
     * @param values
     * @return
     */
    public static Signal<Float> observing(FloatExpression value) {
        return observe(value).startWith(value.getValue());
    }

    /**
     * Signal value changing.
     * 
     * @param values
     * @return
     */
    public static Signal<Double> observing(DoubleExpression value) {
        return observe(value).startWith(value.getValue());
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
        private WeakHashMap<ChangeListener, Disposable> changes;

        /** The listener cache. */
        private WeakHashMap<InvalidationListener, Disposable> invalids;

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

            if (changes == null) {
                changes = new WeakHashMap();
            }
            changes.put(listener, disposer);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized void removeListener(ChangeListener<? super V> listener) {
            if (changes != null) {
                changes.remove(listener);

                if (changes.isEmpty()) {
                    changes = null;
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
            Disposable disposer = variable.observe().to(v -> listener.invalidated(this));

            if (invalids == null) {
                invalids = new WeakHashMap();
            }
            invalids.put(listener, disposer);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void removeListener(InvalidationListener listener) {
            if (invalids != null) {
                invalids.remove(listener);

                if (invalids.isEmpty()) {
                    invalids = null;
                }
            }
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
     * 
     */
    @SuppressWarnings("serial")
    @Managed(value = Singleton.class)
    private static class WindowLocator extends HashMap<String, Location> implements Storable<WindowLocator> {

        /** Magic Number for window state. */
        private static final int Normal = 0;

        /** Magic Number for window state. */
        private static final int Max = 1;

        /** Magic Number for window state. */
        private static final int Min = 2;

        /**
         * Hide
         */
        private WindowLocator() {
            restore();
        }

        /**
         * Apply window size and location setting.
         * 
         * @param stage A target to apply.
         */
        void locate(String id, Stage stage) {
            Location location = get(id);

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
                Location store = computeIfAbsent(id, key -> new Location());

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
     * 
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