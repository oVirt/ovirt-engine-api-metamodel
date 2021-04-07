/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import org.ovirt.api.metamodel.concepts.Name;

/**
 * This interface specifies the rules used to calculate the names of generated Java concepts. These rules may have
 * different implementations, like rules to generate plain traditional Java names and rules to generate versioned
 * Java names.
 */
public interface JavaNames {
    /**
     * Returns a representation of the given name using the capitalization style typically used for Java classes: the
     * first letter of each word in upper case and the rest of the letters in lower case.
     */
    String getJavaClassStyleName(Name name);

    /**
     * Returns a representation of the given name using the capitalization style typically used for Java members: the
     * the first word in lower case and the rest of the words with the first letter in upper case and the rest of the
     * letters in lower case.
     */
    String getJavaMemberStyleName(Name name);

    /**
     * Returns a representation of the given name using the capitalization style typically used for Java properties.
     */
    String getJavaPropertyStyleName(Name name);

    /**
     * Returns a representation of the given name using the capitalization style typically used for Java constants: all
     * the words in uppercase and separated by underscores.
     */
    String getJavaConstantStyleName(Name name);
}

