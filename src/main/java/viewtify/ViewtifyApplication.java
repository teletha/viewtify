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
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeTableColumn;
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
import viewtify.Viewtify.ActivationPolicy;

/**
 * Singleton managed JavaFX application.
 * 
 * @version 2017/11/28 15:24:38
 */
public final class ViewtifyApplication extends Application {

    static {
        I.load(Location.class, false);

        ValueExtractor.addObservableValueExtractor(c -> c instanceof Spinner, c -> ((Spinner) c).valueProperty());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        // =====================================================================
        // Validation Policy
        // =====================================================================
        if (Viewtify.viewtify.policy() != ActivationPolicy.Multiple) {
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
                    if (Viewtify.viewtify.policy() == ActivationPolicy.Earliest) {
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
                        Viewtify.activate(Viewtify.viewtify.getClass());
                        break;

                    case "close":
                        Viewtify.shutdown(Viewtify.viewtify.getClass());
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
    public void start(Stage stage) throws Exception {
        Viewtify.stage = stage;

        // load FXML
        FXMLLoader loader = new FXMLLoader(Viewtify.viewtify.fxml());

        // trace window size and position
        I.make(WindowLocator.class).restore().locate("MainWindow", stage);

        // show window
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();

        List<View> views = I.find(View.class);

        for (View view : views) {
            // inject FXML defined components
            for (Field field : view.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(FXML.class)) {
                    field.setAccessible(true);

                    String id = "#" + field.getName();
                    Class<?> type = field.getType();

                    if (View.class.isAssignableFrom(type)) {
                        // viewtify view
                        for (View v : views) {
                            if (type.isInstance(v)) {
                                field.set(view, v);
                                break;
                            }
                        }
                    } else {
                        Object node = stage.getScene().lookup(id);

                        if (node == null) {
                            // If this exception will be thrown, it is bug of this program. So
                            // we
                            // must rethrow the wrapped error in here.
                            throw new Error("Node [" + id + "] is not found.");
                        }

                        if (type == TableColumn.class || type == TreeTableColumn.class) {
                            // TableColumn returns c.s.jfx.scene.control.skin.TableColumnHeader
                            // so we must unwrap to javafx.scene.control.TreeTableColumn
                            node = ((com.sun.javafx.scene.control.skin.TableColumnHeader) node).getTableColumn();
                        }

                        if (type.getName().startsWith("viewtify.ui.")) {
                            // viewtify ui widget
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
    public void stop() throws Exception {
        for (Disposable disposable : Viewtify.terminators) {
            disposable.dispose();
        }
    }

    /**
     * Enhance Node.
     */
    private void enhanceNode(Object node) {
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
        void locate(String name, Stage stage) {
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
            Signal<Boolean> windowState = Viewtify.signal(stage.maximizedProperty(), stage.iconifiedProperty());
            Signal<Number> windowLocation = Viewtify
                    .signal(stage.xProperty(), stage.yProperty(), stage.widthProperty(), stage.heightProperty());

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
    private static class Location implements Decoder<Location>, Encoder<Location> {

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