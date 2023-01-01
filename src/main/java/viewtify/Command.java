/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.util.Deque;
import java.util.LinkedList;

import kiss.Disposable;
import kiss.Extensible;
import kiss.I;
import kiss.WiseRunnable;

public interface Command<E extends Enum<E>> extends Extensible, WiseRunnable {

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
     * Activate this command.
     */
    @Override
    default void RUN() throws Throwable {
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
    default E shortcut(Key key) {
        if (key != null) {
            I.make(ShortcutManager.class).bindAsDefault(key, this);
        }
        return (E) this;
    }
}