/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.model;

import org.junit.Test;

import kiss.Variable;
import viewtify.model.Selectable;

/**
 * @version 2018/03/13 11:30:55
 */
public class SelectableTest {

    @Test
    public void select() {
        Model model = new Model();
        Variable<String> result = model.select.to();
    }

    /**
     * @version 2018/03/13 11:31:25
     */
    private static class Model extends Selectable<String> {
    }
}
