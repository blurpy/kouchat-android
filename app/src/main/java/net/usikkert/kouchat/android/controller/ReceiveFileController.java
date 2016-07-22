
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

package net.usikkert.kouchat.android.controller;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.component.ReceiveFileDialog;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.net.FileReceiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Controller for showing a accept/reject file transfer dialog, after clicking on a notification.
 *
 * @author Christian Ihle
 */
public class ReceiveFileController extends Activity {

    private ReceiveFileDialog receiveFileDialog = new ReceiveFileDialog();

    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent chatServiceIntent = createChatServiceIntent();
        serviceConnection = createServiceConnection();
        bindService(chatServiceIntent, serviceConnection, Context.BIND_NOT_FOREGROUND);
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);

        serviceConnection = null;
        receiveFileDialog = null;

        super.onDestroy();
    }

    private Intent createChatServiceIntent() {
        return new Intent(this, ChatService.class);
    }

    private ServiceConnection createServiceConnection() {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
                final ChatServiceBinder binder = (ChatServiceBinder) iBinder;
                showDialog(binder.getAndroidUserInterface());
            }

            @Override
            public void onServiceDisconnected(final ComponentName componentName) { }
        };
    }

    private void showDialog(final AndroidUserInterface androidUserInterface) {
        final Intent intent = getIntent();
        final int userCode = intent.getIntExtra("userCode", -1);
        final int fileTransferId = intent.getIntExtra("fileTransferId", -1);

        final FileReceiver fileReceiver = androidUserInterface.getFileReceiver(userCode, fileTransferId);

        if (fileReceiver == null) {
            receiveFileDialog.showMissingFileDialog(this);
        } else {
            receiveFileDialog.showReceiveFileDialog(this, fileReceiver);
        }
    }
}
