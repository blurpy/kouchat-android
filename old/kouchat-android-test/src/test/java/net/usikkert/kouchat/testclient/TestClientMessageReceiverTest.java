
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

package net.usikkert.kouchat.testclient;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link TestClientMessageReceiver}.
 *
 * @author Christian Ihle
 */
public class TestClientMessageReceiverTest {

    private TestClientMessageReceiver messageReceiver;

    @Before
    public void setUp() {
        messageReceiver = new TestClientMessageReceiver();
    }

    @Test
    public void gotMessageShouldFindTheMessageWithTheExactSameNickNameAndMessage() {
        messageReceiver.addMessage("[14:29:21] <Christian>: hello there", -1);

        assertTrue(messageReceiver.gotMessage("Christian", "hello there"));
        assertFalse(messageReceiver.gotMessage("Christian1", "hello there"));
        assertFalse(messageReceiver.gotMessage("Christian", "hello there1"));
    }

    @Test
    public void gotAnyMessagesShouldOnlyBeTrueWhenAMessageExist() {
        assertFalse(messageReceiver.gotAnyMessages());

        messageReceiver.addMessage("something", -1);

        assertTrue(messageReceiver.gotAnyMessages());
    }

    @Test
    public void resetMessagesShouldRemoveAllMessages() {
        messageReceiver.addMessage("something", -1);
        assertTrue(messageReceiver.gotAnyMessages());

        messageReceiver.resetMessages();
        assertFalse(messageReceiver.gotAnyMessages());
    }

    @Test
    public void getMessagesShouldReturnAllStoredMessages() {
        assertEquals(0, messageReceiver.getMessages().size());

        messageReceiver.addMessage("[14:29:21] <Christian>: one", -1);
        assertEquals(1, messageReceiver.getMessages().size());

        messageReceiver.addMessage("[14:29:22] <Christian>: two", -1);
        final List<String> messages = messageReceiver.getMessages();

        assertEquals(2, messages.size());
        assertTrue(messages.contains("[14:29:21] <Christian>: one"));
        assertTrue(messages.contains("[14:29:22] <Christian>: two"));
    }

    @Test
    public void getColorOfMessageShouldReturnMinusOneIfMessageIsNotFound() {
        assertEquals(-1, messageReceiver.getColorOfMessage("NoOne", "With this message"));

        messageReceiver.addMessage("[14:29:21] <Christian>: a message", 100);

        assertEquals(-1, messageReceiver.getColorOfMessage("Christian", "different message"));
        assertEquals(-1, messageReceiver.getColorOfMessage("SomeOneElse", "a message"));
    }

    @Test
    public void getColorOfMessageShouldReturnCorrectColorOfMessage() {
        messageReceiver.addMessage("[14:29:21] <Christian>: a message", 100);
        messageReceiver.addMessage("[14:29:22] <Lilly>: another message", 200);
        messageReceiver.addMessage("[14:29:23] <Christian>: mou?", 300);

        assertEquals(100, messageReceiver.getColorOfMessage("Christian", "a message"));
        assertEquals(200, messageReceiver.getColorOfMessage("Lilly", "another message"));
        assertEquals(300, messageReceiver.getColorOfMessage("Christian", "mou?"));
    }
}
