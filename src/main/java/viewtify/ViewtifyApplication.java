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

import java.util.HashMap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;

import org.controlsfx.tools.ValueExtractor;

import kiss.Decoder;
import kiss.Encoder;
import kiss.I;
import kiss.Manageable;
import kiss.Signal;
import kiss.Singleton;
import kiss.Storable;

/**
 * Singleton managed JavaFX application.
 * 
 * @version 2017/11/28 15:24:38
 */
public final class ViewtifyApplication extends Application {

    static {
        ValueExtractor.addObservableValueExtractor(c -> c instanceof Spinner, c -> ((Spinner) c).valueProperty());
    }

    /**
     * Initialize {@link Viewtify} application.
     */
    @Override
    public void start(Stage stage) throws Exception {
        Viewtify.stage = stage;

        // trace window size and position
        I.make(WindowLocator.class).restore().locate("MainWindow", stage);

        // show window
        Scene scene = new Scene(Viewtify.root().root());
        stage.setScene(scene);
        stage.show();

        // List<View> views = I.find(View.class);
        //
        // for (View view : views) {
        // // inject FXML defined components
        // for (Field field : view.getClass().getDeclaredFields()) {
        // if (field.isAnnotationPresent(FXML.class)) {
        // field.setAccessible(true);
        //
        // String id = "#" + field.getName();
        // Class<?> type = field.getType();
        //
        // if (View.class.isAssignableFrom(type)) {
        // // viewtify view
        // for (View v : views) {
        // if (type.isInstance(v)) {
        // field.set(view, v);
        // break;
        // }
        // }
        // } else {
        // Object node = stage.getScene().lookup(id);
        //
        // if (node == null) {
        // // If this exception will be thrown, it is bug of this program. So
        // // we
        // // must rethrow the wrapped error in here.
        // throw new Error("Node [" + id + "] is not found.");
        // }
        //
        // if (type == TableColumn.class || type == TreeTableColumn.class) {
        // // TableColumn returns c.s.jfx.scene.control.skin.TableColumnHeader
        // // so we must unwrap to javafx.scene.control.TreeTableColumn
        // node = ((com.sun.javafx.scene.control.skin.TableColumnHeader) node).getTableColumn();
        // }
        //
        // if (type.getName().startsWith("viewtify.ui.")) {
        // // viewtify ui widget
        // Constructor constructor = Model.collectConstructors(type)[0];
        // constructor.setAccessible(true);
        //
        // field.set(view, constructor.newInstance(node));
        // } else {
        // // javafx ui
        // field.set(view, node);
        //
        // enhanceNode(node);
        // }
        // }
        // }
        // }
        // view.initialize();
        // }
    }

    /**
     * Terminate {@link Viewtify} application.
     */
    @Override
    public void stop() throws Exception {
        Viewtify.deactivate();
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