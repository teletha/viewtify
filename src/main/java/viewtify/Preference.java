/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import static java.util.concurrent.TimeUnit.*;

import java.lang.reflect.Field;

import kiss.Manageable;
import kiss.Signaling;
import kiss.Singleton;
import kiss.Storable;
import kiss.Variable;
import kiss.model.Model;
import kiss.model.Property;

/**
 * @version 2018/08/27 10:40:07
 */
@Manageable(lifestyle = Singleton.class)
public abstract class Preference<Self extends Preference> implements Storable<Self> {

    /** The save event manager. */
    private final Signaling<Boolean> saver = new Signaling();

    /**
     * Hide constructor.
     */
    protected Preference() {
        saver.expose.debounce(3, SECONDS).to(Storable.super::store);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self store() {
        saver.accept(true);
        return (Self) this;
    }

    /**
     * Make this {@link Preference} auto-savable.
     */
    protected final void autoSave() {
        search(this, Model.of(this), this);
    }

    /**
     * Search autosavable {@link Variable} property.
     * 
     * @param root
     * @param model
     * @param object
     */
    private void search(Preference root, Model<Object> model, Object object) {
        for (Property property : model.properties()) {
            if (property.isAttribute()) {
                try {
                    Field field = model.type.getDeclaredField(property.name);
                    if (field.getType() == Variable.class) {
                        Variable variable = (Variable) field.get(object);
                        variable.observe().diff().to(root::store);
                    }
                } catch (Exception e) {
                    // ignore
                }
            } else {
                search(root, property.model, model.get(object, property));
            }
        }
    }
}
