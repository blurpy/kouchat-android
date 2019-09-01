
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

import static org.mockito.Mockito.*;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.ChatWindow;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link MessageController}.
 *
 * @author Christian Ihle
 */
public class MessageControllerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MessageController messageController;

    private ChatLogger chatLogger;
    private ChatWindow chatWindow;
    private UserInterface userInterface;
    private Settings settings;
    private ErrorHandler errorHandler;

    @Before
    public void setUp() {
        chatWindow = mock(ChatWindow.class);
        userInterface = mock(UserInterface.class);
        settings = mock(Settings.class);
        errorHandler = mock(ErrorHandler.class);

        messageController = new MessageController(chatWindow, userInterface, settings, errorHandler);

        chatLogger = mock(ChatLogger.class);
        TestUtils.setFieldValue(messageController, "cLog", chatLogger);
    }

    @Test
    public void constructorShouldThrowExceptionIfChatWindowIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("ChatWindow can not be null");

        new MessageController(null, userInterface, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfUserInterfaceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("UserInterface can not be null");

        new MessageController(chatWindow, null, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new MessageController(chatWindow, userInterface, null, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new MessageController(chatWindow, userInterface, settings, null);
    }

    @Test
    public void shutdownShouldCloseTheChatLogger() {
        messageController.shutdown();

        verify(chatLogger).close();
    }
}
