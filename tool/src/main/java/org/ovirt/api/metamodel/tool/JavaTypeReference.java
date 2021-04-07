/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a reference to a Java type, including all the imports that are necessary to use it. For
 * example, if the type is an array like {@code BigDecimal[]} then the text of the reference will be
 * {@code BigDecimal[]} and the list of imports will contain {@code java.math.BigDecimal}.
 */
public class JavaTypeReference {
    private String text;
    private List<JavaClassName> imports = new ArrayList<>(1);

    public String getText() {
        return text;
    }

    public void setText(String newText) {
        text = newText;
    }

    public void setText(Class<?> clazz) {
        text = clazz.getSimpleName();
    }

    public List<JavaClassName> getImports() {
        return new ArrayList<>(imports);
    }

    public void setImports(List<JavaClassName> newImports) {
        imports.clear();
        imports.addAll(newImports);
    }

    public void addImport(JavaClassName newImport) {
        imports.add(newImport);
    }

    public void addImport(String packageName, String className) {
        JavaClassName newImport = new JavaClassName();
        newImport.setPackageName(packageName);
        newImport.setSimpleName(className);
        imports.add(newImport);
    }

    public void addImport(Class<?> clazz) {
        JavaClassName newImport = new JavaClassName();
        newImport.setPackageName(clazz.getPackage().getName());
        newImport.setSimpleName(clazz.getSimpleName());
        imports.add(newImport);
    }

    @Override
    public String toString() {
        return text;
    }
}

