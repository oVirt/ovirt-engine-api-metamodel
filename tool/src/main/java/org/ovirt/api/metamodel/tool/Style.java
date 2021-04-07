/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 * CDI qualifier used to select a specific naming style. It is initially intended to work together with the
 * {@link JavaNames} interface, in order to select the desired implementation. For example, the following will
 * select the implementation of {@link JavaNames} that is annotated with {@Style("versioned")} instead of the
 * default one:
 *
 * <pre>
 * @Inject
 * @Style("versioned")
 * private JavaNames javaNames;
 * </pre>
 *
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
public @interface Style {
    /**
     * The naming style, for example <i>versioned</i>.
     */
    String value();
}
