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

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.controlsfx.glyphfont.FontAwesome;

import kiss.I;
import kiss.Managed;
import kiss.Singleton;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.preference.Preferences;
import viewtify.style.FormStyles;
import viewtify.ui.UIButton;
import viewtify.ui.UIHBox;
import viewtify.ui.UILabel;
import viewtify.ui.UISegmentedButton;
import viewtify.ui.UIToggleButton;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.anime.SwapAnime;

@Managed(Singleton.class)
public class CalendarView extends View {

    private UISegmentedButton switchRange;

    private UIToggleButton switchToDay;

    private UIToggleButton switchToWeek;

    private UIToggleButton switchToMonth;

    private UIToggleButton switchToYear;

    private UIButton preference;

    UILabel current;

    private UIButton selectNext;

    private UIButton selectPrevious;

    private UIButton selectToday;

    private UIHBox main;

    private TemporalView currentView;

    LocalDate currentDate;

    /**
     * Hide constructor.
     */
    private CalendarView() {
    }

    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    $(hbox, FormStyles.Row, Styles.header, () -> {
                        $(selectToday, FormStyles.Button, Styles.today);

                        $(hbox, Styles.headerCenter, () -> {
                            $(selectPrevious);
                            $(current, Styles.current);
                            $(selectNext);
                        });

                        $(hbox, Styles.headerRight, () -> {
                            $(switchRange, () -> {
                                $(switchToDay);
                                $(switchToWeek);
                                $(switchToMonth);
                                $(switchToYear);
                            });
                            $(preference, Styles.config);
                        });
                    });

                    $(main, Styles.main);
                });
            }
        };
    }

    interface Styles extends StyleDSL {
        Style today = () -> {
            margin.horizontal(15, px);
        };

        Style selector = () -> {
            font.size(20, px).color("-fx-mid-text-color").family("FontAwesome");
            padding.horizontal(6, px).top(-4, px);
        };

        Style current = () -> {
            display.minWidth(125, px);
            font.size(20, px).smooth.grayscale();
            padding.horizontal(25, px);
            text.align.center();
        };

        Style header = () -> {
            margin.top(8, px).bottom(15, px);
        };

        Style headerCenter = () -> {
            display.width.fill();
            text.align.center();
        };

        Style headerRight = () -> {
            display.width.fitContent();
            text.align.right();
            margin.horizontal(15, px);
        };

        Style year = () -> {
            display.width(80, px);
        };

        Style month = () -> {
            display.width(80, px);
        };

        Style main = () -> {
            display.width.fill().height.fill();
        };

        Style config = () -> {
            display.width(30, px);
            margin.left(20, px);
        };
    }

    @Override
    protected void initialize() {
        CalendarSetting setting = Preferences.of(CalendarSetting.class);

        selectNext.text(FontAwesome.Glyph.ANGLE_RIGHT, Styles.selector).action(() -> currentView.next());
        selectPrevious.text(FontAwesome.Glyph.ANGLE_LEFT, Styles.selector).action(() -> currentView.previous());
        selectToday.text(I.translate("Today")).action(() -> currentView.today());

        switchToDay.text(I.translate("Day")).action(() -> show(DayView.class, currentDate));
        switchToWeek.text(I.translate("Week")).action(() -> show(WeekView.class, currentDate));
        switchToMonth.text(I.translate("Month")).action(() -> show(MonthView.class, currentDate));
        switchToYear.text(I.translate("Year")).action(() -> show(YearView.class, currentDate));

        preference.text(FontAwesome.Glyph.GEAR).popup(CalendarSettingView::new);

        show(setting.initialView.v, LocalDate.now());

        I.signal(I.find(TimeEventSource.class))
                .subscribeOn(Viewtify.WorkerThread)
                .flatMap(source -> Preferences.of(TimeEventSourceSetting.class, source.name()).observe())
                .merge(setting.observe())
                .debounce(200, TimeUnit.MILLISECONDS)
                .on(Viewtify.UIThread)
                .to(() -> draw(currentView.getClass(), currentDate, null));
    }

    /**
     * Show the specified widget on calendar.
     * 
     * @param <V>
     * @param viewType
     * @param date
     */
    protected <V extends TemporalView> void show(Class<V> viewType, LocalDate date) {
        show(viewType, date, null);
    }

    /**
     * Show the specified widget on calendar.
     * 
     * @param <V>
     * @param viewType
     * @param date
     */
    protected <V extends TemporalView> void show(Class<V> viewType, LocalDate date, SwapAnime anime) {
        // avoid re-rendering
        if (!viewType.isInstance(currentView) || !date.isEqual(currentDate)) {
            draw(viewType, date, anime);
        }
    }

    /**
     * Draw the calendar widget.
     * 
     * @param <V>
     * @param viewType
     * @param date
     */
    private <V extends TemporalView> void draw(Class<V> viewType, LocalDate date, SwapAnime anime) {
        anime = Objects.requireNonNullElse(anime, SwapAnime.FadeOutIn);

        V view = I.make(viewType);
        view.ui();

        currentDate = date;
        currentView = view;
        currentView.set(date);

        main.content(currentView, anime);

        if (viewType == DayView.class) {
            switchToDay.select();
        } else if (viewType == WeekView.class) {
            switchToWeek.select();
        } else if (viewType == MonthView.class) {
            switchToMonth.select();
        } else if (viewType == YearView.class) {
            switchToYear.select();
        }
    }
}