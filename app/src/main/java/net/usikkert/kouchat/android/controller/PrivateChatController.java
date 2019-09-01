
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
import net.usikkert.kouchat.android.chatwindow.AndroidPrivateChatWindow;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.misc.User;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Controller for private chat with another user.
 *
 * @author Christian Ihle
 */
public class PrivateChatController extends AppCompatActivity {

    private ControllerUtils controllerUtils = new ControllerUtils();

    private TextView privateChatView;
    private EditText privateChatInput;
    private ScrollView privateChatScroll;
    private ActionBar actionBar;
    private ServiceConnection serviceConnection;

    private AndroidUserInterface androidUserInterface;
    private AndroidPrivateChatWindow privateChatWindow;
    private User user;

    /** If this private chat is currently visible. */
    private boolean visible;

    /** If this private chat has been destroyed. */
    private boolean destroyed;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.private_chat);

        privateChatInput = findViewById(R.id.privateChatInput);
        privateChatView = findViewById(R.id.privateChatView);
        privateChatScroll = findViewById(R.id.privateChatScroll);

        final Intent chatServiceIntent = createChatServiceIntent();
        serviceConnection = createServiceConnection();
        bindService(chatServiceIntent, serviceConnection, Context.BIND_NOT_FOREGROUND);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        controllerUtils.makeLinksClickable(privateChatView);
        privateChatInput.requestFocus();
    }

    @Override
    protected void onDestroy() {
        destroyed = true;

        if (privateChatWindow != null) {
            privateChatWindow.unregisterPrivateChatController();
            unbindService(serviceConnection);
        }

        privateChatInput.setOnKeyListener(null);
        controllerUtils.removeReferencesToTextViewFromText(privateChatView);
        controllerUtils.removeReferencesToTextViewFromText(privateChatInput);

        androidUserInterface = null;
        privateChatWindow = null;
        user = null;

        controllerUtils = null;
        privateChatView = null;
        privateChatInput = null;
        privateChatScroll = null;
        actionBar = null;
        serviceConnection = null;

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        visible = true;

        if (androidUserInterface != null && user != null) {
            // Make sure that new private message notifications are hidden when the private chat is shown again
            // after being hidden. Happens when the screen is turned off and on again, or after pressing home,
            // and returning to the application, or clicking a link and returning, and so on.
            resetNewPrivateMessageIcon();
        }
    }

    @Override
    protected void onPause() {
        visible = false;
        super.onPause();
    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Clicked on KouChat icon in the action bar
                return goBackToMainChat();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean goBackToMainChat() {
        startActivity(new Intent(this, MainChatController.class));
        return true;
    }

    /**
     * Makes sure regular key events from anywhere in the activity are sent to the input field,
     * and giving it focus if it doesn't currently have focus.
     *
     * <p>Always asks the activity first, to make sure special keys are handled correctly, like the back button.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public boolean dispatchKeyEvent(final KeyEvent event) {
        if (super.dispatchKeyEvent(event)) {
            return true;
        }

        if (!privateChatInput.hasFocus()) {
            privateChatInput.requestFocus();
        }

        return privateChatInput.dispatchKeyEvent(event);
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
            public void onServiceDisconnected(final ComponentName componentName) { }
        };
    }

    private void registerPrivateChatInputListener() {
        privateChatInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    sendPrivateMessage(privateChatInput.getText().toString());
                    privateChatInput.setText("");

                    return true;
                }

                return false;
            }
        });
    }

    protected void sendPrivateMessage(final String privateMessage) {
        if (privateMessage != null && privateMessage.trim().length() > 0) {
            androidUserInterface.sendPrivateMessage(privateMessage, user);
        }
    }

    private void setupPrivateChatWithUser() {
        setUser();

        if (user != null) {
            setPrivateChatWindow();
            setTitle();
            resetNewPrivateMessageIcon();
            registerPrivateChatInputListener();
        }
    }

    private void setTitle() {
        privateChatWindow.updateTitle();
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
                // If rotating fast, this activity could already be destroyed before this runs
                if (!destroyed) {
                    controllerUtils.scrollTextViewToBottom(privateChatView, privateChatScroll);
                }
            }
        }, ControllerUtils.ONE_SECOND);
    }

    public void appendToPrivateChat(final CharSequence privateMessage) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (destroyed) {
                    return; // If rotating fast, this activity could already be destroyed before this runs
                }

                privateChatView.append(privateMessage);

                // Allow a way to avoid automatic scrolling to the bottom.
                // Just scroll somewhere and click on the text to remove focus from the input field.
                // Also fixes the annoying jumping scroll that happens sometimes.
                if (privateChatInput.hasFocus()) {
                    controllerUtils.scrollTextViewToBottom(privateChatView, privateChatScroll);
                }
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

    /**
     * Updates the title and subtitle of the activity with the specified values.
     *
     * @param title The title to set.
     * @param subtitle The subtitle to set.
     */
    public void updateTitleAndSubtitle(final String title, final String subtitle) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                actionBar.setTitle(title);
                actionBar.setSubtitle(subtitle);
            }
        });
    }
}
