/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is a producer of the set of Java reserved words.
 */
@Singleton
public class JavaReservedWords {
    private Set<String> words;

    @PostConstruct
    private void init() {
        // Create the set:
        words = new HashSet<>();

        // Populate the set:
        words.add("abstract");
        words.add("assert");
        words.add("boolean");
        words.add("break");
        words.add("byte");
        words.add("case");
        words.add("catch");
        words.add("char");
        words.add("class");
        words.add("const");
        words.add("continue");
        words.add("default");
        words.add("do");
        words.add("double");
        words.add("else");
        words.add("enum");
        words.add("extends");
        words.add("false");
        words.add("final");
        words.add("finally");
        words.add("float");
        words.add("for");
        words.add("goto");
        words.add("if");
        words.add("implements");
        words.add("import");
        words.add("instanceof");
        words.add("int");
        words.add("interface");
        words.add("long");
        words.add("native");
        words.add("new");
        words.add("null");
        words.add("package");
        words.add("private");
        words.add("protected");
        words.add("public");
        words.add("return");
        words.add("short");
        words.add("static");
        words.add("strictfp");
        words.add("super");
        words.add("switch");
        words.add("synchronized");
        words.add("this");
        words.add("throw");
        words.add("throws");
        words.add("transient");
        words.add("true");
        words.add("true");
        words.add("try");
        words.add("void");
        words.add("volatile");
        words.add("while");

        // These aren't reserved words, but it is better to avoid them, as the class with methods of the "Object" class:
        words.add("clone");
        words.add("finalize");

        // Wrap the set so that it is unmodifiable:
        words = Collections.unmodifiableSet(words);
    }

    /**
     * Produces the set of Java reserved words.
     */
    @Produces
    @ReservedWords(language = "java")
    public Set<String> getWords() {
        return words;
    }
}

