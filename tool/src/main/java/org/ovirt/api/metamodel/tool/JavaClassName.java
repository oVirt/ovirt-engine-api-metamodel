/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

/**
 * This class represents the fully qualified name of a Java class, composed by the package name and the simple class
 * name.
 */
public class JavaClassName {
    public JavaClassName() {
        super();
    }

    public JavaClassName(String packageName, String simpleName) {
        super();
        this.packageName = packageName;
        this.simpleName = simpleName;
    }

    private String packageName;
    private String simpleName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String newPackageName) {
        packageName = newPackageName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String newClassName) {
        simpleName = newClassName;
    }

    public void setClass(Class<?> newClass) {
        packageName = newClass.getPackage().getName();
        simpleName = newClass.getSimpleName();
    }

    @Override
    public String toString() {
        return packageName + "." + simpleName;
    }
}

