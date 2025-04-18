/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.calendar;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import javafx.scene.Node;
import kiss.Managed;
import kiss.Singleton;
import stylist.Style;
import stylist.StyleDSL;
import stylist.value.Color;
import viewtify.ViewtyDialog;
import viewtify.ui.UIHBox;
import viewtify.ui.UILabel;
import viewtify.ui.UserInterfaceProvider;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.User;

class Visualizers {

    @Managed(Singleton.class)
    private static class ForMonth implements MonthEventVisualizer<TimeEvent> {

        /**
         * {@inheritDoc}
         */
        @Override
        public UserInterfaceProvider<? extends Node> visualize(TimeEvent event) {
            return new EventUI(event, EventUI.MONTH);
        }
    }

    @Managed(Singleton.class)
    private static class ForWeek implements WeekEventVisualizer<TimeEvent> {

        /**
         * {@inheritDoc}
         */
        @Override
        public UserInterfaceProvider<? extends Node> visualize(TimeEvent event) {
            return new EventUI(event, EventUI.WEEK);
        }
    }

    @Managed(Singleton.class)
    private static class ForDay implements DayEventVisualizer<TimeEvent> {

        /**
         * {@inheritDoc}
         */
        @Override
        public UserInterfaceProvider<? extends Node> visualize(TimeEvent event) {
            return new EventUI(event, EventUI.DAY);
        }
    }

    /**
     * Actual UI
     */
    public static class EventUI extends View {

        /** The view type. */
        private static final int MONTH = 0;

        /** The view type. */
        private static final int WEEK = 1;

        /** The view type. */
        private static final int DAY = 2;

        /** The associated event. */
        private final TimeEvent event;

        /** The view type. */
        private final int view;

        /** widget */
        private UIHBox box;

        /** widget */
        private UILabel time;

        /** widget */
        private UILabel title;

        /** widget */
        private UILabel description;

        private EventUI(TimeEvent event, int view) {
            this.event = event;
            this.view = view;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ViewDSL declareUI() {
            return new ViewDSL() {
                {
                    $(box, Styles.box, () -> {
                        $(time, Styles.time);
                        $(vbox, () -> {
                            $(title);
                            if (view != MONTH) $(description);
                        });
                    });
                }
            };
        }

        private interface Styles extends StyleDSL {
            Style box = () -> {
                padding.horizontal(4, px).bottom(2, px);
                cursor.pointer();
                border.radius(2, px);

                $.hover(() -> {
                    background.color("-fx-accent");
                });
            };

            Style time = () -> {
                display.minWidth(2.8, em);
                padding.right(2, px);
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            time.text(event.startTime());
            title.text(event.title());
            description.text(event.description());

            box.popup(Detail::new);

            Color color = event.source().color();
            if (!Color.Transparent.equals(color)) {
                title.ui.setStyle("-fx-text-fill: " + color.toRGB() + ";");
            }
        }

        private class Detail extends View {

            private UILabel edit;

            private UILabel delete;

            private UILabel close;

            /**
             * {@inheritDoc}
             */
            @Override
            protected ViewDSL declareUI() {
                return new ViewDSL() {
                    {

                        $(hbox, Styles.popup, () -> {
                            $(vbox, Styles.box, () -> {
                                label(event.startDate() + " " + event.startTime(), Styles.date);
                                label(event.title(), Styles.title);
                                label(event.description(), Styles.description);
                            });
                            $(vbox, Styles.menus, () -> {
                                $(close, Styles.icon);
                                $(edit, Styles.icon);
                                $(delete, Styles.icon);
                            });
                        });
                    }
                };
            }

            interface Styles extends StyleDSL {
                Style popup = () -> {
                    padding.size(12, px);
                };

                Style box = () -> {
                    display.minWidth(185, px);
                    padding.bottom(5, px).horizontal(8, px);
                };

                Style title = () -> {
                    font.size(1.4, em);
                };

                Style description = () -> {
                    padding.top(5, px);
                };

                Style date = () -> {
                    font.size(0.9, em);
                    padding.bottom(3, px);
                };

                Style menus = () -> {
                    margin.left(20, px);
                };

                Style icon = () -> {
                    display.width(19, px).height(19, px);
                    font.size(14, px);
                    margin.bottom(6, px);
                    cursor.pointer();
                    border.radius(2, px);
                    text.align.center().verticalAlign.middle();

                    $.hover(() -> {
                        background.color("-fx-accent");
                    });
                };
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void initialize() {
                edit.icon(FontAwesomeSolid.PEN).tooltip(en("Edit")).enable(event.isEditable());
                delete.icon(FontAwesomeSolid.TRASH).tooltip(en("Delete")).enable(event.isEditable());
                close.icon(FontAwesomeSolid.CROSS).tooltip(en("Close")).when(User.LeftClick, ViewtyDialog::close);
            }
        }
    }
}