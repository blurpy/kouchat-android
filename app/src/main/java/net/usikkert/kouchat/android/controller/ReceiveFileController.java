
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

package net.usikkert.kouchat.android.controller;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.component.ReceiveFileDialog;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.net.FileReceiver;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Controller for showing a accept/reject file transfer dialog, after clicking on a notification.
 *
 * @author Christian Ihle
 */
public class ReceiveFileController extends AppCompatActivity {

    private static final int WRITE_REQUEST_CODE = 101;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private ReceiveFileDialog receiveFileDialog = new ReceiveFileDialog();

    private ServiceConnection serviceConnection;
    private FileReceiver fileReceiver;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent chatServiceIntent = createChatServiceIntent();
        serviceConnection = createServiceConnection();
        bindService(chatServiceIntent, serviceConnection, Context.BIND_NOT_FOREGROUND);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Override exit animation to avoid black screen when closing the dialog on
        // some versions of Android, like KitKat.
        // This was the only place the method had any effect.
        overridePendingTransition(android.support.v7.appcompat.R.anim.abc_popup_enter,
                                  android.support.v7.appcompat.R.anim.abc_popup_exit);
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);

        serviceConnection = null;
        receiveFileDialog = null;
        fileReceiver = null;

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
                showDialogOrAskPermission(binder.getAndroidUserInterface());
            }

            @Override
            public void onServiceDisconnected(final ComponentName componentName) { }
        };
    }

    private void showDialogOrAskPermission(final AndroidUserInterface androidUserInterface) {
        final Intent intent = getIntent();
        final int userCode = intent.getIntExtra("userCode", -1);
        final int fileTransferId = intent.getIntExtra("fileTransferId", -1);

        fileReceiver = androidUserInterface.getFileReceiver(userCode, fileTransferId);

        final int permission = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            doShowDialog();
        }

        else {
            ActivityCompat.requestPermissions(
                    this, new String[] {WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
        }
    }

    private void doShowDialog() {
        if (fileReceiver == null) {
            receiveFileDialog.showMissingFileDialog(this);
        } else {
            receiveFileDialog.showReceiveFileDialog(this, fileReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        switch (requestCode) {
            case WRITE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doShowDialog();
                }

                else {
                    Toast.makeText(getApplicationContext(), // "this" just makes it crash
                                   R.string.receive_file_permission_denied,
                                   Toast.LENGTH_LONG).show();
                    fileReceiver.reject();
                    finish();
                }
            }
        }
    }
}
