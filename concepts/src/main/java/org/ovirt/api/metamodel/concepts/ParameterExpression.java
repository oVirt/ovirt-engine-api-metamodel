/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

/**
 * This expression represents the access to parameter of a method.
 */
public class ParameterExpression extends Expression {
    private Parameter parameter;

    public void setParameter(Parameter newParameter) {
        parameter = newParameter;
    }

    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public String toString(boolean protect) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(parameter.getDeclaringMethod().getName());
        buffer.append(":");
        buffer.append(parameter.getName());
        return buffer.toString();
    }
}
