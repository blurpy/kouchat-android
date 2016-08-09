
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

package net.usikkert.kouchat.android.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import net.usikkert.kouchat.android.R;

/**
 * Reusable helper methods for managing notifications.
 *
 * @author Christian Ihle
 */
public class NotificationHelper {

    private static final long[] VIBRATION_PATTERN = new long[] {50, 100, 50, 100};

    private final Uri soundUri;

    public NotificationHelper(final Context context) {
        soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                                  context.getPackageName() + "/" + R.raw.notification_sound);
    }

    /**
     * Sets a notification sound, a vibration pattern and a blinking led.
     *
     * @param notification The notification to update with these effects.
     */
    public void setFeedbackEffects(final NotificationCompat.Builder notification) {
        notification.setSound(soundUri);
        notification.setVibrate(VIBRATION_PATTERN);
        notification.setLights(Color.CYAN, 500, 2000);
    }
}
