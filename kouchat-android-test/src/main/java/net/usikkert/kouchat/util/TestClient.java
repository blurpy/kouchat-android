
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
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

package net.usikkert.kouchat.util;

import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.MessageParser;
import net.usikkert.kouchat.net.MessageResponderMock;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.net.NetworkService;

/**
 * A class that can be used for simulating a KouChat client in tests.
 *
 * @author Christian Ihle
 */
public class TestClient {

    private final NetworkService networkService;
    private final Messages messages;
    private final MessageResponderMock messageResponderMock;

    public TestClient() {
        final User me = new User("Test", 12345678);
        final Settings settings = TestUtils.createInstance(Settings.class);
        TestUtils.setFieldValue(settings, "me", me);

        networkService = new NetworkService();

        messages = new Messages(networkService);
        TestUtils.setFieldValue(messages, "me", me);

        messageResponderMock = new MessageResponderMock();

        final MessageParser messageParser = new MessageParser(messageResponderMock);
        TestUtils.setFieldValue(messageParser, "settings", settings);

        networkService.registerMessageReceiverListener(messageParser);
    }

    public Messages logon() {
        networkService.connect();

        waitForConnection();

        messages.sendLogonMessage();
        messages.sendClient();
        messages.sendExposeMessage();

        sleep(100);

        return messages;
    }

    public void logoff() {
        if (!networkService.isNetworkUp()) {
            return;
        }

        messages.sendLogoffMessage();
        sleep(500);
        networkService.disconnect();
    }

    public MessageResponderMock getMessageResponderMock() {
        return messageResponderMock;
    }

    private void sleep(final int time) {
        try {
            Thread.sleep(time);
        }

        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForConnection() {
        for (int i = 0; i < 20; i++) {
            if (!networkService.isNetworkUp()) {
                sleep(100);
            }
        }

        if (!networkService.isNetworkUp()) {
            throw new RuntimeException("Could not connect");
        }
    }
}
