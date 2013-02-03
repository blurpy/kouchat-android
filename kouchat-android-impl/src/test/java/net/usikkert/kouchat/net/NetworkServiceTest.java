
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

package net.usikkert.kouchat.net;

import static org.junit.Assert.*;

import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Test;

/**
 * Test of {@link NetworkService}.
 *
 * @author Christian Ihle
 */
public class NetworkServiceTest {

    @Test
    public void networkServiceShouldLoadPrivateChatObjectsWhenEnabled() {
        Settings.getSettings().setNoPrivateChat(false);

        final NetworkService networkService = new NetworkService();

        assertNotNull(TestUtils.getFieldValue(networkService, UDPReceiver.class, "udpReceiver"));
        assertNotNull(TestUtils.getFieldValue(networkService, UDPSender.class, "udpSender"));
    }

    @Test
    public void networkServiceShouldNotLoadPrivateChatObjectsWhenDisabled() {
        Settings.getSettings().setNoPrivateChat(true);

        final NetworkService networkService = new NetworkService();

        assertNull(TestUtils.getFieldValue(networkService, UDPReceiver.class, "udpReceiver"));
        assertNull(TestUtils.getFieldValue(networkService, UDPSender.class, "udpSender"));
    }

    @Test
    public void registerUDPReceiverListenerShouldNotFailWhenPrivateChatDisabled() {
        Settings.getSettings().setNoPrivateChat(true);

        final NetworkService networkService = new NetworkService();

        networkService.registerUDPReceiverListener(null);
    }

    @Test
    public void networkCameUpShouldNotFailWhenPrivateChatDisabled() {
        Settings.getSettings().setNoPrivateChat(true);

        final NetworkService networkService = new NetworkService();

        networkService.networkCameUp(false);
    }

    @Test
    public void networkWentDownShouldNotFailWhenPrivateChatDisabled() {
        Settings.getSettings().setNoPrivateChat(true);

        final NetworkService networkService = new NetworkService();

        networkService.networkWentDown(false);
    }

    @Test
    public void sendUDPMsgShouldNotSendMessageWhenPrivateChatDisabled() {
        Settings.getSettings().setNoPrivateChat(true);

        final NetworkService networkService = new NetworkService();

        final boolean messageSent = networkService.sendUDPMsg("Nothing", "192.168.1.1", 1234);
        assertFalse(messageSent);
    }
}
