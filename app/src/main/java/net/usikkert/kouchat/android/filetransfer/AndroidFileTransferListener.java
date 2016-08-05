
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

package net.usikkert.kouchat.android.filetransfer;

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

    private final FileTransfer fileTransfer;
    private final Context context;
    private final AndroidFileUtils androidFileUtils;
    private final MessageController messageController;
    private final NotificationService notificationService;
    private int percentTransferred;

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

        fileTransfer.registerListener(this);
    }

    @Override
    public void statusWaiting() {
        notificationService.updateFileTransferProgress(fileTransfer, "Waiting");
    }

    @Override
    public void statusConnecting() {
        notificationService.updateFileTransferProgress(fileTransfer, "Connecting");
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

            notificationService.updateFileTransferProgress(fileReceiver, "Receiving");

            messageController.showSystemMessage("Receiving " + fileReceiver.getOriginalFileName() +
                                                        " from " + fileReceiver.getUser().getNick());
        }

        else {
            notificationService.updateFileTransferProgress(fileTransfer, "Sending");
        }
    }

    /**
     * Makes sure the received file is scanned and inserted into the media database
     * when the file transfer is completed.
     */
    @Override
    public void statusCompleted() {
        notificationService.completeFileTransferProgress(fileTransfer, "Completed");

        if (fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE) {
            final FileReceiver fileReceiver = (FileReceiver) fileTransfer;

            androidFileUtils.addFileToMediaDatabase(context, fileReceiver.getFile());
        }
    }

    @Override
    public void statusFailed() {
        notificationService.completeFileTransferProgress(fileTransfer, "Failed");
    }

    @Override
    public void transferUpdate() {
        final int percent = fileTransfer.getPercent();

        if (percent != percentTransferred) {
            percentTransferred = percent;

            if (fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE) {
                notificationService.updateFileTransferProgress(fileTransfer, "Receiving");
            }

            else {
                notificationService.updateFileTransferProgress(fileTransfer, "Sending");
            }
        }
    }
}
