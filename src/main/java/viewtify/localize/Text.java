/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.localize;

import java.util.EnumMap;
import java.util.Objects;
import java.util.function.Supplier;

public final class Text implements Supplier<String> {

    /** The text holder. */
    private final EnumMap<Lang, String> texts = new EnumMap(Lang.class);

    /**
     * Hide constructor.
     */
    private Text() {
    }

    /**
     * @param lang A target language.
     * @param text A translated text.
     * @return
     */
    public Text set(Lang lang, String text) {
        Objects.requireNonNull(text);
        texts.put(lang, text);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get() {
        String text = texts.get(Lang.current());

        if (text == null) {
            text = texts.get(Lang.EN);
        }
        return text;
    }

    /**
     * Build with {@link Lang#EN} text.
     * 
     * @param text The english text.
     * @return A {@link Text}.
     */
    public static Text of(String text) {
        return new Text().set(Lang.EN, text);
    }
}
