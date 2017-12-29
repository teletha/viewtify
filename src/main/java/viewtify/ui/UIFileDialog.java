/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.io.File;
import java.nio.file.Path;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import kiss.I;
import kiss.Signal;
import viewtify.Viewtify;

/**
 * @version 2017/12/29 18:30:36
 */
public class UIFileDialog {

    /** The actual ui. */
    private final FileChooser chooser;

    /**
     * 
     */
    private UIFileDialog(String title) {
        this.chooser = new FileChooser();
        this.chooser.setTitle(title);
        this.chooser.setInitialDirectory(new File("").getAbsoluteFile());
    }

    /**
     * @param description
     * @param filters
     * @return
     */
    public UIFileDialog filter(String description, String... filters) {
        this.chooser.setSelectedExtensionFilter(new ExtensionFilter(description, I.list(filters)));

        return this;
    }

    /**
     * @return
     */
    public Signal<Path> select() {
        return new Signal<Path>((observer, disposer) -> {
            File selected = chooser.showOpenDialog(Viewtify.root().root().getScene().getWindow());

            if (selected != null) {
                observer.accept(selected.toPath());
                observer.complete();
            }
            return disposer;
        });
    }

    /**
     * Create new {@link FileChooser} dialog.
     * 
     * @param title
     * @return
     */
    public static final UIFileDialog title(String title) {
        return new UIFileDialog(title);
    }
}
