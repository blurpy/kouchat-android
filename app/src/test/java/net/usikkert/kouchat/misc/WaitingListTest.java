
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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test of {@link WaitingList}.
 *
 * @author Christian Ihle
 */
public class WaitingListTest {

    private WaitingList waitingList;

    @Before
    public void setUp() {
        waitingList = new WaitingList();
    }

    @Test
    public void addAndRemoveWaitingUserShouldWork() {
        final int userCode = 123;

        waitingList.addWaitingUser(userCode);
        assertTrue(waitingList.isWaitingUser(userCode));

        waitingList.removeWaitingUser(userCode);
        assertFalse(waitingList.isWaitingUser(userCode));
    }

    @Test
    public void removeWaitingUserShouldRemoveEvenWhenAddedMultipleTimes() {
        final int userCode = 124;

        waitingList.addWaitingUser(userCode);
        waitingList.addWaitingUser(userCode);
        assertTrue(waitingList.isWaitingUser(userCode));

        waitingList.removeWaitingUser(userCode);
        assertFalse(waitingList.isWaitingUser(userCode));
    }

    @Test
    public void removeWaitingUserShouldIgnoreUnknownUser() {
        final int userCode = 125;

        assertFalse(waitingList.isWaitingUser(userCode));
        waitingList.removeWaitingUser(userCode);
    }
}
