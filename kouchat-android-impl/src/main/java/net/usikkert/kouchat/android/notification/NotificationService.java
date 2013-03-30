
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

import java.util.HashSet;
import java.util.Set;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.misc.User;
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

    private boolean mainChatActivity;
    private final Set<User> privateChatActivityUsers;

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
        mainChatActivity = false;
        privateChatActivityUsers = new HashSet<User>();
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
     * Notifies about a new main chat message.
     *
     * <p>Updates the current notification like this:</p>
     *
     * <ul>
     *   <li>Sets the latest info text to "New unread messages".</li>
     *   <li>Switches to the activity icon.</li>
     *   <li>Sets the flag for activity in the main chat.</li>
     * </ul>
     */
    public void notifyNewMainChatMessage() {
        final Notification notification =
                createNotificationWithLatestInfo(R.drawable.kou_icon_activity_24x24, R.string.notification_new_message);

        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification);
        mainChatActivity = true;
    }

    /**
     * Notifies about a new private chat message.
     *
     * <p>Updates the current notification like this:</p>
     *
     * <ul>
     *   <li>Sets the latest info text to "New unread messages".</li>
     *   <li>Switches to the activity icon.</li>
     *   <li>Sets the flag for activity in the private chat with the specified user.</li>
     * </ul>
     */
    public void notifyNewPrivateChatMessage(final User user) {
        Validate.notNull(user, "User can not be null");

        final Notification notification =
                createNotificationWithLatestInfo(R.drawable.kou_icon_activity_24x24, R.string.notification_new_message);

        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification);
        privateChatActivityUsers.add(user);
    }

    /**
     * Resets the notification to default.
     *
     * <p>Updates the current notification like this:</p>
     *
     * <ul>
     *   <li>Sets the latest info text to "Running".</li>
     *   <li>Switches to the regular icon.</li>
     *   <li>Resets the flag for activity in the main chat.</li>
     *   <li>Resets the flag for activity in all the private chats.</li>
     * </ul>
     */
    public void resetAllNotifications() {
        final Notification notification =
                createNotificationWithLatestInfo(R.drawable.kou_icon_24x24, R.string.notification_running);

        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification);
        mainChatActivity = false;
        privateChatActivityUsers.clear();
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

    /**
     * If there is currently a notification about main chat activity.
     *
     * @return If there is activity in the main chat.
     */
    public boolean isMainChatActivity() {
        return mainChatActivity;
    }

    /**
     * If there is currently a notification about any private chat activity.
     *
     * @return If there is activity in the private chat.
     */
    public boolean isPrivateChatActivity() {
        return !privateChatActivityUsers.isEmpty();
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
