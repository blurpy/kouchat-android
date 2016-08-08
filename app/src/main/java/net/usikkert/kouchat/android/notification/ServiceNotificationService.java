
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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;

/**
 * Service for handling the persistent service notification.
 */
public class ServiceNotificationService {

    private final Context context;

    public ServiceNotificationService(final Context context) {
        this.context = context;
    }

    public Notification createServiceNotification() {
        return createNotificationWithLatestInfo(R.drawable.ic_stat_notify_default, R.string.notification_running).build();
    }

    private NotificationCompat.Builder createNotificationWithLatestInfo(final int iconId,
                                                                        final int latestInfoTextId) {
        final NotificationCompat.Builder notification = createNotification(iconId);
        final PendingIntent pendingIntent = createPendingIntent();

        setLatestEventInfo(notification, pendingIntent, latestInfoTextId);

        return notification;
    }

    private NotificationCompat.Builder createNotification(final int iconId) {
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setSmallIcon(iconId);
        // Text shown when the notification arrives
        notification.setTicker(context.getText(R.string.notification_startup));

        disableSwipeToCancel(notification);

        return notification;
    }

    private PendingIntent createPendingIntent() {
        // Used to launch KouChat when clicking on the notification in the drawer
        return PendingIntent.getActivity(context, 0, new Intent(context, MainChatController.class), 0);
    }

    private void setLatestEventInfo(final NotificationCompat.Builder notification,
                                    final PendingIntent pendingIntent,
                                    final int latestInfoTextId) {
        // First line of the notification in the drawer
        notification.setContentTitle(context.getText(R.string.app_name));
        // Second line of the notification in the drawer
        notification.setContentText(context.getText(latestInfoTextId));

        notification.setContentIntent(pendingIntent);
    }

    private void disableSwipeToCancel(final NotificationCompat.Builder notification) {
        notification.setOngoing(true);
    }
}
