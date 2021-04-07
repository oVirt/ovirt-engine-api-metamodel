/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.runtime.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This is helper class so we can support parsing 'link' element.
 * This class stores 'href' attribute. Which we need to store, so
 * we can properly support 'follow link' method.
 */
public class ArrayListWithHref<E> extends ArrayList<E> implements ListWithHref<E> {

    private String href;

    public ArrayListWithHref() {
        super();
    }

    public ArrayListWithHref(List<E> list) {
        super(list);
        if (list instanceof ListWithHref) {
            this.href = ((ListWithHref) list).href();
        }
    }

    @Override
    public String href() {
        return href;
    }

    @Override
    public void href(String href) {
        this.href = href;
    }
}
