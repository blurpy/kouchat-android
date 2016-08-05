
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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.ReceiveFileController;
import net.usikkert.kouchat.android.service.CancelFileTransferService;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.util.Validate;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Service for handling file transfer notifications.
 *
 * @author Christian Ihle
 */
public class FileTransferNotificationService {

    private static final int FILE_TRANSFER_NOTIFICATION_ID = 10000;

    private final Context context;
    private final NotificationManager notificationManager;

    private final Set<Integer> currentFileTransferIds;
    private final Map<FileTransfer, NotificationCompat.Builder> currentFileTransfers;

    public FileTransferNotificationService(final Context context,
                                           final NotificationManager notificationManager) {
        this.context = context;
        this.notificationManager = notificationManager;

        currentFileTransferIds = new HashSet<>();
        currentFileTransfers = new HashMap<>();
    }

    public void notifyNewFileTransfer(final FileReceiver fileReceiver) {
        Validate.notNull(fileReceiver, "FileReceiver can not be null");

        final int notificationId = fileReceiver.getId() + FILE_TRANSFER_NOTIFICATION_ID;
        final NotificationCompat.Builder notification = createNewFileTransferNotification();
        final Intent intent = createReceiveFileControllerIntent(fileReceiver);
        final PendingIntent pendingIntent = createReceiveFileControllerPendingIntent(notificationId, intent);
        setNewFileTransferLatestEventInfo(fileReceiver, notification, pendingIntent);

        notificationManager.notify(notificationId, notification.build());
        currentFileTransferIds.add(fileReceiver.getId());
    }

    public void updateFileTransferProgress(final FileTransfer fileTransfer, final String text) {
        Validate.notNull(fileTransfer, "FileTransfer can not be null");

        final int notificationId = fileTransfer.getId() + FILE_TRANSFER_NOTIFICATION_ID;
        final NotificationCompat.Builder notification;

        if (currentFileTransfers.containsKey(fileTransfer)) {
            notification = currentFileTransfers.get(fileTransfer);
        }

        else {
            notification = new NotificationCompat.Builder(context);

            if (fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE) {
                notification.setSmallIcon(R.drawable.ic_stat_notify_receive);
                notification.setContentTitle(context.getString(R.string.notification_file_transfer_from,
                                                               fileTransfer.getUser().getNick()));
            } else {
                notification.setSmallIcon(R.drawable.ic_stat_notify_send);
                notification.setContentTitle(context.getString(R.string.notification_file_transfer_to,
                                                               fileTransfer.getUser().getNick()));
            }

            final Intent intent = new Intent(context, CancelFileTransferService.class);
            intent.putExtra("userCode", fileTransfer.getUser().getCode());
            intent.putExtra("fileTransferId", fileTransfer.getId());
            final PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
            notification.addAction(R.drawable.ic_button_cancel, "Cancel", pendingIntent);

            disableSwipeToCancel(notification);
            currentFileTransfers.put(fileTransfer, notification);
        }

        final String contentText = text + ": " + fileTransfer.getFileName();
        notification.setContentText(contentText);
        notification.setStyle(new NotificationCompat.BigTextStyle().bigText(contentText));

        notification.setProgress(100, fileTransfer.getPercent(), false);

        notificationManager.notify(notificationId, notification.build());
    }

    public void completeFileTransferProgress(final FileTransfer fileTransfer, final String text) {
        Validate.notNull(fileTransfer, "FileTransfer can not be null");

        final int notificationId = fileTransfer.getId() + FILE_TRANSFER_NOTIFICATION_ID;
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setProgress(100, fileTransfer.getPercent(), false);
        enableSwipeToCancel(notification);

        if (fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE) {
            notification.setSmallIcon(R.drawable.ic_stat_notify_receive);
            notification.setContentTitle(context.getString(R.string.notification_file_transfer_from,
                                                           fileTransfer.getUser().getNick()));
        } else {
            notification.setSmallIcon(R.drawable.ic_stat_notify_send);
            notification.setContentTitle(context.getString(R.string.notification_file_transfer_to,
                                                           fileTransfer.getUser().getNick()));
        }

        notification.setContentText(text + ": " + fileTransfer.getFileName());

        currentFileTransferIds.remove(fileTransfer.getId());
        currentFileTransfers.remove(fileTransfer);

        notificationManager.notify(notificationId, notification.build());
    }

    public void cancelFileTransferNotification(final FileReceiver fileReceiver) {
        Validate.notNull(fileReceiver, "FileReceiver can not be null");

        notificationManager.cancel(fileReceiver.getId() + FILE_TRANSFER_NOTIFICATION_ID);

        currentFileTransferIds.remove(fileReceiver.getId());
        currentFileTransfers.remove(fileReceiver);
    }

    public Set<Integer> getCurrentFileTransferIds() {
        return Collections.unmodifiableSet(currentFileTransferIds);
    }

    private NotificationCompat.Builder createNewFileTransferNotification() {
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setSmallIcon(R.drawable.ic_stat_notify_receive);
        // Text shown when the notification arrives
        notification.setTicker(context.getText(R.string.notification_new_file_transfer));

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

    private PendingIntent createReceiveFileControllerPendingIntent(final int notificationId,
                                                                   final Intent intent) {
        return PendingIntent.getActivity(context, notificationId, intent, 0);
    }

    private void setNewFileTransferLatestEventInfo(final FileReceiver fileReceiver,
                                                   final NotificationCompat.Builder notification,
                                                   final PendingIntent pendingIntent) {
        final String nick = fileReceiver.getUser().getNick();

        // First line of the notification in the drawer
        notification.setContentTitle(context.getString(R.string.notification_file_transfer_from, nick));
        // Second line of the notification in the drawer
        notification.setContentText(fileReceiver.getFileName());

        notification.setContentIntent(pendingIntent);
    }

    private void enableSwipeToCancel(final NotificationCompat.Builder notification) {
        notification.setOngoing(false);
    }

    private void disableSwipeToCancel(final NotificationCompat.Builder notification) {
        notification.setOngoing(true);
    }
}
