/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.query;

import java.util.regex.Pattern;

import antibug.profiler.Benchmark;
import net.bytebuddy.utility.RandomString;

public class ContainBenchmark {

    public static void main(String[] args) {
        Benchmark benchmark = new Benchmark();

        String value = "abcdefghijklmn";

        benchmark.measure("String.contains", () -> {
            return RandomString.make(10).toLowerCase().contains("hijk".toLowerCase());
        });

        Pattern pattern = Pattern.compile(Pattern.quote("hijk"), Pattern.CASE_INSENSITIVE);
        benchmark.measure("RegEx", () -> {
            return pattern.matcher(RandomString.make(10)).find();
        });
        System.out.println(pattern.matcher(value).find());

        benchmark.perform();
    }
}