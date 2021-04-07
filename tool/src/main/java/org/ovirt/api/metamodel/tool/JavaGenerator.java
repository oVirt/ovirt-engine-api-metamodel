/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ovirt.api.metamodel.concepts.Concept;
import org.ovirt.api.metamodel.concepts.Model;

/**
 * This class contains methods useful for several different kinds of classes that generate Java source code.
 */
public abstract class JavaGenerator {
    // The buffer used to generate Java code:
    protected JavaClassBuffer javaBuffer;

    // The directory were the output will be generated:
    protected File outDir;

    // The directory were the resources file will be generated:
    protected File resourcesDir;

    /**
     * Set the directory were the output will be generated.
     */
    public void setOutDir(File newOutDir) {
        outDir = newOutDir;
    }

    /**
     * Set the directory were the resources files will be generated.
     */
    public void setResourcesDir(File newResourcesDir) {
        resourcesDir = newResourcesDir;
    }

    /**
     * Generate the code for the given model.
     */
    public abstract void generate(Model model);

    protected void generateDoc(Concept concept) {
        List<String> lines = new ArrayList<>();
        String doc = concept.getDoc();
        if (doc != null) {
            Collections.addAll(lines, doc.split("\n"));
        }
        if (!lines.isEmpty()) {
            javaBuffer.addDocComment(lines);
        }
    }
}

