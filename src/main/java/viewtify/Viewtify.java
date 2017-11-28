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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import kiss.Disposable;
import kiss.I;
import kiss.Manageable;
import kiss.Signal;
import kiss.Singleton;
import kiss.Variable;
import kiss.WiseBiFunction;
import viewtify.bind.ListBindingBuilder;
import viewtify.bind.ObservableVariable;
import viewtify.ui.UI;

/**
 * @version 2017/11/15 9:52:40
 */
@Manageable(lifestyle = Singleton.class)
public abstract class Viewtify {

    /** The terminate helper. */
    static final List<Disposable> terminators = new ArrayList();

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

    /** The singleton application. */
    static Viewtify viewtify;

    /** The singleton FX stage. */
    static Stage stage;

    /**
     * Find the location of application FXML.
     * 
     * @return
     */
    protected abstract URL fxml();

    /**
     * Select {@link ActivationPolicy} for applicaiton.
     * 
     * @return
     */
    protected ActivationPolicy policy() {
        return ActivationPolicy.Latest;
    }

    /**
     * Launch {@link Viewtify} application.
     * 
     * @param application
     */
    protected static void launch(Class<? extends Viewtify> application) {
        viewtify = I.make(application);

        Application.launch(ViewtifyApplication.class);
    }

    /**
     * Activate the specified application.
     */
    public static final void activate(Class<? extends Viewtify> application) {
        inUI(() -> {
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
     * Binding utility for {@link ObservableList}.
     * 
     * @param list A {@link ObservableList} source to bind.
     * @return A binding builder.
     */
    public static final <E> ListBindingBuilder<E> bind(ObservableList<E> list) {
        return new ListBindingBuilder<>(list);
    }

    /**
     * Create new {@link ObjectBinding} between two sources.
     * 
     * @param sourceA
     * @param sourceB
     * @param calculator
     * @return
     */
    public static final <A, B, R> ObjectBinding<R> bind(ObservableValue<A> sourceA, ObservableValue<B> sourceB, WiseBiFunction<A, B, R> calculator) {
        return new ObjectBinding<R>() {

            /** The observer. */
            private final WeakInvalidationListener listener = new WeakInvalidationListener(obs -> invalidate());

            {
                sourceA.addListener(listener);
                sourceB.addListener(listener);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void dispose() {
                sourceA.removeListener(listener);
                sourceB.removeListener(listener);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected R computeValue() {
                return calculator.apply(sourceA.getValue(), sourceB.getValue());
            }
        };
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
     * @param side
     * @return
     */
    public static <V> ObservableValue<V> wrap(Variable<V> var) {
        return new ObservableVariable(var);
    }

    /**
     * @param orderStateRow
     * @return
     */
    public static UI wrap(Control ui) {
        return new UI(ui);
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
}
