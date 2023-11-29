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
import viewtify.ui.UIScrollPane;
import viewtify.ui.UIText;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

public class PreferencesView extends View {

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
     * With initial views.
     * 
     * @param views
     */
    public PreferencesView(Class<? extends PreferenceViewBase>... views) {
        add(views);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
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
            }
        };
    }

    /**
     * Style definition.
     */
    interface style extends StyleDSL {
        Style search = () -> {
            display.maxWidth(220, px);
            margin.top(15, px).left(295, px).bottom(15, px);
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
}
