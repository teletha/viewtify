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
import java.util.List;

import org.controlsfx.glyphfont.FontAwesome;

import kiss.I;
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

public class PreferencesView extends View {

    /** The navigation area. */
    private final UIVBox navi = new UIVBox(null);

    /** The root scrollable box. */
    private UIScrollPane scroll;

    /** The search box. */
    private UIText<String> search;

    /** The list of preference views. */
    private final List<PreferenceViewBase> bases = new ArrayList();

    /**
     * Normal constructor.
     */
    public PreferencesView() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(hbox, () -> {
                    $(navi, style.left, () -> {
                        for (PreferenceViewBase view : bases) {
                            $(new UILabel(null).text(view.category()).style(style.navi).when(User.LeftClick, () -> {
                                scroll.scrollTo(view.ui().getParent(), true);
                            }));
                        }
                    });
                    $(vbox, style.right, () -> {
                        $(search, FormStyles.Input, style.search);
                        $(scroll, FormStyles.Preferences, () -> {
                            $(vbox, style.root, () -> {
                                for (PreferenceViewBase view : bases) {
                                    $(vbox, style.box, () -> {
                                        label(view.category(), FormStyles.Title);
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
            display.width(200, px);
            padding.vertical(40, px).horizontal(10, px);
        };

        Style navi = () -> {
            display.width(180, px);
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
            margin.top(15, px).left(310, px).bottom(15, px);
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

            for (PreferenceViewBase base : bases) {
                base.searchPreferenceBy(text);
            }
        });
    }

    /**
     * Add preference views.
     * 
     * @param views
     * @return
     */
    public PreferencesView add(Class<? extends PreferenceViewBase>... views) {
        for (Class<? extends PreferenceViewBase> view : views) {
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
    public PreferencesView add(PreferenceViewBase... views) {
        return add(List.of(views));
    }

    /**
     * Add preference views.
     * 
     * @param views
     * @return
     */
    public PreferencesView add(List<PreferenceViewBase> views) {
        bases.addAll(views);
        return this;
    }

    /**
     * Enable the table of contents on side panel.
     * 
     * @return
     */
    public final PreferencesView enableToC() {
        navi.show(true);
        return this;
    }

    /**
     * Disable the table of contents on side panel.
     * 
     * @return
     */
    public final PreferencesView disableToC() {
        navi.show(false);
        return this;
    }
}
