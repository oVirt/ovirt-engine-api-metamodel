/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

/**
 * This interface is to be implemented by the classes that can translate the documentation included in the Javadoc
 * comments of the model into HTML text.
 */
public interface HtmlGenerator {
    String toHtml(String text);
}
