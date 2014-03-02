
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
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
 *   <li>Own color</li>
 *   <li>System color</li>
 *   <li>Wake lock</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class AndroidSettingsLoader {

    public void loadStoredSettings(final Context context, final Settings settings) {
        Validate.notNull(context, "Context can not be null");
        Validate.notNull(settings, "Settings can not be null");

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        loadNickName(context, preferences, settings.getMe());
        loadOwnColor(context, preferences, settings);
        loadSystemColor(context, preferences, settings);
        loadWakeLock(context, preferences, settings);
    }

    private void loadNickName(final Context context, final SharedPreferences preferences, final User me) {
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

    private void loadOwnColor(final Context context, final SharedPreferences preferences, final Settings settings) {
        final String ownColorKey = context.getString(R.string.settings_own_color_key);
        final int ownColor = preferences.getInt(ownColorKey, -1);

        if (ownColor != -1) {
            settings.setOwnColor(ownColor);
        }
    }

    private void loadSystemColor(final Context context, final SharedPreferences preferences, final Settings settings) {
        final String systemColorKey = context.getString(R.string.settings_sys_color_key);
        final int systemColor = preferences.getInt(systemColorKey, -1);

        if (systemColor != -1) {
            settings.setSysColor(systemColor);
        }
    }

    private void loadWakeLock(final Context context, final SharedPreferences preferences, final Settings settings) {
        final String wakeLockKey = context.getString(R.string.settings_wake_lock_key);
        final boolean wakeLockEnabled = preferences.getBoolean(wakeLockKey, false);

        settings.setWakeLockEnabled(wakeLockEnabled);
    }
}
