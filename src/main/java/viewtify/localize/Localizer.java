/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.localize;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kiss.I;
import psychopath.Location;
import psychopath.Locator;

/**
 * 
 */
public final class Localizer {

    /** The target classes to localize. */
    private final Set<Class> classes = new HashSet();

    /**
     * Add target localized class directory or archive.
     * 
     * @param locator
     * @return
     */
    public Localizer addAllClassesIn(Class locator) {
        if (locator != null) {
            Location location = Locator.locate(locator);
            location.walkFile("**.class")
                    .map(clazz -> location.relativize(clazz).path().replaceAll("/", ".").replaceAll(".class", ""))
                    .map(I::type)
                    .toCollection(classes);
        }
        return this;
    }

    /**
     * Add target localize class.
     * 
     * @param classes
     * @return
     */
    public Localizer addClass(Class... classes) {
        return addClass(List.of(classes));
    }

    /**
     * Add target localize class.
     * 
     * @param classes
     * @return
     */
    public Localizer addClass(Iterable<Class> classes) {
        if (classes != null) {
            for (Class clazz : classes) {
                this.classes.add(clazz);
            }
        }
        return this;
    }

    public void localize() {
        I.signal(classes)
                .flatArray(clazz -> clazz.getDeclaredFields())
                .take(field -> Modifier.isStatic(field.getModifiers()) && field.getType() == Text.class)
                .effect(field -> field.setAccessible(true))
                .map(field -> (Text) field.get(null))
                .to(text -> {
                    System.out.println(text.get());
                });
    }
}
