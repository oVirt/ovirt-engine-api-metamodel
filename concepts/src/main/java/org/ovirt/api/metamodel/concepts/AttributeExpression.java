/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

/**
 * This expression represents the access to structured type attribute.
 */
public class AttributeExpression extends Expression {
    private Expression target;
    private Attribute attribute;

    public Expression getTarget() {
        return target;
    }

    public void setTarget(Expression newTarget) {
        target = newTarget;
    }

    public void setAttribute(Attribute newAttribute) {
        attribute = newAttribute;
    }

    public Attribute getAttribute() {
        return attribute;
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
            buffer.append(attribute);
            if (protect) {
                buffer.append(")");
            }
        }
        else {
            buffer.append(attribute);
        }
        return buffer.toString();
    }
}
