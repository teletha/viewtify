/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.util.Deque;
import java.util.LinkedList;

import kiss.Disposable;
import kiss.Extensible;
import kiss.I;
import transcript.Lang;
import transcript.Transcript;

public interface Command<E extends Enum<E>> extends Extensible {

    /**
     * Command identical name.
     * 
     * @return
     */
    String name();

    /**
     * Command description.
     * 
     * @return
     */
    default String description() {
        String name = name();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i == 0) {
                    builder.append(c);
                } else {
                    builder.append(' ').append(Character.toLowerCase(c));
                }
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * Localized command description.
     * 
     * @param lang
     * @return
     */
    default String localizedDescrition(Lang lang) {
        return Transcript.en(description()).get(lang).v;
    }

    /**
     * Activate this command.
     * 
     */
    default void activate() {
        Deque<Runnable> stack = Viewtify.commands.get(this);

        if (stack != null && !stack.isEmpty()) {
            stack.peekLast().run();
        }
    }

    /**
     * Contribute the actual command.
     * 
     * @param command
     * @return
     */
    default Disposable contribute(Runnable command) {
        if (command == null) {
            return Disposable.empty();
        }

        Deque<Runnable> stack = Viewtify.commands.computeIfAbsent(this, k -> new LinkedList());

        stack.addLast(command);
        return () -> {
            stack.remove(command);

            if (stack.isEmpty()) {
                Viewtify.commands.remove(this);
            }
        };
    }

    /**
     * Asign the default shortcut key.
     * 
     * @param key
     * @return
     */
    default E defaultKey(Key key) {
        if (key != null) {
            I.make(ShortcutManager.class).bindAsDefault(key, this);;
        }
        return (E) this;
    }
}