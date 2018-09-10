/*
 * Copyright (C) 2018 viewtify Development Team
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

/**
 * @version 2018/08/29 11:25:48
 */
class UIDeclarationTest extends JavaFXTester {

    private UILabel label = new UILabel(null);

    @Test
    void label() {
        UI fxml = new UI() {
            {
                $(label);
            }
        };

        Node node = fxml.build();
        assert node instanceof Label;
    }

    @Test
    void vbox() {
        UI fxml = new UI() {
            {
                $(vbox, () -> {
                    $(label);
                });
            }
        };

        VBox root = as(fxml.build(), VBox.class);
        Label label = as(root.getChildren().get(0), Label.class);
    }

    @Test
    void vboxLambda() {
        UI fxml = new UI() {
            {
                $(vbox, () -> {
                    $(label);
                });
            }
        };

        VBox root = as(fxml.build(), VBox.class);
        Label label = as(root.getChildren().get(0), Label.class);
    }

    @Test
    void vboxNest() {
        UI fxml = new UI() {
            {
                $(vbox, () -> {
                    $(vbox, () -> {
                        $(label);
                    });
                });
            }
        };

        VBox root = as(fxml.build(), VBox.class);
        VBox child = as(root.getChildren().get(0), VBox.class);
        Label label = as(child.getChildren().get(0), Label.class);
    }

    @Test
    void vboxNestLambda() {
        UI fxml = new UI() {
            {
                $(vbox, () -> {
                    $(vbox, () -> {
                        $(label);
                    });
                });
            }
        };

        VBox root = as(fxml.build(), VBox.class);
        VBox child = as(root.getChildren().get(0), VBox.class);
        Label label = as(child.getChildren().get(0), Label.class);
    }
}
