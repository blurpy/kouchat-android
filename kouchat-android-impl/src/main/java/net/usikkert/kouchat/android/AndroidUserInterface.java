
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

package net.usikkert.kouchat.android;

import java.util.concurrent.ExecutionException;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.notification.NotificationService;
import net.usikkert.kouchat.event.UserListListener;
import net.usikkert.kouchat.misc.ChatLogger;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.ui.ChatWindow;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Implementation of a KouChat user interface that communicates with the Android GUI.
 *
 * @author Christian Ihle
 */
public class AndroidUserInterface implements UserInterface, ChatWindow, UserListListener {

    private final MessageController msgController;
    private final Controller controller;
    private final UserList userList;
    private final User me;
    private final MessageStylerWithHistory messageStyler;
    private final Context context;
    private final Settings settings;
    private final NotificationService notificationService;

    private MainChatController mainChatController;

    public AndroidUserInterface(final Context context, final Settings settings) {
        Validate.notNull(context, "Context can not be null");
        Validate.notNull(settings, "Settings can not be null");

        this.context = context;
        this.settings = settings;

        messageStyler = new MessageStylerWithHistory(context);
        msgController = new MessageController(this, this, settings);
        controller = new Controller(this, settings);
        notificationService = new NotificationService(context);

        userList = controller.getUserList();
        userList.addUserListListener(this);
        me = settings.getMe();
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

    /**
     * Updates the title of the main chat to the current topic. Looks like this:
     *
     * <p><code>Nick name - Topic: the topic (nick name of the user that set the topic) - KouChat</code>.</p>
     *
     * <p>Example: <code>Penny - Topic: Knock knock (Sheldon) - KouChat</code>.</p>
     */
    @Override
    public void showTopic() {
        if (mainChatController != null) {
            final StringBuilder title = new StringBuilder();

            title.append(me.getNick());

            final Topic topic = controller.getTopic();

            if (!topic.getTopic().isEmpty()) {
                title.append(" - Topic: ").append(topic);
            }

            title.append(" - ").append(Constants.APP_NAME);

            mainChatController.updateTopic(title.toString());
        }
    }

    @Override
    public void clearChat() {

    }

    @Override
    public void changeAway(final boolean away) {

    }

    /**
     * Notifies about a new message if the main chat is not visible.
     */
    @Override
    public void notifyMessageArrived(final User user) {
        if (!isVisible()) {
            notificationService.notifyNewMessage();
        }
    }

    /**
     * Notifies about a new message if neither the main chat nor the private chat with
     * the user who sent the message is visible.
     */
    @Override
    public void notifyPrivateMessageArrived(final User user) {
        if (!isVisible() && !user.getPrivchat().isVisible()) {
            notificationService.notifyNewMessage();
        }
    }

    @Override
    public MessageController getMessageController() {
        return msgController;
    }

    @Override
    public void createPrivChat(final User user) {
        if (user.getPrivchat() == null) {
            user.setPrivchat(new AndroidPrivateChatWindow(context, user));
        }

        if (user.getPrivateChatLogger() == null) {
            user.setPrivateChatLogger(new ChatLogger(user.getNick(), settings));
        }
    }

    @Override
    public boolean isVisible() {
        return mainChatController != null && mainChatController.isVisible();
    }

    @Override
    public boolean isFocused() {
        return isVisible();
    }

    @Override
    public void quit() {

    }

    @Override
    public void appendToChat(final String message, final int color) {
        final CharSequence styledMessage = messageStyler.styleAndAppend(message, color);

        if (mainChatController != null) {
            mainChatController.appendToChat(styledMessage);
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

    public void registerMainChatController(final MainChatController theMainChatController) {
        mainChatController = theMainChatController;
        mainChatController.updateChat(messageStyler.getHistory());

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

    public void sendPrivateMessage(final String privateMessage, final User user) {
        final AsyncTask<Void, Void, Void> sendMessageTask = new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(final Void... voids) {
                try {
                    controller.sendPrivateMessage(privateMessage, user);
                    msgController.showPrivateOwnMessage(user, privateMessage);
                }

                catch (CommandException e) {
                    msgController.showPrivateSystemMessage(user, e.getMessage());
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

    public void setNickNameFromSettings() {
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

    public boolean changeNickName(final String nick) {
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
            return doChangeNickName(trimNick);
        }

        return false;
    }

    private boolean doChangeNickName(final String trimNick) {
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

    /**
     * Resets the new private message field of the user.
     *
     * @param user The user to reset the field for.
     */
    public void activatedPrivChat(final User user) {
        if (user.isNewPrivMsg()) {
            user.setNewPrivMsg(false); // In case the user has logged off
            controller.changeNewMessage(user.getCode(), false);
        }
    }

    /**
     * Updates whether the user is currently writing or not. This makes sure a star is shown
     * by the nick name in the user list, and sends a notice to other users so they can show the same thing.
     *
     * @param isCurrentlyWriting If the application user is currently writing.
     */
    public void updateMeWriting(final boolean isCurrentlyWriting) {
        final AsyncTask<Void, Void, Void> updateMeWritingTask = new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(final Void... voids) {
                controller.updateMeWriting(isCurrentlyWriting);
                return null;
            }
        };

        updateMeWritingTask.execute((Void) null);
    }
}
