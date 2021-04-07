/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.analyzer;

import static org.junit.Assert.assertEquals;
import static org.ovirt.api.metamodel.analyzer.ModelNameParser.parseJavaName;

import org.junit.Test;

/**
 * This test check basic capabilities of the methods that parse the names used in the model language.
 */
public class ModeNameParserTest {
    @Test
    public void testRemovesUnderscorePrefix() {
        assertEquals(parseJavaName("_volatile").toString(), "volatile");
    }

    @Test
    public void testRemovesUnderscoreSuffix() {
        assertEquals(parseJavaName("volatile_").toString(), "volatile");
    }

    @Test
    public void testRemovesUnderscorePrefixAndSuffix() {
        assertEquals(parseJavaName("_volatile_").toString(), "volatile");
    }
}
