/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.analyzer;

import org.ovirt.api.metamodel.concepts.Service;

/**
 * This class represents a service whose definition is not known yet, only its name is known. It is intended to be used
 * while parsing a model, as some services may be referenced before they are used. Once the model is completely parsed
 * these services should be replaced with the real ones.
 */
public class UndefinedService extends Service {
}
