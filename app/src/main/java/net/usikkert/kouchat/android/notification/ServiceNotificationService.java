
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

package net.usikkert.kouchat.android.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;

/**
 * Service for handling the persistent service notification.
 *
 * @author Christian Ihle
 */
public class ServiceNotificationService {

    public static final int SERVICE_NOTIFICATION_ID = 1001;

    private final Context context;

    public ServiceNotificationService(final Context context) {
        this.context = context;
    }

    public Notification createServiceNotification() {
        final String channelId = context.getString(R.string.notifications_channel_id_service);
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId);

        notification.setSmallIcon(R.drawable.ic_stat_notify_default);
        notification.setTicker(context.getText(R.string.notification_startup));
        notification.setContentTitle(context.getText(R.string.app_name));
        notification.setContentText(context.getText(R.string.notification_running));
        notification.setContentIntent(createPendingIntent());
        notification.setPriority(NotificationCompat.PRIORITY_MIN);
        notification.setCategory(NotificationCompat.CATEGORY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notification.setGroup(NotificationGroup.SERVICE.name());
        }

        disableSwipeToCancel(notification);

        return notification.build();
    }

    private PendingIntent createPendingIntent() {
        // Used to launch KouChat when clicking on the notification in the drawer
        return PendingIntent.getActivity(context, 0, new Intent(context, MainChatController.class), 0);
    }

    private void disableSwipeToCancel(final NotificationCompat.Builder notification) {
        notification.setOngoing(true);
    }
}
