/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import viewtify.keys.Command;

class CommandTest {

    @Test
    void contribute() {
        TestTask task = TestTask.NONE;
        assert task.name().equals("NONE");
        task.run();

        List<String> list = new ArrayList();
        task.contribute(() -> list.add("ADD"));
        assert list.size() == 0;

        task.run();
        assert list.size() == 1;

        task.run();
        assert list.size() == 2;
    }

    @Test
    void contributeMultiple() {
        TestTask task = TestTask.NONE;
        assert task.name().equals("NONE");
        task.run();

        List<String> list = new ArrayList();
        task.contribute(() -> list.add("FIRST"));
        task.contribute(() -> list.add("SECOND"));
        assert list.size() == 0;

        task.run();
        assert list.size() == 1;
        assert list.contains("SECOND");
    }

    /**
     * 
     */
    enum TestTask implements Command<TestTask> {
        NONE;
    }
}
