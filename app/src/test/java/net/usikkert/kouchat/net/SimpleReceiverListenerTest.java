
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

package net.usikkert.kouchat.net;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test of {@link SimpleReceiverListener}.
 *
 * @author Christian Ihle
 */
public class SimpleReceiverListenerTest {

    /**
     * Tests that any message and ip address is saved when no
     * expected message is set.
     */
    @Test
    public void testListenerWithNoExpectedMessage() {
        final SimpleReceiverListener listener = new SimpleReceiverListener(null);
        listener.messageArrived("A message", "An ip address");

        assertEquals("A message", listener.getMessage());
        assertEquals("An ip address", listener.getIpAddress());
    }

    /**
     * Tests that the message and ip address is not saved when an expected
     * message is set, but another message is received.
     */
    @Test
    public void testListenerWithWrongMessage() {
        final SimpleReceiverListener listener = new SimpleReceiverListener("Some message :)");
        listener.messageArrived("A message", "An ip address");

        assertNull(listener.getMessage());
        assertNull(listener.getIpAddress());
    }

    /**
     * Tests that the message and ip address is saved when an expected
     * message is set, and received.
     */
    @Test
    public void testListenerWithCorrectMessage() {
        final SimpleReceiverListener listener = new SimpleReceiverListener("Another message :)");
        listener.messageArrived("Another message :)", "An ip address");

        assertEquals("Another message :)", listener.getMessage());
        assertEquals("An ip address", listener.getIpAddress());
    }
}
