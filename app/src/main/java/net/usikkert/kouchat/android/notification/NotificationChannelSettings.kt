
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

package net.usikkert.kouchat.android.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import net.usikkert.kouchat.android.R
import java.util.*

/**
 * Configures the notification channels for Android Oreo (8) and newer.
 *
 * Choices:
 *
 * * service
 * * file_transfers
 * * main_chat_messages
 * * main_chat_away_messages
 * * private_chat_messages
 *
 * @author Christian Ihle
 */
@RequiresApi(api = Build.VERSION_CODES.O)
class NotificationChannelSettings(private val context: Context) {

    val soundUri: Uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
            context.packageName + "/" + R.raw.notification_sound)

    fun setupNotificationChannels() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val service = createServiceChannel()
        val fileTransfers = createFileTransfersChannel()
        val mainChatMessages = createMainChatMessagesChannel()
        val mainChatAwayMessages = createMainChatAwayMessagesChannel()
        val privateChatMessages = createPrivateChatMessagesChannel()

        notificationManager.createNotificationChannels(
                Arrays.asList(service, fileTransfers, mainChatMessages, mainChatAwayMessages, privateChatMessages))
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

    private fun createMainChatAwayMessagesChannel(): NotificationChannel {
        return createChannel(
                R.string.notifications_channel_id_main_chat_away_messages,
                R.string.notifications_channel_name_main_chat_away_messages,
                R.string.notifications_channel_description_main_chat_away_messages,
                NotificationManager.IMPORTANCE_LOW,
                false, false, true,
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
        channel.lightColor = Color.CYAN
        channel.enableVibration(vibration)
        channel.vibrationPattern = NotificationHelper.VIBRATION_PATTERN
        channel.setShowBadge(badge)
        channel.lockscreenVisibility = visibility
        channel.setSound(soundUri, Notification.AUDIO_ATTRIBUTES_DEFAULT)

        return channel
    }
}
