
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

package net.usikkert.kouchat.testclient;

import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;
import net.usikkert.kouchat.util.Tools;

/**
 * A class that can be used for simulating a KouChat client in tests.
 *
 * @author Christian Ihle
 */
public class TestClient {

    private final Controller controller;
    private final TestClientUserInterface ui;

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

        final User me = settings.getMe();
        me.setNick(nickName);
        TestUtils.setFieldValue(me, "code", userCode);

        ui = new TestClientUserInterface(settings);
        controller = new Controller(ui, settings);
    }

    public void logon() {
        controller.logOn();

        waitForConnection();

        Tools.sleep(100);
    }

    public void logoff() {
        if (!controller.isLoggedOn()) {
            return;
        }

        Tools.sleep(500);
        controller.logOff(true);
    }

    public void changeTopic(final String topic) {
        try {
            controller.changeTopic(topic);
        } catch (final CommandException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends the specified chat message to the main chat.
     *
     * @param message The message to send.
     */
    public void sendChatMessage(final String message) {
        try {
            controller.sendChatMessage(message);
        } catch (final CommandException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends the specified chat message to the private chat of the specified user.
     *
     * @param message The message to send.
     * @param toUser The user to send the private message to.
     */
    public void sendPrivateChatMessage(final String message, final User toUser) {
        try {
            controller.sendPrivateMessage(message, toUser);
        } catch (final CommandException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the specified message has arrived from the specified user in the main chat.
     *
     * @param nickName The nick name of the user who sent the message.
     * @param message The message the user sent.
     * @return If the message has arrived.
     */
    public boolean gotMessage(final String nickName, final String message) {
        return ui.gotMessage(nickName, message);
    }

    private void waitForConnection() {
        Tools.sleep(500);

        for (int i = 0; i < 20; i++) {
            if (!controller.isLoggedOn()) {
                Tools.sleep(100);
            }
        }

        if (!controller.isLoggedOn()) {
            throw new RuntimeException("Could not connect");
        }
    }
}
