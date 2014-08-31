
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
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

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link NetworkService}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class NetworkServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Settings settings;
    private ErrorHandler errorHandler;

    @Before
    public void setUp() {
        settings = mock(Settings.class);
        errorHandler = mock(ErrorHandler.class);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new NetworkService(null, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new NetworkService(settings, null);
    }

    @Test
    public void networkServiceShouldLoadPrivateChatObjectsWhenEnabled() {
        when(settings.isNoPrivateChat()).thenReturn(false);

        final NetworkService networkService = new NetworkService(settings, errorHandler);

        assertNotNull(TestUtils.getFieldValue(networkService, UDPReceiver.class, "udpReceiver"));
        assertNotNull(TestUtils.getFieldValue(networkService, UDPSender.class, "udpSender"));
    }

    @Test
    public void networkServiceShouldNotLoadPrivateChatObjectsWhenDisabled() {
        when(settings.isNoPrivateChat()).thenReturn(true);

        final NetworkService networkService = new NetworkService(settings, errorHandler);

        assertNull(TestUtils.getFieldValue(networkService, UDPReceiver.class, "udpReceiver"));
        assertNull(TestUtils.getFieldValue(networkService, UDPSender.class, "udpSender"));
    }

    @Test
    public void registerUDPReceiverListenerShouldNotFailWhenPrivateChatDisabled() {
        when(settings.isNoPrivateChat()).thenReturn(true);

        final NetworkService networkService = new NetworkService(settings, errorHandler);

        networkService.registerUDPReceiverListener(null);
    }

    @Test
    public void beforeNetworkCameUpShouldDoNothing() {
        final NetworkService networkService = new NetworkService(settings, errorHandler);

        networkService.beforeNetworkCameUp();
    }

    @Test
    public void networkCameUpShouldNotFailWhenPrivateChatDisabled() {
        when(settings.isNoPrivateChat()).thenReturn(true);

        final NetworkService networkService = new NetworkService(settings, errorHandler);

        networkService.networkCameUp(false);
    }

    @Test
    public void networkWentDownShouldNotFailWhenPrivateChatDisabled() {
        when(settings.isNoPrivateChat()).thenReturn(true);

        final NetworkService networkService = new NetworkService(settings, errorHandler);

        networkService.networkWentDown(false);
    }

    @Test
    public void sendUDPMsgShouldNotSendMessageWhenPrivateChatDisabled() {
        when(settings.isNoPrivateChat()).thenReturn(true);

        final NetworkService networkService = new NetworkService(settings, errorHandler);

        final boolean messageSent = networkService.sendUDPMsg("Nothing", "192.168.1.1", 1234);
        assertFalse(messageSent);
    }
}
