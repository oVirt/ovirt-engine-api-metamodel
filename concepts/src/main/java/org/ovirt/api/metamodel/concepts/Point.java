/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * This class represents a specific point of the services tree, composed of the path from the root service (a list
 * of locators) and a method.
 */
public class Point implements Serializable {
    private List<Locator> path = new ArrayList<>();
    private Method method;

    /**
     * Returns the a list containing the locators in the path to of this point. The returned list is a copy of the one
     * used internally, so it is safe to modify it in any way. If you aren't going to modify the list consider using the
     * {@link #path()} method instead.
     */
    public List<Locator> getPath() {
        return new CopyOnWriteArrayList<>(path);
    }

    /**
     * Returns a stream that delivers the locators in the path to this point.
     */
    public Stream<Locator> path() {
        return path.stream();
    }

    /**
     * Sets the list of locators in the path to this point. The given list is copied, so it can be safely modified
     * after calling the method.
     */
    public void setPath(List<Locator> path) {
        this.path.clear();
        this.path.addAll(path);
    }

    /**
     * Returns the method located at this point.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Sets the method located at this point.
     */
    public void setMethod(Method method) {
        this.method = method;
    }
}
