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

import static java.util.concurrent.TimeUnit.*;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.controlsfx.tools.ValueExtractor;

import filer.Filer;
import kiss.Decoder;
import kiss.Encoder;
import kiss.I;
import kiss.Manageable;
import kiss.Signal;
import kiss.Singleton;
import kiss.Storable;
import stylist.Stylist;
import stylist.util.JavaFXLizer;
import viewtify.ui.View;

/**
 * Singleton managed JavaFX application.
 * 
 * @version 2018/08/30 3:04:34
 */
public final class ViewtifyApplication extends Application {

    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> e.printStackTrace());
        ValueExtractor.addObservableValueExtractor(c -> c instanceof Spinner, c -> ((Spinner) c).valueProperty());
    }

    /** The style sheet manager. */
    private final StyleSheetObserver styles = new StyleSheetObserver();

    /**
     * Initialize {@link Viewtify} application.
     */
    @Override
    public void start(Stage stage) throws Exception {
        Viewtify.initializer.accept(stage);

        // trace window size and position
        I.make(WindowLocator.class).restore().locate("MainWindow", stage);

        Path application = Stylist.writeTo(Paths.get(".preferences/application.css"), JavaFXLizer.pretty());

        View view = Viewtify.root();
        Scene scene = new Scene(view.root());
        scene.getStylesheets().add(getClass().getResource("dark.css").toExternalForm());
        scene.getStylesheets().add(application.toUri().toURL().toExternalForm());
        configIcon(stage);
        stage.setScene(scene);
        stage.show();

        // observe stylesheets
        styles.observe(scene.getStylesheets());
        styles.observe(scene.getRoot().getStylesheets());
    }

    /**
     * Terminate {@link Viewtify} application.
     */
    @Override
    public void stop() throws Exception {
        Viewtify.deactivate();
    }

    /**
     * Search user specified icon and configure it.
     * 
     * @param stage
     */
    private void configIcon(Stage stage) {
        try {
            InputStream input;
            Path icon = Paths.get("icon.png").toAbsolutePath();

            if (Files.exists(icon)) {
                input = Files.newInputStream(icon);
            } else {
                input = ClassLoader.getSystemResourceAsStream("icon.png");
            }
            stage.getIcons().add(new Image(input));
        } catch (Throwable e) {
            // ignore
        }
    }

    /**
     * @version 2018/01/02 19:04:37
     */
    private static class StyleSheetObserver {

        /**
         * Observe stylesheet.
         * 
         * @param stylesheets
         */
        private void observe(ObservableList<String> stylesheets) {
            for (String stylesheet : stylesheets) {
                if (stylesheet.startsWith("file:/")) {
                    Path path = Paths.get(stylesheet.substring(6));

                    if (Files.exists(path)) {
                        Filer.observe(path).debounce(1, SECONDS).to(e -> {
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
