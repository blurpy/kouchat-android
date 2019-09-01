
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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.ReceiveFileController;
import net.usikkert.kouchat.android.service.CancelFileTransferService;
import net.usikkert.kouchat.android.settings.AndroidSettings;
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
    private final NotificationHelper notificationHelper;

    private final Set<Integer> currentFileTransferIds;
    private final Map<FileTransfer, NotificationCompat.Builder> currentFileTransfers;

    public FileTransferNotificationService(final Context context,
                                           final NotificationManager notificationManager,
                                           final AndroidSettings settings) {
        this.context = context;
        this.notificationManager = notificationManager;

        notificationHelper = new NotificationHelper(context, settings);
        currentFileTransferIds = new HashSet<>();
        currentFileTransfers = new HashMap<>();
    }

    public void notifyNewFileTransfer(final FileReceiver fileReceiver) {
        Validate.notNull(fileReceiver, "FileReceiver can not be null");

        final int notificationId = buildNotificationId(fileReceiver);
        final String channelId = context.getString(R.string.notifications_channel_id_file_transfers);
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId);

        notification.setSmallIcon(R.drawable.ic_stat_notify_receive);
        notification.setTicker(context.getText(R.string.notification_new_file_transfer));
        notification.setContentTitle(context.getString(R.string.notification_file_transfer_from,
                                                       fileReceiver.getUser().getNick()));
        notification.setContentText(fileReceiver.getFileName());
        notification.setContentIntent(createIntentForReceiveFileDialog(notificationId, fileReceiver));
        notification.setPriority(NotificationCompat.PRIORITY_MAX);
        notification.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        setNotificationGroup(notification);

        notificationHelper.setFeedbackEffects(notification);

        disableSwipeToCancel(notification);

        notificationManager.notify(notificationId, notification.build());
        currentFileTransferIds.add(fileReceiver.getId());
    }

    public void updateFileTransferProgress(final FileTransfer fileTransfer, final String status) {
        Validate.notNull(fileTransfer, "FileTransfer can not be null");
        Validate.notEmpty(status, "Status can not be empty");

        final int notificationId = buildNotificationId(fileTransfer);
        final NotificationCompat.Builder notification;

        if (currentFileTransfers.containsKey(fileTransfer)) {
            notification = currentFileTransfers.get(fileTransfer);
        }

        else {
            final String channelId = context.getString(R.string.notifications_channel_id_file_transfers);
            notification = new NotificationCompat.Builder(context, channelId);

            if (fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE) {
                notification.setSmallIcon(R.drawable.ic_stat_notify_receive);
                notification.setContentTitle(context.getString(R.string.notification_file_transfer_from,
                                                               fileTransfer.getUser().getNick()));
            } else {
                notification.setSmallIcon(R.drawable.ic_stat_notify_send);
                notification.setContentTitle(context.getString(R.string.notification_file_transfer_to,
                                                               fileTransfer.getUser().getNick()));
            }

            final PendingIntent pendingIntent = createIntentForCancel(notificationId, fileTransfer);
            notification.addAction(R.drawable.ic_button_cancel, context.getString(R.string.cancel), pendingIntent);

            notification.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notification.setCategory(NotificationCompat.CATEGORY_PROGRESS);
            notification.setOnlyAlertOnce(true); // Avoid sound playing on every update
            setNotificationGroup(notification);

            disableSwipeToCancel(notification);

            currentFileTransfers.put(fileTransfer, notification);
        }

        final String contentText = status + ": " + fileTransfer.getFileName();
        notification.setContentText(contentText);
        notification.setStyle(new NotificationCompat.BigTextStyle().bigText(contentText));
        notification.setProgress(100, fileTransfer.getPercent(), false);

        notificationManager.notify(notificationId, notification.build());
    }

    public void completeFileTransferProgress(final FileTransfer fileTransfer, final String status) {
        Validate.notNull(fileTransfer, "FileTransfer can not be null");
        Validate.notEmpty(status, "Status can not be empty");

        final int notificationId = buildNotificationId(fileTransfer);
        final String channelId = context.getString(R.string.notifications_channel_id_file_transfers);
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId);

        if (fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE) {
            notification.setSmallIcon(R.drawable.ic_stat_notify_receive);
            notification.setContentTitle(context.getString(R.string.notification_file_transfer_from,
                                                           fileTransfer.getUser().getNick()));
        } else {
            notification.setSmallIcon(R.drawable.ic_stat_notify_send);
            notification.setContentTitle(context.getString(R.string.notification_file_transfer_to,
                                                           fileTransfer.getUser().getNick()));
        }

        notification.setProgress(100, fileTransfer.getPercent(), false);
        notification.setContentText(status + ": " + fileTransfer.getFileName());
        notification.setPriority(NotificationCompat.PRIORITY_LOW);
        notification.setCategory(NotificationCompat.CATEGORY_PROGRESS);
        setNotificationGroup(notification);

        enableSwipeToCancel(notification);

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

    private int buildNotificationId(final FileTransfer fileTransfer) {
        return fileTransfer.getId() + FILE_TRANSFER_NOTIFICATION_ID;
    }

    private PendingIntent createIntentForReceiveFileDialog(final int notificationId,
                                                           final FileReceiver fileReceiver) {
        final Intent intent = new Intent(context, ReceiveFileController.class);

        intent.putExtra("userCode", fileReceiver.getUser().getCode());
        intent.putExtra("fileTransferId", fileReceiver.getId());
        intent.setAction("openReceiveFileDialog " + System.currentTimeMillis()); // Unique - to avoid it being cached

        return PendingIntent.getActivity(context, notificationId, intent, 0);
    }

    private PendingIntent createIntentForCancel(final int notificationId,
                                                final FileTransfer fileTransfer) {
        final Intent intent = new Intent(context, CancelFileTransferService.class);

        intent.putExtra("userCode", fileTransfer.getUser().getCode());
        intent.putExtra("fileTransferId", fileTransfer.getId());
        intent.setAction("cancelFileTransfer " + System.currentTimeMillis()); // Unique - to avoid it being cached

        return PendingIntent.getService(context, notificationId, intent, 0);
    }

    private void enableSwipeToCancel(final NotificationCompat.Builder notification) {
        notification.setOngoing(false);
    }

    private void disableSwipeToCancel(final NotificationCompat.Builder notification) {
        notification.setOngoing(true);
    }

    private void setNotificationGroup(final NotificationCompat.Builder notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notification.setGroup(NotificationGroup.FILE_TRANSFER.name());
        }
    }
}
