
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

package net.usikkert.kouchat.ui;

import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;

/**
 * This interface makes it possible for other layers to
 * update the user interface without knowing which
 * user interface that is currently in use.
 *
 * The application needs an implementation of this interface
 * for each type of user interface available.
 *
 * @author Christian Ihle
 */
public interface UserInterface {

    /**
     * Will ask the user to accept or reject a file transfer from
     * another user.
     *
     * @param user The user that's sending the file.
     * @param fileName The name of the file to save.
     * @param size The size of the file, in readable format.
     * @return True if the user wants to receive the file, or false if not.
     */
    boolean askFileSave(String user, String fileName, String size);

    /**
     * Asks the user where to save the file. The file receiver must
     * be updated with the chosen file and status set to accepted or rejected.
     *
     * @param fileReceiver Information about the file to save.
     */
    void showFileSave(FileReceiver fileReceiver);

    /**
     * Creates a {@link net.usikkert.kouchat.event.FileTransferListener} for the file receiver,
     * and updates the ui of changes to the file reception.
     *
     * @param fileRes The file reception object.
     */
    void showTransfer(FileReceiver fileRes);

    /**
     * Creates a FileTransferListener for the file sender,
     * and updates the ui of changes to the file being sent.
     *
     * @param fileSend The file sending object.
     */
    void showTransfer(FileSender fileSend);

    /**
     * Updates the ui after a topic or user info change.
     */
    void showTopic();

    /**
     * Clears all the text from the chat window.
     */
    void clearChat();

    /**
     * Updates the ui when the user changes away state.
     *
     * @param away If the user is away.
     */
    void changeAway(boolean away);

    /**
     * A new message has arrived. Update the ui if necessary.
     *
     * @param user The user which sent a message.
     * @param message The message sent by the user.
     */
    void notifyMessageArrived(User user, String message);

    /**
     * A new private message has arrived. Update the ui if necessary.
     *
     * @param user The user which sent a private message.
     * @param message The private message sent by the user.
     */
    void notifyPrivateMessageArrived(User user, String message);

    /**
     * Returns the message controller.
     *
     * @return The message controller.
     */
    MessageController getMessageController();

    /**
     * Creates a new {@link PrivateChatWindow}, of the correct type for this ui,
     * if the user does not already have a window associated. Also configures extra resources,
     * such as logging.
     *
     * @param user The user create a private chat with.
     */
    void createPrivChat(User user);

    /**
     * Checks if the main chat is visible.
     *
     * @return True if the main chat is visible.
     */
    boolean isVisible();

    /**
     * Checks if the main chat is in focus.
     *
     * @return True if the main chat is in focus.
     */
    boolean isFocused();

    /**
     * Quit the application.
     */
    void quit();
}
