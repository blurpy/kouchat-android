
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

package net.usikkert.kouchat.android.filetransfer;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.notification.NotificationService;
import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.util.Validate;

import android.content.Context;

/**
 * A file transfer listener that updates a notification with progress.
 *
 * @author Christian Ihle
 */
public class AndroidFileTransferListener implements FileTransferListener {

    /**
     * Android applies rate limiting to notification updates. Before Android 7 this was
     * 50 updates pr second, but in Android 7 it was decreased to 10. For the whole app.
     * The issue with rate limit is that the final "completed" update is likely to be ignored,
     * making the notification stuck looking like it's still in progress but never finishing.
     * This value means we can update the notification about 3 times per second, allowing 3
     * parallel quick file transfers without issues. Users aren't likely to be able to start
     * transfer of small files fast enough for the rate limit in Android to give us trouble.
     */
    private static final int MINIMUM_UPDATE_TIME = 334;

    private final FileTransfer fileTransfer;
    private final Context context;
    private final AndroidFileUtils androidFileUtils;
    private final MessageController messageController;
    private final NotificationService notificationService;

    private final String receivingText;
    private final String sendingText;

    private int percentTransferred;
    private long lastTransferUpdate;

    public AndroidFileTransferListener(final FileTransfer fileTransfer,
                                       final Context context,
                                       final AndroidFileUtils androidFileUtils,
                                       final MessageController messageController,
                                       final NotificationService notificationService) {
        Validate.notNull(fileTransfer, "FileTransfer can not be null");
        Validate.notNull(context, "Context can not be null");
        Validate.notNull(androidFileUtils, "AndroidFileUtils can not be null");
        Validate.notNull(messageController, "MessageController can not be null");
        Validate.notNull(notificationService, "NotificationService can not be null");

        this.fileTransfer = fileTransfer;
        this.context = context;
        this.androidFileUtils = androidFileUtils;
        this.messageController = messageController;
        this.notificationService = notificationService;
        this.percentTransferred = -1;
        this.lastTransferUpdate = 0;

        receivingText = context.getString(R.string.notification_receiving);
        sendingText = context.getString(R.string.notification_sending);

        fileTransfer.registerListener(this);
    }

    @Override
    public void statusWaiting() {
        notificationService.updateFileTransferProgress(
                fileTransfer, context.getString(R.string.notification_waiting));
    }

    @Override
    public void statusConnecting() {
        notificationService.updateFileTransferProgress(
                fileTransfer, context.getString(R.string.notification_connecting));
    }

    /**
     * Shows a message if starting to receive a file.
     *
     * <p>There is no need to show a message when sending a message,
     * as that is taken care of elsewhere.</p>
     *
     * <p>It's important to use the original file name instead of the current file name, because
     * when the file transfer has started, the current file might be renamed.</p>
     *
     * <p>The series of messages would then look weird:</p>
     *
     * <ul>
     *   <li>*** Receiving sunset_1.jpg from Dude</li>
     *   <li>*** Successfully received sunset.jpg from Dude, and saved as sunset_1.jpg</li>
     * </ul>
     */
    @Override
    public void statusTransferring() {
        if (fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE) {
            final FileReceiver fileReceiver = (FileReceiver) fileTransfer;

            notificationService.updateFileTransferProgress(fileReceiver, receivingText);

            messageController.showSystemMessage(
                    context.getString(R.string.notification_receiving_file_from,
                                      fileReceiver.getOriginalFileName(),
                                      fileReceiver.getUser().getNick()));
        }

        else {
            notificationService.updateFileTransferProgress(fileTransfer, sendingText);
        }
    }

    /**
     * Makes sure the received file is scanned and inserted into the media database
     * when the file transfer is completed.
     */
    @Override
    public void statusCompleted() {
        notificationService.completeFileTransferProgress(
                fileTransfer, context.getString(R.string.notification_completed));

        if (fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE) {
            final FileReceiver fileReceiver = (FileReceiver) fileTransfer;

            androidFileUtils.addFileToMediaDatabase(context, fileReceiver.getFile());
        }
    }

    @Override
    public void statusFailed() {
        notificationService.completeFileTransferProgress(
                fileTransfer, context.getString(R.string.notification_failed));
    }

    @Override
    public void transferUpdate() {
        final int percent = fileTransfer.getPercent();
        final long currentTime = System.currentTimeMillis();
        final long timePassedSinceUpdate = currentTime - lastTransferUpdate;

        if (percent != percentTransferred && timePassedSinceUpdate > MINIMUM_UPDATE_TIME) {
            percentTransferred = percent;
            lastTransferUpdate = currentTime;

            if (fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE) {
                notificationService.updateFileTransferProgress(fileTransfer, receivingText);
            }

            else {
                notificationService.updateFileTransferProgress(fileTransfer, sendingText);
            }
        }
    }
}
