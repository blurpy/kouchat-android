
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

import java.io.File;

import net.usikkert.kouchat.event.NetworkConnectionListener;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.net.NetworkService;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link Controller}.
 *
 * @author Christian Ihle
 */
public class ControllerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Controller controller;

    private Messages messages;
    private NetworkService networkService;

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

        networkService = mock(NetworkService.class);
        TestUtils.setFieldValue(controller, "networkService", networkService);
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

    @Test
    public void sendFileShouldThrowExceptionIfUserIsNull() throws CommandException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        controller.sendFile(null, mock(File.class));
    }

    @Test
    public void sendFileShouldThrowExceptionIfFileIsNull() throws CommandException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File can not be null");

        controller.sendFile(mock(User.class), null);
    }

    @Test
    public void sendFileShouldThrowExceptionIfUserIsMe() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not send a file to yourself");

        final User user = mock(User.class);
        when(user.isMe()).thenReturn(true);

        controller.sendFile(user, mock(File.class));
    }

    @Test
    public void sendFileShouldThrowExceptionIfNotConnected() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not send a file without being connected");

        controller.sendFile(mock(User.class), mock(File.class));
    }

    @Test
    public void sendFileShouldThrowExceptionIfMeIsAway() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not send a file while away");

        when(networkService.isNetworkUp()).thenReturn(true);
        controller.getChatState().setLoggedOn(true);

        me.setAway(true);

        controller.sendFile(mock(User.class), mock(File.class));
    }

    @Test
    public void sendFileShouldThrowExceptionIfUserIsAway() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not send a file to a user that is away");

        when(networkService.isNetworkUp()).thenReturn(true);
        controller.getChatState().setLoggedOn(true);

        final User user = new User("Test", 123);
        user.setAway(true);

        controller.sendFile(user, mock(File.class));
    }

    @Test
    public void sendFileShouldThrowExceptionIfNameIsTooLong() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not send a file with a name with more than 450 bytes");

        when(networkService.isNetworkUp()).thenReturn(true);
        controller.getChatState().setLoggedOn(true);

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 45; i++) {
            sb.append("1234567890");
        }

        sb.append("1"); // 451 characters with 1 byte each, at least in UTF-8

        final File file = mock(File.class);
        when(file.getName()).thenReturn(sb.toString());

        controller.sendFile(mock(User.class), file);
    }

    @Test
    public void sendFileShouldSendFileIfEverythingValidatedOK() throws CommandException {
        when(networkService.isNetworkUp()).thenReturn(true);
        controller.getChatState().setLoggedOn(true);

        final File file = mock(File.class);
        when(file.getName()).thenReturn("file.txt");
        final User user = mock(User.class);

        controller.sendFile(user, file);

        verify(messages).sendFile(user, file);
    }

    @Test
    public void beforeNetworkCameUpShouldDoNothing() {
        controller.beforeNetworkCameUp();

        verifyZeroInteractions(networkService, messages);
    }

    @Test
    public void registerNetworkConnectionListenerShouldUseNetworkService() {
        final NetworkConnectionListener listener = mock(NetworkConnectionListener.class);

        controller.registerNetworkConnectionListener(listener);

        verify(networkService).registerNetworkConnectionListener(listener);
    }
}
