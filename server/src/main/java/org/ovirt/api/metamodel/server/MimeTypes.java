/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.server;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains the set of MIME types supported by the model servlet.
 */
public class MimeTypes {
    // The log:
    private static final Logger log = LoggerFactory.getLogger(MimeTypes.class);

    // The supported MIME types:
    public static final MimeType APPLICATION_JSON = parseMimeType("application/json");
    public static final MimeType APPLICATION_OCTET_STREAM = parseMimeType("application/octet-stream");
    public static final MimeType APPLICATION_XML = parseMimeType("application/xml");
    public static final MimeType TEXT_HTML = parseMimeType("text/html");

    /**
     * Converts the given text to a MIME type.
     *
     * @param text the text to convert
     * @return the converted MIME type or {@code null} if it can't be converted
     */
    public static MimeType parseMimeType(String text) {
        try {
            return new MimeType(text);
        }
        catch (MimeTypeParseException exception) {
            log.warn("The text \"{}\" isn't a valid mime type, will return null.", text, exception);
            return null;
        }
    }
}
