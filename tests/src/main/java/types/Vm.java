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
public interface Vm {
    String id();
    String name();
    String fqdn();
    Boolean runOnce();
    Boolean deleteProtected();
    Integer memory();
    Date creationTime();
    Cpu cpu();
    Disk[] disks();
    VmType type();
    VmDisplayType[] displayTypes();
    String[] properties();
    Sso sso();
    Boot boot();

    @Link Permission[] permissions();
    @Link Tag[] tags();
}
