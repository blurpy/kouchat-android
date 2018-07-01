
/***************************************************************************
 *   Copyright 2006-2018 by Christian Ihle                                 *
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

package net.usikkert.kouchat.android.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import net.usikkert.kouchat.android.R
import java.util.*

/**
 * Configures the notification channels for Android Oreo and newer.
 *
 * Choices:
 *
 * * service
 * * file_transfers
 * * main_chat_messages
 * * private_chat_messages
 *
 * @author Christian Ihle
 */
@RequiresApi(api = Build.VERSION_CODES.O)
class NotificationChannelSettings(private val context: Context) {

    fun setupNotificationChannels() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val service = createServiceChannel()
        val fileTransfers = createFileTransfersChannel()
        val mainChatMessages = createMainChatMessagesChannel()
        val privateChatMessages = createPrivateChatMessagesChannel()

        notificationManager.createNotificationChannels(
                Arrays.asList(service, fileTransfers, mainChatMessages, privateChatMessages))
    }

    private fun createServiceChannel(): NotificationChannel {
        return createChannel(
                R.string.notifications_channel_id_service,
                R.string.notifications_channel_name_service,
                R.string.notifications_channel_description_service,
                NotificationManager.IMPORTANCE_LOW,
                false, false, false,
                Notification.VISIBILITY_PUBLIC)
    }

    private fun createFileTransfersChannel(): NotificationChannel {
        return createChannel(
                R.string.notifications_channel_id_file_transfers,
                R.string.notifications_channel_name_file_transfers,
                R.string.notifications_channel_description_file_transfers,
                NotificationManager.IMPORTANCE_HIGH,
                true, true, true,
                Notification.VISIBILITY_PUBLIC)
    }

    private fun createMainChatMessagesChannel(): NotificationChannel {
        return createChannel(
                R.string.notifications_channel_id_main_chat_messages,
                R.string.notifications_channel_name_main_chat_messages,
                R.string.notifications_channel_description_main_chat_messages,
                NotificationManager.IMPORTANCE_DEFAULT,
                true, true, true,
                Notification.VISIBILITY_PRIVATE)
    }

    private fun createPrivateChatMessagesChannel(): NotificationChannel {
        return createChannel(
                R.string.notifications_channel_id_private_chat_messages,
                R.string.notifications_channel_name_private_chat_messages,
                R.string.notifications_channel_description_private_chat_messages,
                NotificationManager.IMPORTANCE_DEFAULT,
                true, true, true,
                Notification.VISIBILITY_PRIVATE)
    }

    private fun createChannel(@StringRes id: Int,
                              @StringRes name: Int,
                              @StringRes description: Int,
                              importance: Int,
                              lights: Boolean, vibration: Boolean, badge: Boolean,
                              visibility: Int): NotificationChannel {
        val channel = NotificationChannel(context.getString(id), context.getString(name), importance)

        channel.description = context.getString(description)
        channel.enableLights(lights)
        channel.enableVibration(vibration)
        channel.setShowBadge(badge)
        channel.lockscreenVisibility = visibility

        return channel
    }
}
