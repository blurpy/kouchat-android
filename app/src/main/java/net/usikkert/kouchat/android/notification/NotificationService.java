
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

import java.util.Set;

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.util.Validate;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

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

    private final NotificationManager notificationManager;

    private final ServiceNotificationService serviceNotificationService;
    private final FileTransferNotificationService fileTransferNotificationService;
    private final MessageNotificationService messageNotificationService;

    /**
     * Constructor.
     *
     * @param context Context to associate notifications with, and load resources.
     */
    public NotificationService(final Context context) {
        Validate.notNull(context, "Context can not be null");

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        serviceNotificationService = new ServiceNotificationService(context);
        fileTransferNotificationService = new FileTransferNotificationService(context, notificationManager);
        messageNotificationService = new MessageNotificationService(context, notificationManager);
    }

    /**
     * Creates a notification for association with a foreground service.
     *
     * <p>The latest info text is set to "Running".</p>
     *
     * @return A complete notification.
     */
    public Notification createServiceNotification() {
        return serviceNotificationService.createServiceNotification();
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
     * @param message The private message sent by the user.
     */
    public void notifyNewPrivateChatMessage(final User user, final String message) {
        Validate.notNull(user, "User can not be null");

        messageNotificationService.notifyNewPrivateChatMessage(user, message);
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
    public void resetAllMessageNotifications() {
        messageNotificationService.resetAllNotifications();
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

        messageNotificationService.resetPrivateChatNotification(user);
    }

    /**
     * If there is currently a notification about main chat activity.
     *
     * @return If there is activity in the main chat.
     */
    public boolean isMainChatActivity() {
        return messageNotificationService.isMainChatActivity();
    }

    /**
     * If there is currently a notification about any private chat activity.
     *
     * @return If there is activity in the private chat.
     */
    public boolean isPrivateChatActivity() {
        return messageNotificationService.isPrivateChatActivity();
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

    public void onDestroy() {
        notificationManager.cancelAll();
    }
}
