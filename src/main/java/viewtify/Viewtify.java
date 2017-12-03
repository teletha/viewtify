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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import filer.Filer;
import kiss.Disposable;
import kiss.I;
import kiss.Signal;
import kiss.WiseBiFunction;
import kiss.WiseFunction;
import kiss.WiseSupplier;
import kiss.WiseTriFunction;
import viewtify.calculation.Calculatable;
import viewtify.calculation.Calculation;
import viewtify.calculation.CalculatableList;
import viewtify.ui.UI;

/**
 * @version 2017/12/01 18:25:44
 */
public final class Viewtify {

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
    public static final Consumer<Runnable> UIThread = Platform::runLater;

    /** Executor for Worker Thread. */
    public static final Consumer<Runnable> WorkerThread = pool::submit;

    /** The root view class. */
    private static Class<? extends View> rootViewClass;

    /** The root view instance for cache. */
    private static View rootView;

    /** The singleton FX stage. */
    static Stage stage;

    /**
     * Activate the specified {@link Viewtify} application with {@link ActivationPolicy#Latest}.
     */
    public static final void activate(Class<? extends View> application) {
        activate(application, null);
    }

    /**
     * Activate the specified {@link Viewtify} application.
     */
    public static final synchronized void activate(Class<? extends View> application, ActivationPolicy policy) {
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

                // try to retrieve lock to validate
                FileChannel channel = new RandomAccessFile(root.resolve("lock").toFile(), "rw").getChannel();
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
     * Retrieve the root view.
     * 
     * @return
     */
    public static final <V extends View> V root() {
        if (rootView == null) {
            rootView = I.make(rootViewClass);
        }
        return (V) rootView;
    }

    /**
     * @param sub
     * @param object
     */
    public static <E> Signal<E> signal(ObservableList<E> list) {
        return null;
    }

    /**
     * Binding utility for {@link ObservableList}.
     * 
     * @param list A {@link ObservableList} source to bind.
     * @return A binding builder.
     */
    public static final <E> CalculatableList<E> calculate(ObservableList<E> list) {
        return new CalculatableList<>(list);
    }

    /**
     * Create {@link Calculatable}.
     * 
     * @param o1 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <E> Calculatable<E> calculate(Observable o1, WiseSupplier<E> calculation) {
        return calculate(o1, null, calculation);
    }

    /**
     * Create {@link Calculatable}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <E> Calculatable<E> calculate(Observable o1, Observable o2, WiseSupplier<E> calculation) {
        return calculate(o1, o2, null, calculation);
    }

    /**
     * Create {@link Calculatable}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param o3 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <E> Calculatable<E> calculate(Observable o1, Observable o2, Observable o3, WiseSupplier<E> calculation) {
        return calculate(o1, o2, o3, null, calculation);
    }

    /**
     * Create {@link Calculatable}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param o3 A {@link Observable} target.
     * @param o4 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <E> Calculatable<E> calculate(Observable o1, Observable o2, Observable o3, Observable o4, WiseSupplier<E> calculation) {
        return new Calculation<E>(o1, o2, o3, o4) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected E calculate() {
                return calculation.get();
            }
        };
    }

    /**
     * Create {@link Calculatable}.
     * 
     * @param o1 A {@link Observable} target.
     * @return A lazy evaluated calculation.
     */
    public static final <A> Calculatable<A> calculate(ObservableValue<A> o1) {
        return calculate(o1, () -> o1.getValue());
    }

    /**
     * Create {@link Calculatable}.
     * 
     * @param o1 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <A, R> Calculatable<R> calculate(ObservableValue<A> o1, WiseFunction<A, R> calculator) {
        return calculate(o1, () -> calculator.apply(o1.getValue()));
    }

    /**
     * Create {@link Calculatable}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <A, B, R> Calculatable<R> calculate(ObservableValue<A> o1, ObservableValue<B> o2, WiseBiFunction<A, B, R> calculator) {
        return calculate(o1, o2, () -> calculator.apply(o1.getValue(), o2.getValue()));
    }

    /**
     * Create {@link Calculatable}.
     * 
     * @param o1 A {@link Observable} target.
     * @param o2 A {@link Observable} target.
     * @param o3 A {@link Observable} target.
     * @param calculation A calculation.
     * @return A lazy evaluated calculation.
     */
    public static final <A, B, C, R> Calculatable<R> calculate(ObservableValue<A> o1, ObservableValue<B> o2, ObservableValue<C> o3, WiseTriFunction<A, B, C, R> calculator) {
        return calculate(o1, o2, o3, () -> calculator.apply(o1.getValue(), o2.getValue(), o3.getValue()));
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
            Terminator.add(process.get());
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
            Terminator.add(process.get());
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

    public static UI wrap(Control ui, View view) {
        return new UI(ui, view);
    }

}
