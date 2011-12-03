
/***************************************************************************
 *   Copyright 2006-2009 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
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
public interface UserInterface
{
	/**
	 * Will ask the user to accept or reject a file transfer from
	 * another user.
	 *
	 * @param user The user that's sending the file.
	 * @param fileName The name of the file to save.
	 * @param size The size of the file, in readable format.
	 * @return True if the user wants to receive the file, or false if not.
	 */
	boolean askFileSave( String user, String fileName, String size );

	/**
	 * Asks the user where to save the file. The file receiver must
	 * be updated with the chosen file and status set to accepted or rejected.
	 *
	 * @param fileReceiver Information about the file to save.
	 */
	void showFileSave( FileReceiver fileReceiver );

	/**
	 * Creates a {@link FileTransferListener} for the file receiver,
	 * and updates the ui of changes to the file reception.
	 *
	 * @param fileRes The file reception object.
	 */
	void showTransfer( FileReceiver fileRes );

	/**
	 * Creates a FileTransferListener for the file sender,
	 * and updates the ui of changes to the file being sent.
	 *
	 * @param fileSend The file sending object.
	 */
	void showTransfer( FileSender fileSend );

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
	void changeAway( boolean away );

	/**
	 * A new message has arrived. Update the ui if necessary.
	 *
	 * @param user The user which sent a message.
	 */
	void notifyMessageArrived( User user );

	/**
	 * A new private message has arrived. Update the ui if necessary.
	 *
	 * @param user The user which sent a message.
	 */
	void notifyPrivateMessageArrived( User user );

	/**
	 * Returns the message controller.
	 *
	 * @return The message controller.
	 */
	MessageController getMessageController();

	/**
	 * Creates a new {@link PrivateChatWindow}, of the
	 * correct type for this ui, if the user does not
	 * already have a window associated.
	 *
	 * @param user The user to update with a
	 * {@link PrivateChatWindow}
	 */
	void createPrivChat( User user );

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
