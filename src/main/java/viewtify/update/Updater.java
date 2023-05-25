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

import javafx.beans.property.Property;
import javafx.scene.control.ButtonType;
import kiss.I;
import kiss.Variable;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import psychopath.Progress;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.property.SmartProperty;
import viewtify.task.Monitor;
import viewtify.task.MonitorableTask;
import viewtify.ui.UILabel;
import viewtify.ui.UIProgressBar;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.ValueHelper;
import viewtify.ui.helper.Verifier;
import viewtify.ui.helper.VerifyHelper;

public class Updater extends View implements VerifyHelper<Updater>, ValueHelper<Updater, MonitorableTask> {

    /** The dirty variable bridge. */
    private static MonitorableTask<Progress> task;

    UILabel message;

    UILabel percentage;

    UILabel detail;

    UIProgressBar bar;

    private final Verifier verifier = new Verifier().makeInvalid();

    private final SmartProperty<MonitorableTask> property = new SmartProperty();

    class UI extends ViewDSL {
        {
            $(vbox, style.root, () -> {
                $(hbox, () -> {
                    $(message);
                    $(percentage);
                });
                $(bar, style.bar);
                $(detail);
            });
        }
    }

    interface style extends StyleDSL {

        Style root = () -> {
            display.minWidth(380, px);
            padding.size(10, px);
        };

        Style bar = () -> {
            display.width.fill();
            margin.vertical(5, px);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        message.text("Updating...");
        bar.value(0d);

        MonitorableTask update = task != null ? task : MonitorableTask.restore(System.getenv("updater"));

        Variable<String> mes = Variable.of("");
        mes.observe().switchVariable(x -> I.translate(x)).on(Viewtify.UIThread).to(x -> message.text(x));

        Variable<Double> per = Variable.of(0d);
        per.observe().on(Viewtify.UIThread).to(x -> {
            bar.value(x / 100d);
            percentage.text("(" + x.intValue() + "%)");
        });

        Viewtify.inWorker(() -> {
            try {
                update.accept(new Monitor<Progress>(mes, per, (monitor, progress) -> {
                    Thread.sleep(2);

                    monitor.complete(progress.rateByFiles());
                    Viewtify.inUI(() -> detail.text(progress.location));
                }));

                Viewtify.inUI(() -> {
                    percentage.text("");
                    detail.text("");
                    bar.value(1d);
                });
                verifier.makeValid();

                property.set(update);
            } catch (Throwable e) {
                mes.set(e.getMessage());
                throw I.quiet(e);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Verifier verifier() {
        return verifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<MonitorableTask> valueProperty() {
        return property;
    }

    /**
     * Entry point for updater.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Viewtify.application().title("Updater").size(400, 150).activate(Updater.class);
    }

    /**
     * Build the update task for the specified new version.
     * 
     * @param archive A locaiton of the new version.
     */
    public static void apply(String archive) {
        Directory updateDir = Locator.directory(".updater").absolutize();
        ApplicationPlatform origin = ApplicationPlatform.current();

        // prepare to update
        Updater.task = monitor -> {
            monitor.message("Verify the update.");

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
                monitor.error("The current version is latest, no need to update.");
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
