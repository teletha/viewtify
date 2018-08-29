/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.dsl;

import kiss.Extensible;
import kiss.Manageable;
import kiss.Singleton;
import viewtify.dsl.Style;

/**
 * @version 2018/08/29 15:09:15
 */
@Manageable(lifestyle = Singleton.class)
public abstract class StyleDSL implements Extensible {

    /**
     * Create empty {@link Style}.
     * 
     * @return
     */
    protected static Style empty() {
        // DON'T use lambda, we need different instance at every call.
        return new Style() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void declare() {
            }
        };
    }
}
