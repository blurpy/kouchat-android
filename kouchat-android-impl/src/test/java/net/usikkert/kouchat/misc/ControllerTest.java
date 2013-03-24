
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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

import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link Controller}.
 *
 * @author Christian Ihle
 */
public class ControllerTest {

    private Controller controller;

    private Messages messages;
    private User me;

    @Before
    public void setUp() {
        final Settings settings = mock(Settings.class);

        me = new User("TestUser", 123);
        when(settings.getMe()).thenReturn(me);

        final UserInterface ui = mock(UserInterface.class);
        when(ui.getMessageController()).thenReturn(mock(MessageController.class));

        controller = new Controller(ui, settings);

        messages = mock(Messages.class);
        TestUtils.setFieldValue(controller, "messages", messages);
    }

    @Test
    public void updateMeWritingShouldUpdateMeAndNotifyOthersOnlyWhenStateChanges() {
        assertFalse(me.isWriting());

        // Not writing - nothing happens
        controller.updateMeWriting(false);
        verifyZeroInteractions(messages);
        assertFalse(me.isWriting());

        // Wrote something - notify others and update me
        controller.updateMeWriting(true);
        verify(messages).sendWritingMessage();
        assertTrue(me.isWriting());

        // Continues to write - nothing happens
        controller.updateMeWriting(true);
        verifyNoMoreInteractions(messages);
        assertTrue(me.isWriting());

        // Stopped writing - notify others and update me
        controller.updateMeWriting(false);
        verify(messages).sendStoppedWritingMessage();
        assertFalse(me.isWriting());

        // Still not writing - nothing happens
        controller.updateMeWriting(false);
        verifyNoMoreInteractions(messages);
        assertFalse(me.isWriting());
    }
}
