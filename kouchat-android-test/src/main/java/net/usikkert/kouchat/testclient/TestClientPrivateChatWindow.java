
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

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.ui.PrivateChatWindow;

/**
 * Representation of a private chat window in the test client.
 *
 * @author Christian Ihle
 */
public class TestClientPrivateChatWindow implements PrivateChatWindow {

    private final User user;
    private final TestClientMessageReceiver messageReceiver;

    public TestClientPrivateChatWindow(final User user) {
        this.user = user;
        this.messageReceiver = new TestClientMessageReceiver();
    }

    @Override
    public void appendToPrivateChat(final String message, final int color) {
        messageReceiver.addMessage(message);
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
    public void setAway(final boolean away) {

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

    public boolean gotAnyPrivateMessages() {
        return messageReceiver.gotAnyMessages();
    }

    public void resetPrivateMessages() {
        messageReceiver.resetMessages();
    }
}
