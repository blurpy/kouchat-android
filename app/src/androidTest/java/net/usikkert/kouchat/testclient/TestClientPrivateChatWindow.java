
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

package net.usikkert.kouchat.testclient;

import java.io.BufferedWriter;
import java.io.IOException;

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.ui.PrivateChatWindow;

/**
 * Representation of a private chat window in the test client.
 *
 * @author Christian Ihle
 */
public class TestClientPrivateChatWindow implements PrivateChatWindow {

    private final User user;
    private final BufferedWriter writer;
    private final TestClientMessageReceiver messageReceiver;

    public TestClientPrivateChatWindow(final User user, final BufferedWriter writer) {
        this.user = user;
        this.writer = writer;
        this.messageReceiver = new TestClientMessageReceiver();
    }

    @Override
    public void appendToPrivateChat(final String message, final int color) {
        messageReceiver.addMessage(message, color);

        if (writer != null) {
            sendPrivateMessage(message);
        }
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getChatText() {
        return null;
    }

    @Override
    public void clearChatText() {

    }

    @Override
    public void setVisible(final boolean visible) {

    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void updateAwayState() {

    }

    @Override
    public void setLoggedOff() {

    }

    @Override
    public void updateUserInformation() {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    public boolean gotPrivateMessage(final User theUser, final String message) {
        return messageReceiver.gotMessage(theUser.getNick(), message);
    }

    public int getColorOfPrivateMessage(final User theUser, final String message) {
        return messageReceiver.getColorOfMessage(theUser.getNick(), message);
    }

    public boolean gotAnyPrivateMessages() {
        return messageReceiver.gotAnyMessages();
    }

    public void resetPrivateMessages() {
        messageReceiver.resetMessages();
    }

    private void sendPrivateMessage(final String message) {
        try {
            writer.write("(privmsg) " + message);
            writer.newLine();
            writer.flush();
        }

        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
