/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.controlsfx.glyphfont.FontAwesome;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import kiss.I;
import kiss.Variable;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.style.FormStyles;
import viewtify.ui.UILabel;
import viewtify.ui.UIScrollPane;
import viewtify.ui.UIText;
import viewtify.ui.UIVBox;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.User;

public class PreferenceView extends View {

    /** The navigation area. */
    private final UIVBox navi = new UIVBox(null);

    /** The root scrollable box. */
    private UIScrollPane scroll;

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
                        $(search, FormStyles.Input, style.search);
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
            display.minWidth(180, px);
            padding.vertical(40, px).left(10, px);
        };

        Style navi = () -> {
            display.minWidth(160, px);
            font.size(14, px).smooth.grayscale();
            padding.vertical(10, px).left(20, px);
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
            display.maxWidth(220, px);
            margin.top(15, px).left(296, px).bottom(15, px);
        };

        Style root = () -> {
            padding.left(50, px).right(30, px).top(15, px);
        };

        Style box = () -> {
            padding.bottom(65, px);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        search.placeholder(en("Search from preferences")).clearable().prefix(FontAwesome.Glyph.SEARCH).observe().to(text -> {
            text = text.strip().toLowerCase();

            for (View base : bases) {
                searchPreferenceBy(base, text);
            }
        });
    }

    /**
     * Add preference views.
     * 
     * @param views
     * @return
     */
    public final PreferenceView add(Class<? extends View>... views) {
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
    public final PreferenceView add(View... views) {
        return add(List.of(views));
    }

    /**
     * Add preference views.
     * 
     * @param views
     * @return
     */
    public final PreferenceView add(List<View> views) {
        bases.addAll(views);
        return this;
    }

    /**
     * Enable the table of contents on side panel.
     * 
     * @return
     */
    public final PreferenceView enableToC() {
        navi.show(true);
        return this;
    }

    /**
     * Disable the table of contents on side panel.
     * 
     * @return
     */
    public final PreferenceView disableToC() {
        navi.show(false);
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
