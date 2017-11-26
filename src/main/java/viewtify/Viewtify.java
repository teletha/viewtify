/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import static java.util.concurrent.TimeUnit.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;

import org.controlsfx.tools.ValueExtractor;

import filer.Filer;
import kiss.Decoder;
import kiss.Disposable;
import kiss.Encoder;
import kiss.I;
import kiss.Manageable;
import kiss.Signal;
import kiss.Singleton;
import kiss.Storable;
import kiss.model.Model;

/**
 * @version 2017/11/15 9:52:40
 */
public abstract class Viewtify extends Application {

    static {
        I.load(Location.class, false);

        ValueExtractor.addObservableValueExtractor(c -> c instanceof Spinner, c -> ((Spinner) c).valueProperty());
    }

    /** The terminate helper. */
    private static final List<Disposable> terminators = new ArrayList();

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
    public static final Consumer<Runnable> UIThread = Platform::runLater;

    /** Executor for Worker Thread. */
    public static final Consumer<Runnable> WorkerThread = pool::submit;

    /** The application holder. */
    private static final ConcurrentHashMap<Class<? extends Viewtify>, Stage> applications = new ConcurrentHashMap();

    /**
     * Find the location of application FXML.
     * 
     * @return
     */
    protected abstract URL findFXML();

    /**
     * Select {@link ActivationPolicy} for applicaiton.
     * 
     * @return
     */
    protected ActivationPolicy policy() {
        return ActivationPolicy.Latest;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("resource")
    @Override
    public final void init() throws Exception {
        // =====================================================================
        // Validation Policy
        // =====================================================================
        if (policy() != ActivationPolicy.Multiple) {
            try {
                // create application specified directory for lock
                Path root = Filer.locate(System.getProperty("java.io.tmpdir")).resolve(getClass().getName());

                if (Files.notExists(root)) {
                    Files.createDirectory(root);
                }

                // try to retrieve lock to validate
                FileChannel channel = new RandomAccessFile(root.resolve("lock").toFile(), "rw").getChannel();
                FileLock lock = channel.tryLock();

                if (lock == null) {
                    // another application is activated
                    if (policy() == ActivationPolicy.Earliest) {
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
                        activate(getClass());
                        break;

                    case "close":
                        shutdown(getClass());
                        break;
                    }
                });
            } catch (Exception e) {
                throw I.quiet(e);
            }
        }
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
    private void touch(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.setLastModifiedTime(path, FileTime.fromMillis(System.currentTimeMillis()));
        } else {
            Files.createFile(path);
        }
    }

    /**
     * Initialize {@link Viewtify} application.
     */
    @Override
    public final void start(Stage stage) throws Exception {
        applications.put(getClass(), stage);

        // load FXML
        FXMLLoader loader = new FXMLLoader(findFXML());

        // trace window size and position
        I.make(WindowLocator.class).restore().locate("MainWindow", stage);

        // show window
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();

        for (View view : I.find(View.class)) {
            // inject FXML defined components
            for (Field field : view.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(FXML.class)) {
                    String id = "#" + field.getName();
                    Node node = stage.getScene().lookup(id);

                    if (node == null) {
                        // If this exception will be thrown, it is bug of this program. So we must
                        // rethrow the wrapped error in here.
                        throw new Error("Node [" + id + "] is not found.");
                    } else {
                        field.setAccessible(true);

                        Class<?> type = field.getType();

                        if (type.getName().startsWith("viewtify.ui.")) {
                            // viewtify ui
                            Constructor constructor = Model.collectConstructors(type)[0];
                            constructor.setAccessible(true);

                            field.set(view, constructor.newInstance(node));
                        } else {
                            // javafx ui
                            field.set(view, node);

                            enhanceNode(node);
                        }
                    }
                }
            }
            view.initialize();
        }
    }

    /**
     * Terminate {@link Viewtify} application.
     */
    @Override
    public final void stop() throws Exception {
        for (Disposable disposable : terminators) {
            disposable.dispose();
        }
    }

    /**
     * Enhance Node.
     */
    private static void enhanceNode(Node node) {
        if (node instanceof Spinner) {
            Spinner spinner = (Spinner) node;
            spinner.setOnScroll(e -> {
                if (e.getDeltaY() > 0) {
                    spinner.increment();
                } else if (e.getDeltaY() < 0) {
                    spinner.decrement();
                }
            });
        }
    }

    /**
     * Activate the specified application.
     */
    public static final void activate(Class<? extends Viewtify> application) {
        inUI(() -> {
            Stage stage = applications.get(application);

            if (stage != null && !stage.isAlwaysOnTop()) {
                stage.setAlwaysOnTop(true);
                stage.setAlwaysOnTop(false);
            }
        });
    }

    /**
     * Shutdown the specified application.
     */
    public static final void shutdown(Class<? extends Viewtify> application) {
        Platform.exit();
    }

    /**
     * Execute task in pooled-background-worker thread.
     * 
     * @param process
     */
    public static void inWorker(Runnable process) {
        pool.submit(process);
    }

    /**
     * Execute task in pooled-background-worker thread.
     * 
     * @param process
     */
    public static void inWorker(Supplier<Disposable> process) {
        pool.submit(() -> {
            terminators.add(process.get());
        });
    }

    /**
     * Execute task in UI thread.
     * 
     * @param process
     */
    public static void inUI(Runnable process) {
        Platform.runLater(process::run);
    }

    /**
     * Execute task in UI thread.
     * 
     * @param process
     */
    public static void inUI(Supplier<Disposable> process) {
        Platform.runLater(() -> {
            terminators.add(process.get());
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
     * @version 2017/11/26 8:33:23
     */
    protected static enum ActivationPolicy {

        /**
         * Continue to process the earliest application. The subsequent applications will not start
         * up.
         */
        Earliest,

        /**
         * Application has multiple processes.
         */
        Multiple,

        /**
         * Terminate the prior applications immediately, then the latest application will start up.
         */
        Latest;
    }

    /**
     * @version 2017/11/25 23:59:20
     */
    @SuppressWarnings("serial")
    @Manageable(lifestyle = Singleton.class)
    private static class WindowLocator extends HashMap<String, Location> implements Storable<WindowLocator> {

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
        private void locate(String name, Stage stage) {
            Location location = get(name);

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
            Signal<Boolean> windowState = signal(stage.maximizedProperty(), stage.iconifiedProperty());
            Signal<Number> windowLocation = signal(stage.xProperty(), stage.yProperty(), stage.widthProperty(), stage.heightProperty());

            windowState.merge(windowLocation.mapTo(true)).debounce(500, MILLISECONDS).to(() -> {
                Location store = computeIfAbsent(name, key -> new Location());

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
