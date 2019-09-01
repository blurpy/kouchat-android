
/***************************************************************************
 *   Copyright 2006-2019 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.autocomplete;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

/**
 * This class can give suggestions for autocompleting words.
 *
 * At least one {@link AutoCompleteList} with some words is needed
 * to get results.
 *
 * @author Christian Ihle
 */
public class AutoCompleter {

    /**
     * The word that was used to build autocomplete suggestions in the
     * previous search.
     */
    private String lastWord;

    /**
     * The suggested autocompleted word from previous search.
     */
    private String lastCompletedWord;

    /**
     * The full line of text from the previous search, with the autocompleted
     * word replacing the original word.
     */
    private String lastCompletedLine;

    /**
     * The position in the {@link #lastCompletedLine} that marks the end
     * of the {@link #lastCompletedWord}. Useful for setting the caret
     * at the end of the autocompleted word.
     */
    private int newCaretPosition;

    /**
     * A list of {@link AutoCompleteList}s with support for different
     * kinds of words to autocomplete.
     */
    private final List<AutoCompleteList> autoCompleteLists;

    /**
     * Constructor. Initializes variables.
     */
    public AutoCompleter() {
        lastCompletedLine = "";
        lastCompletedWord = "";
        lastWord = "";
        autoCompleteLists = new ArrayList<>();
    }

    /**
     * Extracts the word at the given position from the line,
     * and looks for suggestions for autocompleting the word.
     * <br><br>
     * The previous search is saved, so if there are more than one
     * suggestion, repeated calls to this method will give the next
     * result in the suggestion list.
     *
     * @param line The line of text where the word to autocomplete is.
     * @param caretPosition The position in the line to look for the word.
     * @return The complete line, where the original word is replaced by
     *         the suggested autocompleted word.
     *         Use {@link #getNewCaretPosition()} to get the new caret position.
     */
    public String completeWord(final String line, final int caretPosition) {
        String completedLine = "";

        if (autoCompleteLists.size() > 0) {
            final int stop = findStopPosition(line, caretPosition);
            final int start = findStartPosition(line, caretPosition);
            final String word = line.substring(start, stop);

            if (word.trim().length() > 0) {
                final boolean continueLastSearch = continueLastSearch(word, line);
                String checkword = "";

                if (continueLastSearch) {
                    checkword = lastWord;
                } else {
                    checkword = word;
                }

                final AutoCompleteList autoCompleteList = getAutoCompleteList(checkword);

                if (autoCompleteList != null) {
                    final List<String> suggestions = getAutoCompleteSuggestions(
                            autoCompleteList.getWordList(), checkword);

                    if (suggestions.size() > 0) {
                        final int nextSuggestionPosition = findNextSuggestionPosition(
                                continueLastSearch, suggestions, word);
                        final String newWord = suggestions.get(nextSuggestionPosition);
                        completedLine = line.substring(0, start) + newWord;
                        newCaretPosition = completedLine.length();
                        completedLine += line.substring(stop);
                        lastCompletedLine = completedLine;
                        lastCompletedWord = newWord;

                        if (!continueLastSearch) {
                            lastWord = word;
                        }
                    }
                }
            }
        }

        return completedLine;
    }

    /**
     * Finds where in the list of suggestions to get the next suggestion.
     *
     * @param continueLastSearch If the previous search should be continued.
     * @param suggestions The list of suggested words.
     * @param word The word that is going to be autocompleted by the suggestion
     *             this method finds. If this search continues from the previous
     *             search, the word will be the same as the suggestion from that search.
     * @return The position in the list where the next suggestion can be found.
     */
    private int findNextSuggestionPosition(final boolean continueLastSearch,
            final List<String> suggestions, final String word) {
        int nextSuggestionPosition = -1;

        if (continueLastSearch) {
            // Locate the position of the previous suggestion in the list
            for (int i = 0; i < suggestions.size(); i++) {
                if (suggestions.get(i).equals(word)) {
                    nextSuggestionPosition = i;
                    break;
                }
            }

            /* If more suggestions are available, increase position,
             * or else start from the beginning again. */
            if (nextSuggestionPosition > -1 && nextSuggestionPosition < suggestions.size() - 1) {
                nextSuggestionPosition++;
            } else {
                nextSuggestionPosition = 0;
            }
        }

        // New search, start with first suggestion
        if (nextSuggestionPosition == -1) {
            nextSuggestionPosition = 0;
        }

        return nextSuggestionPosition;
    }

    /**
     * Checks if the new search should continue where the last left off.
     * The reason to continue is to see if there are more matches to the
     * previous search.
     * <br><br>
     * To find this, a check is done to see if the previous autocompleted
     * word and line is the same as the new word and line. If that's the case,
     * no changes to the text has been done since last autocomplete attempt.
     *
     * @param word The word to compare against the previous autocompleted word.
     * @param line The line to compare against the previous autocompleted line.
     * @return True if the search should be continued instead of restarted.
     */
    private boolean continueLastSearch(final String word, final String line) {
        return lastCompletedWord.equals(word) && lastCompletedLine.equals(line);
    }

    /**
     * Locates the position in the line where the word ends.
     *
     * @param line The line of text where the word is.
     * @param caretPosition The position in the line where the word is.
     * @return The position where the word ends.
     */
    private int findStopPosition(final String line, final int caretPosition) {
        int stop = line.indexOf(' ', caretPosition);

        if (stop == -1) {
            stop = line.length();
        }

        return stop;
    }

    /**
     * Locates the position in the line where the word starts.
     *
     * @param line The line of text where the word is.
     * @param caretPosition The position in the line where the word is.
     * @return The position where the word starts.
     */
    private int findStartPosition(final String line, final int caretPosition) {
        int start = line.lastIndexOf(' ', caretPosition - 1);

        if (start == -1) {
            start = 0;
        } else {
            start++;
        }

        return start;
    }

    /**
     * Asks the {@link AutoCompleteList}s available if any of them supports
     * this kind of word, and returns the match, if found.
     *
     * @param word The word to ask if any {@link AutoCompleteList} supports.
     * @return The first {@link AutoCompleteList} to support that word,
     *         or <em>null</em> if none.
     */
    @Nullable
    private AutoCompleteList getAutoCompleteList(final String word) {
        for (final AutoCompleteList acl : autoCompleteLists) {
            if (acl.acceptsWord(word)) {
                return acl;
            }
        }

        return null;
    }

    /**
     * Compares the word with the word list to find suggestions for
     * autocompleting the word.
     *
     * @param wordList A list of words to compare the word with.
     * @param word The word to get suggestions for.
     * @return A list of suggestions.
     */
    private List<String> getAutoCompleteSuggestions(final String[] wordList, final String word) {
        final List<String> suggestions = new ArrayList<>();

        for (final String wordFromList : wordList) {
            if (wordFromList.toLowerCase().startsWith(word.toLowerCase())) {
                suggestions.add(wordFromList);
            }
        }

        return suggestions;
    }

    /**
     * Returns the new caret position for the last completed search.
     *
     * @return The new caret position.
     */
    public int getNewCaretPosition() {
        return newCaretPosition;
    }

    /**
     * Adds a new {@link AutoCompleteList} to use for autocompletion.
     *
     * @param acl The list to add.
     */
    public void addAutoCompleteList(final AutoCompleteList acl) {
        autoCompleteLists.add(acl);
    }
}
