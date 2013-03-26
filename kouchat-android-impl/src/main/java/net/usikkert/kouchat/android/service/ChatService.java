
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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

package net.usikkert.kouchat.android.service;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.android.AndroidUserInterface;
import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.misc.Settings;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service for accessing the chat operations in lower layers.
 *
 * <p>Starts as a foreground service to avoid being killed randomly.
 * This means a notification is available at all times.</p>
 *
 * @author Christian Ihle
 */
public class ChatService extends Service {

    private static final int STARTUP_NOTIFICATION_ID = 1001;

    private AndroidUserInterface androidUserInterface;

    @Override
    public void onCreate() {
        System.setProperty(Constants.PROPERTY_CLIENT_UI, "Android");

        androidUserInterface = new AndroidUserInterface(this, new Settings());
        androidUserInterface.setNickNameFromSettings();

        super.onCreate();
    }

    @Override
    public void onStart(final Intent intent, final int startId) {
        if (!androidUserInterface.isLoggedOn()) {
            androidUserInterface.logOn();
        }

        startForeground(STARTUP_NOTIFICATION_ID, createStartupNotification());

        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new ChatServiceBinder(androidUserInterface);
    }

    @Override
    public void onDestroy() {
        androidUserInterface.logOff();

        super.onDestroy();
    }

    private Notification createStartupNotification() {
        final Notification notification = new Notification(
                R.drawable.kou_icon_24x24,
                getText(R.string.notification_startup), // Text shown when starting KouChat
                System.currentTimeMillis());

        // Used to launch KouChat when clicking on the notification in the drawer
        final PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, MainChatController.class), 0);

        notification.setLatestEventInfo(this,
                Constants.APP_NAME, // First line of the notification in the drawer
                getText(R.string.notification_running), // Second line of the notification in the drawer
                pendingIntent);

        return notification;
    }
}
