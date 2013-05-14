
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
     * Configures the nick name in the shadow pretending to the the actual settings.
     *
     * @param nickName The nick name to use in the settings.
     */
    public static void setNickNameInTheAndroidSettingsTo(final String nickName) {
        final SharedPreferences sharedPreferences =
                ShadowPreferenceManager.getDefaultSharedPreferences(Robolectric.application.getApplicationContext());

        sharedPreferences.edit().putString("nick_name", nickName).commit();
    }
}
