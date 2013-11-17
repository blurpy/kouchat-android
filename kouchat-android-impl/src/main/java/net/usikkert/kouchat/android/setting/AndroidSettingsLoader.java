
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
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

package net.usikkert.kouchat.android.setting;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Loads settings from the stored Android preferences.
 *
 * <p>Supports the following settings:</p>
 *
 * <ul>
 *   <li>Nick name</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class AndroidSettingsLoader {

    public void loadStoredSettings(final Context context, final Settings settings) {
        Validate.notNull(context, "Context can not be null");
        Validate.notNull(settings, "Settings can not be null");

        setNickName(context, settings.getMe());
    }

    private void setNickName(final Context context, final User me) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        final String nickNameKey = context.getString(R.string.settings_key_nick_name);
        final String nickName = getNickNameFromPreferences(preferences, nickNameKey, me);

        me.setNick(nickName);
    }

    private String getNickNameFromPreferences(final SharedPreferences preferences,
                                              final String nickNameKey,
                                              final User me) {
        final String nickNameFromPreferences = preferences.getString(nickNameKey, null);

        if (Tools.isValidNick(nickNameFromPreferences)) {
            return nickNameFromPreferences;
        }

        return Integer.toString(me.getCode());
    }
}
