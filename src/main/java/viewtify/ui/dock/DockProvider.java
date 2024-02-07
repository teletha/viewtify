/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.dock;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;

import kiss.Extensible;
import kiss.I;
import kiss.Managed;
import kiss.Model;
import kiss.Singleton;
import kiss.Ⅱ;

@Managed(Singleton.class)
public abstract class DockProvider implements Extensible {

    /** The managed independent dock items. */
    private List<Dock> docks;

    private List<Ⅱ<TypedDock, Class>> typed;

    /**
     * Initialize and analyze
     */
    protected DockProvider() {
    }

    private List<Dock> docks() {
        if (docks == null) {
            docks = I.signal(getClass().getFields())
                    .take(field -> Modifier.isFinal(field.getModifiers()) && field.getType() == Dock.class)
                    .map(field -> (Dock) field.get(this))
                    .sort(Comparator.comparing(Dock::id))
                    .toList();
        }
        return docks;
    }

    private List<Ⅱ<TypedDock, Class>> typed() {
        if (typed == null) {
            typed = I.signal(getClass().getFields())
                    .take(field -> Modifier.isFinal(field.getModifiers()) && field.getType() == TypedDock.class)
                    .map(field -> I
                            .pair((TypedDock) field.get(this), (Class) Model.collectParameters(field.getGenericType(), TypedDock.class)[0]))
                    .toList();
        }
        return typed;
    }

    /**
     * Query all independent views.
     */
    public List<Dock> findDocks() {
        return docks();
    }

    /**
     * Query all independent views.
     */
    public List<TypedDock> findTypedDocks() {
        return typed().stream().map(x -> x.ⅰ).toList();
    }

    /**
     * Request registering dock by id.
     * 
     * @param id
     * @return
     */
    protected boolean register(String id) {
        for (Dock dock : docks()) {
            if (dock.id().equals(id)) {
                dock.registration.accept(dock);
                return true;
            }
        }

        int index = id.indexOf(' ');
        if (index != -1) {
            String prefix = id.substring(0, index);
            String param = id.substring(index + 1);

            for (Ⅱ<TypedDock, Class> dock : typed()) {
                if (dock.ⅰ.id.equalsIgnoreCase(prefix)) {
                    dock.ⅰ.show(I.transform(param, dock.ⅱ));
                }
            }
        }

        return false;
    }
}