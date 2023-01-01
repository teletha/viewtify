/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.property;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

import antibug.profiler.Benchmark;

class PropertyBenchmark {

    public static void main(String[] args) {
        Benchmark benchmark = new Benchmark();
        InvalidationListener listener = o -> {
        };

        benchmark.measure("Smart", () -> {
            Property<String> p = new SmartProperty();
            p.addListener(listener);
            p.setValue("ok");
            p.removeListener(listener);
            return p;
        });

        benchmark.measure("JavaFX", () -> {
            Property<String> p = new SimpleStringProperty();
            p.addListener(listener);
            p.setValue("ok");
            p.removeListener(listener);
            return p;
        });
        benchmark.perform();
    }
}