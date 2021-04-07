/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

/**
 * This class represents an expression that applies a binary operator too a pair of operands.
 */
public class BinaryExpression extends Expression {
    private Operator operator;
    private Expression left;
    private Expression right;

    public void setOperator(Operator newOperator) {
        operator = newOperator;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression newLeft) {
        left = newLeft;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression newRight) {
        right = newRight;
    }

    @Override
    public String toString(boolean protect) {
        StringBuilder buffer = new StringBuilder();
        if (protect) {
            buffer.append("(");
        }
        buffer.append(left.toString(true));
        buffer.append(" ");
        buffer.append(operator);
        buffer.append(" ");
        buffer.append(right.toString(true));
        if (protect) {
            buffer.append(")");
        }
        return buffer.toString();
    }
}
