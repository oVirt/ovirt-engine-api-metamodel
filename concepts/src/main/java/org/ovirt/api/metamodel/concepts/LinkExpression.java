/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

/**
 * This expression represents the access to structured type link.
 */
public class LinkExpression extends Expression {
    private Expression target;
    private Link link;

    public Expression getTarget() {
        return target;
    }

    public void setTarget(Expression newTarget) {
        target = newTarget;
    }

    public void setLink(Link newLink) {
        link = newLink;
    }

    public Link getLink() {
        return link;
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
            buffer.append(link);
            if (protect) {
                buffer.append(")");
            }
        }
        else {
            buffer.append(link);
        }
        return buffer.toString();
    }
}
