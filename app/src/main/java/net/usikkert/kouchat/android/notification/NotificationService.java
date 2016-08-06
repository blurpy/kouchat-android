
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

import java.util.HashSet;
import java.util.Set;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.util.Validate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * Service for handling notifications.
 *
 * <p>The main notification will always be running, but updated with new statuses when necessary.</p>
 *
 * <p>Main chat activity will be notified when a new message arrives and the main chat is not visible.
 * F.ex when another application is on top, or if you are in a private chat.</p>
 *
 * <p>Private chat activity will be notified when a new private message arrives and the private chat with that user
 * is not visible. F.ex when another application is on top, or if you are in a private chat with another user.
 * Activity will not be notified if the main chat is visible, because of the already existing new message icon
 * in the user list.</p>
 *
 * <p>New separate notifications will be created on file transfer requests.</p>
 *
 * @author Christian Ihle
 */
public class NotificationService {

    public static final int SERVICE_NOTIFICATION_ID = 1001;

    private final Context context;
    private final NotificationManager notificationManager;
    private final FileTransferNotificationService fileTransferNotificationService;
    private final MessageNotificationService messageNotificationService;

    private boolean mainChatActivity;
    private final Set<User> privateChatActivityUsers;

    // These are necessary because it's not otherwise possible to get the current notifications in integration tests
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
        fileTransferNotificationService = new FileTransferNotificationService(context, notificationManager);
        messageNotificationService = new MessageNotificationService(context, notificationManager);

        mainChatActivity = false;
        privateChatActivityUsers = new HashSet<>();
    }

    /**
     * Creates a notification for association with a foreground service.
     *
     * <p>The latest info text is set to "Running".</p>
     *
     * @return A complete notification.
     */
    public Notification createServiceNotification() {
        return createNotificationWithLatestInfo(R.drawable.ic_stat_notify_default, R.string.notification_running).build();
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
     *
     * @param user The user that sent the message.
     * @param message The message sent by the user.
     */
    public void notifyNewMainChatMessage(final User user, final String message) {
        messageNotificationService.notifyNewMainChatMessage(user, message);

        sendNewMessageNotification();

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
     *
     * @param user The user who got a private message.
     */
    public void notifyNewPrivateChatMessage(final User user) {
        Validate.notNull(user, "User can not be null");

        sendNewMessageNotification();

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
        messageNotificationService.resetAllNotifications();

        sendDefaultNotification();

        mainChatActivity = false;
        privateChatActivityUsers.clear();
    }

    /**
     * Removes the activity flag for the specified user, and resets the notification if there is no more activity
     * in neither the main chat nor the private chats.
     *
     * <p>Updates the current notification like this:</p>
     *
     * <ul>
     *   <li>Resets the flag for activity in the private chat for the specified user.</li>
     * </ul>
     *
     * <p>If no more activity after that, then also updates the notification like this:</p>
     *
     * <ul>
     *   <li>Sets the latest info text to "Running".</li>
     *   <li>Switches to the regular icon.</li>
     * </ul>
     *
     * @param user The user that should no longer have a notification.
     */
    public void resetPrivateChatNotification(final User user) {
        Validate.notNull(user, "User can not be null");

        privateChatActivityUsers.remove(user);

        if (!isMainChatActivity() && !isPrivateChatActivity()) {
            sendDefaultNotification();
        }
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

    /**
     * Notifies the user that a file transfer request has arrived. Clicking on the notification
     * should open the activity to accept or reject the file transfer.
     *
     * @param fileReceiver The file receiver to create the notification for.
     */
    public void notifyNewFileTransfer(final FileReceiver fileReceiver) {
        fileTransferNotificationService.notifyNewFileTransfer(fileReceiver);
    }

    public void updateFileTransferProgress(final FileTransfer fileTransfer, final String text) {
        fileTransferNotificationService.updateFileTransferProgress(fileTransfer, text);
    }

    public void completeFileTransferProgress(final FileTransfer fileTransfer, final String text) {
        fileTransferNotificationService.completeFileTransferProgress(fileTransfer, text);
    }

    /**
     * Cancels an ongoing notification for a file transfer request.
     *
     * @param fileReceiver The file receiver to cancel the notification for.
     */
    public void cancelFileTransferNotification(final FileReceiver fileReceiver) {
        fileTransferNotificationService.cancelFileTransferNotification(fileReceiver);
    }

    /**
     * Gets the ids of the currently active file transfer notifications.
     *
     * @return The ids of the currently active file transfer notifications.
     */
    public Set<Integer> getCurrentFileTransferIds() {
        return fileTransferNotificationService.getCurrentFileTransferIds();
    }

    public void removeAllNotifications() {
        notificationManager.cancelAll();
    }

    private void sendDefaultNotification() {
        final NotificationCompat.Builder notification =
                createNotificationWithLatestInfo(R.drawable.ic_stat_notify_default, R.string.notification_running);

        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification.build());
    }

    private void sendNewMessageNotification() {
        final NotificationCompat.Builder notification =
                createNotificationWithLatestInfo(R.drawable.ic_stat_notify_activity, R.string.notification_new_message);

        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification.build());
    }

    private NotificationCompat.Builder createNotificationWithLatestInfo(final int iconId,
                                                                        final int latestInfoTextId) {
        final NotificationCompat.Builder notification = createNotification(iconId);
        final PendingIntent pendingIntent = createPendingIntent();

        setLatestEventInfo(notification, pendingIntent, latestInfoTextId);

        return notification;
    }

    private NotificationCompat.Builder createNotification(final int iconId) {
        currentIconId = iconId;

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
        currentLatestInfoTextId = latestInfoTextId;

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
