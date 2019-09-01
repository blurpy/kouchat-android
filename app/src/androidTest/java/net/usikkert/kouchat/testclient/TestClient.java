
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

package net.usikkert.kouchat.testclient;

import java.io.File;

import net.usikkert.kouchat.android.settings.AndroidSettingsSaver;
import net.usikkert.kouchat.message.CoreMessages;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.CommandParser;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.FileToSend;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.NonNls;

/**
 * A class that can be used for simulating a KouChat client in tests.
 *
 * @author Christian Ihle
 */
public class TestClient {

    private final Controller controller;
    private final TestClientUserInterface ui;
    private final TransferList transferList;
    private final CommandParser commandParser;

    private final User me;

    public TestClient() {
        this("Test", 12345678, 0);
    }

    public TestClient(final String nickName, final int userCode) {
        this(nickName, userCode, 0);
    }

    public TestClient(@NonNls final String nickName, final int userCode, final int ownColor) {
        final Settings settings = new Settings();
        settings.setClient("Test");

        if (ownColor != 0) {
            settings.setOwnColor(ownColor);
        }

        me = settings.getMe();
        me.setNick(nickName);

        if (userCode != 0) {
            TestUtils.setFieldValue(me, "code", userCode);
        }

        final ErrorHandler errorHandler = new ErrorHandler();
        ui = new TestClientUserInterface(settings, errorHandler);
        final CoreMessages coreMessages = new CoreMessages();
        controller = new Controller(ui, settings, new AndroidSettingsSaver(), coreMessages, errorHandler);
        transferList = controller.getTransferList();
        commandParser = new CommandParser(controller, ui, settings, coreMessages);
    }

    public int getUserCode() {
        return me.getCode();
    }

    /**
     * Starts a telnet server at port 20000 and blocks until /quit.
     */
    public void startTelnetServer() {
        new TestClientTelnetServer(ui, controller, commandParser);
    }

    public void logon() {
        controller.start();
        controller.logOn();

        waitForConnection();

        Tools.sleep(100);
    }

    public void logoff() {
        if (!controller.isLoggedOn()) {
            return;
        }

        Tools.sleep(500);
        controller.logOff(true);
        controller.shutdown();
    }

    public void changeTopic(final String topic) {
        try {
            controller.changeTopic(topic);
        } catch (final CommandException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This is a hack to set the initial topic of the chat.
     *
     * @param topic The topic to set.
     * @param date The date the topic was set, in milliseconds.
     */
    public void setInitialTopic(final String topic, final long date) {
        if (controller.isLoggedOn()) {
            throw new RuntimeException("Can't set initial topic when already logged on");
        }

        controller.getTopic().changeTopic(topic, me.getNick(), date);
    }

    /**
     * Sends the specified chat message to the main chat.
     *
     * @param message The message to send.
     */
    public void sendChatMessage(final String message) {
        try {
            controller.sendChatMessage(message);
        } catch (final CommandException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends the specified chat message to the private chat of the specified user.
     *
     * @param message The message to send.
     * @param toUser The user to send the private message to.
     */
    public void sendPrivateChatMessage(final String message, final User toUser) {
        try {
            controller.sendPrivateMessage(message, toUser);
        } catch (final CommandException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the specified message has arrived from the specified user in the main chat.
     *
     * @param user The user who sent the message.
     * @param message The message the user sent.
     * @return If the message has arrived.
     */
    public boolean gotMessage(final User user, final String message) {
        return ui.gotMessage(user, message);
    }

    /**
     * Gets the color of the specified message from the specified user in the main chat.
     *
     * @param user The user who sent the message.
     * @param message The message the user sent.
     * @return The color of the message.
     */
    public int getColorOfMessage(final User user, final String message) {
        return ui.getColorOfMessage(user, message);
    }

    /**
     * Checks if the specified private message has arrived from the specified user in the private chat.
     *
     * @param user The user who sent the private message.
     * @param message The private message the user sent.
     * @return If the private message has arrived.
     */
    public boolean gotPrivateMessage(final User user, final String message) {
        final User localUser = controller.getUser(user.getCode()); // Because user might be from another context
        final TestClientPrivateChatWindow privchat = (TestClientPrivateChatWindow) localUser.getPrivchat();

        return privchat.gotPrivateMessage(localUser, message);
    }

    /**
     * Gets the color of the specified private message from the specified user in the private chat.
     *
     * @param user The user who sent the private message.
     * @param message The private message the user sent.
     * @return The color of the private message.
     */
    public int getColorOfPrivateMessage(final User user, final String message) {
        final User localUser = controller.getUser(user.getCode()); // Because user might be from another context
        final TestClientPrivateChatWindow privchat = (TestClientPrivateChatWindow) localUser.getPrivchat();

        return privchat.getColorOfPrivateMessage(localUser, message);
    }

    public boolean gotAnyPrivateMessages() {
        final UserList userList = controller.getUserList();

        for (int i = 0; i < userList.size(); i++) {
            final User user = userList.get(i);
            final TestClientPrivateChatWindow privchat = (TestClientPrivateChatWindow) user.getPrivchat();

            if (privchat != null && privchat.gotAnyPrivateMessages()) {
                return true;
            }
        }

        return false;
    }

    public void resetPrivateMessages() {
        final UserList userList = controller.getUserList();

        for (int i = 0; i < userList.size(); i++) {
            final User user = userList.get(i);
            final TestClientPrivateChatWindow privchat = (TestClientPrivateChatWindow) user.getPrivchat();

            if (privchat != null) {
                privchat.resetPrivateMessages();
            }
        }
    }

    public void goAway(final String awayMessage) {
        try {
            controller.changeAwayStatus(me.getCode(), true, awayMessage);
        } catch (final CommandException e) {
            throw new RuntimeException(e);
        }
    }

    public void comeBack() {
        try {
            controller.changeAwayStatus(me.getCode(), false, "");
        } catch (final CommandException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeNickName(final String newNickName) {
        try {
            controller.changeMyNick(newNickName);
        } catch (final CommandException e) {
            throw new RuntimeException(e);
        }
    }

    public void startWriting() {
        controller.updateMeWriting(true);
    }

    public void stopWriting() {
        controller.updateMeWriting(false);
    }

    /**
     * Accepts a file transfer of a file with the given name from the given user.
     *
     * @param user The user who is trying to send a file.
     * @param fileName The name of the file that the user is trying to send.
     * @param newFile The full path and file name to use when saving the file.
     */
    public void acceptFile(final User user, final String fileName, final File newFile) {
        final FileReceiver fileReceiver = findFileReceiver(user, fileName);

        fileReceiver.setFile(newFile);
        fileReceiver.accept();
    }

    /**
     * Rejects  a file transfer of a file with the given name from the given user.
     *
     * @param user The user who is trying to send a file.
     * @param fileName The name of the file that the user is trying to send.
     */
    public void rejectFile(final User user, final String fileName) {
        final FileReceiver fileReceiver = findFileReceiver(user, fileName);

        fileReceiver.reject();
    }

    /**
     * Sends the given file from this client to the given user.
     *
     * @param user The user to send the file to.
     * @param file The file to send to the user.
     */
    public void sendFile(final User user, final File file) {
        final User localUser = controller.getUser(user.getCode()); // Because user might be from another context

        try {
            commandParser.sendFile(localUser, new FileToSend(file));
        } catch (final CommandException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFileTransferDelay(final int delay) {
        ui.setFileTransferDelay(delay);
    }

    /**
     * Cancels sending the given file to the given user.
     *
     * @param user The user the file was sent to.
     * @param file The file that was sent to the user.
     */
    public void cancelFileSending(final User user, final File file) {
        final FileSender fileSender = findFileSender(user, file);

        commandParser.cancelFileTransfer(fileSender);
    }

    private FileReceiver findFileReceiver(final User user, final String fileName) {
        final User localUser = controller.getUser(user.getCode()); // Because user might be from another context

        final FileReceiver fileReceiver = transferList.getFileReceiver(localUser, fileName);
        Validate.notNull(fileReceiver,
                String.format("Unable to find the file with the name '%s' from the user '%s'", fileName, user));

        return fileReceiver;
    }

    private FileSender findFileSender(final User user, final File file) {
        final User localUser = controller.getUser(user.getCode()); // Because user might be from another context

        final FileSender fileSender = transferList.getFileSender(localUser, file.getName());
        Validate.notNull(fileSender,
                String.format("Unable to find the file with the name '%s' for the user '%s'", file.getName(), user));

        return fileSender;
    }

    private void waitForConnection() {
        Tools.sleep(500);

        for (int i = 0; i < 20; i++) {
            if (!controller.isLoggedOn()) {
                Tools.sleep(100);
            }
        }

        if (!controller.isLoggedOn()) {
            throw new RuntimeException("Could not connect");
        }
    }
}
