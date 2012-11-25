
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
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

package net.usikkert.kouchat.android;

import java.util.concurrent.ExecutionException;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.event.UserListListener;
import net.usikkert.kouchat.misc.ChatLogger;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.ui.ChatWindow;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

/**
 * Implementation of a KouChat user interface that communicates with the Android GUI.
 *
 * @author Christian Ihle
 */
public class AndroidUserInterface implements UserInterface, ChatWindow, UserListListener {

    private final SpannableStringBuilder savedChat;
    private final MessageController msgController;
    private final Controller controller;
    private final UserList userList;
    private final User me;

    private MainChatController mainChatController;

    public AndroidUserInterface() {
        System.setProperty(Constants.PROPERTY_CLIENT_UI, "Android");

        savedChat = new SpannableStringBuilder();
        msgController = new MessageController(this, this);
        controller = new Controller(this);
        userList = controller.getUserList();
        userList.addUserListListener(this);
        me = Settings.getSettings().getMe();
    }

    @Override
    public boolean askFileSave(final String user, final String fileName, final String size) {
        return false;
    }

    @Override
    public void showFileSave(final FileReceiver fileReceiver) {

    }

    @Override
    public void showTransfer(final FileReceiver fileRes) {

    }

    @Override
    public void showTransfer(final FileSender fileSend) {

    }

    @Override
    public void showTopic() {
        if (mainChatController != null) {
            final String nick = me.getNick();
            final String topic = controller.getTopic().toString();

            mainChatController.updateTopic(nick + " - Topic: " + topic + " - " + Constants.APP_NAME);
        }
    }

    @Override
    public void clearChat() {

    }

    @Override
    public void changeAway(final boolean away) {

    }

    @Override
    public void notifyMessageArrived(final User user) {

    }

    @Override
    public void notifyPrivateMessageArrived(final User user) {

    }

    @Override
    public MessageController getMessageController() {
        return msgController;
    }

    @Override
    public void createPrivChat(final User user) {
        if (user.getPrivchat() == null) {
            user.setPrivchat(new AndroidPrivateChatWindow(user, controller));
        }

        if (user.getPrivateChatLogger() == null) {
            user.setPrivateChatLogger(new ChatLogger(user.getNick()));
        }
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public void quit() {

    }

    @Override
    public void appendToChat(final String message, final int color) {
        final SpannableStringBuilder builder = new SpannableStringBuilder(message + "\n");
        builder.setSpan(new ForegroundColorSpan(color), 0, message.length(), 0);
        savedChat.append(builder);

        if (mainChatController != null) {
            mainChatController.appendToChat(message, color);
        }
    }

    public void logOn() {
        controller.logOn();
    }

    public void logOff() {
        final AsyncTask<Void, Void, Void> logOffTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... voids) {
                controller.logOff(false);
                return null;
            }
        };

        logOffTask.execute((Void) null);
    }

    public boolean isLoggedOn() {
        return controller.isLoggedOn();
    }

    public void registerMainChatController(final MainChatController mainChatController) {
        this.mainChatController = mainChatController;
        mainChatController.updateChat(savedChat);

        for (int i = 0; i < userList.size(); i++) {
            userAdded(i);
        }
    }

    public void unregisterMainChatController() {
        mainChatController = null;
    }

    public void sendMessage(final String message) {
        final AsyncTask<Void, Void, Void> sendMessageTask = new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(final Void... voids) {
                try {
                    controller.sendChatMessage(message);
                    msgController.showOwnMessage(message);
                }

                catch (final CommandException e) {
                    msgController.showSystemMessage(e.getMessage());
                }

                return null;
            }
        };

        sendMessageTask.execute((Void) null);
    }

    @Override
    public void userAdded(final int pos) {
        if (mainChatController != null) {
            final User user = userList.get(pos);
            mainChatController.addUser(user);
        }
    }

    @Override
    public void userChanged(final int pos) {
        if (mainChatController != null) {
            final User user = userList.get(pos);
            mainChatController.updateUser(user);
        }
    }

    @Override
    public void userRemoved(final int pos) {
        if (mainChatController != null) {
            mainChatController.removeUser(pos);
        }
    }

    public void setNickNameFromSettings(final Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String nickNameKey = context.getString(R.string.settings_key_nick_name);
        final String nickName = getNickNameFromSettings(preferences, nickNameKey);

        me.setNick(nickName);
    }

    private String getNickNameFromSettings(final SharedPreferences preferences, final String nickNameKey) {
        final String nickNameFromSettings = preferences.getString(nickNameKey, null);

        if (Tools.isValidNick(nickNameFromSettings)) {
            return nickNameFromSettings;
        }

        return Integer.toString(me.getCode());
    }

    public boolean changeNickName(final Context context, final String nick) {
        final String trimNick = nick.trim();

        if (trimNick.equals(me.getNick())) {
            return false;
        }

        if (!Tools.isValidNick(trimNick)) {
            Toast.makeText(context, context.getString(R.string.error_nick_name_invalid), Toast.LENGTH_LONG).show();
        }

        else if (controller.isNickInUse(trimNick)) {
            Toast.makeText(context, R.string.error_nick_name_in_use, Toast.LENGTH_LONG).show();
        }

        else {
            return doChangeNickName(context, trimNick);
        }

        return false;
    }

    private boolean doChangeNickName(final Context context, final String trimNick) {
        final AsyncTask<Void, Void, Boolean> changeNickNameTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            public Boolean doInBackground(final Void... voids) {
                try {
                    controller.changeMyNick(trimNick);
                    msgController.showSystemMessage(context.getString(R.string.message_your_nick_name_changed, me.getNick()));
                    showTopic();

                    return true;
                }

                catch (final CommandException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        };

        changeNickNameTask.execute((Void) null);

        try {
            return changeNickNameTask.get();
        }

        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser(final int code) {
        for (int i = 0; i < userList.size(); i++) {
            final User user = userList.get(i);
            if (user.getCode() == code) {
                return user;
            }
        }

        throw new RuntimeException("Unknow user: " + code);
    }
}
