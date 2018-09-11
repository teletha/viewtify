/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.util;

import javafx.beans.property.SimpleIntegerProperty;

import org.junit.jupiter.api.Test;

/**
 * @version 2018/09/11 16:29:05
 */
class RegulatablePropertyTest {

    @Test
    void ensure() {
        RegulatableProperty<Integer> property = new RegulatableProperty(new SimpleIntegerProperty(1));
        property.ensure(v -> v < 10);

        property.setValue(10);
        assert property.getValue() == 1;
    }
}
