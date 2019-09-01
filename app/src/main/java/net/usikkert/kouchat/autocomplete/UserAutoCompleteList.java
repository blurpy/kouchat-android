
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

import net.usikkert.kouchat.event.UserListListener;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.util.Tools;

/**
 * This autocompleter has a list of all the users currently online.
 *
 * @author Christian Ihle
 */
public class UserAutoCompleteList implements AutoCompleteList, UserListListener {

    /** The real user list. */
    private final UserList userList;

    /** A simple array with users, for use in auto completion. */
    private String[] users;

    /**
     * Constructor. Registers itself as a user list listener.
     *
     * @param userList The list of online users.
     */
    public UserAutoCompleteList(final UserList userList) {
        this.userList = userList;
        userList.addUserListListener(this);
        updateWords();
    }

    /**
     * Updates the list of users.
     *
     * {@inheritDoc}
     */
    @Override
    public void userAdded(final int pos, final User user) {
        updateWords();
    }

    /**
     * Updates the list of users.
     *
     * {@inheritDoc}
     */
    @Override
    public void userChanged(final int pos, final User user) {
        updateWords();
    }

    /**
     * Updates the list of users.
     *
     * {@inheritDoc}
     */
    @Override
    public void userRemoved(final int pos, final User user) {
        updateWords();
    }

    /**
     * Iterates through the user list, and adds all the nick names to the
     * list of words.
     */
    private void updateWords() {
        users = new String[userList.size()];

        for (int i = 0; i < userList.size(); i++) {
            users[i] = userList.get(i).getNick();
        }
    }

    /**
     * Checks if the word is a valid nick name.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean acceptsWord(final String word) {
        return Tools.isValidNick(word);
    }

    /**
     * Returns a list of all the users.
     *
     * {@inheritDoc}
     */
    @Override
    public String[] getWordList() {
        return users;
    }
}
