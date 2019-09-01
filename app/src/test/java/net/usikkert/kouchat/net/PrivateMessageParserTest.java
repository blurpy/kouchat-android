
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
import static org.mockito.Mockito.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.TestUtils;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * Test of {@link PrivateMessageParser}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class PrivateMessageParserTest {

    private PrivateMessageParser privateMessageParser;

    private Logger log;

    @Before
    public void setUp() {
        final Settings settings = mock(Settings.class);
        when(settings.getMe()).thenReturn(new User("Test", 1234));

        privateMessageParser = new PrivateMessageParser(mock(PrivateMessageResponder.class), settings);

        log = TestUtils.setFieldValueWithMock(privateMessageParser, "LOG", Logger.class);
    }

    @Test
    public void messageArrivedShouldLogIfUnableToFindNecessaryDetailsInMessage() {
        privateMessageParser.messageArrived("Error", "192.168.1.2");

        final ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

        verify(log).log(eq(Level.SEVERE),
                        eq("Failed to parse message. message=Error, ipAddress=192.168.1.2"),
                        exceptionCaptor.capture());

        // String index out of range: -1 - but different on JDK 11
        checkException(exceptionCaptor, StringIndexOutOfBoundsException.class, null);
    }

    @Test
    public void messageArrivedShouldLogIfUnableToParseUserCodeInMessage() {
        privateMessageParser.messageArrived("a12516938!PRIVMSG#12516938:(18737868)[-6750208]hello", "192.168.1.2");

        final ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

        verify(log).log(eq(Level.SEVERE),
                        eq("Failed to parse message. " +
                                   "message=a12516938!PRIVMSG#12516938:(18737868)[-6750208]hello, " +
                                   "ipAddress=192.168.1.2"),
                        exceptionCaptor.capture());

        checkException(exceptionCaptor, NumberFormatException.class, "For input string: \"a12516938\"");
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void checkException(final ArgumentCaptor<Exception> exceptionCaptor,
                                final Class<? extends Exception> expectedException,
                                @Nullable final String expectedMessage) {
        final Exception exception = exceptionCaptor.getValue();

        assertEquals(expectedException, exception.getClass());

        if (expectedMessage != null) {
            assertEquals(expectedMessage, exception.getMessage());
        }
    }
}
