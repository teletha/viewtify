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

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.WindowEvent;
import kiss.I;
import kiss.Variable;
import psychopath.Progress;
import stylist.Style;
import stylist.StyleDSL;
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
            display.minWidth(380, px).width(380, px);
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
        stage().to(stage -> {
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.setOnCloseRequest(WindowEvent::consume);
        });

        UIButton ok = find(ButtonData.OK_DONE);

        if (ok != null) ok.disableNow();
        value = task != null ? task : MonitorableTask.restore(System.getenv(Updater.class.getName()));

        Variable<String> mes = Variable.of("");
        mes.observe().switchVariable(x -> I.translate(x)).on(Viewtify.UIThread).to(x -> message.text(x));

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
                    Viewtify.inUI(() -> detail.text(progress.location.name()));
                }));

                Viewtify.inUI(() -> {
                    percentage.text("");
                    detail.text("");
                    bar.value(1d);

                    if (ok != null) {
                        ok.enableNow();
                        if (forcibly) ok.fire();
                    }
                });
            } catch (Throwable e) {
                mes.set(e.getMessage());
                throw I.quiet(e);
            }
        });
    }

    /**
     * Entry point for updater.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Viewtify.application().title("Updater").size(400, 180).activate(Updater.class);
    }
}