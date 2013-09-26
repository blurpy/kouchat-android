
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

package net.usikkert.kouchat.android.util;

import android.os.Build;

/**
 * Utility methods that don't fit anywhere else.
 *
 * @author Christian Ihle
 */
public final class MiscTestUtils {

    private MiscTestUtils() {
        // Only static methods here
    }

    /**
     * Creates a message with the text <code>This is message number x.y!</code> repeated ten times,
     * where x is replaced by message number, and y is replaced by a counter from 0 to 9.
     *
     * @param messageNumber The number to use to identify this long message.
     * @return A long message.
     */
    public static String createLongMessage(final int messageNumber) {
        final StringBuilder longMessage = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            longMessage.append("This is message number ").append(messageNumber).append(".").append(i).append("! ");
        }

        return longMessage.toString();
    }

    /**
     * Checks if the test is running on Android 2.3.3, Gingerbread, API 10.
     *
     * @return If running on Android 2.3.3 or not.
     */
    public static boolean isRunningOnAndroid233() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD_MR1;
    }
}
