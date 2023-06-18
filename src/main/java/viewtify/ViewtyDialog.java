/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.util.List;
import java.util.function.Supplier;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import kiss.Disposable;
import kiss.I;
import kiss.Variable;
import kiss.WiseConsumer;
import viewtify.style.FormStyles;
import viewtify.ui.UIButton;
import viewtify.ui.UILabel;
import viewtify.ui.UIText;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

/**
 * The specialized dialog builder.
 */
public final class ViewtyDialog<T> {

    /** The associated stage. */
    private final Stage stage;

    /** The dialog style. */
    private StageStyle style = StageStyle.DECORATED;

    /** The title of this dialog. */
    private Variable<String> title;

    /** The button set. */
    private List<ButtonType> buttons;

    /** The translation mode. */
    private boolean needTranslate;

    /** The button's visibility. */
    private boolean disableButtons;

    /** The close button's visibility. */
    private boolean disableCloseButton;

    /** The button's order setting. */
    private boolean disableSystemButtonOrder;

    /** The width of this dialog. */
    private int width;

    /** The height of this dialog. */
    private int height;

    /** The diposer. */
    private final Disposable disposer = Disposable.empty();

    /**
     * Hide constructor.
     */
    ViewtyDialog(Stage stage) {
        this.stage = stage;
    }

    /**
     * Configure title of this dialog.
     * 
     * @param title A title.
     * @return Chainable API.
     */
    public ViewtyDialog<T> title(String title) {
        this.title = Variable.of(title);
        return this;
    }

    /**
     * Configure title of this dialog.
     * 
     * @param title A title.
     * @return Chainable API.
     */
    public ViewtyDialog<T> title(Variable<String> title) {
        this.title = title;
        return this;
    }

    /**
     * Configure style of this dialog.
     * 
     * @param style A dialog style.
     * @return Chainable API.
     */
    public ViewtyDialog<T> style(StageStyle style) {
        if (style != null) {
            this.style = style;
        }
        return this;
    }

    /**
     * Configure the button set of this dialog.
     * 
     * @param buttons
     * @return
     */
    public ViewtyDialog<T> button(ButtonType... buttons) {
        this.buttons = List.of(buttons);
        return this;
    }

    /**
     * Configure the button set of this dialog.
     * 
     * @param buttonOK
     * @param buttonOthers
     * @return
     */
    public ViewtyDialog<T> button(String buttonOK, String... buttonOthers) {
        this.buttons = I.signal(buttonOthers)
                .map(x -> new ButtonType(x, ButtonData.CANCEL_CLOSE))
                .startWith(new ButtonType(buttonOK, ButtonData.OK_DONE))
                .toList();

        return this;
    }

    /**
     * Configure automatic translation of this dialog.
     * 
     * @return
     */
    public ViewtyDialog<T> translateButtons() {
        this.needTranslate = true;
        return this;
    }

    /**
     * Configure the button enable / disable.
     * 
     * @return
     */
    public ViewtyDialog<T> disableButtons(boolean disable) {
        this.disableButtons = disable;
        return this;
    }

    /**
     * Configure the close button enable / disable.
     * 
     * @return
     */
    public ViewtyDialog<T> disableCloseButton(boolean disable) {
        this.disableCloseButton = disable;
        return this;
    }

    /**
     * Configure the button order.
     * 
     * @return
     */
    public ViewtyDialog<T> disableSystemButtonOrder() {
        this.disableSystemButtonOrder = true;
        return this;
    }

    /**
     * Configure the size of dialog.
     * 
     * @return
     */
    public ViewtyDialog<T> width(int width) {
        this.width = width;
        return this;
    }

    /**
     * Configure the size of dialog.
     * 
     * @return
     */
    public ViewtyDialog<T> height(int height) {
        this.height = height;
        return this;
    }

    /**
     * Configure the size of dialog.
     * 
     * @return
     */
    public ViewtyDialog<T> size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Show and wait dialog.
     * 
     * @param <V>
     * @param view
     * @return
     */
    public <V, D extends DialogView<V>> Variable<V> show(Class<D> view) {
        return show(view, null);
    }

    /**
     * Show and wait dialog.
     * 
     * @param <V>
     * @param view
     * @return
     */
    public <V, D extends DialogView<V>> Variable<V> show(D view) {
        return show(view, null);
    }

    /**
     * Show and wait dialog.
     * 
     * @param <V>
     * @param view
     * @return
     */
    public <V, D extends DialogView<V>> Variable<V> show(Class<D> view, WiseConsumer<D> initializer) {
        return show(I.make(view), initializer);
    }

    /**
     * Show and wait dialog.
     * 
     * @param <V>
     * @param view
     * @return
     */
    public <V, D extends DialogView<V>> Variable<V> show(D view, WiseConsumer<D> initializer) {
        Dialog<V> dialog = initialize(new Dialog());
        dialog.setResultConverter(x -> x.getButtonData() == ButtonData.OK_DONE ? view.value : null);

        DialogPane dialogPane = dialog.getDialogPane();
        view.injectButtons(dialogPane);

        Node ui = view.ui();
        if (initializer != null) {
            initializer.accept(view);
        }

        dialogPane.setContent(ui);

        return showAndTell(dialog, disableButtons ? () -> view.value : () -> null);
    }

    /**
     * Show the notification dialog.
     * 
     * @param type
     * @param message
     * @return
     */
    public Variable<ButtonType> show(AlertType type, String message) {
        return show(type, Variable.of(message));
    }

    /**
     * Show the notification dialog.
     * 
     * @param type
     * @param message
     * @return
     */
    public Variable<ButtonType> show(AlertType type, Variable<String> message) {
        Alert dialog = initialize(new Alert(type));
        dialog.setContentText(null);
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        message.observing().on(Viewtify.UIThread).to(dialog::setContentText, disposer);

        I.signal(type)
                .skip(AlertType.NONE)
                .map(x -> x.name().toLowerCase().replace("confirmation", "confirm"))
                .map(x -> "com/sun/javafx/scene/control/skin/caspian/dialog-" + x + ".png")
                .to(x -> {
                    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream(x)));
                });

        return showAndTell(dialog, () -> ButtonType.CANCEL);
    }

    /**
     * Show the text input dialog.
     * 
     * @param message
     * @return
     */
    public Variable<String> showInput(String message) {
        return showInput(message, (WiseConsumer) null);
    }

    /**
     * Show the text input dialog.
     * 
     * @param message
     * @return
     */
    public Variable<String> showInput(String message, String defaultValue) {
        return showInput(message, ui -> ui.value(defaultValue));
    }

    /**
     * Show the text input dialog.
     * 
     * @param message
     * @return
     */
    public Variable<String> showInput(String message, WiseConsumer<UIText<String>> setting) {
        return button(ButtonType.OK, ButtonType.CANCEL).show(TextInputDialog.class, view -> {
            view.label.text(message);
            if (setting != null) {
                setting.accept(view.input);
            }
        });
    }

    /**
     * Initialize the dialog.
     * 
     * @param dialog
     * @return
     */
    private <D extends Dialog<R>, R> D initialize(D dialog) {
        dialog.initOwner(stage);
        dialog.initStyle(style);
        DialogPane dialogPane = dialog.getDialogPane();
        dialog.setGraphic(null);
        dialog.setContentText(null);
        dialogPane.getButtonTypes().clear();
        dialogPane.setPrefHeight(0);

        if (title != null) {
            title.observing().to(dialog::setTitle, disposer);
        }

        ButtonBar buttonBar = (ButtonBar) dialogPane.lookup(".button-bar");
        buttonBar.setPadding(new Insets(12, 3, 12, 3));

        if (disableSystemButtonOrder) {
            buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);
        }

        if (disableButtons) {
            for (ButtonType type : dialogPane.getButtonTypes()) {
                dialogPane.lookupButton(type).setDisable(true);
            }
        }

        if (buttons != null) {
            dialogPane.getButtonTypes().clear();
            dialogPane.getButtonTypes().addAll(buttons);
        } else {
            Node lookup = buttonBar.lookup(".container");
            // System.out.println(dialogPane.getStyleClass() + " " +
            // buttonBar.getChildrenUnmodifiable());
            // System.out.println(lookup.getClass());
        }

        if (needTranslate) {
            for (ButtonType type : dialogPane.getButtonTypes()) {
                Button button = (Button) dialogPane.lookupButton(type);
                I.translate(type.getText()).observing().on(Viewtify.UIThread).to(button::setText, disposer);
            }
        }

        if (disableCloseButton) {
            ((Stage) dialogPane.getScene().getWindow()).setOnCloseRequest(WindowEvent::consume);
        }

        if (0 < width) {
            dialogPane.setMinWidth(width);
            dialogPane.setPrefWidth(width);
        }

        if (0 < height) {
            dialogPane.setMinHeight(height);
            dialogPane.setPrefHeight(height);
        }

        Viewtify.manage("dialog", dialogPane.getScene(), true);

        return dialog;
    }

    /**
     * Show the dialog.
     * 
     * @param <V>
     * @param dialog
     * @param defaultValue
     * @return
     */
    private <V> Variable<V> showAndTell(Dialog<V> dialog, Supplier<V> defaultValue) {
        try {
            return Variable.of(dialog.showAndWait().orElseGet(defaultValue));
        } finally {
            disposer.dispose();
        }
    }

    /**
     * Specialized view for dialog.
     */
    public static abstract class DialogView<V> extends View {

        /** The value holder. */
        public V value;

        /** The button for OK. */
        public UIButton buttonOK;

        /**
         * Inject dialog's buttons.
         * 
         * @param pane The actual dialog pane.
         */
        void injectButtons(DialogPane pane) {
            I.signal(pane.getButtonTypes())
                    .take(x -> x.getButtonData() == ButtonData.OK_DONE)
                    .map(x -> pane.lookupButton(x))
                    .as(Button.class)
                    .first()
                    .to(x -> buttonOK = new UIButton(x, this));
        }
    }

    /**
     * Specialized dialog.
     */
    private static class TextInputDialog extends DialogView<String> {

        private UILabel label;

        private UIText<String> input;

        /**
         * {@inheritDoc}
         */
        @Override
        protected ViewDSL declareUI() {
            return new ViewDSL() {
                {
                    $(vbox, () -> {
                        $(label, FormStyles.FormRow);
                        $(input, FormStyles.FormRow);
                    });
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            input.focus().observing().to(x -> value = x);
        }
    }
}
