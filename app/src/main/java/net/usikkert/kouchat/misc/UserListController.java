
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

package net.usikkert.kouchat.misc;

import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * This is the controller responsible for the user list.
 *
 * It contains methods for getting information about users,
 * and updating the state of users.
 *
 * @author Christian Ihle
 */
public class UserListController {

    /** The user list. */
    private final UserList userList;

    /**
     * Constructor.
     *
     * Initializes the user list and puts <code>me</code> in the list.
     *
     * @param settings The settings to use.
     */
    public UserListController(final Settings settings) {
        Validate.notNull(settings, "Settings can not be null");

        userList = new SortedUserList();
        userList.add(settings.getMe());
    }

    /**
     * Gets a user by the user's unique code.
     *
     * @param code The unique code of the user to get.
     * @return The user, or <code>null</code> if the user was not found.
     */
    @Nullable
    public User getUser(final int code) {
        User user = null;

        for (int i = 0; i < userList.size(); i++) {
            final User temp = userList.get(i);

            if (temp.getCode() == code) {
                user = temp;
                break;
            }
        }

        return user;
    }

    /**
     * Gets a user by the user's unique nick name.
     *
     * @param nickname The unique nick name of the user to get.
     * @return The user, or <code>null</code> if the user was not found.
     */
    @Nullable
    public User getUser(final String nickname) {
        User user = null;

        for (int i = 0; i < userList.size(); i++) {
            final User temp = userList.get(i);

            if (temp.getNick().equalsIgnoreCase(nickname)) {
                user = temp;
                break;
            }
        }

        return user;
    }

    /**
     * Changes the nick name of a user.
     *
     * @param code The unique code of the user to change the nick name of.
     * @param nickname The new nick name of the user.
     */
    public void changeNickName(final int code, final String nickname) {
        for (int i = 0; i < userList.size(); i++) {
            final User temp = userList.get(i);

            if (temp.getCode() == code) {
                temp.setNick(nickname);
                userList.set(i, temp);
                break;
            }
        }
    }

    /**
     * Changes the away status of a user.
     *
     * @param code The unique code of the user.
     * @param away If the user is away.
     * @param awaymsg The new away message.
     */
    public void changeAwayStatus(final int code, final boolean away, final String awaymsg) {
        for (int i = 0; i < userList.size(); i++) {
            final User temp = userList.get(i);

            if (temp.getCode() == code) {
                temp.setAway(away);
                temp.setAwayMsg(awaymsg);
                userList.set(i, temp);
                break;
            }
        }
    }

    /**
     * Changes if the user is writing or not.
     *
     * @param code The unique code of the user.
     * @param writing If the user is writing.
     */
    public void changeWriting(final int code, final boolean writing) {
        for (int i = 0; i < userList.size(); i++) {
            final User temp = userList.get(i);

            if (temp.getCode() == code) {
                temp.setWriting(writing);
                userList.set(i, temp);
                break;
            }
        }
    }

    /**
     * Changes if the user has new private messages.
     *
     * @param code The unique code of the user.
     * @param newMsg If the user has new private messages.
     */
    public void changeNewMessage(final int code, final boolean newMsg) {
        for (int i = 0; i < userList.size(); i++) {
            final User temp = userList.get(i);

            if (temp.getCode() == code) {
                temp.setNewPrivMsg(newMsg);
                userList.set(i, temp);
                break;
            }
        }
    }

    /**
     * Checks if the nick name is in use by any other users.
     *
     * @param nickname The nick name to check.
     * @return If the nick name is in use.
     */
    public boolean isNickNameInUse(final String nickname) {
        boolean inUse = false;

        for (int i = 0; i < userList.size(); i++) {
            final User temp = userList.get(i);

            if (temp.getNick().equalsIgnoreCase(nickname) && !temp.isMe()) {
                inUse = true;
                break;
            }
        }

        return inUse;
    }

    /**
     * Checks if the user already exists in the user list.
     *
     * @param code The unique code of the user.
     * @return If the user is new, which means it is not in the user list.
     */
    public boolean isNewUser(final int code) {
        boolean newUser = true;

        for (int i = 0; i < userList.size(); i++) {
            final User temp = userList.get(i);

            if (temp.getCode() == code) {
                newUser = false;
                break;
            }
        }

        return newUser;
    }

    /**
     * Checks if the user list contains <em>timeout users</em>.
     *
     * <p>A timeout user is a user which disconnected from the chat without
     * logging off, and then logging on the chat again before the original
     * user has timed out from the chat. The user will then get a nick name
     * which is identical to the user's unique code to avoid nick crash.</p>
     *
     * @return If there are any timeout users.
     */
    public boolean isTimeoutUsers() {
        for (int i = 0; i < userList.size(); i++) {
            final User temp = userList.get(i);

            if (temp.getNick().equals("" + temp.getCode())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the user list.
     *
     * @return The user list.
     */
    public UserList getUserList() {
        return userList;
    }
}
