
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

import static org.mockito.Mockito.*;

import net.usikkert.kouchat.ui.ChatWindow;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link MessageController}.
 *
 * @author Christian Ihle
 */
public class MessageControllerTest {

    private MessageController messageController;

    private ChatLogger chatLogger;

    @Before
    public void setUp() {
        messageController = new MessageController(mock(ChatWindow.class), mock(UserInterface.class), mock(Settings.class));

        chatLogger = mock(ChatLogger.class);
        TestUtils.setFieldValue(messageController, "cLog", chatLogger);
    }

    @Test
    public void shutdownShouldCloseTheChatLogger() {
        messageController.shutdown();

        verify(chatLogger).close();
    }
}
