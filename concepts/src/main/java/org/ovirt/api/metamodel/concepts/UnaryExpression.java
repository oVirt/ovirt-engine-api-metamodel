/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

/**
 * This class represents an expression that applies an unary operator too an operand.
 */
public class UnaryExpression extends Expression {
    private Operator operator;
    private Expression operand;

    public void setOperator(Operator newOperator) {
        operator = newOperator;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getOperand() {
        return operand;
    }

    public void setOperand(Expression newOperand) {
        operand = newOperand;
    }

    @Override
    public String toString(boolean protect) {
        StringBuilder buffer = new StringBuilder();
        if (protect) {
            buffer.append("(");
        }
        if (operator != null) {
            buffer.append(operator);
            if (operator == Operator.NOT) {
                buffer.append(" ");
            }
        }
        if (operand != null) {
            buffer.append(operand.toString(true));
        }
        if (protect) {
            buffer.append(")");
        }
        return buffer.toString();
    }
}
