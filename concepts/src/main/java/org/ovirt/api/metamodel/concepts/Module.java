/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

public class Module extends Concept {
    private Model model;

    public Model getModel() {
        return model;
    }

    public void setModel(Model newModel) {
        model = newModel;
    }
}

