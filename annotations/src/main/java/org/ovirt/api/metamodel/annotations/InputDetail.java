/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used during model generation. It's job is to mark the
 * method, which lists the fields of the @In object that are expected
 * as input for the API operation at hand.
 *
 * For example:
 *
 *       @InputDetail
 *       default void anyName() {
 *           optional(disk().alias());
 *           optional(disk().name());
 *           ...
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.SOURCE)
public @interface InputDetail {

}
