/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.concepts;

import static java.lang.String.join;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This class represents a name formed of multiple words. It is intended to simplify the use of different strategies for
 * representing names as strings, like using different separators or using camel case. The words that form the name are
 * stored separated, so there is no need to parse the name each time that the words are needed.
 */
public class Name implements Comparable<Name>, Serializable {
    /**
     * The list of words of this name.
     */
    private ArrayList<String> words = new ArrayList<>(1);

    public Name() {
        super();
    }

    @SuppressWarnings("unchecked")
    /**
     * Copy Constructor
     */
    public Name(Name nameToClone) {
        super();
        words = (ArrayList<String>)nameToClone.words.clone();
    }

    /**
     * Creates a new word using the given list of words.
     *
     * @param theWords the words that will be used to construct the name
     */
    public Name(String... theWords) {
        words.ensureCapacity(theWords.length);
        for (String theWord : theWords) {
            words.add(theWord.toLowerCase());
        }
    }

    /**
     * Creates a new word using the given list of words.
     *
     * @param theWords the words that will be used to construct the name
     */
    public Name(List<String> theWords) {
        words.ensureCapacity(theWords.size());
        words.addAll(theWords);
    }

    /**
     * Returns the list of words of this name. The returned list is a copy of the one used internally, so any changes
     * to it won't have any effect on this name, and changes in the name won't affect the returned list.
     */
    public List<String> getWords() {
        return new ArrayList<>(words);
    }

    /**
     * Replaces the list of words of this name. The list passed as parameter isn't modified or referenced, instead
     * its contents are copied, so any later changes to it won't have any effect on this name.
     *
     * @param newWords the list of words that will replace the words of this name
     */
    public void setWords(List<String> newWords) {
        words.clear();
        for (String newWord : newWords) {
            words.add(newWord.toLowerCase());
        }
    }

    /**
     * Replaces the element at the specified position in this list of words with the specified element
     * @param index the position in the list
     * @param word the replacement word
     */
    public void setWord(int index, String word) {
        words.set(index, word);
    }

    /**
     * Adds a new word to the end of the list of words of this name.
     *
     * @param newWord the world that will be added
     */
    public void addWord(String newWord) {
        words.add(newWord.toLowerCase());
    }

    /**
     * Adds a new word to the end of the list of words of this name.
     *
     * @param newWord the world that will be added
     */
    public void addWords(List<String> words) {
        this.words.addAll(words);
    }

    /**
     * Returns a sequential {@code Stream} with the list of words of the name as source.
     */
    public Stream<String> words() {
        return words.stream();
    }

    /**
     * Returns a string representation of this name, consisting on the list of words of the name separated by underscores.
     */
    @Override
    public String toString() {
        return join("_", words);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Name)) {
            return false;
        }
        Name other = (Name) obj;
        return Objects.equals(words, other.words);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(words);
    }

    /**
     * Compares two names lexicographically.
     */
    @Override
    public int compareTo(Name that) {
        int thisLength = this.words.size();
        int thatLength = that.words.size();
        int minLength = Math.min(thisLength, thatLength);
        for (int i = 0; i < minLength; i++) {
            String thisWord = this.words.get(i);
            String thatWord = that.words.get(i);
            int result = thisWord.compareTo(thatWord);
            if (result != 0) {
                return result;
            }
        }
        return Integer.compare(thisLength, thatLength);
    }
}
