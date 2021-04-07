/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package types;

import java.util.Date;

import org.ovirt.api.metamodel.annotations.Link;
import org.ovirt.api.metamodel.annotations.Type;

/**
 * This class exists only to be a victim of the metamodel tests.
 */
@Type
public interface Boot {
    BootDevice[] devices();
}
