/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package types;

import org.ovirt.api.metamodel.annotations.Type;

/**
 * This class exists only to be a victim of the metamodel tests.
 */
@Type
public interface Sso {
    SsoMethod[] methods();
}
