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
 * This annotation is used to mark interfaces whose contents will be included in other types, services or methods. For
 * example, the a {@code follow} parameter common to multiple operations could be defined like this:
 *
 * <pre>
 * @Mixin
 * public interface Follow {
 *     @In String follow();
 * }
 * </pre>
 *
 * And then included in multiple methods without having to repeat the complete definition:
 *
 * <pre>
 * interface Get extends Follow {
 *     ...
 * }
 *
 * interface List extends Follow {
 *     ...
 * }
 * </pre>
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.SOURCE)
public @interface Mixin {
}

