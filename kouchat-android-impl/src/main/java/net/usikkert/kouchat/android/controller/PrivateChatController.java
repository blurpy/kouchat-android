
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

package net.usikkert.kouchat.android.controller;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.android.AndroidPrivateChatWindow;
import net.usikkert.kouchat.android.AndroidUserInterface;
import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.misc.User;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Controller for private chat with another user.
 *
 * @author Christian Ihle
 */
public class PrivateChatController extends Activity {

    private TextView privateChatView;
    private EditText privateChatInput;
    private ServiceConnection serviceConnection;

    private AndroidUserInterface androidUserInterface;
    private AndroidPrivateChatWindow privateChatWindow;
    private User user;

    private boolean visible;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.private_chat);
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.kou_icon_16x16);

        privateChatInput = (EditText) findViewById(R.id.privateChatInput);
        privateChatView = (TextView) findViewById(R.id.privateChatView);

        final Intent chatServiceIntent = createChatServiceIntent();
        serviceConnection = createServiceConnection();
        bindService(chatServiceIntent, serviceConnection, BIND_NOT_FOREGROUND);

        registerPrivateChatInputListener();
        ControllerUtils.makeTextViewScrollable(privateChatView);
        ControllerUtils.makeLinksClickable(privateChatView);
    }

    @Override
    protected void onDestroy() {
        privateChatWindow.unregisterPrivateChatController();
        unbindService(serviceConnection);
        super.onDestroy();

        androidUserInterface = null;
        privateChatWindow = null;
        user = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        visible = true;

        if (androidUserInterface != null) {
            // Make sure that new private message notifications are hidden when the private chat is shown again
            // after being hidden. Happens when the screen is turned off and on again, or after pressing home,
            // and returning to the application, or clicking a link and returning, and so on.
            resetNewPrivateMessageIcon();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        visible = false;
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

                setupPrivateChatWithUser();
            }

            @Override
            public void onServiceDisconnected(final ComponentName componentName) {

            }
        };
    }

    private void registerPrivateChatInputListener() {
        privateChatInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    sendPrivateMessage(privateChatInput.getText().toString());
                    privateChatInput.setText("");

                    return true;
                }

                return false;
            }
        });
    }

    private void sendPrivateMessage(final String privateMessage) {
        if (privateMessage != null && privateMessage.trim().length() > 0) {
            androidUserInterface.sendPrivateMessage(privateMessage, user);
        }
    }

    private void setupPrivateChatWithUser() {
        setUser();
        setTitle();
        setPrivateChatWindow();
        resetNewPrivateMessageIcon();
    }

    private void setTitle() {
        final StringBuilder title = new StringBuilder();

        title.append(user.getNick());

        if (!user.isOnline()) {
            title.append(" (offline)");
        }

        else if (user.isAway()) {
            title.append(" (away: ");
            title.append(user.getAwayMsg());
            title.append(")");
        }

        title.append(" - ");
        title.append(Constants.APP_NAME);

        setTitle(title.toString());
    }

    private void setUser() {
        final Intent intent = getIntent();

        final int userCode = intent.getIntExtra("userCode", -1);
        user = androidUserInterface.getUser(userCode);
    }

    private void setPrivateChatWindow() {
        androidUserInterface.createPrivChat(user);

        privateChatWindow = (AndroidPrivateChatWindow) user.getPrivchat();
        privateChatWindow.registerPrivateChatController(this);
    }

    private void resetNewPrivateMessageIcon() {
        androidUserInterface.activatedPrivChat(user);
    }

    public void updatePrivateChat(final CharSequence savedChat) {
        privateChatView.setText(savedChat);

        // Run this after 1 second, because right after a rotate the layout is null and it's not possible to scroll yet
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ControllerUtils.scrollTextViewToBottom(privateChatView);
            }
        }, ControllerUtils.ONE_SECOND);
    }

    public void appendToPrivateChat(final CharSequence privateMessage) {
        runOnUiThread(new Runnable() {
            public void run() {
                privateChatView.append(privateMessage);
                ControllerUtils.scrollTextViewToBottom(privateChatView);
            }
        });
    }

    /**
     * Returns if this private chat view is currently visible.
     *
     * @return If the view is visible.
     */
    public boolean isVisible() {
        return visible;
    }

    public void updateTitle() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle();
            }
        });
    }
}
