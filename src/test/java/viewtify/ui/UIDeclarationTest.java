/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import org.junit.jupiter.api.Test;

import viewtify.JavaFXTester;

class UIDeclarationTest extends JavaFXTester {

    private UILabel label = new UILabel(null);

    @Test
    void label() {
        ViewDSL fxml = new ViewDSL() {
            {
                $(label);
            }
        };

        Node node = fxml.ui();
        assert node instanceof Label;
    }

    @Test
    void vbox() {
        ViewDSL fxml = new ViewDSL() {
            {
                $(vbox, () -> {
                    $(label);
                });
            }
        };

        VBox root = as(fxml.ui(), VBox.class);
        as(root.getChildren().get(0), Label.class);
    }

    @Test
    void vboxLambda() {
        ViewDSL fxml = new ViewDSL() {
            {
                $(vbox, () -> {
                    $(label);
                });
            }
        };

        VBox root = as(fxml.ui(), VBox.class);
        as(root.getChildren().get(0), Label.class);
    }

    @Test
    void vboxNest() {
        ViewDSL fxml = new ViewDSL() {
            {
                $(vbox, () -> {
                    $(vbox, () -> {
                        $(label);
                    });
                });
            }
        };

        VBox root = as(fxml.ui(), VBox.class);
        VBox child = as(root.getChildren().get(0), VBox.class);
        as(child.getChildren().get(0), Label.class);
    }

    @Test
    void vboxNestLambda() {
        ViewDSL fxml = new ViewDSL() {
            {
                $(vbox, () -> {
                    $(vbox, () -> {
                        $(label);
                    });
                });
            }
        };

        VBox root = as(fxml.ui(), VBox.class);
        VBox child = as(root.getChildren().get(0), VBox.class);
        as(child.getChildren().get(0), Label.class);
    }
}