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

public final class Localizable implements Supplier<String>, CharSequence, Comparable<Localizable> {

    /** The text holder. */
    private final EnumMap<Lang, String> texts = new EnumMap(Lang.class);

    /**
     * Hide constructor.
     */
    private Localizable() {
    }

    /**
     * @param lang A target language.
     * @param text A translated text.
     * @return
     */
    public Localizable set(Lang lang, String text) {
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
     * {@inheritDoc}
     */
    @Override
    public int length() {
        return get().length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char charAt(int index) {
        return get().charAt(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        return get().subSequence(start, end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Localizable o) {
        return get().compareTo(o.get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return get().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Localizable) {
            return get().equals(((Localizable) obj).get());
        } else {
            return false;
        }
    }

    /**
     * Build with {@link Lang#EN} text.
     * 
     * @param text The english text.
     * @return A {@link Localizable}.
     */
    public static Localizable of(String text) {
        return new Localizable().set(Lang.EN, text);
    }
}
