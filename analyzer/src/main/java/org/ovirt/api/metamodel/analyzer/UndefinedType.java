/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.analyzer;

import org.ovirt.api.metamodel.concepts.Type;

/**
 * This class represents a type whose definition is not known yet, only its name is known. It is intended to be used
 * while parsing a model, as some types may be referenced before they are used. Once the model is completely parsed
 * these types should be replaced with the real ones.
 */
public class UndefinedType extends Type {
}

