
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
import java.util.List;

import net.usikkert.kouchat.misc.ChatLogger;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.settings.Settings;
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
    private final ErrorHandler errorHandler;

    private BufferedWriter writer;

    private int fileTransferDelay;

    public TestClientUserInterface(final Settings settings, final ErrorHandler errorHandler) {
        this.settings = settings;
        this.errorHandler = errorHandler;
        this.messageController = new MessageController(this, this, settings, errorHandler);
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
        new TestClientFileTransferListener(fileRes, fileTransferDelay);
    }

    @Override
    public void showTransfer(final FileSender fileSend) {
        new TestClientFileTransferListener(fileSend, fileTransferDelay);
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
    public void notifyMessageArrived(final User user, final String message) {

    }

    @Override
    public void notifyPrivateMessageArrived(final User user, final String message) {

    }

    @Override
    public MessageController getMessageController() {
        return messageController;
    }

    @Override
    public void createPrivChat(final User user) {
        if (user.getPrivchat() == null) {
            user.setPrivchat(new TestClientPrivateChatWindow(user, writer));
        }

        if (user.getPrivateChatLogger() == null) {
            user.setPrivateChatLogger(new ChatLogger(user.getNick(), settings, errorHandler));
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
        messageReceiver.addMessage(message, color);

        if (writer != null) {
            sendMessage(message);
        }
    }

    public boolean gotMessage(final User user, final String message) {
        return messageReceiver.gotMessage(user.getNick(), message);
    }

    public int getColorOfMessage(final User user, final String message) {
        return messageReceiver.getColorOfMessage(user.getNick(), message);
    }

    public void setWriter(final BufferedWriter writer) {
        this.writer = writer;

        final List<String> messages = messageReceiver.getMessages();

        // Send all previously registered messages as well
        for (final String message : messages) {
            sendMessage(message);
        }
    }

    private void sendMessage(final String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        }

        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFileTransferDelay(final int fileTransferDelay) {
        this.fileTransferDelay = fileTransferDelay;
    }
}
