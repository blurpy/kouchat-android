
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
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.controller.PrivateChatController;
import net.usikkert.kouchat.android.settings.AndroidSettings;
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
    private final NotificationHelper notificationHelper;

    private final Collection<CharSequence> messages;
    private final Map<User, Collection<String>> privateMessages;

    private final Map<User, Integer> privateMessageCount;
    private int messageCount;

    public MessageNotificationService(final Context context,
                                      final NotificationManager notificationManager,
                                      final AndroidSettings settings) {
        this.context = context;
        this.notificationManager = notificationManager;

        notificationHelper = new NotificationHelper(context, settings);
        messages = new CircularFifoQueue<>(MAX_MESSAGES);
        privateMessages = new HashMap<>();
        privateMessageCount = new HashMap<>();
    }

    public void notifyNewMainChatMessage(final User user, final String message) {
        final CharSequence latestMessage = createMainChatMessage(user, message);
        addMainChatMessageToList(latestMessage);

        final String channelId = context.getString(notificationHelper.chooseMainChatChannel());
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId);
        notification.setTicker(context.getString(R.string.notification_new_message_ticker, user.getNick()));
        notification.setContentTitle(context.getString(R.string.notification_main_chat));
        notification.setContentText(latestMessage);
        notification.setNumber(messageCount);
        notification.setSmallIcon(R.drawable.ic_stat_notify_activity);
        notification.setStyle(fillMainChatInbox());
        notification.setContentIntent(createIntentForMainChat());
        notification.setPriority(NotificationCompat.PRIORITY_MAX);
        notification.setCategory(NotificationCompat.CATEGORY_MESSAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notification.setGroup(NotificationGroup.MAIN_CHAT.name());
        }

        notificationHelper.setFeedbackEffects(notification);

        notificationManager.notify(MESSAGE_NOTIFICATION_ID, notification.build());
    }

    public void notifyNewPrivateChatMessage(final User user, final String message) {
        addPrivateMessageToList(user, message);

        final String channelId = context.getString(R.string.notifications_channel_id_private_chat_messages);
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId);
        notification.setTicker(context.getString(R.string.notification_new_private_message_ticker, user.getNick()));
        notification.setContentTitle(user.getNick());
        notification.setContentText(message);
        notification.setNumber(privateMessageCount.get(user));
        notification.setSmallIcon(R.drawable.ic_stat_notify_activity);
        notification.setStyle(fillPrivateChatInbox(user));
        notification.setContentIntent(createIntentForPrivateChat(user));
        notification.setPriority(NotificationCompat.PRIORITY_MAX);
        notification.setCategory(NotificationCompat.CATEGORY_MESSAGE);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            notification.setGroup(NotificationGroup.PRIVATE_CHAT.name());
//        }

        notificationHelper.setFeedbackEffects(notification);

        notificationManager.notify(getNotificationIdForUser(user), notification.build());
    }

    private CharSequence createMainChatMessage(final User user, final String message) {
        final String nick = user.getNick();

        final SpannableString messageWithBoldNick = new SpannableString(nick + ": " + message);
        messageWithBoldNick.setSpan(new StyleSpan(Typeface.BOLD), 0, nick.length() + 1, 0);

        return messageWithBoldNick;
    }

    private void addMainChatMessageToList(final CharSequence latestMessage) {
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

        final Integer userCount = privateMessageCount.get(user);

        if (userCount == null) {
            privateMessageCount.put(user, 1);
        } else {
            privateMessageCount.put(user, userCount + 1);
        }
    }

    public void resetAllNotifications() {
        notificationManager.cancel(MESSAGE_NOTIFICATION_ID);
        cancelPrivateChatNotifications();

        messages.clear();
        privateMessages.clear();

        messageCount = 0;
        privateMessageCount.clear();
    }

    private void cancelPrivateChatNotifications() {
        final Set<User> users = privateMessages.keySet();

        for (final User user : users) {
            notificationManager.cancel(getNotificationIdForUser(user));
        }
    }

    public void resetPrivateChatNotification(final User user) {
        privateMessages.remove(user);
        privateMessageCount.remove(user);
        notificationManager.cancel(getNotificationIdForUser(user));
    }

    private NotificationCompat.InboxStyle fillMainChatInbox() {
        final NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        for (final CharSequence msg : messages) {
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
        privateChatIntent.setAction("openPrivateChat " + System.currentTimeMillis()); // Unique - to avoid it being cached

        return PendingIntent.getActivity(context, 0, privateChatIntent, 0);
    }

    public boolean isMainChatActivity() {
        return messageCount > 0;
    }

    public boolean isPrivateChatActivity() {
        return !privateMessageCount.isEmpty();
    }
}
