
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.controller.ReceiveFileController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.util.Validate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

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
    public static final int FILE_TRANSFER_NOTIFICATION_ID = 10000;

    private final Context context;
    private final NotificationManager notificationManager;

    private boolean mainChatActivity;
    private final Set<User> privateChatActivityUsers;

    // These are necessary because it's not otherwise possible to get the current notifications in integration tests
    private int currentIconId;
    private int currentLatestInfoTextId;
    private final Set<Integer> currentFileTransferIds;

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
        currentFileTransferIds = new HashSet<Integer>();
    }

    /**
     * Creates a notification for association with a foreground service.
     *
     * <p>The latest info text is set to "Running".</p>
     *
     * @return A complete notification.
     */
    public Notification createServiceNotification() {
        return createNotificationWithLatestInfo(R.drawable.ic_stat_notify_default, R.string.notification_running);
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
        Validate.notNull(fileReceiver, "FileReceiver can not be null");

        final int notificationId = fileReceiver.getId() + FILE_TRANSFER_NOTIFICATION_ID;
        final Notification notification = createNewFileTransferNotification();
        final Intent intent = createReceiveFileControllerIntent(fileReceiver);
        final PendingIntent pendingIntent = createReceiveFileControllerPendingIntent(notificationId, intent);
        setNewFileTransferLatestEventInfo(fileReceiver, notification, pendingIntent);

        notificationManager.notify(notificationId, notification);
        currentFileTransferIds.add(fileReceiver.getId());
    }

    /**
     * Cancels an ongoing notification for a file transfer request.
     *
     * @param fileReceiver The file receiver to cancel the notification for.
     */
    public void cancelFileTransferNotification(final FileReceiver fileReceiver) {
        Validate.notNull(fileReceiver, "FileReceiver can not be null");

        notificationManager.cancel(fileReceiver.getId() + FILE_TRANSFER_NOTIFICATION_ID);
        currentFileTransferIds.remove(fileReceiver.getId());
    }

    /**
     * Gets the ids of the currently active file transfer notifications.
     *
     * @return The ids of the currently active file transfer notifications.
     */
    public Set<Integer> getCurrentFileTransferIds() {
        return Collections.unmodifiableSet(currentFileTransferIds);
    }

    private void sendDefaultNotification() {
        final Notification notification =
                createNotificationWithLatestInfo(R.drawable.ic_stat_notify_default, R.string.notification_running);

        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification);
    }

    private void sendNewMessageNotification() {
        final Notification notification =
                createNotificationWithLatestInfo(R.drawable.ic_stat_notify_activity, R.string.notification_new_message);

        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification);
    }

    private Notification createNotificationWithLatestInfo(final int iconId, final int latestInfoTextId) {
        final Notification notification = createNotification(iconId);
        final PendingIntent pendingIntent = createPendingIntent();

        setLatestEventInfo(notification, pendingIntent, latestInfoTextId);

        return notification;
    }

    private Notification createNotification(final int iconId) {
        currentIconId = iconId;

        final Notification notification = new Notification(
                iconId,
                context.getText(R.string.notification_startup), // Text shown when starting KouChat
                System.currentTimeMillis());

        disableSwipeToCancel(notification);

        return notification;
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

    private Notification createNewFileTransferNotification() {
        final Notification notification = new Notification(
                R.drawable.ic_stat_notify_activity, // Icon
                context.getString(R.string.notification_new_file_transfer), // Text shown when the notification arrives
                System.currentTimeMillis());

        disableSwipeToCancel(notification);

        return notification;
    }

    private Intent createReceiveFileControllerIntent(final FileReceiver fileReceiver) {
        final Intent intent = new Intent(context, ReceiveFileController.class);

        intent.putExtra("userCode", fileReceiver.getUser().getCode());
        intent.putExtra("fileTransferId", fileReceiver.getId());
        intent.setAction("openReceiveFileDialog " + System.currentTimeMillis()); // Unique - to avoid it being cached

        return intent;
    }

    private PendingIntent createReceiveFileControllerPendingIntent(final int notificationId, final Intent intent) {
        return PendingIntent.getActivity(context, notificationId, intent, 0);
    }

    private void setNewFileTransferLatestEventInfo(final FileReceiver fileReceiver, final Notification notification,
                                                   final PendingIntent pendingIntent) {
        final String nick = fileReceiver.getUser().getNick();

        notification.setLatestEventInfo(context,
                context.getString(R.string.notification_file_transfer_from, nick), // First line of the notification in the drawer
                fileReceiver.getFileName(), // Second line of the notification in the drawer
                pendingIntent);
    }

    private void disableSwipeToCancel(final Notification notification) {
        notification.flags |= Notification.FLAG_NO_CLEAR;
    }
}
