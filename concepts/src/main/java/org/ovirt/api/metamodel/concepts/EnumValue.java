/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

/**
 * This class represents a single value of an enumerated type.
 */
public class EnumValue extends Concept {
    // Reference to the type that declared this attribute:
    private EnumType declaringType;

    /**
     * Returns the enumerated type where this value is declared.
     */
    public EnumType getDeclaringType() {
        return declaringType;
    }

    /**
     * Sets the enumerated type where this value is declared.
     */
    public void setDeclaringType(EnumType newDeclaringType) {
        declaringType = newDeclaringType;
    }
}

