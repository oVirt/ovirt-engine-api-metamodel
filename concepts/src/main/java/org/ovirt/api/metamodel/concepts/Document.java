/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

/**
 * This class represents a document included in the model.
 */
public class Document extends Concept {
    // Indicates if this document is an appendix:
    private boolean appendix;

    public boolean isAppendix() {
        return appendix;
    }

    public void setAppendix(boolean appendix) {
        this.appendix = appendix;
    }
}
