/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.keys;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.input.KeyEvent;
import kiss.Managed;
import kiss.Singleton;
import kiss.Storable;
import kiss.Variable;
import kiss.WiseRunnable;

@Managed(Singleton.class)
public final class ShortcutManager implements Storable<ShortcutManager> {

    /** Command Repository */
    static final Map<Command, Deque<WiseRunnable>> commands = new ConcurrentHashMap();

    private Map<Key, Command> overridden = new HashMap();

    private Map<Key, Command> defaults = new HashMap();

    /**
     * Hide
     */
    private ShortcutManager() {
        restore();
    }

    /**
     * Assign the default shortcut key for the specified command.
     * 
     * @param key
     * @param id
     */
    public void bindAsDefault(Key key, Command id) {
        if (key != null && id != null) {
            defaults.put(key, id);
        }
    }

    /**
     * Assign the shortcut key for the specified command.
     * 
     * @param key
     * @param id
     */
    public void bind(Key key, Command id) {
        if (key != null && id != null) {
            overridden.put(key, id);
        }
    }

    public void unbind(Command id) {
        if (id != null) {
            Iterator<Entry<Key, Command>> iterator = overridden.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<Key, Command> entry = iterator.next();
                if (entry.getValue() == id) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Activate command by {@link KeyEvent}.
     * 
     * @param e
     */
    public void activate(KeyEvent e) {
        Key key = new Key(e);

        Command command = overridden.get(key);
        if (command != null) {
            command.run();
            return;
        }

        command = defaults.get(key);
        if (command != null) {
            command.run();
            return;
        }
    }

    /**
     * Detect the associated shortcut key.
     * 
     * @param command
     * @return
     */
    public Variable<Key> detectKey(Command command) {
        Variable<Key> set = findByCommand(overridden, command);
        if (set.isAbsent()) {
            set = findByCommand(defaults, command);
        }
        return set;
    }

    private Variable<Key> findByCommand(Map<Key, Command> map, Command command) {
        for (Entry<Key, Command> entry : map.entrySet()) {
            if (entry.getValue() == command) {
                return Variable.of(entry.getKey());
            }
        }
        return Variable.empty();
    }
}