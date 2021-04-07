/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.asciidoctor.Attributes;

/**
 * This class stores the configuration used to generate AsciiDoc text.
 */
@ApplicationScoped
public class AsciiDocConfiguration {
    private Attributes attributes;
    private String separator;

    @PostConstruct
    private void init() {
        // Set the default attributes:
        attributes = new Attributes();
        attributes.setSourceHighlighter("highlightjs");

        // Set the default separator:
        separator = "/";
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttribute(String name, String value) {
        attributes.setAttribute(name, value);
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String newSeparator) {
        separator = newSeparator;
    }
}

