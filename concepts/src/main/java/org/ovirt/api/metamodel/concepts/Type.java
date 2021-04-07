/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

public abstract class Type extends Concept {
    private Module module;

    public Module getModule() {
        return module;
    }

    public void setModule(Module newModule) {
        module = newModule;
    }

    public Model getModel() {
        return module.getModel();
    }
}

