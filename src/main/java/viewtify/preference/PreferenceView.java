/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.preference;

import java.awt.Desktop;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.stage.FileChooser.ExtensionFilter;

import org.controlsfx.glyphfont.FontAwesome;

import kiss.I;
import kiss.Variable;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.style.FormStyles;
import viewtify.ui.UILabel;
import viewtify.ui.UIScrollPane;
import viewtify.ui.UIText;
import viewtify.ui.UIVBox;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.User;
import viewtify.ui.toast.Toast;

public class PreferenceView extends View {

    /** The navigation area. */
    private final UIVBox navi = new UIVBox(null);

    /** The root scrollable box. */
    private UIScrollPane scroll;

    /** The configuration manager. */
    private UILabel importer;

    /** The configuration manager. */
    private UILabel exporter;

    /** The search box. */
    private UIText<String> search;

    /** The list of preference views. */
    private final List<View> bases = new ArrayList();

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(hbox, () -> {
                    $(navi, style.left, () -> {
                        for (View view : bases) {
                            $(new UILabel(null).text(view.title()).style(style.navi).when(User.LeftClick, () -> {
                                scroll.scrollTo(view.ui().getParent(), true);
                            }));
                        }
                    });
                    $(vbox, style.right, () -> {
                        $(hbox, style.bar, () -> {
                            $(importer);
                            $(exporter);
                            $(search, FormStyles.Input, style.search);
                        });
                        $(scroll, FormStyles.Preferences, () -> {
                            $(vbox, style.root, () -> {
                                for (View view : bases) {
                                    $(vbox, style.box, () -> {
                                        label(view.title(), FormStyles.Title);
                                        $(view);
                                    });
                                }
                            });
                        });
                    });
                });
            }
        };
    }

    /**
     * Style definition.
     */
    interface style extends StyleDSL {
        Style left = () -> {
            padding.vertical(40, px).left(10, px);
        };

        Style navi = () -> {
            display.minWidth(160, px);
            font.size(15, px);
            padding.vertical(10, px).horizontal(15, px);
            cursor.pointer();
            border.radius(3, px);

            $.hover(() -> {
                background.color("-fx-accent");
            });
        };

        Style right = () -> {
            display.width.fill();
        };

        Style search = () -> {
            display.minWidth(180, px);
            margin.top(15, px).left(15, px).bottom(15, px);
        };

        Style root = () -> {
            padding.left(40, px).right(30, px).top(15, px);
        };

        Style box = () -> {
            padding.bottom(65, px);
        };

        Style bar = () -> {
            display.maxWidth(550, px);
            text.align.right();
        };

        Style icon = () -> {
            font.size(18, px).family("FontAwesome").color("-fx-mid-text-color");
            padding.size(6, px);
            cursor.pointer();

            $.hover(() -> {
                font.color("-fx-focus-color");
            });
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        importer.text(FontAwesome.Glyph.DOWNLOAD, style.icon)
                .tooltip(en("Import preferences from file"))
                .when(User.LeftClick, this::importPrefernces);
        exporter.text(FontAwesome.Glyph.UPLOAD, style.icon)
                .tooltip(en("Export the current preferences"))
                .when(User.LeftClick, this::exportPreferences);

        search.placeholder(en("Search from preferences")).clearable().prefix(FontAwesome.Glyph.SEARCH).observe().to(text -> {
            text = text.strip().toLowerCase();

            for (View base : bases) {
                searchPreferenceBy(base, text);
            }
        });
    }

    private Directory locateHome() {
        return Locator.directory(System.getProperty("user.home")).directory(Viewtify.application().launcher().getSimpleName());
    }

    /**
     * Export user preferences.
     */
    private void exportPreferences() {
        Viewtify.inUI(() -> {
            Directory directory = locateHome();
            String name = "config-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".zip";
            File zip = directory.file(name);

            Viewtify.UserPreference.exact().packTo(zip, o -> o.glob("**.json").strip());

            Toast.show(en("Saved the current preferences to [file](0)."), () -> {
                Desktop.getDesktop().open(zip.parent().asJavaFile());
            });
        });
    }

    /**
     * Import user preferences.
     */
    private void importPrefernces() {
        Viewtify.dialog()
                .title(en("Select the archived preference file."))
                .showFile(locateHome(), new ExtensionFilter("Archive", List.of("config-*.zip")))
                .to(zip -> {
                    Viewtify.inWorker(() -> {
                        zip.unpackTo(Viewtify.UserPreference.exact());

                        Preferences.reload();

                        Toast.show(en("Restored from the archived preferences."));
                    });
                });
    }

    /**
     * Add preference views.
     * 
     * @param views
     * @return
     */
    public final PreferenceView manage(Class<? extends View>... views) {
        for (Class<? extends View> view : views) {
            bases.add(I.make(view));
        }
        return this;
    }

    /**
     * Add preference views.
     * 
     * @param views
     * @return
     */
    public final PreferenceView manage(View... views) {
        return manage(List.of(views));
    }

    /**
     * Add preference views.
     * 
     * @param views
     * @return
     */
    public final PreferenceView manage(List<View> views) {
        bases.addAll(views);
        return this;
    }

    /**
     * Enable the table of contents on side panel.
     * 
     * @return
     */
    public final PreferenceView tableOfContents(boolean show) {
        navi.show(show);
        return this;
    }

    /**
     * @param text
     */
    private static void searchPreferenceBy(View view, String text) {
        String[] classes = {".label", ".button", ".check-box", ".hyperlink", " .toggle-button", ".cell"};
        boolean precondition = text == null || text.isBlank() || view.title().exact().contains(text);

        int shown = 0;
        Set<Node> shownDescriptions = new HashSet();

        List<Node> rows = new ArrayList();
        rows.addAll(view.ui().lookupAll(FormStyles.Row.selector()));
        rows.addAll(view.ui().lookupAll(".table-view"));

        for (Node row : rows) {
            Variable<Node> description = findDescription(row);
            if (precondition || searchLabel(row, text, classes) || searchCombo(row, text) || searchDescription(description, text, classes)) {
                shown++;
                row.setManaged(true);
                row.setVisible(true);
                description.to(x -> {
                    x.setManaged(true);
                    x.setVisible(true);
                    shownDescriptions.add(x);
                });
            } else {
                row.setManaged(false);
                row.setVisible(false);
                description.to(x -> {
                    if (!shownDescriptions.contains(x)) {
                        x.setManaged(false);
                        x.setVisible(false);
                    }
                });
            }
        }

        Node title = view.ui().getParent();
        title.setVisible(precondition || shown != 0);
        title.setManaged(precondition || shown != 0);
    }

    private static boolean searchLabel(Node row, String text, String... classes) {
        for (String clazz : classes) {
            for (Node node : row.lookupAll(clazz)) {
                if (node instanceof Labeled labeled && labeled.getText() != null && labeled.getText().toLowerCase().contains(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean searchCombo(Node row, String text) {
        for (Node node : row.lookupAll(".combo-box")) {
            if (node instanceof ComboBox combo) {
                for (Object object : combo.getItems()) {
                    if (object.toString().toLowerCase().contains(text)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean searchDescription(Variable<Node> description, String text, String... classes) {
        if (description.isAbsent()) {
            return false;
        }

        for (String clazz : classes) {
            for (Node node : description.v.lookupAll(clazz)) {
                if (node instanceof Labeled labeled && labeled.getText().toLowerCase().contains(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Variable<Node> findDescription(Node row) {
        Node target = row;
        Parent node = target.getParent();
        while (node != null && !node.getStyleClass().contains(FormStyles.Preferences.className()[0])) {
            ObservableList<Node> children = node.getChildrenUnmodifiable();
            for (int i = children.indexOf(target) - 1; 0 <= i; i--) {
                Node child = children.get(i);
                ObservableList<String> classes = child.getStyleClass();
                if (classes.contains(FormStyles.Description.className()[0])) {
                    return Variable.of(child);
                }
            }
            target = node;
            node = node.getParent();
        }
        return Variable.empty();
    }
}
