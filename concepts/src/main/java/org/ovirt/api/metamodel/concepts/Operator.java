/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

public enum Operator {
    // Arithmetic operators:
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    REMAINDER("%"),

    // Boolean operators:
    AND("and"),
    OR("or"),
    NOT("not"),

    // Relational operators:
    EQUAL("="),
    NOT_EQUAL("!="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<=");

    private String image;

    Operator(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return image;
    }
}
