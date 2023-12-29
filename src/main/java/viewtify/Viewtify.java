/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.awt.Rectangle;
import java.awt.SplashScreen;
import java.lang.StackWalker.Option;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.FloatExpression;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.binding.LongExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import com.sun.javafx.application.PlatformImpl;

import kiss.Decoder;
import kiss.Disposable;
import kiss.Encoder;
import kiss.I;
import kiss.JUL;
import kiss.Managed;
import kiss.Signal;
import kiss.Singleton;
import kiss.Storable;
import kiss.Variable;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import stylist.design.DesignScheme;
import viewtify.keys.ShortcutManager;
import viewtify.preference.Preferences;
import viewtify.ui.UIWeb;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.anime.Anime;
import viewtify.ui.helper.User;
import viewtify.ui.helper.UserActionHelper;
import viewtify.ui.toast.Toast;
import viewtify.ui.view.AppearanceSetting;
import viewtify.update.Blueprint;
import viewtify.update.Update;
import viewtify.update.UpdateSetting;

public final class Viewtify {

    /** The runtime info. */
    private static final boolean inTest;

    /** The status of toolkit. */
    private static boolean toolkitInitialized;

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
    public static final Consumer<Runnable> WorkerThread = Viewtify::inWorker;

    /** The directory of user's preference. */
    public static final Variable<Directory> UserPreference = Variable.empty();

    static {
        JUL.replace();

        // I really want to enable anti-aliasing all the time, but I don't use it because the text
        // is unusually dirty in dark mode.
        //
        // configure text anti-aliasing
        // System.setProperty("prism.lcdtext", "false");
        // System.setProperty("prism.text", "t2k");
        // System.setProperty("prism.subpixeltext", "on native");

        // For Test
        inTest = I.signal(new Error().getStackTrace())
                .take(e -> e.getClassName().startsWith("org.junit."))
                .take(1)
                .mapTo(true)
                .startWith(false)
                .to()
                .get();

        CSS.enhance();

        // user settings are reloadable
        UserPreference.observe().to(() -> {
            Preferences.of(AppearanceSetting.class);
        });

        // automatic gc
        I.schedule(5, 30, TimeUnit.MINUTES, true).to(System::gc);
    }

    /** The application configurator. */
    private static final Viewtify viewtify = new Viewtify();

    /** All managed views. */
    private static final List<View> views = new ArrayList();

    /** The managed application stylesheets. */
    private static final CopyOnWriteArrayList<String> stylesheets = new CopyOnWriteArrayList();

    /** Queue to store UI actions as they are requested before launching the UI. */
    private static Queue<Runnable> waitingActions = new ConcurrentLinkedQueue();

    /** The estimated application class. */
    private static volatile Class applicationLaunchingClass;

    /** The main stage. */
    private static volatile Stage mainStage;

    /** The configurable setting. */
    private ActivationPolicy activationPolicy = ActivationPolicy.Latest;

    /** The configurable setting. */
    private String updateArchive;

    /** The configurable setting. */
    private StageStyle stageStyle = StageStyle.DECORATED;

    /** The configurable setting. */
    private Class<? extends DesignScheme> scheme;

    /** The configurable setting. */
    private BiConsumer<Stage, Scene> initializer;

    /** The configurable setting. */
    private Variable<Class<? extends View>> opener = Variable.empty();

    /** The configurable setting. */
    private BooleanSupplier closer;

    /** The configurable setting. */
    private String icon = "";

    /** The configurable setting. */
    private String title;

    /** The configurable setting. */
    private String version = "1.0.0";

    /** We must continue to hold the lock object to avoid releasing by GC. */
    @SuppressWarnings("unused")
    private FileLock lock;

    /**
     * Hide.
     */
    private Viewtify() {
    }

    /**
     * Configures the GUI to start in headless mode. This setting cannot be reversed. It has no
     * effect if the GUI has already been started. It is recommended to call this method at a
     * location right after the program is started. It is also possible to set the environment
     * variable "javafx.headless" to true.
     */
    public static void inHeadless() {
        I.env("javafx.headless", true);
    }

    /**
     * Test headless mode.
     * 
     * @return
     */
    private static boolean isHeadless() {
        return I.env("javafx.headless", false);
    }

    /**
     * Check headless mode.
     */
    private static void checkHeadlessMode() {
        if (isHeadless()) {
            // ====================================
            // Support for JavaFX
            // ====================================
            // Disables hardware accelerated rendering and switches to software rendering.
            System.setProperty("prism.order", "sw");

            // See com.sun.glass.ui.PlatformFactory#getPlatformFactory()
            // Set com.sun.glass.ui.monocle.MonoclePlatformFactory as platform factory.
            System.setProperty("glass.platform", "Monocle");

            // See com.sun.glass.ui.Platform#determinePlatform()
            // Set Headless as monocle internal platform.
            System.setProperty("monocle.platform", "Headless");

            // ====================================
            // Support for AWT
            // ====================================
            System.setProperty("java.awt.headless", "true");
        }
    }

    /**
     * Gain application builder.
     * 
     * @return
     */
    public static synchronized Viewtify application() {
        if (applicationLaunchingClass == null) {
            applicationLaunchingClass = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)
                    .walk(stacks -> stacks.filter(stack -> stack.getMethodName().equals("main")).findAny())
                    .get()
                    .getDeclaringClass();
        }
        return viewtify;
    }

    /**
     * Configure the initialize phase.
     * 
     * @param initializer
     * @return
     */
    public Viewtify onInitialize(BiConsumer<Stage, Scene> initializer) {
        this.initializer = initializer;
        return this;
    }

    /**
     * Configure the opening request.
     * 
     * @param opener
     * @return
     */
    public Viewtify onOpening(Class<? extends View> opener) {
        return onOpening(Variable.of(opener));
    }

    /**
     * Configure the closing request.
     * 
     * @param opener
     * @return
     */
    public Viewtify onOpening(Variable<Class<? extends View>> opener) {
        this.opener = opener;
        return this;
    }

    /**
     * Configure the closing request.
     * 
     * @param closer
     * @return
     */
    public Viewtify onClosing(BooleanSupplier closer) {
        this.closer = closer;
        return this;
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
            this.activationPolicy = policy;
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
            Preferences.of(AppearanceSetting.class).theme.setDefault(theme);
        }
        return this;
    }

    /**
     * Configure application {@link ThemeType}.
     * 
     * @param themeType
     * @return
     */
    public Viewtify use(ThemeType themeType) {
        if (themeType != null) {
            Preferences.of(AppearanceSetting.class).themeType.setDefault(themeType);
        }
        return this;
    }

    /**
     * Configure application {@link Font}.
     * 
     * @param font
     * @return
     */
    public Viewtify use(Font font) {
        if (font != null) {
            AppearanceSetting appearance = Preferences.of(AppearanceSetting.class);
            appearance.font.setDefault(font.getFamily());
            appearance.fontSize.setDefault((int) font.getSize());
        }
        return this;
    }

    /**
     * Configure application {@link Theme}.
     * 
     * @param scheme
     * @return
     */
    public Viewtify design(Class<? extends DesignScheme> scheme) {
        if (scheme != null) {
            this.scheme = scheme;
        }
        return this;
    }

    /**
     * Get application metadata.
     * 
     * @return
     */
    public String icon() {
        return icon;
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
     * Configure application title.
     * 
     * @return A title of this application.
     */
    public Viewtify title(String title) {
        if (title != null) {
            this.title = title;
        }
        return this;
    }

    /**
     * Configure application update strategy.
     * 
     * @return Chainable API.
     */
    public Viewtify update(String archive) {
        this.updateArchive = archive;

        return this;
    }

    /**
     * Get applicaiton metadata.
     * 
     * @return
     */
    public String version() {
        return version;
    }

    /**
     * Configure application metadata.
     * 
     * @return Chainable API.
     */
    public Viewtify version(String version) {
        this.version = version;

        return this;
    }

    /**
     * Configure error logging.
     * 
     * @param errorHandler
     * @return
     */
    public Viewtify error(BiConsumer<String, Throwable> errorHandler) {
        if (errorHandler == null) {
            Thread.setDefaultUncaughtExceptionHandler(null);
        } else {
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
        activate(I.make(applicationClass));
    }

    /**
     * Activate the specified application. You can call this method as many times as you like.
     * 
     * @param application The application {@link View} to activate.
     */
    public void activate(View application) {
        // Execute a configuration for an application that should be processed only once throughout
        // the entire life cycle of the application. If you run it more than once, nothing happens.
        initializeOnlyOnce(application.getClass());

        // launch application
        PlatformImpl.startup(() -> {
            toolkitInitialized = true;

            activate(application, opener.isPresent());
        }, false);
    }

    /**
     * Activate the specified application.
     * 
     * @param application The application {@link View} to activate.
     */
    private void activate(View application, boolean isOperner) {
        // root stage management
        views.add(application);
        mainStage = new Stage();
        mainStage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                views.remove(application);

                // If the last window has been closed, deactivate this application.
                if (views.isEmpty()) deactivate();
            }
        });

        View mainView = isOperner ? I.make(opener.v) : application;
        Scene mainScene = new Scene((Parent) mainView.ui());

        if (isOperner) {
            // ================================================
            // Pre Application
            // ================================================
            mainScene.setFill(null);
            mainStage.initStyle(StageStyle.TRANSPARENT);
            mainStage.setOnHidden(e -> {
                if (!Terminator.isDisposed()) {
                    SplashScreen splash = SplashScreen.getSplashScreen();
                    if (splash != null && splash.isVisible()) {
                        splash.close();
                    }

                    opener = null;
                    activate(application, false);
                }
            });
        } else {
            // ================================================
            // Main Application
            // ================================================
            mainStage.initStyle(stageStyle);

            // check update
            UpdateSetting updater = Preferences.of(UpdateSetting.class);
            if (updater.checkOnStartup.is(true) && Update.isAvailable(updateArchive)) {
                I.schedule(5, TimeUnit.SECONDS).to(() -> {
                    Toast.show(I
                            .translate(Terminator, "A newer version is available. Would you like to update? [Update](0)  [Not now](1)"), Update::apply, I.NoOP);
                });
            }
        }

        if (closer != null) {
            mainStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
                if (!closer.getAsBoolean()) {
                    e.consume();
                }
            });
        }

        // window management
        manage(mainView.getClass().getName(), mainScene, mainStage, isOperner);

        // the user initializer must be executed at last
        if (initializer != null) initializer.accept(mainStage, mainScene);

        // show window actually
        mainStage.setScene(mainScene);
        mainStage.show();

        if (!isHeadless()) {
            mainStage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> {
                // release resources for splash screen
                SplashScreen screen = SplashScreen.getSplashScreen();
                if (screen != null) screen.close();
            });
        }

        // process the unexecuted UI action
        if (waitingActions != null) {
            waitingActions.forEach(Runnable::run);
            waitingActions = null;
        }
    }

    /**
     * Execute a configuration for an application that should be processed only once throughout the
     * entire life cycle of the application. If you run it more than once, nothing happens.
     * 
     * @param applicationClass An application class.
     */
    private synchronized void initializeOnlyOnce(Class applicationClass) {
        if (stylesheets.size() == 0) {
            // setting for headless mode
            checkHeadlessMode();

            // Configure the directory of application's preference
            String prefs = ".preferences for " + applicationClass.getSimpleName().toLowerCase();
            I.env("PreferenceDirectory", prefs);

            // Configure the directory of user's preference
            if (UserPreference.isAbsent()) UserPreference.set(Locator.directory(prefs));

            // Compute application title
            if (title == null) title(applicationClass.getSimpleName());

            // Specify JavaFX cache directory
            System.setProperty("javafx.cachedir", prefs + "/native");

            // How to handle simultaneous application startup
            checkActivationPolicy(prefs);

            // load extensions in viewtify package
            I.load(Location.class);

            // load extensions in application package
            I.load(applicationClass);

            // collect stylesheets for application
            AppearanceSetting appearance = Preferences.of(AppearanceSetting.class);
            stylesheets.add(Theme.locate("ui"));
            stylesheets.add(appearance.theme.v.location);
            stylesheets.add(appearance.themeType.v.location);
            stylesheets.add(Locator.file(CSSProcessor.pretty().scheme(scheme).formatTo(prefs + "/application.css")).externalForm());
            stylesheets.add(writeFontStylesheet(null));

            // observe stylesheet's modification
            I.signal(stylesheets)
                    .take(uri -> uri.startsWith("file:/"))
                    .map(uri -> Locator.file(uri.substring(6).replace("%20", " ")))
                    .scan(Collectors.groupingBy(File::parent, Collectors.mapping(File::name, Collectors.toList())))
                    .last()
                    .flatIterable(m -> m.entrySet())
                    .flatMap(e -> e.getKey().observe("*.css"))
                    .debounce(300, TimeUnit.MILLISECONDS)
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
        if (activationPolicy != ActivationPolicy.Multiple) {
            // create application specified directory for lock
            Directory root = Locator.directory(prefs + "/lock").touch();
            FileChannel channel = root.file(".lock").newFileChannel(StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            try {
                while ((lock = channel.tryLock()) == null) {
                    // another application is activated
                    if (activationPolicy == ActivationPolicy.Earliest) {
                        // make the window active
                        root.file("active").touch();

                        throw new Error("Application is running already.");
                    } else {
                        // close another application
                        root.file("close").touch();
                    }

                    Thread.sleep(500);
                }
            } catch (Throwable e) {
                throw I.quiet(e);
            }

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
     * Write the special stylesheet for font definition.
     * 
     * @param fontName
     * @return
     */
    private String writeFontStylesheet(Font font) {
        font = Objects.requireNonNullElse(font, Font.getDefault());

        String prefs = I.env("PreferenceDirectory");
        File css = Locator.file(prefs + "/font.css");

        // write font rule
        css.text(".root { -fx-font-family : \"" + font.getName() + "\"; -fx-font-size : " + font.getSize() + "px;}");

        return css.externalForm();
    }

    /**
     * Reload the specified stylesheet.
     * 
     * @param changed A target shtylesheet's location.
     */
    private void reloadStylesheet(String changed) {
        for (Window window : Window.getWindows()) {
            ObservableList<String> stylesheets = window.getScene().getStylesheets();
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
     * Find the application launcher.
     * 
     * @return
     */
    public Class launcher() {
        return applicationLaunchingClass;
    }

    /**
     * Find the update site.
     * 
     * @return
     */
    public String updateSite() {
        return updateArchive;
    }

    /**
     * Deactivate the current application.
     */
    public void deactivate() {
        Terminator.dispose();

        Platform.exit();
    }

    /**
     * Reactivate the current application.
     */
    public void reactivate() {
        Blueprint.detect().boot();
        deactivate();
    }

    /**
     * Generates a separate window with only {@link UIWeb}. If the application is not running, it
     * will automatically launch an anonymous application.
     */
    public static synchronized void browser(Consumer<UIWeb> browser) {
        if (!toolkitInitialized) {
            checkHeadlessMode();
            Platform.startup(() -> toolkitInitialized = true);
        }

        Viewtify.inUI(() -> {
            AnonyBrowser anon = new AnonyBrowser();
            application().activate(anon);
            Platform.runLater(() -> browser.accept(anon.web));
        });
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

        /**
         * {@inheritDoc}
         */
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
        if (Platform.isFxApplicationThread()) {
            pool.submit(process);
        } else {
            process.run();
        }
    }

    /**
     * Execute task in pooled-background-worker thread.
     * 
     * @param process
     */
    public final static void inWorker(Supplier<Disposable> process) {
        inWorker(() -> Terminator.add(process.get()));
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
            try {
                Platform.runLater(process::run);
            } catch (IllegalStateException e) {
                if (waitingActions == null) {
                    throw e;
                } else {
                    waitingActions.add(process);
                }
            }
        }
    }

    /**
     * Execute task in UI thread.
     * 
     * @param process
     */
    public final static void inUI(Supplier<Disposable> process) {
        inUI(() -> {
            Terminator.add(process.get());
        });
    }

    /**
     * Create the general dialog builder.
     * 
     * @return
     */
    public static <V> ViewtyDialog<?> dialog() {
        return new ViewtyDialog();
    }

    /**
     * Manage the viewtify applicaiton theme.
     * 
     * @param theme
     */
    public static void manage(Theme theme) {
        inUI(() -> {
            for (Window window : Window.getWindows()) {
                Scene scene = window.getScene();
                ObservableList<String> classes = scene.getRoot().getStyleClass();
                ObservableList<String> sheets = scene.getStylesheets();

                // clear previous theme
                for (Theme old : Theme.values()) {
                    stylesheets.remove(old.location);
                    sheets.remove(old.location);
                    classes.remove(old.name().toLowerCase());
                }

                // add new theme
                stylesheets.add(theme.location);
                sheets.add(theme.location);
                classes.add(theme.name().toLowerCase());
            }
        });
    }

    /**
     * Manage the viewtify applicaiton theme.
     * 
     * @param theme
     */
    public static void manage(ThemeType theme) {
        inUI(() -> {
            for (Window window : Window.getWindows()) {
                Scene scene = window.getScene();
                ObservableList<String> classes = scene.getRoot().getStyleClass();
                ObservableList<String> sheets = scene.getStylesheets();

                // clear previous theme
                for (ThemeType old : ThemeType.values()) {
                    stylesheets.remove(old.location);
                    sheets.remove(old.location);
                    classes.remove(old.name().toLowerCase());
                }

                // add new theme
                stylesheets.add(theme.location);
                sheets.add(theme.location);
                classes.add(theme.name().toLowerCase());
            }
        });
    }

    /**
     * Manage the viewtify application font.
     * 
     * @param font
     */
    public static void manage(Font font) {
        application().writeFontStylesheet(font);
    }

    /**
     * Manage as viewtify application window. Apply window size and location setting and track the
     * upcoming modification. Apply the application styles (design, icon etc) to the specified
     * window.
     * 
     * @param id An identical name of the window.
     * @param scene A target window to manage.
     * @param untrackable Window tracking state.
     */
    public static void manage(String id, Scene scene, boolean untrackable) {
        if (scene != null) {
            manage(id, scene, (Stage) scene.getWindow(), untrackable);
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

        // apply title and icon
        stage.setTitle(viewtify.title);
        if (viewtify.icon.length() != 0) {
            stage.getIcons().add(new Image(viewtify.icon));
        }

        // ================================================================
        // Keyboard Binding System
        //
        // Monitors the shortcut keys and invokes the corresponding commands.
        // Bug Fix: Prevent the KeyPress event from occurring continuously if you hold down a key.
        // ================================================================
        EnumSet<KeyCode> pressed = EnumSet.noneOf(KeyCode.class);
        UserActionHelper<?> helper = UserActionHelper.of(scene);
        helper.when(User.KeyPress).take(e -> pressed.add(e.getCode())).to(I.make(ShortcutManager.class)::activate);
        helper.when(User.KeyRelease).to(e -> pressed.remove(e.getCode()));

        // ================================================================
        // Window Tracking System
        //
        // Restores the position and size of the window from its previous state.
        // It constantly monitors the status and saves any changes.
        // ================================================================
        I.make(WindowLocator.class).locate(id, stage);
        if (untrackable) {
            stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> {
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
     * @param value
     * @return
     */
    public static Signal<Integer> observe(IntegerExpression value) {
        return observe(new ObservableValue[] {value});
    }

    /**
     * Signal value changing.
     * 
     * @param value
     * @return
     */
    public static Signal<Long> observe(LongExpression value) {
        return observe(new ObservableValue[] {value});
    }

    /**
     * Signal value changing.
     * 
     * @param value
     * @return
     */
    public static Signal<Float> observe(FloatExpression value) {
        return observe(new ObservableValue[] {value});
    }

    /**
     * Signal value changing.
     * 
     * @param value
     * @return
     */
    public static Signal<Double> observe(DoubleExpression value) {
        return observe(new ObservableValue[] {value});
    }

    /**
     * Signal value changing.
     * 
     * @param value
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
                try {
                    value.addListener(listener);
                } catch (ArrayIndexOutOfBoundsException e) {
                    // FIXME I think it's a bug on Javafx side, but I'll try to re-register only
                    // once, because sometimes an error occurs at the time of listener registration.
                    try {
                        Thread.sleep(100);
                        value.addListener(listener);
                    } catch (InterruptedException e1) {
                        throw I.quiet(e);
                    }
                }
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
     * @param value
     * @return
     */
    public static Signal<Integer> observing(IntegerExpression value) {
        return observe(value).startWith(value.getValue());
    }

    /**
     * Signal value changing.
     * 
     * @param value
     * @return
     */
    public static Signal<Long> observing(LongExpression value) {
        return observe(value).startWith(value.getValue());
    }

    /**
     * Signal value changing.
     * 
     * @param value
     * @return
     */
    public static Signal<Float> observing(FloatExpression value) {
        return observe(value).startWith(value.getValue());
    }

    /**
     * Signal value changing.
     * 
     * @param value
     * @return
     */
    public static Signal<Double> observing(DoubleExpression value) {
        return observe(value).startWith(value.getValue());
    }

    /**
     * Signal value changing.
     * 
     * @param value
     * @return
     */
    public static <T> Signal<T> observing(ObservableValue<T> value) {
        return observe(value).startWith(value.getValue());
    }

    /**
     * Observe set change evnet.
     * 
     * @param set A set to observe its modification.
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
     * Create the wrapped property of the specified setter.
     * 
     * @param getter
     * @param setter
     * @return
     */
    public static DoubleProperty property(DoubleSupplier getter, DoubleConsumer setter) {
        return new DoublePropertyBase() {

            @Override
            public String getName() {
                return null;
            }

            @Override
            public Object getBean() {
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public double get() {
                return getter.getAsDouble();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void set(double newValue) {
                setter.accept(newValue);
            }
        };
    }

    /**
     * Create the wrapped property of the specified {@link Variable}.
     * 
     * @param variable
     * @return
     */
    public static <T> Property<T> property(Variable<T> variable) {
        return new PropertyVariable(variable, false);
    }

    /**
     * Create the wrapped UI property of the specified {@link Variable}.
     * 
     * @param variable
     * @return
     */
    public static <T> Property<T> propertyForUI(Variable<T> variable) {
        return new PropertyVariable(variable, true);
    }

    /**
     * Replace the splash screen by the specified view with fade-in effect.
     * 
     * @param view
     */
    public static void replaceSplashScreen(View view) {
        SplashScreen splash = SplashScreen.getSplashScreen();
        if (view != null && splash != null && splash.isVisible()) {
            view.stage().to(stage -> {
                Rectangle bounds = splash.getBounds();
                stage.setX(bounds.getX());
                stage.setY(bounds.getY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());

                URL image = splash.getImageURL();
                if (image != null) {
                    Region region = (Region) view.ui();
                    region.setBackground(new Background(new BackgroundImage(new Image(image
                            .toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
                }

                DoubleProperty o = view.ui().opacityProperty();
                Anime.define().init(o, 0).effect(o, 1, 0.4).run(splash::close);
            });
        }
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

        /** The user is ui or not. */
        private final boolean ui;

        /**
         * @param variable
         */
        private PropertyVariable(Variable<V> variable, boolean ui) {
            this.variable = variable;
            this.ui = ui;
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
            Disposable disposer = variable.observe().on(ui ? UIThread : null).to(v -> listener.changed(this, null, v));

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
                Disposable removed = changes.remove(listener);

                if (removed != null) {
                    removed.dispose();
                }

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
            Disposable disposer = variable.observe().on(ui ? UIThread : null).to(v -> listener.invalidated(this));

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
                Disposable removed = invalids.remove(listener);

                if (removed != null) {
                    removed.dispose();
                }

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

            windowState.merge(windowLocation.mapTo(true)).debounce(500, TimeUnit.MILLISECONDS).to(() -> {
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