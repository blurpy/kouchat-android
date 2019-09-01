
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

import java.util.Set;

import net.usikkert.kouchat.android.settings.AndroidSettings;
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
 * <p>The service notification will always be running to keep the app from getting killed.</p>
 *
 * <p>Main chat activity will be notified when a new message arrives and the main chat is not visible.
 * F.ex when another application is on top, or if you are in a private chat.
 * The 5 latest messages will be displayed in the notification, including nick name.</p>
 *
 * <p>Private chat activity will be notified when a new private message arrives and the private chat with that user
 * is not visible. F.ex when another application is on top, or if you are in a private chat with another user.
 * Activity will not be notified if the main chat is visible, because of the already existing new message icon
 * in the user list. There will be separate notifications for each user, with the
 * 5 latest messages displayed for that user.</p>
 *
 * <p>Notifications will also be created on file transfer requests,
 * with a progress bar and a cancel button.</p>
 *
 * <p>All non-persistent notifications can optionally use blinking light, sound or vibration.
 * Configurable in the Settings.</p>
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
     * @param settings Settings to use for notifications.
     */
    public NotificationService(final Context context, final AndroidSettings settings) {
        Validate.notNull(context, "Context can not be null");
        Validate.notNull(settings, "Settings can not be null");

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        serviceNotificationService = new ServiceNotificationService(context);
        fileTransferNotificationService = new FileTransferNotificationService(context, notificationManager, settings);
        messageNotificationService = new MessageNotificationService(context, notificationManager, settings);
    }

    /**
     * Creates a persistent notification for association with a foreground service.
     *
     * @return A complete notification.
     */
    public Notification createServiceNotification() {
        return serviceNotificationService.createServiceNotification();
    }

    /**
     * Notifies about a new main chat message.
     *
     * <p>Either creates a new notification, or appends to an existing.</p>
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
     * <p>Either creates a new notification for that user, or appends to an existing.</p>
     *
     * @param user The user who got a private message.
     * @param message The private message sent by the user.
     */
    public void notifyNewPrivateChatMessage(final User user, final String message) {
        Validate.notNull(user, "User can not be null");

        messageNotificationService.notifyNewPrivateChatMessage(user, message);
    }

    /**
     * Resets all the message notifications.
     *
     * <p>The main chat notification is removed, and all the private chat notifications.</p>
     */
    public void resetAllMessageNotifications() {
        messageNotificationService.resetAllNotifications();
    }

    /**
     * Removes the private chat notification for the specified user.
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
     * <p>The notification can not be swept away while in this state, but will either update
     * with progress (see below) or be removed if rejected.</p>
     *
     * @param fileReceiver The file receiver to create the notification for.
     */
    public void notifyNewFileTransfer(final FileReceiver fileReceiver) {
        fileTransferNotificationService.notifyNewFileTransfer(fileReceiver);
    }

    /**
     * Creates or updates a file transfer notification with the current status from the
     * file transfer object.
     *
     * <p>The first call here will update the notification created above when it's a
     * file being received, but when sending a file there will be no existing notification
     * to update. A new one will therefore be created.</p>
     *
     * <p>The notification displays a progress bar with current progress and a cancel button.
     * This method is expected to run every time status or progress changes.</p>
     *
     * <p>The notification can not be swept away while it's in this state.</p>
     *
     * @param fileTransfer The file receiver to create/update the notification for.
     * @param status Status text to display in the notification.
     */
    public void updateFileTransferProgress(final FileTransfer fileTransfer, final String status) {
        fileTransferNotificationService.updateFileTransferProgress(fileTransfer, status);
    }

    /**
     * Completes the file transfer. Either because it completes, one of the users press
     * cancel, or something unexpected interrupts.
     *
     * <p>The notification can now be swept away, and the cancel button is removed.</p>
     *
     * @param fileTransfer The file receiver to create/update the notification for.
     * @param status Status text to display in the notification.
     */
    public void completeFileTransferProgress(final FileTransfer fileTransfer, final String status) {
        fileTransferNotificationService.completeFileTransferProgress(fileTransfer, status);
    }

    /**
     * Cancels an ongoing notification for a file transfer request. That is, the notification is
     * removed. Most likely because you didn't accept a file transfer request before it started.
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

    /**
     * Removes absolutely all visible notifications. Does not clean up any state.
     */
    public void onDestroy() {
        notificationManager.cancelAll();
    }
}
