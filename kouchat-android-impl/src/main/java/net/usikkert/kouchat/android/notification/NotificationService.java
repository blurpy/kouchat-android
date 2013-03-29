
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

package net.usikkert.kouchat.android.notification;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.util.Validate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Service for handling notifications.
 *
 * @author Christian Ihle
 */
public class NotificationService {

    public static final int SERVICE_NOTIFICATION_ID = 1001;

    private final Context context;
    private final NotificationManager notificationManager;

    // These are necessary because it's not otherwise possible to get the current notification in integration tests
    private int currentIconId;
    private int currentLatestInfoTextId;

    /**
     * Constructor.
     *
     * @param context Context to associate notifications with, and load resources.
     */
    public NotificationService(final Context context) {
        Validate.notNull(context, "Context can not be null");
        this.context = context;

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Creates a notification for association with a foreground service.
     *
     * <p>The latest info text is set to "Running".</p>
     *
     * @return A complete notification.
     */
    public Notification createServiceNotification() {
        return createNotificationWithLatestInfo(R.drawable.kou_icon_24x24, R.string.notification_running);
    }

    /**
     * Updates the foreground service notification and sets the latest info text to "New unread messages",
     * and changes to the "activity" icon.
     */
    public void notifyNewMessage() {
        final Notification notification =
                createNotificationWithLatestInfo(R.drawable.kou_icon_activity_24x24, R.string.notification_new_message);

        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification);
    }

    /**
     * Updates the foreground service notification and sets the latest info text back to "Running",
     * and changes back to the regular icon.
     */
    public void resetNotification() {
        final Notification notification =
                createNotificationWithLatestInfo(R.drawable.kou_icon_24x24, R.string.notification_running);

        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification);
    }

    /**
     * Gets the ID of the icon used in the last notification sent.
     *
     * @return The ID of the icon used in the currently visible notification.
     */
    public int getCurrentIconId() {
        return currentIconId;
    }

    /**
     * Gets the ID of the latest info text used in the last notification sent.
     *
     * @return The ID of the latest info text used in the currently visible notification.
     */
    public int getCurrentLatestInfoTextId() {
        return currentLatestInfoTextId;
    }

    private Notification createNotificationWithLatestInfo(final int iconId, final int latestInfoTextId) {
        final Notification notification = createNotification(iconId);
        final PendingIntent pendingIntent = createPendingIntent();

        setLatestEventInfo(notification, pendingIntent, latestInfoTextId);

        return notification;
    }

    private Notification createNotification(final int iconId) {
        currentIconId = iconId;

        return new Notification(
                iconId,
                context.getText(R.string.notification_startup), // Text shown when starting KouChat
                System.currentTimeMillis());
    }

    private PendingIntent createPendingIntent() {
        // Used to launch KouChat when clicking on the notification in the drawer
        return PendingIntent.getActivity(context, 0, new Intent(context, MainChatController.class), 0);
    }

    private void setLatestEventInfo(final Notification notification, final PendingIntent pendingIntent,
                                    final int latestInfoTextId) {
        currentLatestInfoTextId = latestInfoTextId;

        notification.setLatestEventInfo(context,
                context.getText(R.string.app_name), // First line of the notification in the drawer
                context.getText(latestInfoTextId), // Second line of the notification in the drawer
                pendingIntent);
    }
}
