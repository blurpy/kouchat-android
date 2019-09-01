
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

import java.util.HashSet;
import java.util.Set;

/**
 * This waiting list is used to store unknown users while asking them to
 * identify. Usually it's users that timed out at some point, and are returning.
 * By doing this, messages from unknown users can be held back until they
 * have identified themselves.
 *
 * @author Christian Ihle
 */
public class WaitingList {

    private final Set<Integer> users;

    /**
     * Constructor.
     */
    public WaitingList() {
        users = new HashSet<>();
    }

    /**
     * Adds a user to the waiting list.
     *
     * @param userCode The unique code of the user to add.
     */
    public void addWaitingUser(final int userCode) {
        users.add(userCode);
    }

    /**
     * Checks if a user is on the waiting list.
     *
     * @param userCode The unique code of the user to check for.
     * @return If the user is on the waiting list.
     */
    public boolean isWaitingUser(final int userCode) {
        return users.contains(userCode);
    }

    /**
     * Removes a user from the waiting list.
     *
     * @param userCode The unique code of the user to remove.
     */
    public void removeWaitingUser(final int userCode) {
        users.remove(userCode);
    }
}
