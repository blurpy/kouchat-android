
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.ui.ChatWindow;
import net.usikkert.kouchat.ui.UserInterface;

/**
 * User interface for the test client.
 *
 * @author Christian Ihle
 */
public class TestClientUserInterface implements UserInterface, ChatWindow {

    private final MessageController messageController;

    private final List<String> receivedMessages;
    private final Pattern messagePattern;

    public TestClientUserInterface(final Settings settings) {
        messageController = new MessageController(this, this, settings);
        receivedMessages = new ArrayList<String>();
        messagePattern = Pattern.compile("\\[\\d{2}:\\d{2}:\\d{2}\\] <(\\w+)>: (.+)");
    }

    @Override
    public boolean askFileSave(final String user, final String fileName, final String size) {
        return false;
    }

    @Override
    public void showFileSave(final FileReceiver fileReceiver) {

    }

    @Override
    public void showTransfer(final FileReceiver fileRes) {

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
        receivedMessages.add(message);
    }

    /**
     * Checks if the specified message has arrived from the specified user in the main chat.
     *
     * <p>Expects messages in this format: <code>[14:29:21] &lt;Christian&gt;: hello there</code>.</p>
     *
     * <p>Only the actual nick name and the actual message is checked. The rest is ignored.</p>
     *
     * @param nickName The nick name of the user who sent the message.
     * @param message The message the user sent.
     * @return If the message has arrived.
     */
    public boolean gotMessage(final String nickName, final String message) {
        for (final String receivedMessage : receivedMessages) {
            final Matcher matcher = messagePattern.matcher(receivedMessage);

            if (matcher.matches()) {
                final String nickNameMatch = matcher.group(1);
                final String messageMatch = matcher.group(2);

                if (nickNameMatch.equals(nickName) && messageMatch.equals(message)) {
                    return true;
                }
            }
        }

        return false;
    }
}
