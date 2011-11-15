
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

package net.usikkert.kouchat.ui.swing;

import java.io.File;

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.ui.PrivateChatWindow;

/**
 * This is the interface for the mediator between the user interface and the controller.
 *
 * @author Christian Ihle
 */
public interface Mediator
{
	/**
	 * Hides the main window in the system tray,
	 * if a system tray is supported.
	 */
	void minimize();

	/**
	 * Clears all the text from the main chat area.
	 */
	void clearChat();

	/**
	 * If the user is not away, asks for an away reason,
	 * and sets the user as away.
	 *
	 * If user is away, asks if the user wants to come back.
	 */
	void setAway();

	/**
	 * Asks for the new topic, and changes it.
	 */
	void setTopic();

	/**
	 * Logs on to the network.
	 */
	void start();

	/**
	 * Asks if the user wants to quit.
	 */
	void quit();

	/**
	 * Updates the titlebar and the system tray tooltip with
	 * current information about the application and the user.
	 */
	void updateTitleAndTray();

	/**
	 * Shows or hides the main window.
	 * The window will always be brought to front when shown.
	 */
	void showOrHideWindow();

	/**
	 * If the main window is hidden it is set visible,
	 * but only as minimized in the taskbar.
	 */
	void minimizeWindowIfHidden();

	/**
	 * Opens the settings dialog window.
	 */
	void showSettings();

	/**
	 * Opens a file chooser, where the user can select a file to send to
	 * another user.
	 *
	 * @param user The user to send the file to.
	 * @param selectedFile A file that already exists to open the file chooser with
	 *                     that file already selected, or <code>null</code> if the
	 *                     file chooser should start fresh.
	 */
	void sendFile( User user, File selectedFile );

	/**
	 * Gets the text written in the input field and either sends it to
	 * the command parser or sends it as a message.
	 */
	void write();

	/**
	 * Gets the text from the input field of the private chat, and
	 * sends it as a message to the user.
	 *
	 * @param privchat The private chat.
	 */
	void writePrivate( PrivateChatWindow privchat );

	/**
	 * Checks if the user is currently writing, and updates the status.
	 */
	void updateWriting();

	/**
	 * Changes the nick name of the user, if the nick is valid.
	 *
	 * @param nick The new nick name to change to.
	 * @return If the nick name was changed successfully.
	 */
	boolean changeNick( String nick );

	/**
	 * Runs when the user presses the cancel/close button in the
	 * transfer dialog. If the button's text is close, the dialog should
	 * close. If the text is cancel, the file transfer should stop,
	 * and the button should change text to close.
	 *
	 * @param transferDialog The transfer dialog.
	 */
	void transferCancelled( TransferDialog transferDialog );

	/**
	 * Shows a list of the supported commands and their syntax.
	 */
	void showCommands();

	/**
	 * Shows the user's private chat window.
	 *
	 * @param user The user to show the private chat for.
	 */
	void showPrivChat( User user );

	/**
	 * Resets the new private message field of the user.
	 *
	 * @param user The user to reset the field for.
	 */
	void activatedPrivChat( User user );
}
