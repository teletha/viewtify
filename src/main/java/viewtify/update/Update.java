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

import java.util.List;

import javafx.scene.control.ButtonType;

import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import viewtify.Viewtify;

public class Update {

    /**
     * Build the update task for the specified new version.
     * 
     * @param archive A locaiton of the new version.
     */
    public static void apply(String archive) {
        Directory updateDir = Locator.directory(".updater").absolutize();
        Blueprint origin = Blueprint.detect();

        // prepare to update
        Updater.task = monitor -> {
            monitor.message("Checking the latest version.");

            // ====================================
            // check parameter
            // ====================================
            if (archive == null || archive.isBlank()) {
                monitor.error("Unable to update because the location of the new application is unknown.");
            }

            // ====================================
            // check archive
            // ====================================
            File file = Locator.file(archive).absolutize();

            if (file.isAbsent() || !file.extension().equals("zip")) {
                monitor.error("Zipped archive [" + archive + "] is not found.");
            }

            // ====================================
            // check modification
            // ====================================
            if (file.isBefore(origin.root)) {
                monitor.error("The latest version is used, no need to update.");
            }

            // ====================================
            // unpack archive
            // ====================================
            monitor.message("Prepare to update.", 2);
            file.trackUnpackingTo(updateDir, option -> option.sync().replaceDifferent()).to(monitor.spawn(98));

            monitor.message("Ready for update.", 100);
        };

        Viewtify.dialog("Updater", Updater.class, ButtonType.APPLY, ButtonType.CLOSE).ifPresent(tasks -> {
            origin.updater().reboot(monitor -> {
                monitor.message("Installing the new version, please wait a minute.");

                // ====================================
                // copying resources
                // ====================================
                List<String> patterns = updateDir.children().map(c -> c.isFile() ? c.name() : c.name() + "/**").toList();
                patterns.add("!.preferences for */**");
                updateDir.trackCopyingTo(origin.root, o -> o.strip().glob(patterns).replaceDifferent().sync()).to(monitor);

                monitor.message("Update is completed, reboot.", 100);
                origin.reboot();
            });
        });
    }
}
