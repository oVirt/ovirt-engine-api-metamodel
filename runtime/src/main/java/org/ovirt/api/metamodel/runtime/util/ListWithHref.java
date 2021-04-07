/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.runtime.util;

import java.util.List;

/**
 * This is helper interface so we can support parsing 'link' element.
 * This interface only specify method to access 'href' attribute, which
 * is needed to properly parse 'link' element.
 */
public interface ListWithHref<E> extends List<E> {
    String href();
    void href(String href);
}
