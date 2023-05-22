/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.update;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import antibug.CleanRoom;
import psychopath.Directory;
import psychopath.Locator;

class InstructionTest {

    @RegisterExtension
    CleanRoom room = new CleanRoom();

    @Test
    void task() {
        Instruction task = Instruction.create(inst -> {
            inst.message("Do nothing");
        });

        Instruction restored = Instruction.restore(task.store());
        restored.execute();

        assert restored.message.get().equals("Do nothing");
    }

    @Test
    void directory() {
        Directory dir = Locator.directory(room.locateDirectory("dir"));

        Instruction task = Instruction.create(inst -> {
            dir.file("file").text("create");
        });

        Instruction restored = Instruction.restore(task.store());
        restored.execute();

        assert dir.file("file").isPresent();
    }
}
