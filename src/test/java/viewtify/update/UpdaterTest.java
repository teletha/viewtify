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

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import antibug.CleanRoom;

class UpdaterTest {

    @RegisterExtension
    private CleanRoom room = new CleanRoom();

    @Test
    void directory() {
        Path current = room.locateDirectory("current", dir -> {
            dir.file("file.txt", "text");
        });

        Path site = room.locateDirectory("site", dir -> {
            dir.file("file.txt", "updated");
        });

        Updater updater = new Updater().addUpdateSite(site).setRoot(current);
        assert updater.canUpdate().success == true;
    }
}
