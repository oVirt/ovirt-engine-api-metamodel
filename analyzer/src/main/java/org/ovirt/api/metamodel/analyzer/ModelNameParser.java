/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.analyzer;

import org.ovirt.api.metamodel.concepts.Name;
import org.ovirt.api.metamodel.concepts.NameParser;

/**
 * This class contains methods useful for parsing the kind of names used in the model language.
 */
public class ModelNameParser {
    /**
     * Creates a model name from a Java name, doing any processing that is required, for example removing the prefixes
     * or suffixes that are used to avoid conflicts with Java reserved words.
     */
    public static Name parseJavaName(String text) {
        // Remove the underscore prefixes and suffixes, as they only make sense to avoid conflicts with Java reserved
        // words and they aren't needed in the model:
        while (text.startsWith("_")) {
            text = text.substring(1);
        }
        while (text.endsWith("_")) {
            text = text.substring(0, text.length() - 1);
        }

        // Once the name is clean it can be parsed:
        return NameParser.parseUsingCase(text);
    }
}
