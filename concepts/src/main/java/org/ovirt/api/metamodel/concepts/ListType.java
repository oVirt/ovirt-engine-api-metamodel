/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

/**
 * This class is used to represent list types.
 */
public class ListType extends Type {
    private Type elementType;

    public Type getElementType() {
        return elementType;
    }

    public void setElementType(Type elementType) {
        this.elementType = elementType;
        setModule(elementType.getModule());
    }

    @Override
    public String toString() {
        return elementType.toString() + "[]";
    }
}

