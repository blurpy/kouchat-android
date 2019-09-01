
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

import net.usikkert.kouchat.misc.User;

/**
 * This interface is used by other layers to communicate directly with
 * the private chat window for a user, without needing to know which kind
 * of user interface is behind.
 *
 * @author Christian Ihle
 */
public interface PrivateChatWindow {

    /**
     * Adds a new line of text to the private chat area, in the specified color.
     *
     * @param message The text to add to the private chat.
     * @param color The color to show the text in.
     */
    void appendToPrivateChat(String message, int color);

    /**
     * Gets the user this private chat is connected to.
     *
     * @return The user of this private chat.
     */
    User getUser();

    /**
     * Gets the full contents of the private chat area.
     *
     * @return The text in the chat.
     */
    String getChatText();

    /**
     * Removes the text in the chat area.
     */
    void clearChatText();

    /**
     * Hides or shows the private chat window.
     *
     * @param visible True to show the window, false to hide.
     */
    void setVisible(boolean visible);

    /**
     * Checks if the window is visible at the moment.
     *
     * @return True if the window is visible.
     */
    boolean isVisible();

    /**
     * Gives the window a chance to change settings that depend on the away state of the user.
     */
    void updateAwayState();

    /**
     * Gives the window a chance to update after the user logged off.
     */
    void setLoggedOff();

    /**
     * Gives the window a chance to update after a change to user information.
     */
    void updateUserInformation();

    /**
     * Checks if the window is focused at the moment.
     *
     * @return True if the window is focused.
     */
    boolean isFocused();
}
