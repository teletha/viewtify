/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.concurrent.Worker.State;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import kiss.Decoder;
import kiss.Disposable;
import kiss.Encoder;
import kiss.I;
import kiss.Observer;
import kiss.Signal;
import kiss.Signaling;
import kiss.Storable;
import kiss.Variable;
import kiss.WiseFunction;
import netscape.javascript.JSObject;
import viewtify.Viewtify;

public class UIWeb extends UserInterface<UIWeb, WebView> {

    /** The internal cookie manager. */
    private static final Cookies cookies = new Cookies();

    static {
        CookieHandler.setDefault(new CookieManager(cookies, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
    }

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
        ui.setFontSmoothingType(FontSmoothingType.LCD);
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
            observer.complete();

            return disposer;
        }).flatMap(UIWeb::awaitContentLoading);
    }

    /**
     * Wait for the page to load completely.
     * 
     * @return
     */
    public Signal<UIWeb> awaitContentLoading() {
        return new Signal<UIWeb>((observer, disposer) -> {
            Viewtify.observe(engine.getLoadWorker().stateProperty())
                    .take(state -> state == State.SUCCEEDED)
                    .take(1)
                    .effect(this::initialize)
                    .to(state -> observer.accept(this), observer::error, observer::complete);
            return disposer;
        });
    }

    /**
     * Wait until the element specified by the CSS selector appears.
     * 
     * @param cssSelector A css selector to find.
     * @return Chainable API.
     */
    public Signal<UIWeb> awaitAppearingElement(String cssSelector) {
        return new Signal<UIWeb>((observer, disposer) -> {
            Coder coder = new Coder();
            coder.write("while(!document.querySelector('", cssSelector, "')){");
            coder.write("  await sleep();");
            coder.write("}");

            return bridge.await(observer, disposer, coder.toString());
        });
    }

    /**
     * Wait until the element specified by the CSS selector disappears.
     * 
     * @param cssSelector A css selector to find.
     * @return Chainable API.
     */
    public Signal<UIWeb> awaitDisappearingElement(String cssSelector) {
        return new Signal<UIWeb>((observer, disposer) -> {
            Coder coder = new Coder();
            coder.write("while(document.querySelector('", cssSelector, "')){");
            coder.write("  await sleep();");
            coder.write("}");

            return bridge.await(observer, disposer, coder.toString());
        });
    }

    /**
     * Wait until the element specified by the CSS selector appears.
     * 
     * @param cssSelector A css selector to find.
     * @param expectedText A text to be expected.
     * @return Chainable API.
     */
    public Signal<UIWeb> awaitAppearingText(String cssSelector, String expectedText) {
        return new Signal<UIWeb>((observer, disposer) -> {
            Coder coder = new Coder();
            coder.write("while(!document.querySelector('", cssSelector, "')?.textContent?.include('", expectedText, "')){");
            coder.write("  await sleep();");
            coder.write("}");

            return bridge.await(observer, disposer, coder.toString());
        });
    }

    /**
     * Wait until the specified event occurs on the element specified by the CSS selector.
     * 
     * @param cssSelector A css selector to find.
     * @param eventType An event type.
     * @return Chainable API.
     */
    public Signal<UIWeb> awaitNextEvent(String cssSelector, String eventType) {
        return new Signal<UIWeb>((observer, disposer) -> {
            Coder coder = new Coder();
            coder.write("await new Promise(next => {");
            coder.write("  document.querySelector('", cssSelector, "').addEventListener('", eventType, "', next, {once:true});");
            coder.write("});");

            return bridge.await(observer, disposer, coder.toString());
        });
    }

    /**
     * Retrieve the cookie with the specified name.
     * 
     * @param name A cookie name.
     */
    public Variable<HttpCookie> cookie(String name) {
        return cookies.get(URI.create(engine.getLocation()), name);
    }

    /**
     * Click on the first element specified in the CSS selector.
     * 
     * @param cssSelector A css selector to click.
     * @return Chainable API.
     */
    public Signal<UIWeb> click(String cssSelector) {
        return new Signal<>((observer, disposer) -> {
            return bridge.await(observer, disposer, "document.querySelector('{0}').click()", cssSelector);
        });
    }

    /**
     * Enter text on the first element specified by the CSS selector.
     * 
     * @param cssSelector A css selector to select input form.
     * @param inputText A text to input.
     * @return Chainable API.
     */
    public Signal<UIWeb> input(String cssSelector, String inputText) {
        return new Signal<>((observer, disposer) -> {
            return bridge.await(observer, disposer, "document.querySelector('{0}').value='{1}'", cssSelector, inputText);
        });
    }

    /**
     * Enter text on the first element specified by the CSS selector. Prompts you to enter text on
     * the page.
     * 
     * @param cssSelector A css selector to select input form.
     * @return Chainable API.
     */
    public Signal<UIWeb> inputByHuman(String cssSelector) {
        return new Signal<UIWeb>((observer, disposer) -> {
            return bridge.await(observer, disposer, "document.querySelector('{0}').focus()", cssSelector);
        }).flatMap(Operation.awaitNextEvent(cssSelector, "blur"));
    }

    /**
     * Enter text on the first element specified by the CSS selector. The input dialog will appear.
     * 
     * @param cssSelector A css selector to select input form.
     * @param description Description of the input.
     * @return Chainable API.
     */
    public Signal<UIWeb> inputByHuman(String cssSelector, String description) {
        return new Signal<String>((observer, disposer) -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText(description);
            dialog.setTitle(description);
            dialog.showAndWait().ifPresentOrElse(value -> {
                observer.accept(value);
                observer.complete();
            }, () -> {
                observer.error(new InputMismatchException("Nothing was entered. [" + description + "]"));
            });
            return disposer;
        }).flatMap(value -> input(cssSelector, value));
    }

    /**
     * Enter text on the first element specified by the CSS selector. The input dialog will appear.
     * 
     * @param cssSelector A css selector to select input form.
     * @param description Description of the input.
     * @return Chainable API.
     */
    public Signal<UIWeb> inputByHuman(String cssSelector, Variable<String> description) {
        return new Signal<String>((observer, disposer) -> {
            TextInputDialog dialog = new TextInputDialog();
            description.observing().on(Viewtify.UIThread).to(text -> {
                dialog.setTitle(text);
                dialog.setHeaderText(text);
            });
            dialog.showAndWait().ifPresentOrElse(value -> {
                observer.accept(value);
                observer.complete();
            }, () -> {
                observer.error(new InputMismatchException("Nothing was entered. [" + description + "]"));
            });
            return disposer;
        }).flatMap(value -> input(cssSelector, value));
    }

    /**
     * Retireve the current locaiton.
     * 
     * @return
     */
    public String location() {
        return engine.getLocation();
    }

    /**
     * Returns the text of the first element specified by the CSS selector.
     * 
     * @param cssSelector CSS selector to find.
     * @return Chainable API.
     */
    public Signal<String> text(String cssSelector) {
        return new Signal<>((observer, disposer) -> {
            return bridge.await(observer, disposer, "return document.querySelector('{0}').textContent", cssSelector);
        });
    }

    /**
     * Returns the text of the first element specified by the CSS selector.
     * 
     * @param cssSelector CSS selector to find.
     * @param text A found text consumer.
     * @return Chainable API.
     */
    public Signal<UIWeb> text(String cssSelector, Consumer<String> text) {
        return text(cssSelector).map(v -> {
            text.accept(v);
            return this;
        });
    }

    /**
     * Initialize page.
     */
    private void initialize() {
        JSObject global = (JSObject) engine.executeScript("window");
        global.setMember("bridge", bridge = new Bridge());

        Coder coder = new Coder();
        coder.write("window.bridge.sleep = () => new Promise(next => setTimeout(next, 50));");
        coder.execute();
    }

    /**
     * 
     */
    private class Coder {
        private final StringBuilder builder = new StringBuilder();

        private void write(Object... codes) {
            for (Object code : codes) {
                builder.append(code);
            }
        }

        private void execute() {
            engine.executeScript(builder.toString());
        }

        @Override
        public String toString() {
            return builder.toString();
        }
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
        private Disposable await(Observer observer, Disposable disposer, String script, Object... items) {
            if (disposer.isNotDisposed()) {
                try {
                    int id = counter.getAndIncrement();
                    observers.put(id, observer);

                    // Converts to an immediate execution function that executes asynchronously.
                    StringBuilder code = new StringBuilder("(async()=>{");
                    code.append(items.length == 0 ? script : I.express(script, I.list(items)));
                    code.append("})().then(v=>{bridge.resume(" + id + ",v===undefined?null:v)})");

                    // The function is executed asynchronously in a Javascript context, and the
                    // method call is immediately executed in Return. The return value is Promise,
                    // but it is not used.
                    engine.executeScript(code.toString());
                } catch (Throwable e) {
                    observer.error(e);
                }
            }
            return disposer;
        }

        /**
         * Resumes processing of the specified ID. This method is public but only called internally
         * from the JS environment.
         * 
         * @param id A process identifier.
         * @param value A processed value.
         */
        public void resume(Integer id, Object value) {
            Observer o = observers.remove(id);
            o.accept(value == null ? UIWeb.this : value);
            o.complete();
        }

        /**
         * Debugger.
         * 
         * @param message
         */
        public void log(Object message) {
            System.out.println(message);
        }
    }

    /**
     * 
     */
    public static class Operation {
        /**
         * Wait for the page to load completely.
         * 
         * @return
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> awaitContentLoading() {
            return UIWeb::awaitContentLoading;
        }

        /**
         * Wait until the element specified by the CSS selector appears.
         * 
         * @param cssSelector A css selector to find.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> awaitAppearingElement(String cssSelector) {
            return web -> web.awaitAppearingElement(cssSelector);
        }

        /**
         * Wait until the element specified by the CSS selector disappears.
         * 
         * @param cssSelector A css selector to find.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> awaitDisappearingElement(String cssSelector) {
            return web -> web.awaitDisappearingElement(cssSelector);
        }

        /**
         * Wait until the element specified by the CSS selector appears.
         * 
         * @param cssSelector A css selector to find.
         * @param expectedText A text to be expected.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> awaitAppearingText(String cssSelector, String expectedText) {
            return web -> web.awaitAppearingText(cssSelector, expectedText);
        }

        /**
         * Wait until the specified event occurs on the element specified by the CSS selector.
         * 
         * @param cssSelector A css selector to find.
         * @param eventType An event type.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> awaitNextEvent(String cssSelector, String eventType) {
            return web -> web.awaitNextEvent(cssSelector, eventType);
        }

        /**
         * Click on the first element specified in the CSS selector.
         * 
         * @param cssSelector A css selector to click.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> click(String cssSelector) {
            return web -> web.click(cssSelector);
        }

        /**
         * If the current URI matches the specified one, additional processing will be performed.
         * 
         * @param uri The target page's URI.
         * @param detour Additional Processing.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> detour(String uri, WiseFunction<UIWeb, Signal<UIWeb>> detour) {
            return detour(web -> web.location().equals(uri), detour);
        }

        /**
         * If the specified conditions are met, additional processing will be performed.
         * 
         * @param condition Conditions for processing.
         * @param detour Additional Processing.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> detour(Predicate<UIWeb> condition, WiseFunction<UIWeb, Signal<UIWeb>> detour) {
            return web -> {
                if (condition.test(web)) {
                    return detour.apply(web);
                } else {
                    return I.signal(web);
                }
            };
        }

        /**
         * Enter text on the first element specified by the CSS selector.
         * 
         * @param cssSelector A css selector to select input form.
         * @param inputText A text to input.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> input(String cssSelector, String inputText) {
            return web -> web.input(cssSelector, inputText);
        }

        /**
         * Enter text on the first element specified by the CSS selector. Prompts you to enter text
         * on the page.
         * 
         * @param cssSelector A css selector to select input form.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> inputByHuman(String cssSelector) {
            return web -> web.inputByHuman(cssSelector);
        }

        /**
         * Enter text on the first element specified by the CSS selector. The input dialog will
         * appear.
         * 
         * @param cssSelector A css selector to select input form.
         * @param description Description of the input.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> inputByHuman(String cssSelector, String description) {
            return web -> web.inputByHuman(cssSelector, description);
        }

        /**
         * Enter text on the first element specified by the CSS selector. The input dialog will
         * appear.
         * 
         * @param cssSelector A css selector to select input form.
         * @param description Description of the input.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> inputByHuman(String cssSelector, Variable<String> description) {
            return web -> web.inputByHuman(cssSelector, description);
        }

        /**
         * Returns the text of the first element specified by the CSS selector.
         * 
         * @param cssSelector CSS selector to find.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<String>> text(String cssSelector) {
            return web -> web.text(cssSelector);
        }

        /**
         * Returns the text of the first element specified by the CSS selector.
         * 
         * @param cssSelector CSS selector to find.
         * @param text A found text consumer.
         * @return Chainable API.
         */
        public static WiseFunction<UIWeb, Signal<UIWeb>> text(String cssSelector, Consumer<String> text) {
            return web -> web.text(cssSelector, text);
        }
    }

    /**
     * Cookie Manager for {@link WebView}.
     */
    private static class Cookies implements CookieStore, Storable<CookieManager> {

        /** The save timing coordinator. */
        private final Signaling<String> timing = new Signaling();

        /** In memory storage. */
        public Map<String, List<HttpCookie>> byDomain = new HashMap();

        /**
         * Hide
         */
        private Cookies() {
            restore();

            timing.expose.debounce(1, TimeUnit.SECONDS).to(this::store);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized void add(URI uri, HttpCookie cookie) {
            String domain = cookie.getDomain();
            if (domain.charAt(0) == '.') {
                domain = domain.substring(1);
            }

            List<HttpCookie> list = byDomain.computeIfAbsent(domain, key -> new ArrayList());

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(cookie)) {
                    list.set(i, cookie); // update
                    timing.accept("save");
                    return;
                }
            }

            list.add(cookie);
            timing.accept("save");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized List<HttpCookie> get(URI uri) {
            return I.signal(byDomain.get(uri.getHost()))
                    .take(cookie -> matchHost(cookie.getDomain(), uri.getHost()))
                    .take(cookie -> matchPath(cookie.getPath(), uri.getPath()))
                    .toList();
        }

        /**
         * If the cookie with the specified name exists, we will retrieve it.
         * 
         * @param uri
         * @param key
         * @return
         */
        public synchronized Variable<HttpCookie> get(URI uri, String key) {
            return I.signal(byDomain.get(uri.getHost()))
                    .take(cookie -> matchHost(cookie.getDomain(), uri.getHost()))
                    .take(cookie -> matchPath(cookie.getPath(), uri.getPath()))
                    .take(cookie -> cookie.getName().equalsIgnoreCase(key))
                    .take(1)
                    .to();
        }

        /**
         * http://tools.ietf.org/html/rfc6265#section-5.1.3 A string domain-matches a given domain
         * string if at least one of the following conditions hold: o The domain string and the
         * string are identical. (Note that both the domain string and the string will have been
         * canonicalized to lower case at this point.) o All of the following conditions hold: The
         * domain string is a suffix of the string. The last character of the string that is not
         * included in the domain string is a %x2E (".") character. The string is a host name (i.e.,
         * not an IP address).
         */
        private boolean matchHost(String cookieHost, String requestHost) {
            return requestHost.equals(cookieHost) || requestHost.endsWith(".".concat(cookieHost));
        }

        /**
         * http://tools.ietf.org/html/rfc6265#section-5.1.4 A request-path path-matches a given
         * cookie-path if at least one of the following conditions holds: o The cookie-path and the
         * request-path are identical. o The cookie-path is a prefix of the request-path, and the
         * last character of the cookie-path is %x2F ("/"). o The cookie-path is a prefix of the
         * request-path, and the first character of the request-path that is not included in the
         * cookie- path is a %x2F ("/") character.
         */
        private boolean matchPath(String cookiePath, String requestPath) {
            if (requestPath.startsWith(cookiePath)) {
                int length = cookiePath.length();

                if (length == requestPath.length()) {
                    return true;
                }

                if (cookiePath.charAt(length - 1) == '/') {
                    return true;
                }

                if (requestPath.charAt(length) == '/') {
                    return true;
                }
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized List<HttpCookie> getCookies() {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized List<URI> getURIs() {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized boolean remove(URI uri, HttpCookie cookie) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized boolean removeAll() {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }
    }

    /**
     * Codec for cookie.
     */
    @SuppressWarnings("unused")
    private static class CookieCodec implements Encoder<HttpCookie>, Decoder<HttpCookie> {

        private static final String separator = " ";

        @Override
        public HttpCookie decode(String value) {
            String[] values = value.split(separator);
            HttpCookie cookie = new HttpCookie(values[0], values[1]);
            cookie.setDomain(values[2]);
            cookie.setPath(values[3]);
            cookie.setMaxAge(Long.parseLong(values[4]));
            cookie.setSecure(Boolean.parseBoolean(values[5]));
            cookie.setHttpOnly(Boolean.parseBoolean(values[6]));
            cookie.setVersion(Integer.parseInt(values[7]));

            return cookie;
        }

        @Override
        public String encode(HttpCookie value) {
            StringBuilder builder = new StringBuilder();
            builder.append(value.getName()).append(separator);
            builder.append(value.getValue()).append(separator);
            builder.append(value.getDomain()).append(separator);
            builder.append(value.getPath()).append(separator);
            builder.append(value.getMaxAge()).append(separator);
            builder.append(value.getSecure()).append(separator);
            builder.append(value.isHttpOnly()).append(separator);
            builder.append(value.getVersion());

            return builder.toString();
        }
    }
}