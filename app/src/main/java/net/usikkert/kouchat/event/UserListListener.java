
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

package net.usikkert.kouchat.event;

import net.usikkert.kouchat.misc.User;

/**
 * This interface can be used to be notified when
 * the user list is updated.
 *
 * @author Christian Ihle
 */
public interface UserListListener {

    /**
     * A new user has been added to the user list.
     *
     * @param pos The position in the user list where
     * the user was added.
     * @param user The user that was added.
     */
    void userAdded(int pos, User user);

    /**
     * A user has updated some of its fields,
     * so the ui needs to refresh.
     *
     * @param pos The new position of the changed user in the user list.
     * @param user The (new) changed user.
     */
    void userChanged(int pos, User user);

    /**
     * A user has been removed from the user list.
     *
     * @param pos The position where the user used to be in the user list.
     * @param user The user that was removed.
     */
    void userRemoved(int pos, User user);
}
