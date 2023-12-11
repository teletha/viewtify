/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.update;

import java.text.DecimalFormat;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.text.Font;

import kiss.I;
import kiss.Variable;
import psychopath.Progress;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Theme;
import viewtify.ThemeType;
import viewtify.Viewtify;
import viewtify.ViewtyDialog.DialogView;
import viewtify.task.Monitor;
import viewtify.task.MonitorableTask;
import viewtify.ui.UIButton;
import viewtify.ui.UILabel;
import viewtify.ui.UIProgressBar;
import viewtify.ui.ViewDSL;

public class Updater extends DialogView<MonitorableTask> {

    /** The registered task. */
    static MonitorableTask<Progress> task;

    /** The update mode. */
    private final boolean forcibly;

    /** The updater UI. */
    private UILabel message;

    /** The updater UI. */
    private UILabel percentage;

    /** The updater UI. */
    private UILabel detail;

    /** The updater UI. */
    private UIProgressBar bar;

    /**
     * @param forcibly
     */
    Updater(boolean forcibly) {
        this.forcibly = forcibly;
    }

    /**
     * UI definition.
     */
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

    /**
     * Style definition.
     */
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
        Variable<UIButton> ok = find(ButtonData.OK_DONE);

        ok.to(button -> button.disableNow());
        value = task != null ? task : MonitorableTask.restore(System.getenv(Updater.class.getName()));

        Variable<String> mes = Variable.of("");
        mes.observe().switchVariable(I::translate).on(Viewtify.UIThread).to(x -> message.text(x));

        Variable<Double> per = Variable.of(0d);
        per.observe().on(Viewtify.UIThread).to(x -> {
            bar.value(x / 100d);
            percentage.text("(" + x.intValue() + "%)");
        });

        Viewtify.inWorker(() -> {
            try {
                value.accept(new Monitor<Progress>(mes, per, (monitor, progress) -> {
                    Thread.sleep(2);

                    monitor.complete(progress.rateByFiles());
                    Viewtify.inUI(() -> detail.text(progress.location.name() + "  (" + formatFileSize(progress.location.size()) + ")"));
                }));

                Viewtify.inUI(() -> {
                    percentage.text("");
                    detail.text("");
                    bar.value(1d);

                    ok.to(button -> {
                        button.enableNow();
                        if (forcibly) button.fire();
                    });
                });
            } catch (Throwable e) {
                mes.set(e.getMessage());
                throw I.quiet(e);
            }
        });
    }

    private static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes <= 0) {
            return "0B";
        }

        final String[] units = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        int digitGroups = (int) (Math.log10(sizeInBytes) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(sizeInBytes / Math.pow(1024, digitGroups)) + units[digitGroups];
    }

    /**
     * Entry point for updater.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Viewtify.application()
                .title("Updating")
                .icon(I.env("Icon"))
                .use(I.env("Theme", Theme.Light))
                .use(I.env("ThemeType", ThemeType.Flat))
                .use(Font.font(I.env("Font"), I.env("FontSize", 12)))
                .onInitialize((stage, scene) -> {
                    stage.setX(I.env("LocationX", 0d));
                    stage.setY(I.env("LocationY", 0d));
                })
                .activate(Updater.class);
    }
}