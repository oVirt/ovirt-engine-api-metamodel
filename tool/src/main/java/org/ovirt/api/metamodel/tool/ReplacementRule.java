/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplacementRule {
    private Pattern pattern;
    private String replacement;

    public ReplacementRule(String theExpression, String theReplacement) {
        pattern = Pattern.compile(theExpression);
        replacement = theReplacement;
    }

    /**
     * Checks if the given text matches the pattern of this rule. If it does then the replacement is applied and the
     * result returned. If it doesn't match then {@code null} is returned.
     */
    public String process(String text) {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.replaceAll(replacement);
    }
}

