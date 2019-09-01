
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

package net.usikkert.kouchat.android.service;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;

/**
 * Service called from file transfer notification to cancel ongoing file transfer.
 *
 * @author Christian Ihle
 */
public class CancelFileTransferService extends IntentService {

    private ServiceConnection serviceConnection;
    private AndroidUserInterface androidUserInterface;

    public CancelFileTransferService() {
        super("CancelFileTransferService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final Intent chatServiceIntent = createChatServiceIntent();
        serviceConnection = createServiceConnection();
        bindService(chatServiceIntent, serviceConnection, Context.BIND_NOT_FOREGROUND);
    }

    @Override
    public void onDestroy() {
        unbindService(serviceConnection);

        serviceConnection = null;
        androidUserInterface = null;

        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (intent != null) {
            // Service might finish after onHandleIntent() so need to do this now
            waitForServiceToBind();

            if (androidUserInterface != null) {
                cancelFileTransfer(intent);
            }
        }
    }

    private void waitForServiceToBind() {
        try {
            for (int i = 0; i < 100; i++) {
                if (androidUserInterface == null) {
                    Thread.sleep(50L);
                } else {
                    break;
                }
            }
        }

        catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void cancelFileTransfer(final Intent intent) {
        final int userCode = intent.getIntExtra("userCode", -1);
        final int fileTransferId = intent.getIntExtra("fileTransferId", -1);

        androidUserInterface.cancelFileTransfer(userCode, fileTransferId);
    }

    private Intent createChatServiceIntent() {
        return new Intent(this, ChatService.class);
    }

    private ServiceConnection createServiceConnection() {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
                final ChatServiceBinder binder = (ChatServiceBinder) iBinder;
                androidUserInterface = binder.getAndroidUserInterface();
            }

            @Override
            public void onServiceDisconnected(final ComponentName componentName) { }
        };
    }
}
