/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.analyzer;

import org.ovirt.api.metamodel.concepts.Name;
import org.ovirt.api.metamodel.concepts.Expression;

/**
 * This class represents a field access.
 */
public class FieldExpression extends Expression {
    private Expression target;
    private Name field;

    public void setField(Name newField) {
        field = newField;
    }

    public Expression getTarget() {
        return target;
    }

    public void setTarget(Expression newTarget) {
        target = newTarget;
    }

    public Name getField() {
        return field;
    }

    @Override
    public String toString(boolean protect) {
        StringBuilder buffer = new StringBuilder();
        if (target != null) {
            if (protect) {
                buffer.append("(");
            }
            buffer.append(target.toString(true));
            buffer.append(".");
            buffer.append(field);
            if (protect) {
                buffer.append(")");
            }
        }
        else {
            buffer.append(field);
        }
        return buffer.toString();
    }
}
