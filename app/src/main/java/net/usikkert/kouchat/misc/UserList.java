
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

import net.usikkert.kouchat.event.UserListListener;

/**
 * This is the interface used for keeping a list of the users
 * connected to the chat.
 *
 * <p>This is not a normal {@link java.util.List}, but the interface reminds of
 * one, so it can be used like a normal list. The reason a normal
 * list is not used instead is because of the need to notify of changes
 * to the list.</p>
 *
 * <p>The {@link java.util.List} interface is not extended because all the methods
 * there are not needed.</p>
 *
 * @author Christian Ihle
 */
public interface UserList {

    /**
     * Adds a user to the list, and notifies with {@link UserListListener#userAdded(int, User)}.
     *
     * @param user The user to add.
     * @return If the user was successfully added to the list.
     */
    boolean add(User user);

    /**
     * Gets the user at the specified position.
     *
     * @param pos The position to get the user.
     * @return The user, or <code>null</code> of the user was not found.
     */
    User get(int pos);

    /**
     * Gets the position in the list where this user is located.
     *
     * @param user The user to locate the position of.
     * @return The position, or -1 if not found.
     */
    int indexOf(User user);

    /**
     * Removes the specified user from the list,
     * and notifies with {@link UserListListener#userRemoved(int, User)}.
     *
     * @param user The user to remove.
     * @return If the user was successfully removed.
     */
    boolean remove(User user);

    /**
     * Sets the specified user at the specified position in the user list,
     * and notifies with {@link UserListListener#userChanged(int, User)}.
     *
     * @param pos The position to put the user.
     * @param user The user to put in the position.
     * @return The user that was previously in that position.
     */
    User set(int pos, User user);

    /**
     * Gets the number for users in the list.
     *
     * @return The number of users.
     */
    int size();

    /**
     * Adds a listener for changes to the user list.
     *
     * @param listener The listener to add.
     */
    void addUserListListener(UserListListener listener);

    /**
     * Removes a listener for changes to the user list.
     *
     * @param listener The listener to remove.
     */
    void removeUserListListener(UserListListener listener);
}
