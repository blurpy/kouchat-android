
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU General Public License as               *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with KouChat. If not, see <http://www.gnu.org/licenses/>.       *
 ***************************************************************************/

package net.usikkert.kouchat.android;

import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.ui.PrivateChatWindow;

/**
 * Represents a private chat window with a user.
 *
 * <p>Not implemented!</p>
 *
 * @author Christian Ihle
 */
public class AndroidPrivateChatWindow implements PrivateChatWindow {

    private final User user;
    private final Controller controller;
    private boolean userNotified;

    public AndroidPrivateChatWindow(final User user, final Controller controller) {
        this.user = user;
        this.controller = controller;
        this.userNotified = false;
    }

    @Override
    public void appendToPrivateChat(final String message, final int color) {
        if (userNotified) {
            // To avoid spam
            return;
        }

        try {
            controller.sendPrivateMessage("Private chat not implemented yet!", user);
            userNotified = true;
        }

        catch (final CommandException e) {
            // Don't care ...
            e.printStackTrace();
        }
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getChatText() {
        return "";
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
}
