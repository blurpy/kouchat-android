
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
import net.usikkert.kouchat.android.controller.PrivateChatController;
import net.usikkert.kouchat.misc.User;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Service for handling new message notifications.
 *
 * @author Christian Ihle
 */
public class MessageNotificationService {

    private static final int MESSAGE_NOTIFICATION_ID = 100000000;
    private static final int MAX_MESSAGES = 5; // Stores the 5 newest messages

    private final Context context;
    private final NotificationManager notificationManager;

    private final Collection<String> messages;
    private final Map<User, Collection<String>> privateMessages;

    private int messageCount;
    private int privateMessageCount;

    public MessageNotificationService(final Context context,
                                      final NotificationManager notificationManager) {
        this.context = context;
        this.notificationManager = notificationManager;

        messages = new CircularFifoQueue<>(MAX_MESSAGES);
        privateMessages = new HashMap<>();
    }

    public void notifyNewMainChatMessage(final User user, final String message) {
        final String latestMessage = user.getNick() + ": " + message;
        addMainChatMessageToList(latestMessage);

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setContentTitle(context.getString(R.string.notification_main_chat));
        notification.setContentText(latestMessage);
        notification.setNumber(messageCount);
        notification.setSmallIcon(R.drawable.ic_stat_notify_activity);
        notification.setStyle(fillMainChatInbox());
        notification.setContentIntent(createIntentForMainChat());

        notificationManager.notify(MESSAGE_NOTIFICATION_ID, notification.build());
    }

    public void notifyNewPrivateChatMessage(final User user, final String message) {
        addPrivateMessageToList(user, message);

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setContentTitle(user.getNick());
        notification.setContentText(message);
        notification.setNumber(privateMessageCount);
        notification.setSmallIcon(R.drawable.ic_stat_notify_activity);
        notification.setStyle(fillPrivateChatInbox(user));
        notification.setContentIntent(createIntentForPrivateChat(user));

        notificationManager.notify(getNotificationIdForUser(user), notification.build());
    }

    private void addMainChatMessageToList(final String latestMessage) {
        messages.add(latestMessage);
        messageCount++;
    }

    private void addPrivateMessageToList(final User user, final String message) {
        Collection<String> userMessages = privateMessages.get(user);

        if (userMessages == null) {
            userMessages = new CircularFifoQueue<>(MAX_MESSAGES);
            privateMessages.put(user, userMessages);
        }

        userMessages.add(message);
        privateMessageCount++;
    }

    public void resetAllNotifications() {
        notificationManager.cancel(MESSAGE_NOTIFICATION_ID);
        resetPrivateChatNotifications();

        messages.clear();
        privateMessages.clear();

        messageCount = 0;
        privateMessageCount = 0;
    }

    private void resetPrivateChatNotifications() {
        final Set<User> users = privateMessages.keySet();

        for (final User user : users) {
            notificationManager.cancel(getNotificationIdForUser(user));
        }
    }

    private NotificationCompat.InboxStyle fillMainChatInbox() {
        final NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        for (final String msg : messages) {
            inboxStyle.addLine(msg);
        }

        inboxStyle.setSummaryText(context.getString(R.string.notification_new_message));

        return inboxStyle;
    }

    private NotificationCompat.Style fillPrivateChatInbox(final User user) {
        final NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        final Collection<String> userMessages = privateMessages.get(user);

        if (userMessages != null) {
            for (final String msg : userMessages) {
                inboxStyle.addLine(msg);
            }

            inboxStyle.setSummaryText(context.getString(R.string.notification_new_private_message));
        }

        return inboxStyle;
    }

    private int getNotificationIdForUser(final User user) {
        return MESSAGE_NOTIFICATION_ID + user.getCode();
    }

    private PendingIntent createIntentForMainChat() {
        return PendingIntent.getActivity(context, 0, new Intent(context, MainChatController.class), 0);
    }

    private PendingIntent createIntentForPrivateChat(final User user) {
        final Intent privateChatIntent = new Intent(context, PrivateChatController.class);
        privateChatIntent.putExtra("userCode", user.getCode());

        return PendingIntent.getActivity(context, 0, privateChatIntent, 0);
    }
}
