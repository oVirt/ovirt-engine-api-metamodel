/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

import static org.ovirt.api.metamodel.concepts.Named.named;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * This class contains the fields and methods common to all the members of a service, including methods and locators.
 */
public class ServiceMember extends Concept {
    // The service that declared this method:
    private Service declaringService;

    // The parameters of this method:
    private List<Parameter> parameters = new ArrayList<>();

    /**
     * Returns the service where this member is directly declared.
     */
    public Service getDeclaringService() {
        return declaringService;
    }

    /**
     * Sets the service that directly declares this member.
     */
    public void setDeclaringService(Service newDeclaringService) {
        declaringService = newDeclaringService;
    }

    /**
     * Returns the list of parameters of this member. The returned list is a copy of the one used internally, so it is
     * safe to modify it in any way. If you aren't going to modify the list consider using the {@link #parameters()}
     * method instead.
     */
    public List<Parameter> getParameters() {
        return new CopyOnWriteArrayList<>(parameters);
    }

    /**
     * Returns a stream that delivers the parameters of this member.
     */
    public Stream<Parameter> parameters() {
        return parameters.stream();
    }

    /**
     * Returns the parameter that has the given name, or {@code null} if there is no such parameter.
     */
    public Parameter getParameter(Name name) {
        return parameters.stream().filter(named(name)).findFirst().orElse(null);
    }

    /**
     * Adds a new parameter to this service member.
     */
    public void addParameter(Parameter newParameter) {
        parameters.add(newParameter);
    }

    /**
     * Adds a list of new parameters to this member.
     */
    public void addParameters(List<Parameter> newParameters) {
        parameters.addAll(newParameters);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (declaringService != null) {
            buffer.append(declaringService);
            buffer.append(":");
        }
        buffer.append(getName());
        return buffer.toString();
    }
}

