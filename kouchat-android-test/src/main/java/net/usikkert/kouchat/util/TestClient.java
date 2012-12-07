
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
import net.usikkert.kouchat.net.ConnectionWorker;
import net.usikkert.kouchat.net.MessageParser;
import net.usikkert.kouchat.net.MessageResponderMock;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.net.NetworkService;
import net.usikkert.kouchat.net.OperatingSystemNetworkInfo;
import net.usikkert.kouchat.net.PrivateMessageParser;
import net.usikkert.kouchat.net.PrivateMessageResponderMock;
import net.usikkert.kouchat.net.UDPReceiver;

/**
 * A class that can be used for simulating a KouChat client in tests.
 *
 * @author Christian Ihle
 */
public class TestClient {

    private final NetworkService networkService;
    private final Messages messages;
    private final MessageResponderMock messageResponderMock;
    private final PrivateMessageResponderMock privateMessageResponderMock;
    private TestClient.SimpleIdleThread simpleIdleThread;

    public TestClient() {
        this("Test", 12345678);
    }

    public TestClient(final String nickName, final int userCode) {
        final Settings settings = TestUtils.createInstance(Settings.class);

        final User me = settings.getMe();
        me.setNick(nickName);
        TestUtils.setFieldValue(me, "code", userCode);

        networkService = new NetworkService();

        final UDPReceiver udpReceiver = TestUtils.getFieldValue(networkService, UDPReceiver.class, "udpReceiver");
        TestUtils.setFieldValue(udpReceiver, "me", me);

        final ConnectionWorker connectionWorker =
                TestUtils.getFieldValue(networkService, ConnectionWorker.class, "connectionWorker");

        final OperatingSystemNetworkInfo osNetworkInfo =
                TestUtils.getFieldValue(connectionWorker, OperatingSystemNetworkInfo.class, "osNetworkInfo");
        TestUtils.setFieldValue(osNetworkInfo, "me", me);

        messages = new Messages(networkService);
        TestUtils.setFieldValue(messages, "me", me);

        messageResponderMock = new MessageResponderMock(me);
        final MessageParser messageParser = new MessageParser(messageResponderMock);
        TestUtils.setFieldValue(messageParser, "settings", settings);

        networkService.registerMessageReceiverListener(messageParser);

        privateMessageResponderMock = new PrivateMessageResponderMock();
        final PrivateMessageParser privateMessageParser = new PrivateMessageParser(privateMessageResponderMock);
        TestUtils.setFieldValue(privateMessageParser, "settings", settings);

        networkService.registerUDPReceiverListener(privateMessageParser);
    }

    public Messages logon() {
        networkService.connect();

        waitForConnection();

        messages.sendLogonMessage();
        messages.sendClient();
        messages.sendExposeMessage();

        simpleIdleThread = new SimpleIdleThread();
        simpleIdleThread.start();

        Tools.sleep(100);

        return messages;
    }

    public void logoff() {
        if (!networkService.isNetworkUp()) {
            return;
        }

        simpleIdleThread.die();
        messages.sendLogoffMessage();
        Tools.sleep(500);
        networkService.disconnect();
    }

    public MessageResponderMock getMessageResponderMock() {
        return messageResponderMock;
    }

    public PrivateMessageResponderMock getPrivateMessageResponderMock() {
        return privateMessageResponderMock;
    }

    private void waitForConnection() {
        Tools.sleep(500);

        for (int i = 0; i < 20; i++) {
            if (!networkService.isNetworkUp()) {
                Tools.sleep(100);
            }
        }

        if (!networkService.isNetworkUp()) {
            throw new RuntimeException("Could not connect");
        }
    }

    /**
     * A very simplified idle thread. Its only purpose is to make sure the test client
     * doesn't time out during the longer tests.
     */
    private class SimpleIdleThread extends Thread {

        private boolean run;

        @Override
        public void run() {
            run = true;
            Tools.sleep(15000);

            while (run) {
                messages.sendIdleMessage();
                Tools.sleep(15000);
            }
        }

        public void die() {
            run = false;
        }
    }
}
