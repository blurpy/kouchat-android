
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

import static org.mockito.Mockito.*;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.ErrorHandler;

import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link MessageReceiver}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class MessageReceiverTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructor1ShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new MessageReceiver(null);
    }

    @Test
    public void constructor2ShouldThrowExceptionIfIpAddressIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("IP address can not be empty");

        new MessageReceiver(null, 0, mock(ErrorHandler.class));
    }

    @Test
    public void constructor2ShouldThrowExceptionIfIpAddressIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("IP address can not be empty");

        new MessageReceiver(" ", 0, mock(ErrorHandler.class));
    }

    @Test
    public void constructor2ShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new MessageReceiver("ip", 0, null);
    }
}
