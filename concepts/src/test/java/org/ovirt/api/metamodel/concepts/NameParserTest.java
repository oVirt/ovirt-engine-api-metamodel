/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NameParserTest {
    @Test
    public void testTwoWords() {
        Name name = NameParser.parseUsingCase("HelloWorld");
        assertEquals("hello_world", name.toString());
    }

    @Test
    public void testOneAcronym() {
        Name name = NameParser.parseUsingCase("Scsi");
        assertEquals("scsi", name.toString());
    }

    @Test
    public void testOneAcronymAndOneWord() {
        Name name = NameParser.parseUsingCase("ScsiDisk");
        assertEquals("scsi_disk", name.toString());
    }
}
