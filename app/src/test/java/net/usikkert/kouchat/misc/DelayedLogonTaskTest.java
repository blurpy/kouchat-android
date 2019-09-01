
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.net.NetworkService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link DelayedLogonTask}.
 *
 * @author Christian Ihle
 */
public class DelayedLogonTaskTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DelayedLogonTask logonTask;

    private NetworkService networkService;
    private ChatState chatState;

    @Before
    public void setUp() {
        networkService = mock(NetworkService.class);
        chatState = new ChatState();

        logonTask = new DelayedLogonTask(networkService, chatState);
    }

    @Test
    public void constructorShouldThrowExceptionIfNetworkServiceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("NetworkService can not be null");

        new DelayedLogonTask(null, chatState);
    }

    @Test
    public void constructorShouldThrowExceptionIfChatStateIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("ChatState can not be null");

        new DelayedLogonTask(networkService, null);
    }

    @Test
    public void runShouldDoNothingIfNetworkIsDown() {
        assertFalse(networkService.isNetworkUp());
        assertFalse(chatState.isLogonCompleted());

        logonTask.run();

        assertFalse(chatState.isLogonCompleted());
    }

    @Test
    public void runShouldSetLogonCompletedIfNetworkIsUp() {
        when(networkService.isNetworkUp()).thenReturn(true);
        assertFalse(chatState.isLogonCompleted());

        logonTask.run();

        assertTrue(chatState.isLogonCompleted());
    }
}
