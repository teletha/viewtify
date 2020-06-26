/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import kiss.Observer;
import kiss.Signal;
import netscape.javascript.JSObject;
import viewtify.Viewtify;

public class UIWeb extends UserInterface<UIWeb, WebView> {

    private final WebEngine engine;

    // Maintain a strong reference to prevent garbage collection:
    // https://bugs.openjdk.java.net/browse/JDK-8154127
    private Bridge bridge;

    /**
     * @param view
     */
    public UIWeb(View view) {
        super(new WebView(), view);
        engine = ui.getEngine();
    }

    /**
     * Load the specified page.
     * 
     * @param uri A page URI to load.
     * @return Chainable API.
     */
    public Signal<UIWeb> load(String uri) {
        return new Signal<UIWeb>((observer, disposer) -> {
            engine.load(uri);
            observer.accept(this);

            return disposer;
        }).flatMap(UIWeb::awaitContentLoading);
    }

    /**
     * Input text to the specified form.
     * 
     * @param selector A css selector to select input form.
     * @param inputText A text to input.
     * @return Chainable API.
     */
    public Signal<UIWeb> input(String selector, String inputText) {
        return new Signal<>((observer, disposer) -> {
            Script script = new Script();
            script.write("document.querySelector(`", selector, "`).value = `", inputText, "`;");

            bridge.await(script, observer);
            return disposer;
        });
    }

    /**
     * Click the specified element.
     * 
     * @param selector A css selector to click.
     * @return Chainable API.
     */
    public Signal<UIWeb> click(String selector) {
        return new Signal<>((observer, disposer) -> {
            Script script = new Script();
            script.write("document.querySelector(`", selector, "`).click();");
            bridge.await(script, observer);

            return disposer;
        });
    }

    public Signal<String> text(String selector) {
        return new Signal<>((observer, disposer) -> {
            Script script = new Script();
            script.write("return document.querySelector(`", selector, "`).textContent");
            bridge.await(script, observer);
            return disposer;
        });
    }

    public Signal<UIWeb> awaitContentLoading() {
        return new Signal<UIWeb>((observer, diposer) -> {
            Viewtify.observe(engine.getLoadWorker().stateProperty())
                    .take(state -> state == State.SUCCEEDED)
                    .take(1)
                    .effect(this::initialize)
                    .to(state -> observer.accept(this));
            return diposer;
        }).on(Viewtify.UIThread);
    }

    /**
     * Initialize page.
     */
    private void initialize() {
        JSObject global = (JSObject) engine.executeScript("window");
        global.setMember("bridge", bridge = new Bridge());
    }

    /**
     * A utility that bridges the gap between Java and JS environments.
     */
    public class Bridge {

        /** The id counter. */
        private final AtomicInteger counter = new AtomicInteger();

        /** The observer holder. */
        private final Map<Integer, Observer> observers = new HashMap();

        /**
         * Hide.
         */
        private Bridge() {
        }

        /**
         * Runs the specified script asynchronously and passes the results to the Observer.
         * 
         * @param script A script to execute.
         * @param observer A result consumer.
         */
        private void await(Script script, Observer observer) {
            try {
                int id = counter.getAndIncrement();
                observers.put(id, observer);

                // Converts to an immediate execution function that executes asynchronously.
                script.code.insert(0, "(async()=>{");
                script.code.append("})().then(v=>{bridge.resume(" + id + ",v===undefined?null:v)})");

                // The function is executed asynchronously in a Javascript context, and the method
                // call is immediately executed in Return. The return value is Promise, but it is
                // not used.
                engine.executeScript(script.code.toString());
            } catch (Throwable e) {
                observer.error(e);
            }
        }

        /**
         * Resumes processing of the specified ID. This method is public but only called internally
         * from the JS environment.
         * 
         * @param id A process identifier.
         * @param value A processed value.
         */
        public void resume(Integer id, Object value) {
            observers.remove(id).accept(value == null ? UIWeb.this : value);
        }
    }

    /**
     * 
     */
    private class Script {
        StringBuilder code = new StringBuilder();

        void write(Object... codeFragments) {
            for (Object fragment : codeFragments) {
                code.append(String.valueOf(fragment).replace('`', '"'));
            }
        }
    }
}
