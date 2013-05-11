
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

import net.usikkert.kouchat.misc.ChatLogger;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.ui.ChatWindow;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Tools;

/**
 * User interface for the test client.
 *
 * @author Christian Ihle
 */
public class TestClientUserInterface implements UserInterface, ChatWindow {

    private final MessageController messageController;
    private final TestClientMessageReceiver messageReceiver;
    private final Settings settings;

    public TestClientUserInterface(final Settings settings) {
        this.settings = settings;
        this.messageController = new MessageController(this, this, this.settings);
        this.messageReceiver = new TestClientMessageReceiver();
    }

    @Override
    public boolean askFileSave(final String user, final String fileName, final String size) {
        return true;
    }

    @Override
    public void showFileSave(final FileReceiver fileReceiver) {
        // Waits until the client makes a decision to return. If not, the file transfer will abort automatically.
        while (!fileReceiver.isAccepted() && !fileReceiver.isRejected() && !fileReceiver.isCanceled()) {
            Tools.sleep(500);
        }
    }

    @Override
    public void showTransfer(final FileReceiver fileRes) {
        new TestClientFileTransferListener(fileRes);
    }

    @Override
    public void showTransfer(final FileSender fileSend) {

    }

    @Override
    public void showTopic() {

    }

    @Override
    public void clearChat() {

    }

    @Override
    public void changeAway(final boolean away) {

    }

    @Override
    public void notifyMessageArrived(final User user) {

    }

    @Override
    public void notifyPrivateMessageArrived(final User user) {

    }

    @Override
    public MessageController getMessageController() {
        return messageController;
    }

    @Override
    public void createPrivChat(final User user) {
        if (user.getPrivchat() == null) {
            user.setPrivchat(new TestClientPrivateChatWindow(user));
        }

        if (user.getPrivateChatLogger() == null) {
            user.setPrivateChatLogger(new ChatLogger(user.getNick(), settings));
        }
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public void quit() {

    }

    @Override
    public void appendToChat(final String message, final int color) {
        messageReceiver.addMessage(message);
    }

    public boolean gotMessage(final User user, final String message) {
        return messageReceiver.gotMessage(user.getNick(), message);
    }
}
