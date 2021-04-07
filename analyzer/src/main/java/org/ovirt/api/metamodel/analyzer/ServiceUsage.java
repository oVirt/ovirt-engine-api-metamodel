/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.analyzer;

import org.ovirt.api.metamodel.concepts.Name;

/**
 * This class is used to remember that an undefined service was used, and how to replace it with the real one.
 */
public class ServiceUsage {
    // The name of the undefined service:
    private Name name;

    // The method used to replace the service:
    private ServiceSetter setter;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public ServiceSetter getSetter() {
        return setter;
    }

    public void setSetter(ServiceSetter setter) {
        this.setter = setter;
    }
}
