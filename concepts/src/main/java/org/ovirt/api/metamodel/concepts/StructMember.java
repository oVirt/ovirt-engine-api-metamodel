/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

/**
 * This class contains all the fields and methods common to all the members of struct types, including attributes and
 * links.
 */
public class StructMember extends Concept {
    /**
     * Reference to the type that declared this member.
     */
    private StructType declaringType;

    /**
     * Reference to the type of the values of this member.
     */
    private Type type;

    /**
     * Returns the type where this member is directly declared.
     */
    public StructType getDeclaringType() {
        return declaringType;
    }

    /**
     * Sets the type that directly declares this member.
     */
    public void setDeclaringType(StructType newDeclaringType) {
        declaringType = newDeclaringType;
    }

    /**
     * Returns the type of this member.
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of this member.
     */
    public void setType(Type newType) {
        type = newType;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (declaringType != null) {
            buffer.append(declaringType);
            buffer.append(":");
        }
        buffer.append(getName());
        return buffer.toString();
    }
}

