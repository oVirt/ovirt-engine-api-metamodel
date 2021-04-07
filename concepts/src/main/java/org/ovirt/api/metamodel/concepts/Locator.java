/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

/**
 * A locator is reference from one service to another service.
 */
public class Locator extends ServiceMember {
    // The service that is resolved by this locator:
    private Service service;

    /**
     * Returns the service that is resolved by this locator.
     */
    public Service getService() {
        return service;
    }

    /**
     * Sets the service that is resolved by this locator.
     */
    public void setService(Service newService) {
        service = newService;
    }
}
