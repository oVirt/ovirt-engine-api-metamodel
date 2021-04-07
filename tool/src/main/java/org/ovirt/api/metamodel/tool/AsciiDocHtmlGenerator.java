/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;

/**
 * This class translates model documentation into HTML, assuming that it is formatted using AsciiDoc.
 */
@Named("asciidoc")
public class AsciiDocHtmlGenerator implements HtmlGenerator {
    // Reference to the object that stores the global AsciiDoc configuration:
    @Inject private AsciiDocConfiguration configuration;

    // The state of generator:
    private Asciidoctor doctor;

    @PostConstruct
    public void init() {
        doctor = Asciidoctor.Factory.create();
    }

    public String toHtml(String text) {
        // Create the options:
        Options options = new Options();
        options.setAttributes(configuration.getAttributes());

        // Perform the rendering:
        return doctor.convert(text, options);
    }
}

