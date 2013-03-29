
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

package net.usikkert.kouchat.util;

import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.MessageParser;
import net.usikkert.kouchat.net.MessageResponderMock;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.net.NetworkService;
import net.usikkert.kouchat.net.PrivateMessageParser;
import net.usikkert.kouchat.net.PrivateMessageResponderMock;

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
    private final User me;

    private TestClient.SimpleIdleThread simpleIdleThread;

    public TestClient() {
        this("Test", 12345678, 0);
    }

    public TestClient(final String nickName, final int userCode) {
        this(nickName, userCode, 0);
    }

    public TestClient(final String nickName, final int userCode, final int ownColor) {
        final Settings settings = new Settings();

        if (ownColor != 0) {
            settings.setOwnColor(ownColor);
        }

        me = settings.getMe();
        me.setNick(nickName);
        TestUtils.setFieldValue(me, "code", userCode);

        networkService = new NetworkService(settings);
        messages = new Messages(networkService, settings);

        messageResponderMock = new MessageResponderMock(me, messages);
        networkService.registerMessageReceiverListener(new MessageParser(messageResponderMock, settings));

        privateMessageResponderMock = new PrivateMessageResponderMock();
        networkService.registerUDPReceiverListener(new PrivateMessageParser(privateMessageResponderMock, settings));
    }

    public Messages logon() {
        networkService.connect();

        waitForConnection();

        messages.sendLogonMessage();
        messages.sendClient();
        messages.sendExposeMessage();
        messages.sendGetTopicMessage();

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

    public void changeTopic(final String topic) {
        final long time = System.currentTimeMillis();

        messages.sendTopicChangeMessage(new Topic(topic, me.getNick(), time));
        messageResponderMock.topicChanged(me.getCode(), topic, me.getNick(), time);
    }

    /**
     * Sends the specified chat message to the main chat.
     *
     * @param message The message to send.
     */
    public void sendChatMessage(final String message) {
        try {
            messages.sendChatMessage(message);
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends the specified chat message to the private chat of the specified user.
     *
     * @param toUser The user to send the private message to.
     * @param message The message to send.
     */
    public void sendPrivateChatMessage(final User toUser, final String message) {
        try {
            messages.sendPrivateMessage(message, toUser);
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }
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
