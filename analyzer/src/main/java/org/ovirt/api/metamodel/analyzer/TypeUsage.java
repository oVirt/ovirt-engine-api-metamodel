/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.analyzer;

import org.ovirt.api.metamodel.concepts.Name;

/**
 * This class is used to remember that an undefined type was used, and how to replace it with the real one.
 */
public class TypeUsage {
    // The name of the undefined type:
    private Name name;

    // The method used to replace the type:
    private TypeSetter setter;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public TypeSetter getSetter() {
        return setter;
    }

    public void setSetter(TypeSetter setter) {
        this.setter = setter;
    }
}

