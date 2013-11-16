
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
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

package net.usikkert.kouchat.android.service;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.notification.NotificationService;
import net.usikkert.kouchat.misc.Settings;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;

/**
 * Service for accessing the chat operations in lower layers.
 *
 * <p>Starts as a foreground service to avoid being killed randomly.
 * This means a notification is available at all times.</p>
 *
 * @author Christian Ihle
 */
public class ChatService extends Service {

    private AndroidUserInterface androidUserInterface;
    private NotificationService notificationService;
    private LockHandler lockHandler;
    private ChatServiceBinder chatServiceBinder;

    @Override
    public void onCreate() {
        System.setProperty(Constants.PROPERTY_CLIENT_UI, "Android");

        notificationService = new NotificationService(this);
        final Settings settings = new Settings();
        androidUserInterface = new AndroidUserInterface(this, settings, notificationService);
        androidUserInterface.setNickNameFromSettings();

        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        final PowerManager powerManager = (PowerManager) this.getSystemService(POWER_SERVICE);
        lockHandler = new LockHandler(androidUserInterface, settings, wifiManager, powerManager);

        chatServiceBinder = new ChatServiceBinder(androidUserInterface);

        super.onCreate();
    }

    @Override
    public void onStart(final Intent intent, final int startId) {
        if (!androidUserInterface.isLoggedOn()) {
            androidUserInterface.logOn();
        }

        startForeground(NotificationService.SERVICE_NOTIFICATION_ID, notificationService.createServiceNotification());

        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return chatServiceBinder;
    }

    @Override
    public void onDestroy() {
        androidUserInterface.logOff();
        lockHandler.releaseAllLocks();
        chatServiceBinder.onDestroy();

        androidUserInterface = null;
        notificationService = null;
        lockHandler = null;
        chatServiceBinder = null;

        super.onDestroy();
    }
}
