/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.util.List;
import java.util.function.Supplier;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import kiss.Disposable;
import kiss.I;
import kiss.Model;
import kiss.Variable;
import kiss.WiseConsumer;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import viewtify.style.FormStyles;
import viewtify.ui.UIButton;
import viewtify.ui.UILabel;
import viewtify.ui.UIText;
import viewtify.ui.UserInterface;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.anime.Anime;
import viewtify.ui.view.PrintPreview;
import viewtify.ui.view.PrintPreview.PrintInfo;

/**
 * The specialized dialog builder.
 */
public final class ViewtyDialog<T> {

    /** The associated stage. */
    private Stage stage;

    /** The dialog style. */
    private StageStyle style = StageStyle.DECORATED;

    /** The dialog modality. */
    private Modality modality = Modality.APPLICATION_MODAL;

    /** The title of this dialog. */
    private Variable<String> title;

    /** The button set. */
    private List<ButtonType> buttons;

    /** The button's visibility. */
    private boolean hideButtons;

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

    /** The translation mode. */
    private boolean translatable;

    /** The dialog effect. */
    private boolean fadable;

    /** The dialog effect. */
    private boolean blurable;

    /** The dialog mode. */
    private boolean blockable = true;

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
     * Configure modality of this dialog.
     * 
     * @param modality A dialog modality.
     * @return Chainable API.
     */
    public ViewtyDialog<T> modal(Modality modality) {
        if (modality != null) {
            this.modality = modality;
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
    public ViewtyDialog<T> translatable() {
        this.translatable = true;
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
     * Congifure the loading effect.
     * 
     * @return
     */
    public ViewtyDialog<T> fadable() {
        fadable = true;
        return this;
    }

    /**
     * Congifure the loading effect.
     * 
     * @return
     */
    public ViewtyDialog<T> blurable() {
        blurable = true;
        return this;
    }

    /**
     * Congifure the loading mode.
     * 
     * @return
     */
    public ViewtyDialog<T> unblockable() {
        blockable = false;
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
        dialog.setResultConverter(x -> {
            switch (x.getButtonData()) {
            case APPLY:
            case OK_DONE:
            case FINISH:
                return view.value;

            default:
                return null;
            }
        });

        DialogPane dialogPane = dialog.getDialogPane();
        view.pane = dialogPane;

        Node ui = view.ui();
        if (initializer != null) {
            initializer.accept(view);
        }

        if (fadable) {
            DoubleProperty opacity = dialogPane.getScene().getWindow().opacityProperty();

            dialog.setOnShowing(e -> {
                Anime.define().init(opacity, 0).effect(opacity, 1, 0.3).run();
            });
            dialog.addEventHandler(DialogEvent.DIALOG_CLOSE_REQUEST, e -> {
                if (opacity.doubleValue() != 0) {
                    e.consume();
                    Anime.define().effect(opacity, 0, 0.3).run(dialog::close);
                }
            });
        }

        if (blurable) {
            Parent owner = dialog.getOwner().getScene().getRoot();
            owner.setEffect(new BoxBlur(5, 5, 8));
            dialog.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> {
                owner.setEffect(null);
            });
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
     * Show the directory chooser dialog.
     */
    public Variable<Directory> showDirectory() {
        return showDirectory(null);
    }

    /**
     * Show the directory chooser dialog.
     */
    public Variable<Directory> showDirectory(Directory initial) {
        DirectoryChooser chooser = new DirectoryChooser();
        if (title != null) {
            title.observing().to(chooser::setTitle);
        }

        if (initial != null) {
            chooser.setInitialDirectory(initial.asJavaFile());
        }

        return Variable.of(chooser.showDialog(stage)).map(dir -> Locator.directory(dir.toPath()));
    }

    /**
     * Show the file chooser dialog.
     */
    public Variable<File> showFile() {
        return showFile(null, null);
    }

    /**
     * Show the file chooser dialog.
     */
    public Variable<File> showFile(Directory initial, ExtensionFilter filter) {
        FileChooser chooser = new FileChooser();
        if (title != null) {
            title.observing().to(chooser::setTitle);
        }

        if (initial != null) {
            chooser.setInitialDirectory(initial.asJavaFile());
        }

        if (filter != null) {
            chooser.getExtensionFilters().add(filter);
        }

        java.io.File result = chooser.showOpenDialog(stage);
        return Variable.of(result).map(x -> Locator.file(result.toPath()));
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
     * Show the print preview dialog.
     */
    public Variable<PrintInfo> showPrintPreview(WritableImage... images) {
        return fadable().blurable().button("Print", "Cancel").translatable().show(PrintPreview.class, preview -> preview.loadImage(images));
    }

    public <V> Variable<V> showWizard(Class<? extends DialogView<V>>... views) {
        Class<V> type = (Class<V>) Model.collectParameters(views[0], DialogView.class)[0];
        return showWizard(I.make(type), views);
    }

    public <V> Variable<V> showWizard(V value, Class<? extends DialogView<V>>... views) {
        hideButtons = true;

        return button(ButtonType.PREVIOUS, ButtonType.NEXT, ButtonType.FINISH, ButtonType.CANCEL).disableSystemButtonOrder()
                .show(new WizardDialog(value, views));
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
        dialog.initModality(modality);
        DialogPane dialogPane = dialog.getDialogPane();

        if (title != null) {
            title.observing().to(dialog::setTitle, disposer);
        }

        ButtonBar buttonBar = (ButtonBar) dialogPane.lookup(".button-bar");
        buttonBar.setPadding(new Insets(12, 3, 12, 3));

        if (buttons != null) {
            dialogPane.getButtonTypes().clear();
            dialogPane.getButtonTypes().addAll(buttons);
        } else {
            dialogPane.getChildren().remove(buttonBar);
        }

        if (translatable) {
            for (ButtonType type : dialogPane.getButtonTypes()) {
                Button button = (Button) dialogPane.lookupButton(type);
                I.translate(type.getText()).observing().on(Viewtify.UIThread).to(button::setText, disposer);
            }
        }

        if (disableCloseButton) {
            ((Stage) dialogPane.getScene().getWindow()).setOnCloseRequest(WindowEvent::consume);
        }

        if (disableSystemButtonOrder) {
            buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);
        }

        if (disableButtons) {
            for (ButtonType type : dialogPane.getButtonTypes()) {
                dialogPane.lookupButton(type).setDisable(true);
            }
        }

        if (hideButtons) {
            for (ButtonType type : dialogPane.getButtonTypes()) {
                dialogPane.lookupButton(type).setVisible(false);
            }
        }

        if (0 < width) {
            dialogPane.setMinWidth(width);
            dialogPane.setPrefWidth(width);
        }

        if (0 < height) {
            dialogPane.setMinHeight(height);
            dialogPane.setPrefHeight(height);
        }

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
            // TODO The JVM throws an error when exiting an application with a dialog open. Even if
            // I try to close the dialog before exiting the application, it throws an exception due
            // to a NestedLoop problem. I have no idea how to deal with this, so I just ignore the
            // error. This is a wall of grief.
            disposer.add(() -> {
                try {
                    dialog.close();
                } catch (IllegalArgumentException e) {
                    // wall of grief
                }
            });

            if (blockable) {
                return Variable.of(dialog.showAndWait().orElseGet(defaultValue));
            } else {
                dialog.show();
                return Variable.empty();

            }
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

        /** The associated dialog pane. */
        protected DialogPane pane;

        /** The page title. */
        @Override
        public Variable<String> title() {
            return Variable.of(getClass().getSimpleName());
        }

        /**
         * Find the dialog button.
         * 
         * @param type
         * @return
         */
        protected final Variable<UIButton> find(ButtonType type) {
            return find(type.getButtonData());
        }

        /**
         * Find the dialog button.
         * 
         * @param data
         * @return
         */
        protected final Variable<UIButton> find(ButtonData data) {
            return I.signal(pane.getButtonTypes())
                    .take(x -> x.getButtonData() == data)
                    .map(x -> pane.lookupButton(x))
                    .as(Button.class)
                    .first()
                    .map(x -> new UIButton(x, this))
                    .to();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
        }

        public boolean isInvalid() {
            return findUI(UserInterface.class).any(ui -> ui.verifier().isInvalid()).to().exact();
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
                        $(label, FormStyles.Row);
                        $(input, FormStyles.Row);
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