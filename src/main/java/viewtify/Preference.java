/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kiss.Manageable;
import kiss.Observer;
import kiss.Signal;
import kiss.Singleton;
import kiss.Storable;
import kiss.Variable;
import kiss.model.Model;
import kiss.model.Property;

/**
 * @version 2017/12/28 14:23:00
 */
@Manageable(lifestyle = Singleton.class)
public abstract class Preference<Self extends Preference> implements Storable<Self> {

    private final List<Observer<? super Boolean>> observers = new ArrayList();

    /**
     * 
     */
    protected Preference() {
        new Signal<>(observers).debounce(3, TimeUnit.SECONDS).to(Storable.super::store);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self store() {
        observers.forEach(o -> o.accept(true));
        return (Self) this;
    }

    /**
     * 
     */
    protected final void autoSave() {
        search(this, Model.of(this), this);
    }

    private void search(Preference root, Model<Object> model, Object object) {
        for (Property property : model.properties()) {
            if (property.isAttribute()) {
                try {
                    Field field = model.type.getDeclaredField(property.name);
                    if (field.getType() == Variable.class) {
                        Variable variable = (Variable) field.get(object);
                        variable.observe().diff().to(() -> {
                            root.store();
                        });
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
