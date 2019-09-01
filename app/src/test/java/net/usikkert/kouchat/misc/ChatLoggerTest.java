
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
import net.usikkert.kouchat.settings.Settings;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link ChatLogger}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ChatLoggerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ChatLogger chatLogger;
    private Settings settings;
    private ErrorHandler errorHandler;

    @Before
    public void setUp() {
        settings = new Settings();
        settings.setLogLocation(System.getProperty("java.io.tmpdir"));

        errorHandler = mock(ErrorHandler.class);

        chatLogger = new ChatLogger(settings, errorHandler);
    }

    @Test
    public void constructor1ShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new ChatLogger(null, errorHandler);
    }

    @Test
    public void constructor1ShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new ChatLogger(settings, null);
    }

    @Test
    public void constructor2ShouldThrowExceptionIfLogFilePrefixIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Log file prefix can not be empty");

        new ChatLogger(null, settings, errorHandler);
    }

    @Test
    public void constructor2ShouldThrowExceptionIfLogFilePrefixIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Log file prefix can not be empty");

        new ChatLogger(" ", settings, errorHandler);
    }

    @Test
    public void constructor2ShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new ChatLogger("prefix", null, errorHandler);
    }

    @Test
    public void constructor2ShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new ChatLogger("prefix", settings, null);
    }

    @Test
    public void settingsListenerShouldEnableAndDisableLoggingBasedOnChangedSetting() {
        assertFalse(chatLogger.isOpen());

        settings.setLogging(true);
        assertTrue(chatLogger.isOpen());

        settings.setLogging(false);
        assertFalse(chatLogger.isOpen());
    }
}
