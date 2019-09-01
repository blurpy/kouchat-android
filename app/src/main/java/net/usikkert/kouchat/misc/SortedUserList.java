
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

package net.usikkert.kouchat.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.usikkert.kouchat.event.UserListListener;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * This is a sorted version of the user list.
 *
 * <p>The users in the list are sorted by nick name,
 * as specified in {@link User#compareTo(User)}.</p>
 *
 * @author Christian Ihle
 */
public class SortedUserList implements UserList {

    /** The list of users in the chat. */
    private final List<User> userList;

    /** The list of listeners of changes to the user list. */
    private final List<UserListListener> listeners;

    /**
     * Constructor.
     */
    public SortedUserList() {
        userList = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    /**
     * Adds the user, and then sorts the list.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean add(final User user) {
        Validate.notNull(user, "User can not be null");

        final boolean success = userList.add(user);

        if (success) {
            Collections.sort(userList);
            fireUserAdded(userList.indexOf(user), user);
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public User get(final int pos) {
        if (pos < userList.size()) {
            return userList.get(pos);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(final User user) {
        Validate.notNull(user, "User can not be null");

        return userList.indexOf(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(final User user) {
        Validate.notNull(user, "User can not be null");

        final int pos = userList.indexOf(user);
        final boolean success = userList.remove(user);

        if (success) {
            fireUserRemoved(pos, user);
        }

        return success;
    }

    /**
     * Sets the user, and then sorts the list.
     *
     * {@inheritDoc}
     */
    @Override
    public User set(final int pos, final User user) {
        Validate.notNull(user, "User can not be null");

        final User oldUser = userList.set(pos, user);
        Collections.sort(userList);
        fireUserChanged(userList.indexOf(user), user);

        return oldUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return userList.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUserListListener(final UserListListener listener) {
        Validate.notNull(listener, "UserListListener can not be null");

        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeUserListListener(final UserListListener listener) {
        Validate.notNull(listener, "UserListListener can not be null");

        listeners.remove(listener);
    }

    /**
     * Returns the current listeners.
     *
     * @return The current listeners.
     */
    public List<UserListListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    /**
     * Notifies the listeners that a user was added.
     *
     * @param pos The position where the user was added.
     */
    private void fireUserAdded(final int pos, final User user) {
        for (final UserListListener listener : listeners) {
            listener.userAdded(pos, user);
        }
    }

    /**
     * Notifies the listeners that a user was changed.
     *
     * @param pos The new position of the changed user.
     */
    private void fireUserChanged(final int pos, final User user) {
        for (final UserListListener listener : listeners) {
            listener.userChanged(pos, user);
        }
    }

    /**
     * Notifies the listeners that a user was removed.
     *
     * @param pos The position of the removed user.
     */
    private void fireUserRemoved(final int pos, final User user) {
        for (final UserListListener listener : listeners) {
            listener.userRemoved(pos, user);
        }
    }
}
