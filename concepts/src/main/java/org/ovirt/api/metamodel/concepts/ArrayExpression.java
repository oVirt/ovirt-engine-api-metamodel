/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

public class ArrayExpression extends Expression {
    private Expression array;
    private Expression index;

    public void setArray(Expression newArray) {
        array = newArray;
    }

    public Expression getArray() {
        return array;
    }

    public void setIndex(Expression newIndex) {
        index = newIndex;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public String toString(boolean protect) {
        StringBuilder buffer = new StringBuilder();
        if (protect) {
            buffer.append("(");
        }
        if (array != null) {
            buffer.append(array.toString(true));
        }
        buffer.append("[");
        if (index != null) {
            buffer.append(index.toString(false));
        }
        buffer.append("]");
        if (protect) {
            buffer.append(")");
        }
        return buffer.toString();
    }
}
