/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package types;

import org.ovirt.api.metamodel.annotations.Type;

/**
 * This class exists only to ensure that conflicts in the generated code caused by the existence of an {@code Event}
 * type in the model are detected early.
 */
@Type
public interface Event {
}
