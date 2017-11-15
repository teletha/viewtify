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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;

import org.controlsfx.tools.ValueExtractor;

import kiss.Disposable;
import kiss.I;
import kiss.model.Model;

/**
 * @version 2017/11/15 9:52:40
 */
public class Viewtify {

    static {
        I.load(View.class, false);

        ValueExtractor.addObservableValueExtractor(c -> c instanceof Spinner, c -> ((Spinner) c).valueProperty());
    }

    /**
     * Initialize {@link Viewtify}.
     */
    public static void initialize(Scene scene) throws Exception {
        for (View view : I.find(View.class)) {
            // inject FXML defined components
            for (Field field : view.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(FXML.class)) {
                    String id = "#" + field.getName();
                    Node node = scene.lookup(id);

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
     * Terminate application.
     */
    public static void terminate() {
        for (Disposable disposable : terminators) {
            disposable.dispose();
        }
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
}
