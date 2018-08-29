/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.dsl;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import org.junit.jupiter.api.Test;

import viewtify.JavaFXTester;
import viewtify.UI;
import viewtify.dsl.UIDefinition;
import viewtify.ui.UILabel;

/**
 * @version 2018/08/29 11:25:48
 */
class UIDefinitionTest extends JavaFXTester {

    private @UI UILabel label = new UILabel(new Label(), null);

    @Test
    void label() {
        UIDefinition fxml = new UIDefinition() {
            {
                $(label);
            }
        };

        Node node = fxml.build();
        assert node instanceof Label;
    }

    @Test
    void vbox() {
        UIDefinition fxml = new UIDefinition() {
            {
                vbox(label);
            }
        };

        VBox root = as(fxml.build(), VBox.class);
        Label label = as(root.getChildren().get(0), Label.class);
    }

    @Test
    void vboxLambda() {
        UIDefinition fxml = new UIDefinition() {
            {
                vbox(() -> {
                    $(label);
                });
            }
        };

        VBox root = as(fxml.build(), VBox.class);
        Label label = as(root.getChildren().get(0), Label.class);
    }

    @Test
    void vboxNest() {
        UIDefinition fxml = new UIDefinition() {
            {
                vbox(() -> {
                    vbox(label);
                });
            }
        };

        VBox root = as(fxml.build(), VBox.class);
        VBox child = as(root.getChildren().get(0), VBox.class);
        Label label = as(child.getChildren().get(0), Label.class);
    }

    @Test
    void vboxNestLambda() {
        UIDefinition fxml = new UIDefinition() {
            {
                vbox(() -> {
                    vbox(() -> {
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
