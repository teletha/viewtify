/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import java.awt.Desktop;
import java.net.URI;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import viewtify.Viewtify;

public class TextNotation {

    /**
     * Concat all messages.
     * 
     * @param messages
     * @return
     */
    public static Signal<String> concat(Object... messages) {
        if (messages.length == 0) {
            return I.signal();
        }

        Signal<String> first = literalize(messages[0]);
        Signal<String>[] remainings = new Signal[messages.length - 1];
        for (int i = 0; i < remainings.length; i++) {
            remainings[i] = literalize(messages[i + 1]);
        }
        return first.combineLatest(remainings, String::concat);
    }

    /**
     * Literalize the message.
     * 
     * @param message
     * @return
     */
    private static Signal<String> literalize(Object message) {
        if (message == null) {
            return I.signal("");
        }

        Signal<Object> signal;
        if (message instanceof Signal) {
            signal = (Signal) message;
        } else if (message instanceof Variable) {
            signal = ((Variable) message).observing();
        } else if (message instanceof ObservableValue) {
            signal = Viewtify.observing((ObservableValue) message);
        } else {
            signal = I.signal(message);
        }
        return signal.map(value -> I.transform(value, String.class));
    }

    /**
     * Parse as {@link TextFlow}.
     * 
     * @param message A wiki-like notation text.
     * @return
     */
    public static TextFlow parse(String message) {
        TextFlow flow = new TextFlow();
        parse(flow.getChildren(), message);
        return flow;
    }

    /**
     * Parse as {@link TextFlow}.
     * 
     * @param message A wiki-like notation text.
     * @return
     */
    public static TextFlow parse(Variable message) {
        TextFlow flow = new TextFlow();
        ObservableList<Node> children = flow.getChildren();

        message.observing().on(Viewtify.UIThread).to(text -> {
            children.clear();
            parse(children, I.transform(text, String.class));
        });
        return flow;
    }

    private static void parse(ObservableList<Node> children, String message) {
        if (message == null) {
            return;
        }

        boolean inLink = false;
        boolean inURL = false;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);

            switch (c) {
            case '[':
                inLink = true;
                if (builder.length() != 0) {
                    children.add(new Label(builder.toString()));
                    builder = new StringBuilder();
                }
                break;

            case ']':
                if (inLink) {
                    inLink = false;
                    if (builder.length() != 0) {
                        children.add(new Hyperlink(builder.toString()));
                        builder = new StringBuilder();

                        if (message.charAt(i + 1) == '(') {
                            inURL = true;
                            i++;
                        }
                    }
                } else {
                    builder.append(c);
                }
                break;

            case ')':
                if (inURL) {
                    inURL = false;
                    String uri = builder.toString();
                    Hyperlink link = (Hyperlink) children.get(children.size() - 1);
                    link.setOnAction(e -> {
                        try {
                            Desktop.getDesktop().browse(new URI(uri));
                        } catch (Throwable error) {
                            throw I.quiet(error);
                        }
                    });
                    builder = new StringBuilder();
                    break;
                }

            default:
                builder.append(c);
                break;
            }
        }

        if (children.isEmpty()) {
            children.add(new Label(builder.toString()));
        } else if (builder.length() != 0) {
            children.add(new Label(builder.toString()));
        }
    }
}