/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class represents an enumerated type.
 */
public class EnumType extends Type {
    // The valid values of the enumerated tyupe:
    private List<EnumValue> values = new ArrayList<>();

    /**
     * Returns the list of values of this enumerated type. The returned list is a copy of the one used internally, so it
     * is safe to modify it in any way. If you aren't going to modify the list consider using the {@link #values()}
     * method * instead.
     */
    public List<EnumValue> getValues() {
        return Collections.unmodifiableList(values);
    }

    /**
     * Returns a stream that delivers the values of this enumerated type.
     */
    public Stream<EnumValue> values() {
        return values.stream();
    }

    /**
     * Adds a new value to this enumerated type.
     */
    public void addValue(EnumValue value) {
        values.add(value);
    }
}

