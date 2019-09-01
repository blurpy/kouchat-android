
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

package net.usikkert.kouchat.settings;

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Tools;

/**
 * Factory for creating <code>me</code>, the application user.
 *
 * @author Christian Ihle
 */
public class MeFactory {

    /**
     * Creates <code>me</code>, the application user.
     *
     * <p>Note: there must only be one instance of <code>me</code> used in the application.
     * Use {@link Settings#getMe()} to access the correct instance.</p>
     *
     * <p>Sets the following values:</p>
     *
     * <ul>
     *   <li>A random user code.</li>
     *   <li>Nick name from the system property <code>user.name</code>, or the user code if property is invalid.</li>
     *   <li>Time of logon.</li>
     *   <li>Time when last idle.</li>
     *   <li>Operating name from the system property <code>os.name</code>.</li>
     * </ul>
     *
     * @return Me.
     */
    public User createMe() {
        final int code = createUniqueCode();
        final String nickName = createNickName(code);

        final User me = new User(nickName, code);

        me.setMe(true);
        me.setLastIdle(System.currentTimeMillis());
        me.setLogonTime(System.currentTimeMillis());
        me.setOperatingSystem(System.getProperty("os.name"));

        return me;
    }

    /**
     * Creates a random number between 10000000 and 20000000.
     *
     * @return Unique code for this user.
     */
    private int createUniqueCode() {
        return 10000000 + (int) (Math.random() * 9999999);
    }

    /**
     * Creates a new default nick name from the name of the user logged in to
     * the operating system. The name is shortened to 10 characters and the
     * first letter is capitalized.
     *
     * <p>If the name is invalid as a nick name then the user code is used instead.</p>
     *
     * @param code The user code.
     * @return The created nick name.
     */
    private String createNickName(final int code) {
        final String userName = System.getProperty("user.name");

        if (userName == null) {
            return Integer.toString(code);
        }

        final String[] splitUserName = userName.split(" ");
        final String defaultNick = Tools.capitalizeFirstLetter(Tools.shorten(splitUserName[0].trim(), 10));

        if (Tools.isValidNick(defaultNick)) {
            return defaultNick;
        }

        return Integer.toString(code);
    }
}
