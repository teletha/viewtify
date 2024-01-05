/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.update;

import java.util.List;

import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;

import kiss.I;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import viewtify.Viewtify;
import viewtify.preference.Preferences;
import viewtify.ui.view.AppearanceSetting;
import viewtify.util.ScreenSelector;

public class Update {

    /**
     * Check whether the application can be updatable or not.
     * 
     * @return An error message.
     */
    public static boolean isAvailable(String archive) {
        return validate(archive) == null;
    }

    /**
     * Check whether the application can be updatable or not.
     * 
     * @return An error message.
     */
    private static String validate(String archive) {
        // ====================================
        // check parameter
        // ====================================
        if (archive == null || archive.isBlank()) {
            return "Unable to update because the location of the new application is unknown.";
        }

        // ====================================
        // check archive
        // ====================================
        File file = Locator.file(archive).absolutize();

        if (file.isAbsent() || !file.extension().equals("zip")) {
            return "Zipped archive [" + archive + "] is not found.";
        }

        // ====================================
        // check version
        // ====================================
        Blueprint origin = Blueprint.detect();
        if (!file.isAfter(origin.root)) {
            return "The latest version is used, no need to update.";
        }

        // We can update
        return null;
    }

    /**
     * Build the update task for the specified new version.
     */
    public static void apply() {
        apply(Viewtify.application().updateSite(), false);
    }

    /**
     * Build the update task for the specified new version.
     * 
     * @param archive A location of new version.
     * @param forcibly Force to update or not.
     */
    public static void apply(String archive, boolean forcibly) {
        File file = Locator.file(archive).absolutize();
        Directory updateDir = Locator.directory(".updater").absolutize();
        Blueprint origin = Blueprint.detect();

        // prepare to update
        Updater.task = monitor -> {
            monitor.message("Checking the latest version.");

            String error = validate(archive);
            if (error != null) {
                monitor.error(error);
            }

            // ====================================
            // unpack archive
            // ====================================
            monitor.message("Prepare to update.", 2);
            file.trackUnpackingTo(updateDir, option -> option.sync().replaceDifferent()).to(monitor.spawn(98));

            monitor.message("Ready for update.", 100);
        };

        Viewtify.dialog()
                .title(I.translate("Preparing to update application."))
                .button("Update", "Cancel")
                .translatable()
                .disableButtons(forcibly)
                .disableCloseButton(forcibly)
                .fadable(Side.TOP)
                .show(new Updater(forcibly))
                .to(tasks -> {
                    Rectangle2D bounds = ScreenSelector.Application.select();
                    AppearanceSetting appearance = Preferences.of(AppearanceSetting.class);

                    origin.updater()
                            .env("Icon", Viewtify.application().icon())
                            .env("Theme", appearance.theme.v.name())
                            .env("ThemeType", appearance.themeType.v.name())
                            .env("Font", appearance.font.v)
                            .env("FontSize", appearance.fontSize.v.toString())
                            .env("LocationX", String.valueOf(bounds.getMinX() + bounds.getWidth() / 2 - 190))
                            .env("LocationY", String.valueOf(bounds.getMinY() + bounds.getHeight() / 2 - 60))
                            .reboot(monitor -> {
                                monitor.message("Installing the new version, please wait a minute.");

                                // ====================================
                                // copy resources
                                // ====================================
                                List<String> patterns = updateDir.children().map(c -> c.isFile() ? c.name() : c.name() + "/**").toList();
                                patterns.add("!.preferences for */**");
                                updateDir.trackCopyingTo(origin.root, o -> o.strip().glob(patterns).replaceDifferent().sync()).to(monitor);

                                // ====================================
                                // update version
                                // ====================================
                                origin.root.lastModifiedTime(file.lastModifiedTime());

                                monitor.message("Update is completed, reboot.", 100);
                                origin.reboot();
                            });
                });
    }
}