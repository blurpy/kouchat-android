
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

package net.usikkert.kouchat.android;

import net.usikkert.kouchat.android.controller.PrivateChatController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.ui.PrivateChatWindow;
import net.usikkert.kouchat.util.Validate;

import android.content.Context;

/**
 * Represents a private chat window with a user.
 *
 * @author Christian Ihle
 */
public class AndroidPrivateChatWindow implements PrivateChatWindow {

    private final User user;
    private final MessageStylerWithHistory messageStyler;

    private PrivateChatController privateChatController;

    public AndroidPrivateChatWindow(final Context context, final User user) {
        Validate.notNull(context, "Context can not be null");
        Validate.notNull(user, "User can not be null");

        this.user = user;

        messageStyler = new MessageStylerWithHistory(context);
    }

    public void registerPrivateChatController(final PrivateChatController thePrivateChatController) {
        Validate.notNull(thePrivateChatController, "Private chat controller can not be null");

        privateChatController = thePrivateChatController;
        privateChatController.updatePrivateChat(messageStyler.getHistory());
    }

    public void unregisterPrivateChatController() {
        privateChatController = null;
    }

    @Override
    public void appendToPrivateChat(final String privateMessage, final int color) {
        Validate.notEmpty(privateMessage, "Private message can not be empty");

        final CharSequence styledPrivateMessage = messageStyler.styleAndAppend(privateMessage, color);

        if (privateChatController != null) {
            privateChatController.appendToPrivateChat(styledPrivateMessage);
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
        return privateChatController != null && privateChatController.isVisible();
    }

    @Override
    public void setAway(final boolean away) {
        if (privateChatController != null) {
            privateChatController.updateTitle();
        }
    }

    @Override
    public void setLoggedOff() {
        if (privateChatController != null) {
            privateChatController.updateTitle();
        }
    }

    @Override
    public void updateUserInformation() {

    }

    @Override
    public boolean isFocused() {
        return isVisible();
    }
}
