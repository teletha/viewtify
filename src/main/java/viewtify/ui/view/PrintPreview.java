/*
 * Copyright (C) 2023 The YAMATO Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.beans.property.Property;
import javafx.event.Event;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrintColor;
import javafx.print.PrintQuality;
import javafx.print.PrintSides;
import javafx.print.Printer;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import org.controlsfx.control.SegmentedButton;

import kiss.I;
import stylist.Style;
import stylist.StyleDSL;
import stylist.value.Color;
import viewtify.property.SmartProperty;
import viewtify.style.FormStyles;
import viewtify.ui.UIComboBox;
import viewtify.ui.UIImage;
import viewtify.ui.UILabel;
import viewtify.ui.UISpinner;
import viewtify.ui.UIText;
import viewtify.ui.UIToggleButton;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.User;
import viewtify.ui.helper.ValueHelper;
import viewtify.ui.helper.Verifier;
import viewtify.ui.helper.VerifyHelper;
import viewtify.ui.view.PrintPreview.PrintInfo;
import viewtify.util.FXUtils;

public class PrintPreview extends View implements VerifyHelper<PrintPreview>, ValueHelper<PrintPreview, PrintInfo> {

    private static final List<Paper> JP = List.of(Paper.A3, Paper.A4, Paper.A5, Paper.JIS_B5, Paper.JIS_B6, Paper.JAPANESE_POSTCARD);

    private static final Map<String, List<Paper>> PaperSet = Map.of("jp", JP);

    /** The main image view. */
    private UIImage view;

    /** The associated print info. */
    private SmartProperty<PrintInfo> property = new SmartProperty(new PrintInfo());

    /** The empty verifier. */
    private Verifier verifier = new Verifier();

    /** The printer UI. */
    private UILabel pageSize;

    /** The printer UI. */
    private UISpinner<Integer> copies;

    /** The printer UI. */
    private UIText<Integer> pager;

    /** The printer UI. */
    private UIComboBox<Printer> printer;

    /** The printer UI. */
    private UIComboBox<Paper> paper;

    /** The printer UI. */
    private UIComboBox<PrintColor> color;

    /** The printer UI. */
    private UIComboBox<PageOrientation> orientation;

    /** The printer UI. */
    private UIComboBox<PrintSides> side;

    /** The printer UI. */
    private UIComboBox<PrintQuality> quality;

    /** The max page size. */
    private int maxPage;

    /** The current page index. */
    private int currentPage = -1;

    /** The page image set. */
    private WritableImage[] images;

    /** The navigation UI. */
    private UIToggleButton start;

    /** The navigation UI. */
    private UIToggleButton prev;

    /** The navigation UI. */
    private UIToggleButton location;

    /** The navigation UI. */
    private UIToggleButton next;

    /** The navigation UI. */
    private UIToggleButton end;

    /** The navigation UI. */
    private SegmentedButton navi = new SegmentedButton();

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(hbox, () -> {
                    $(sbox, () -> {
                        $(view);
                        $(() -> navi, style.navi);
                    });
                    $(vbox, style.side, () -> {
                        $(hbox, FormStyles.FormRow, style.title, () -> {
                            label(en("Settings"), FormStyles.FormLabelMin);

                            $(pageSize, FormStyles.FormInput, style.pageSize);
                        });

                        $(hbox, FormStyles.FormRow, () -> {
                            label(en("Number of copies"), FormStyles.FormLabelMin);
                            $(copies, FormStyles.FormInput);
                        });
                        $(hbox, FormStyles.FormRow, () -> {
                            label(en("Page"), FormStyles.FormLabelMin);
                            $(pager, FormStyles.FormInput);
                        });
                        $(hbox, FormStyles.FormRow, () -> {
                            label(en("Printer"), FormStyles.FormLabelMin);
                            $(printer, FormStyles.FormInput);
                        });

                        $(hbox, FormStyles.FormRow, style.advanced, () -> {
                            label(en("Advanced Settings"), FormStyles.FormLabelMin);
                        });
                        $(hbox, FormStyles.FormRow, () -> {
                            label(en("Paper"), FormStyles.FormLabelMin);
                            $(paper, FormStyles.FormInput);
                        });
                        $(hbox, FormStyles.FormRow, () -> {
                            label(en("Color mode"), FormStyles.FormLabelMin);
                            $(color, FormStyles.FormInput);
                        });
                        $(hbox, FormStyles.FormRow, () -> {
                            label(en("Print orientation"), FormStyles.FormLabelMin);
                            $(orientation, FormStyles.FormInput);
                        });
                        $(hbox, FormStyles.FormRow, () -> {
                            label(en("Print side"), FormStyles.FormLabelMin);
                            $(side, FormStyles.FormInput);
                        });
                        $(hbox, FormStyles.FormRow, () -> {
                            label(en("Quality"), FormStyles.FormLabelMin);
                            $(quality, FormStyles.FormInput);
                        });
                    });
                });
            }
        };
    }

    interface style extends StyleDSL {
        Style side = () -> {
            margin.left(22, px);
        };

        Style pageSize = () -> {
            text.align.right();
        };

        Style title = () -> {
            border.bottom.solid().width(1, px).color(Color.White);
            margin.bottom(10, px);
            padding.bottom(7, px);
            font.weight.bolder();
        };

        Style advanced = () -> {
            title.style();
            margin.top(33, px);
        };

        Style navi = () -> {
            position.absolute().top(8, px);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        copies.ui.setValueFactory(new IntegerSpinnerValueFactory(1, 300));

        pager.placeholder(en("all pages")).disable(true);
        printer.items(Printer.getAllPrinters()).value(Printer.getDefaultPrinter()).render(Printer::getName);
        paper.items(PaperSet.getOrDefault(I.Lang.v, JP)).placeholder(en("Default")).renderByVariable(x -> en(x.getName()));
        color.items(PrintColor.values()).value(PrintColor.COLOR).renderByVariable(x -> en(x.name()));
        orientation.items(PageOrientation.values()).placeholder(en("Default")).renderByVariable(x -> en(x.name()));
        side.items(PrintSides.values()).value(PrintSides.ONE_SIDED).renderByVariable(x -> en(x.name()));
        quality.items(PrintQuality.values()).value(PrintQuality.NORMAL).renderByVariable(x -> en(x.name()));

        start.text("<<").focusable(false).when(User.MousePress, e -> drawPage(0, e));
        prev.text("<").focusable(false).when(User.MousePress, e -> drawPage(currentPage - 1, e));
        location.focusable(false).ignore(User.MousePress);
        next.text(">").focusable(false).when(User.MousePress, e -> drawPage(currentPage + 1, e));
        end.text(">>").focusable(false).when(User.MousePress, e -> drawPage(maxPage - 1, e));
        navi.getButtons().addAll(start.ui, prev.ui, location.ui, next.ui, end.ui);

        // bind configuration
        copies.observing().to(v -> value().copies = v);
        paper.observing().to(v -> value().paper = v);
        pager.observing(true).to(v -> value().pageSize = v);
        printer.observing().to(v -> value().printer = v);
        color.observing().to(v -> value().color = v);
        orientation.observing().to(v -> value().orientation = v);
        side.observing().to(v -> value().side = v);
        quality.observing().to(v -> value().quality = v);

        view.when(User.Scroll, e -> {
            if (0 < e.getDeltaY()) {
                drawPage(currentPage - 1);
            } else {
                drawPage(currentPage + 1);
            }
        }).when(User.MouseEnter, e -> {
            FXUtils.animate(250, navi.opacityProperty(), 1);
        }).when(User.MouseExit, e -> {
            if (!view.ui.getLayoutBounds().contains(e.getX(), e.getY())) {
                FXUtils.animate(250, navi.opacityProperty(), 0);
            }
        });
    }

    /**
     * @param images
     */
    public void images(WritableImage... images) {
        this.maxPage = images.length;
        this.images = images;
        pageSize.text(en("{0} pages", images.length));

        value().pageSize = images.length;
        for (int i = 0; i < images.length; i++) {
            value().pages.add(i);
        }

        drawPage(0);
    }

    /**
     * Draw the specified page.
     */
    private void drawPage(int page, Event e) {
        e.consume();
        drawPage(page);
    }

    /**
     * Draw the specified page.
     */
    private void drawPage(int page) {
        if (page < 0 || maxPage <= page || (page == currentPage)) {
            return;
        }

        currentPage = page;

        location.text("  " + (currentPage + 1) + " / " + maxPage + "  ");

        WritableImage image = images[currentPage];
        view.ui.setFitWidth(image.getWidth());
        view.ui.setFitHeight(image.getHeight());

        FXUtils.animate(150, view.ui.opacityProperty(), 0.1, () -> {
            view.value(image);
            FXUtils.animate(300, view.ui.opacityProperty(), 1);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<PrintInfo> valueProperty() {
        return property;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Verifier verifier() {
        return verifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();

        view.value((Image) null);
        view = null;
        navi = null;
    }

    /**
     * 
     */
    public static class PrintInfo {
        public int copies;

        public int pageSize;

        public List<Integer> pages = new ArrayList();

        public Paper paper;

        public Printer printer;

        public PrintColor color;

        public PageOrientation orientation;

        public PrintSides side;

        public PrintQuality quality;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "PrintInfo [copies=" + copies + ", pageSize=" + pageSize + ", pages=" + pages + ", paper=" + paper + ", printer=" + printer + ", color=" + color + ", orientation=" + orientation + ", side=" + side + ", quality=" + quality + "]";
        }
    }
}