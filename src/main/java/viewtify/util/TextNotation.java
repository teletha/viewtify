/*
 * Copyright (C) 2019 viewtify Development Team
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
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;

import kiss.I;
import kiss.Signal;
import transcript.Transcript;
import viewtify.Viewtify;

/**
 * @version 2018/08/30 2:09:47
 */
public class TextNotation {

    /**
     * Parse as {@link TextFlow}.
     * 
     * @param message A wiki-like notation text.
     * @return
     */
    public static TextFlow parse(String message) {
        return parse(() -> message);
    }

    /**
     * Parse as {@link TextFlow}.
     * 
     * @param message A wiki-like notation text.
     * @return
     */
    public static TextFlow parse(Supplier<String> message) {
        return parse(lang -> I.signal(message));
    }

    /**
     * Parse as {@link TextFlow}.
     * 
     * @param message A wiki-like notation text.
     * @return
     */
    public static TextFlow parse(Transcript message) {
        return parse(message::as);
    }

    /**
     * Parse as {@link TextFlow}.
     * 
     * @param message A wiki-like notation text.
     * @return
     */
    private static TextFlow parse(Function<String, Signal<String>> message) {
        TextFlow flow = new TextFlow();
        ObservableList<Node> children = flow.getChildren();

        Transcript.current.observing().switchMap(I.wiseF(message)).on(Viewtify.UIThread).to(text -> {
            children.clear();
            parse(children, text);
        });
        return flow;
    }

    private static void parse(ObservableList<Node> children, String message) {
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
