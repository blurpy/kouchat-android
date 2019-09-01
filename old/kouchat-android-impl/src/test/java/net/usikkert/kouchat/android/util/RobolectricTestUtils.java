
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

package net.usikkert.kouchat.android.util;

import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowPreferenceManager;

import android.content.SharedPreferences;

/**
 * Reusable utilities for tests using Robolectric.
 *
 * @author Christian Ihle
 */
public final class RobolectricTestUtils {

    private RobolectricTestUtils() {
        // Only static methods
    }

    /**
     * Configures the nick name in the shadow pretending to be the actual settings.
     *
     * @param nickName The nick name to use in the settings.
     */
    public static void setNickNameInTheAndroidSettingsTo(final String nickName) {
        final SharedPreferences sharedPreferences = getSharedPreferences();

        sharedPreferences.edit().putString("nick_name", nickName).commit();
    }

    /**
     * Configures the wake lock in the shadow pretending to be the actual settings.
     *
     * @param enabled If the wake lock is enabled in the settings.
     */
    public static void setWakeLockInTheAndroidSettingsTo(final boolean enabled) {
        final SharedPreferences sharedPreferences = getSharedPreferences();

        sharedPreferences.edit().putBoolean("wake_lock", enabled).commit();
    }

    /**
     * Configures own color in the shadow pretending to be the actual settings.
     *
     * @param ownColor The color to use as own color in the settings.
     */
    public static void setOwnColorInTheAndroidSettingsTo(final int ownColor) {
        final SharedPreferences sharedPreferences = getSharedPreferences();

        sharedPreferences.edit().putInt("own_color", ownColor).commit();
    }

    /**
     * Configures system color in the shadow pretending to be the actual settings.
     *
     * @param systemColor The color to use as system color in the settings.
     */
    public static void setSystemColorInTheAndroidSettingsTo(final int systemColor) {
        final SharedPreferences sharedPreferences = getSharedPreferences();

        sharedPreferences.edit().putInt("sys_color", systemColor).commit();
    }

    private static SharedPreferences getSharedPreferences() {
        return ShadowPreferenceManager.getDefaultSharedPreferences(Robolectric.application);
    }
}
