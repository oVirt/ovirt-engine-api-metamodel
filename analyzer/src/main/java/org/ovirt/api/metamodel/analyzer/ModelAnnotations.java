/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.analyzer;

import org.ovirt.api.metamodel.annotations.Allowed;
import org.ovirt.api.metamodel.annotations.In;
import org.ovirt.api.metamodel.annotations.Link;
import org.ovirt.api.metamodel.annotations.Method;
import org.ovirt.api.metamodel.annotations.Mixin;
import org.ovirt.api.metamodel.annotations.Out;
import org.ovirt.api.metamodel.annotations.Required;
import org.ovirt.api.metamodel.annotations.Root;
import org.ovirt.api.metamodel.annotations.Service;
import org.ovirt.api.metamodel.annotations.Type;

/**
 * During the analysis of the model the names of the annotations will be used frequently. To avoid importing them in
 * many places we extract the names here.
 */
public class ModelAnnotations {
    public static final String ALLOWED = Allowed.class.getName();
    public static final String IN = In.class.getName();
    public static final String LINK = Link.class.getName();
    public static final String METHOD = Method.class.getName();
    public static final String MIXIN = Mixin.class.getName();
    public static final String OUT = Out.class.getName();
    public static final String REQUIRED = Required.class.getName();
    public static final String ROOT = Root.class.getName();
    public static final String SERVICE = Service.class.getName();
    public static final String TYPE = Type.class.getName();
}
