/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ovirt.api.metamodel.concepts.Name;

/**
 * This class contains the rules used to calculate the names of generated Java concepts taking into account the
 * version prefix.
 */
@ApplicationScoped
@Style("versioned")
public class VersionedJavaNames implements JavaNames {
    /**
     * Reference to the object used to calculate plain Java names.
     */
    @Inject
    @Style("plain")
    private JavaNames javaNames;

    /**
     * Version prefix to add to all the generate class names.
     */
    private String versionPrefix;

    /**
     * Get the version prefix.
     */
    public String getVersionPrefix() {
        return versionPrefix;
    }

    /**
     * Set the version prefix.
     */
    public void setVersionPrefix(String newVersionPrefix) {
        versionPrefix = newVersionPrefix;
    }

    /**
     * {@inheritDoc}
     */
    public String getJavaClassStyleName(Name name) {
        // Classes need to have the version prefix.
        return javaNames.getJavaClassStyleName(addPrefix(name));
    }

    /**
     * {@inheritDoc}
     */
    public String getJavaMemberStyleName(Name name) {
        // Members don't need the version prefix:
        return javaNames.getJavaMemberStyleName(name);
    }

    /**
     * {@inheritDoc}
     */
    public String getJavaPropertyStyleName(Name name) {
        // Properties don't need a version prefix:
        return javaNames.getJavaPropertyStyleName(name);
    }

    /**
     * Returns a representation of the given name using the capitalization style typically used for Java constants: all
     * the words in uppercase and separated by underscores.
     */
    public String getJavaConstantStyleName(Name name) {
        return javaNames.getJavaConstantStyleName(addPrefix(name));
    }

    /**
     * Adds the version prefix to the given name.
     */
    private Name addPrefix(Name name) {
        if (versionPrefix != null && !versionPrefix.isEmpty()) {
            List<String> words = name.getWords();
            words.add(0, versionPrefix);
            name = new Name(words);
        }
        return name;
    }
}

