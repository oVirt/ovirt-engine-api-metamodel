/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import static java.util.stream.Collectors.joining;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.ovirt.api.metamodel.concepts.Name;

/**
 * This class contains the rules used to calculate the names of generated Java concepts, using the traditional Java
 * mapping rules.
 */
@ApplicationScoped
@Style("plain")
@Default
public class PlainJavaNames implements JavaNames {
    // Reference to the object used to do calculations with words:
    @Inject private Words words;

    // We need the Java reserved words in order to avoid producing names that aren't legal in Java:
    @Inject
    @ReservedWords(language = "java")
    private Set<String> javaReservedWords;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavaClassStyleName(Name name) {
        return name.words().map(words::capitalize).collect(joining());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavaMemberStyleName(Name name) {
        StringBuilder buffer = new StringBuilder();
        name.words().findFirst().map(String::toLowerCase).ifPresent(buffer::append);
        name.words().skip(1).map(words::capitalize).forEach(buffer::append);
        String result = buffer.toString();
        if (javaReservedWords.contains(result)) {
            result = result + "_";
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavaPropertyStyleName(Name name) {
        return getJavaClassStyleName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavaConstantStyleName(Name name) {
        return name.words().map(String::toUpperCase).collect(joining("_"));
    }
}

