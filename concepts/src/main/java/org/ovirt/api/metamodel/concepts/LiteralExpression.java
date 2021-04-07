/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

public class LiteralExpression extends Expression {
    private Object value;

    public void setValue(Object newValue) {
        value = newValue;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString(boolean protect) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(value);
        return buffer.toString();
    }
}
