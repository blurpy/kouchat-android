
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

import java.net.NetworkInterface;
import java.util.Enumeration;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link OperatingSystemNetworkInfo}.
 *
 * <p>This test might fail on Mac OS X. If so, add <code>-Djava.net.preferIPv4Stack=true</code>
 * as VM option.</p>
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class OperatingSystemNetworkInfoTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final NetworkUtils networkUtils = new NetworkUtils();

    private Settings settings;
    private ErrorHandler errorHandler;

    @Before
    public void setUp() {
        settings = mock(Settings.class);
        when(settings.getMe()).thenReturn(new User("testuser", 123));
        errorHandler = mock(ErrorHandler.class);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new OperatingSystemNetworkInfo(null, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new OperatingSystemNetworkInfo(settings, null);
    }

    /**
     * Tests if the network interface for the operating system can be found.
     *
     * <p>But only if there are usable network interfaces available.</p>
     */
    @Test
    public void testFindingTheOSNetworkInterface() {
        final Enumeration<NetworkInterface> networkInterfaces = networkUtils.getNetworkInterfaces();
        final OperatingSystemNetworkInfo osNicInfo = new OperatingSystemNetworkInfo(settings, errorHandler);
        final NetworkInterface osInterface = osNicInfo.getOperatingSystemNetworkInterface();

        if (networkInterfaces == null) {
            System.err.println("No network interfaces found.");
            assertNull(osInterface);
            return;
        }

        boolean validNetworkAvailable = false;

        while (networkInterfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = networkInterfaces.nextElement();

            if (networkUtils.isUsable(networkInterface)) {
                validNetworkAvailable = true;
                break;
            }
        }

        if (!validNetworkAvailable) {
            System.err.println("No usable network interfaces found.");
            assertNull(osInterface);
            return;
        }

        assertNotNull(osInterface);

        // This is known to sometimes fail in Vista. It is unknown why Vista
        // prefers unusable network interfaces.
        assertTrue(networkUtils.isUsable(osInterface));
    }
}
