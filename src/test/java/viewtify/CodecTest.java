/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import org.junit.Test;

import viewtify.Viewtify.Location;

/**
 * @version 2017/11/25 12:05:43
 */
public class CodecTest {

    @Test
    public void bounds() throws Exception {
        Location codec = new Location();

        Location locator = new Location();
        locator.x = 1;
        locator.y = 2;
        locator.w = 3;
        locator.h = 4;
        locator.state = 5;

        String encoded = codec.encode(locator);
        Location decoded = codec.decode(encoded);

        assert encoded.equals("1.0 2.0 3.0 4.0 5");
        assert decoded.x == locator.x;
        assert decoded.y == locator.y;
        assert decoded.w == locator.w;
        assert decoded.h == locator.h;
        assert decoded.state == locator.state;
    }
}
