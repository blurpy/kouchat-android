
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

/**
 * This interface is used by {@link AutoCompleter} to check for suggestions
 * for autocompleting a word.
 * <br><br>
 * Implement this to add support for more types of words.
 *
 * @author Christian Ihle
 */
public interface AutoCompleteList {

    /**
     * Gets the list of words this autocompleter currently contains.
     *
     * @return The list of words for this autocompleter.
     */
    String[] getWordList();

    /**
     * Checks if this autocompleter supports that type of word.
     * <br><br>
     * This should <strong>not</strong> check if the list of words contains
     * a match, only if the word matches a pattern of words this list can
     * have.
     * <br><br>
     * The use of a regex match, or a {@link String#startsWith(String)}
     * is a good start.
     *
     * @param word The word to check.
     * @return True if this autocompleter can give suggestions for that
     * type of word.
     */
    boolean acceptsWord(String word);
}
