
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
 * This autocompleter has a list of all the commands the application
 * supports.
 *
 * @author Christian Ihle
 */
public class CommandAutoCompleteList implements AutoCompleteList {

    /** The commands. */
    private static final String[] COMMANDS = {
        "/about",
        "/away",
        "/back",
        "/cancel",
        "/clear",
        "/help",
        "/msg",
        "/nick",
        "/quit",
        "/receive",
        "/reject",
        "/send",
        "/topic",
        "/transfers",
        "/users",
        "/whois",
        "//"
    };

    /**
     * Checks if the word is a command, by seeing if the first character is
     * a slash.
     *
     * @param word The word to check.
     * @return If the word is a command.
     */
    @Override
    public boolean acceptsWord(final String word) {
        return word.startsWith("/");
    }

    /**
     * Returns the list of commands.
     * @return The list of commands.
     */
    @Override
    public String[] getWordList() {
        return COMMANDS;
    }
}
