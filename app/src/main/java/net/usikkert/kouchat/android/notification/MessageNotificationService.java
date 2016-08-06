
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
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.misc.User;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Collection;

/**
 * Service for handling new message notifications.
 *
 * @author Christian Ihle
 */
public class MessageNotificationService {

    private static final int MESSAGE_NOTIFICATION_ID = 20000;

    private final Context context;
    private final NotificationManager notificationManager;

    private final Collection<String> messages;

    public MessageNotificationService(final Context context,
                                      final NotificationManager notificationManager) {
        this.context = context;
        this.notificationManager = notificationManager;

        messages = new CircularFifoQueue<>(5); // Stores the 5 newest messages
    }

    public void notifyNewMainChatMessage(final User user, final String message) {
        final String latestMessage = user.getNick() + ": " + message;
        messages.add(latestMessage);

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setContentTitle(context.getString(R.string.notification_new_message));
        notification.setContentText(latestMessage);
        notification.setSmallIcon(R.drawable.ic_stat_notify_activity);
        notification.setStyle(fillInboxStyle());
        notification.setContentIntent(createIntentForMainChat());

        notificationManager.notify(MESSAGE_NOTIFICATION_ID, notification.build());
    }

    public void resetAllNotifications() {
        messages.clear();
        notificationManager.cancel(MESSAGE_NOTIFICATION_ID);
    }

    private NotificationCompat.InboxStyle fillInboxStyle() {
        final NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        for (final String msg : messages) {
            inboxStyle.addLine(msg);
        }

        return inboxStyle;
    }

    private PendingIntent createIntentForMainChat() {
        return PendingIntent.getActivity(context, 0, new Intent(context, MainChatController.class), 0);
    }
}
