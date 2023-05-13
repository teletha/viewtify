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

import kiss.I;
import kiss.Variable;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.ui.UILabel;
import viewtify.ui.UIProgressBar;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.Verifier;
import viewtify.ui.helper.VerifyHelper;
import viewtify.update.UpdateTask.Task;

public class UpdaterView extends View implements VerifyHelper<UpdaterView> {

    UILabel message;

    UILabel detail;

    UIProgressBar bar;

    private final Verifier verifier = new Verifier().makeInvalid();

    class UI extends ViewDSL {
        {
            $(vbox, style.root, () -> {
                $(message);
                $(bar, style.bar);
                $(detail);
            });
        }
    }

    interface style extends StyleDSL {

        Style root = () -> {
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

        execute();
    }

    private void execute() {
        Viewtify.inWorker(() -> {
            // if (property == null) {
            // Directory root = Locator.directory("app").absolutize();
            // Directory backup = Locator.directory(".backup");
            // Directory lib = Locator.directory("lib");
            // Directory jre = Locator.directory("jre");
            //
            // UpdateTask tasks = new UpdateTask();
            // tasks.delay = 10;
            // tasks.rebootApp = root.file("yamato.exe");
            // tasks.delete("Deleting the old version.", root.directory(".test"));
            // tasks.unpack("Unpacking the new version.", root.file("test.zip"), root);
            // property = I.write(tasks);
            // System.out.println(property);
            // }

            UpdateTask update = new UpdateTask();
            update.restore();

            List<Task> tasks = update.tasks;

            try {
                for (int i = 0; i < tasks.size(); i++) {
                    Task task = tasks.get(i);
                    Variable<String> m = I.translate(task.message);
                    Viewtify.inUI(() -> message.text(m));

                    double part = 1d / tasks.size();
                    task.accept(progress -> {
                        if (update.delay != 0) {
                            try {
                                Thread.sleep(update.delay);
                            } catch (InterruptedException e) {
                                // ignore
                            }
                        }

                        bar.value(v -> v + part / progress.totalFiles);
                        Viewtify.inUI(() -> {
                            message.text(m + " (" + progress.rateByFiles() + "%)");
                            detail.text(progress.location);
                        });
                    });
                    bar.value(part * (i + 1));
                    Viewtify.inUI(() -> detail.text(""));
                }
                verifier.makeValid();
            } catch (Throwable e) {
                message.text(I.translate(e.getMessage()));
            }

            // try {
            // new
            // ProcessBuilder().directory(update.rebootApp.parent().asJavaFile()).inheritIO().command(update.rebootApp.path()).start();
            // } catch (Throwable e) {
            // e.printStackTrace();
            // }
            //
            // Viewtify.application().deactivate();
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
     * Entry point for updater.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Viewtify.application().title("Updater").size(400, 150).activate(UpdaterView.class);
    }
}
