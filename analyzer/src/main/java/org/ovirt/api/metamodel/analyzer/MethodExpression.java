/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ovirt.api.metamodel.concepts.Name;
import org.ovirt.api.metamodel.concepts.Expression;

/**
 * This class represents a call to a method.
 */
public class MethodExpression extends Expression {
    private Expression target;
    private Name method;
    private List<Expression> parameters = new ArrayList<>(0);

    public Expression getTarget() {
        return target;
    }

    public void setTarget(Expression newTarget) {
        target = newTarget;
    }

    public void setMethod(Name newMethod) {
        method = newMethod;
    }

    public Name getMethod() {
        return method;
    }

    public List<Expression> getParameters() {
        return new CopyOnWriteArrayList<>(parameters);
    }

    public void addParameter(Expression newParameter) {
        parameters.add(newParameter);
    }

    public void addParameters(List<Expression> newParameters) {
        parameters.addAll(newParameters);
    }

    @Override
    public String toString(boolean protect) {
        StringBuilder buffer = new StringBuilder();
        if (protect) {
            buffer.append("(");
        }
        if (target != null) {
            buffer.append(target.toString(true));
            buffer.append(".");
        }
        if (method != null) {
            buffer.append(method);
        }
        buffer.append("(");
        boolean first = true;
        for (Expression parameter : parameters) {
            if (!first) {
                buffer.append(", ");
            }
            buffer.append(parameter.toString(false));
            first = false;
        }
        buffer.append(")");
        if (protect) {
            buffer.append(")");
        }
        return buffer.toString();
    }
}
