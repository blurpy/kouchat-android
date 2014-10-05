
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

package net.usikkert.kouchat.misc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Arrays;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.NetworkConnectionListener;
import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.message.CoreMessages;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.NetworkMessages;
import net.usikkert.kouchat.net.NetworkService;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.settings.SettingsSaver;
import net.usikkert.kouchat.ui.PrivateChatWindow;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.DateTools;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link Controller}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ControllerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Controller controller;

    private NetworkMessages networkMessages;
    private NetworkService networkService;
    private IdleThread idleThread;
    private DayTimer dayTimer;
    private TransferList transferList;
    private MessageController messageController;
    private UserInterface ui;
    private Settings settings;
    private SettingsSaver settingsSaver;
    private CoreMessages coreMessages;
    private ErrorHandler errorHandler;
    private DateTools dateTools;

    private User me;
    private UserList userList;

    @Before
    public void setUp() {
        settings = mock(Settings.class);
        settingsSaver = mock(SettingsSaver.class);
        coreMessages = new CoreMessages();
        errorHandler = mock(ErrorHandler.class);

        me = new User("TestUser", 123);
        when(settings.getMe()).thenReturn(me);

        ui = mock(UserInterface.class);
        messageController = mock(MessageController.class);
        when(ui.getMessageController()).thenReturn(messageController);

        controller = spy(new Controller(ui, settings, settingsSaver, coreMessages, errorHandler));

        networkMessages = TestUtils.setFieldValueWithMock(controller, "networkMessages", NetworkMessages.class);
        networkService = TestUtils.setFieldValueWithMock(controller, "networkService", NetworkService.class);

        // The idle thread makes tests fail randomly, because it sometimes runs in parallel and removes idle users...
        final IdleThread realIdleThread = TestUtils.getFieldValue(controller, IdleThread.class, "idleThread");
        realIdleThread.stopThread();

        idleThread = TestUtils.setFieldValueWithMock(controller, "idleThread", IdleThread.class);
        dayTimer = TestUtils.setFieldValueWithMock(controller, "dayTimer", DayTimer.class);

        final UserListController userListController =
                TestUtils.getFieldValue(controller, UserListController.class, "userListController");
        userList = userListController.getUserList();

        transferList = TestUtils.setFieldValueWithMock(controller, "tList", TransferList.class);
        dateTools = TestUtils.setFieldValueWithMock(controller, "dateTools", DateTools.class);

        // The shutdown hook makes tests fail randomly, because it sometimes runs in parallel...
        final Thread shutdownHook = TestUtils.getFieldValue(controller, Thread.class, "shutdownHook");
        Runtime.getRuntime().removeShutdownHook(shutdownHook);
    }

    @Test
    public void constructorShouldThrowExceptionIfUserInterfaceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User interface can not be null");

        new Controller(null, settings, settingsSaver, coreMessages, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new Controller(ui, null, settingsSaver, coreMessages, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsSaverIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings saver can not be null");

        new Controller(ui, settings, null, coreMessages, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfCoreMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Core messages can not be null");

        new Controller(ui, settings, settingsSaver, null, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new Controller(ui, settings, settingsSaver, coreMessages, null);
    }

    @Test
    public void updateMeWritingShouldUpdateMeAndNotifyOthersOnlyWhenStateChanges() {
        assertFalse(me.isWriting());

        // Not writing - nothing happens
        controller.updateMeWriting(false);
        verifyZeroInteractions(networkMessages);
        assertFalse(me.isWriting());

        // Wrote something - notify others and update me
        controller.updateMeWriting(true);
        verify(networkMessages).sendWritingMessage();
        assertTrue(me.isWriting());

        // Continues to write - nothing happens
        controller.updateMeWriting(true);
        verifyNoMoreInteractions(networkMessages);
        assertTrue(me.isWriting());

        // Stopped writing - notify others and update me
        controller.updateMeWriting(false);
        verify(networkMessages).sendStoppedWritingMessage();
        assertFalse(me.isWriting());

        // Still not writing - nothing happens
        controller.updateMeWriting(false);
        verifyNoMoreInteractions(networkMessages);
        assertFalse(me.isWriting());
    }

    @Test
    public void sendFileShouldThrowExceptionIfUserIsNull() throws CommandException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        controller.sendFile(null, mock(File.class));
    }

    @Test
    public void sendFileShouldThrowExceptionIfFileIsNull() throws CommandException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File can not be null");

        controller.sendFile(mock(User.class), null);
    }

    @Test
    public void sendFileShouldThrowExceptionIfUserIsMe() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not send a file to yourself");

        final User user = mock(User.class);
        when(user.isMe()).thenReturn(true);

        controller.sendFile(user, mock(File.class));
    }

    @Test
    public void sendFileShouldThrowExceptionIfNotConnected() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not send a file without being connected");

        controller.sendFile(mock(User.class), mock(File.class));
    }

    @Test
    public void sendFileShouldThrowExceptionIfMeIsAway() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not send a file while away");

        when(networkService.isNetworkUp()).thenReturn(true);
        controller.getChatState().setLoggedOn(true);

        me.setAway(true);

        controller.sendFile(mock(User.class), mock(File.class));
    }

    @Test
    public void sendFileShouldThrowExceptionIfUserIsAway() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not send a file to a user that is away");

        when(networkService.isNetworkUp()).thenReturn(true);
        controller.getChatState().setLoggedOn(true);

        final User user = new User("Test", 124);
        user.setAway(true);

        controller.sendFile(user, mock(File.class));
    }

    @Test
    public void sendFileShouldThrowExceptionIfNameIsTooLong() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not send a file with a name with more than 450 bytes");

        when(networkService.isNetworkUp()).thenReturn(true);
        controller.getChatState().setLoggedOn(true);

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 45; i++) {
            sb.append("1234567890");
        }

        sb.append("1"); // 451 characters with 1 byte each, at least in UTF-8

        final File file = mock(File.class);
        when(file.getName()).thenReturn(sb.toString());

        controller.sendFile(mock(User.class), file);
    }

    @Test
    public void sendFileShouldSendFileIfEverythingValidatedOK() throws CommandException {
        when(networkService.isNetworkUp()).thenReturn(true);
        controller.getChatState().setLoggedOn(true);

        final File file = mock(File.class);
        when(file.getName()).thenReturn("file.txt");
        final User user = mock(User.class);

        controller.sendFile(user, file);

        verify(networkMessages).sendFile(user, file);
    }

    @Test
    public void beforeNetworkCameUpShouldDoNothing() {
        controller.beforeNetworkCameUp();

        verifyZeroInteractions(networkService, networkMessages);
    }

    @Test
    public void registerNetworkConnectionListenerShouldUseNetworkService() {
        final NetworkConnectionListener listener = mock(NetworkConnectionListener.class);

        controller.registerNetworkConnectionListener(listener);

        verify(networkService).registerNetworkConnectionListener(listener);
    }

    @Test
    public void shutdownShouldStopThreadsAndShutdownTheMessageController() {
        controller.shutdown();

        verify(idleThread).stopThread();
        verify(dayTimer).stopTimer();
        verify(messageController).shutdown();
    }

    @Test
    public void removeUserShouldRemoveUserAndSetOffline() {
        final User user = new User("User1", 124);
        userList.add(user);

        assertEquals(1, userList.indexOf(user));
        assertTrue(user.isOnline());

        controller.removeUser(user, "Bla bla");

        assertEquals(-1, userList.indexOf(user));
        assertFalse(user.isOnline());
    }

    @Test
    public void removeUserShouldCancelFileTransfers() {
        final User user = new User("User1", 124);
        userList.add(user);

        final FileReceiver fileReceiver1 = mock(FileReceiver.class);
        final FileReceiver fileReceiver2 = mock(FileReceiver.class);
        when(transferList.getFileReceivers(user)).thenReturn(Arrays.asList(fileReceiver1, fileReceiver2));

        final FileSender fileSender1 = mock(FileSender.class);
        final FileSender fileSender2 = mock(FileSender.class);
        when(transferList.getFileSenders(user)).thenReturn(Arrays.asList(fileSender1, fileSender2));

        controller.removeUser(user, "Bla bla");

        verify(fileReceiver1).cancel();
        verify(fileReceiver2).cancel();
        verify(fileSender1).cancel();
        verify(fileSender2).cancel();

        verify(transferList).removeFileReceiver(fileReceiver1);
        verify(transferList).removeFileReceiver(fileReceiver2);
        verify(transferList).removeFileSender(fileSender1);
        verify(transferList).removeFileSender(fileSender2);
    }

    @Test
    public void removeUserShouldLogOffPrivateChat() {
        final User user = new User("User1", 124);
        userList.add(user);

        final PrivateChatWindow privchat = mock(PrivateChatWindow.class);
        user.setPrivchat(privchat);

        controller.removeUser(user, "Bla bla");

        verify(messageController).showPrivateSystemMessage(user, "Bla bla");
        verify(privchat).setLoggedOff();
    }

    @Test
    public void removeUserShouldClosePrivateChatLogger() {
        final User user = new User("User1", 124);
        userList.add(user);

        final ChatLogger chatLogger = mock(ChatLogger.class);
        user.setPrivateChatLogger(chatLogger);

        controller.removeUser(user, "Bla bla");

        verify(chatLogger).close();
    }

    @Test
    public void logOffShouldClosePrivateChatLoggersWhenRemoveUsersIsTrue() {
        final User user1 = new User("User1", 124);
        final ChatLogger chatLogger1 = mock(ChatLogger.class);
        user1.setPrivateChatLogger(chatLogger1);

        final User user2 = new User("User2", 125);
        final ChatLogger chatLogger2 = mock(ChatLogger.class);
        user2.setPrivateChatLogger(chatLogger2);

        userList.add(user1);
        userList.add(user2);

        controller.logOff(true);

        verify(chatLogger1).close();
        verify(chatLogger2).close();
    }

    @Test
    public void logOffShouldClosePrivateChatLoggersWhenRemoveUsersIsFalse() {
        final User user1 = new User("User1", 124);
        final ChatLogger chatLogger1 = mock(ChatLogger.class);
        user1.setPrivateChatLogger(chatLogger1);

        final User user2 = new User("User2", 125);
        final ChatLogger chatLogger2 = mock(ChatLogger.class);
        user2.setPrivateChatLogger(chatLogger2);

        userList.add(user1);
        userList.add(user2);

        controller.logOff(false);

        verify(chatLogger1).close();
        verify(chatLogger2).close();
    }

    @Test
    public void logOffShouldCancelFileTransfersWhenRemoveUsersIsTrue() {
        final User user1 = new User("User1", 124);
        userList.add(user1);

        final FileReceiver fileReceiver1 = mock(FileReceiver.class);
        when(transferList.getFileReceivers(user1)).thenReturn(Arrays.asList(fileReceiver1));

        final User user2 = new User("User2", 125);
        userList.add(user2);

        final FileSender fileSender1 = mock(FileSender.class);
        when(transferList.getFileSenders(user2)).thenReturn(Arrays.asList(fileSender1));

        controller.logOff(true);

        verify(fileReceiver1).cancel();
        verify(fileSender1).cancel();

        verify(transferList).removeFileReceiver(fileReceiver1);
        verify(transferList).removeFileSender(fileSender1);
    }

    @Test
    public void logOffShouldCancelFileTransfersWhenRemoveUsersIsFalse() {
        final User user1 = new User("User1", 124);
        userList.add(user1);

        final FileReceiver fileReceiver1 = mock(FileReceiver.class);
        when(transferList.getFileReceivers(user1)).thenReturn(Arrays.asList(fileReceiver1));

        final User user2 = new User("User2", 125);
        userList.add(user2);

        final FileSender fileSender1 = mock(FileSender.class);
        when(transferList.getFileSenders(user2)).thenReturn(Arrays.asList(fileSender1));

        controller.logOff(false);

        verify(fileReceiver1).cancel();
        verify(fileSender1).cancel();

        verify(transferList).removeFileReceiver(fileReceiver1);
        verify(transferList).removeFileSender(fileSender1);
    }

    @Test
    public void startShouldStartThreadsAndShowWelcomeMessages() {
        when(dateTools.currentDateToString(anyString())).thenReturn("X-mass");

        controller.start();

        verify(dayTimer).startTimer();
        verify(idleThread).start();

        verify(messageController).showSystemMessage("Welcome to KouChat v" + Constants.APP_VERSION + "!");
        verify(messageController).showSystemMessage("Today is X-mass");
        verify(dateTools).currentDateToString("EEEE, d MMMM yyyy");
    }

    @Test
    public void changeAwayStatusShouldThrowExceptionIfMeAndNotLoggedOn() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not change away mode without being connected");

        controller.changeAwayStatus(me.getCode(), true, "something");
    }

    @Test
    public void changeAwayStatusShouldThrowExceptionIfMessageIsTooLong() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not set an away message with more than 450 bytes");

        when(controller.isLoggedOn()).thenReturn(true);

        controller.changeAwayStatus(me.getCode(), true, createStringOfSize(451));
    }

    @Test
    public void changeAwayStatusToAwayWithMeShouldSendAwayMessageAndChangeStatus() throws CommandException {
        when(controller.isLoggedOn()).thenReturn(true);
        final UserListController userListController =
                TestUtils.setFieldValueWithMock(controller, "userListController", UserListController.class);

        controller.changeAwayStatus(me.getCode(), true, "this is the message");

        verify(networkMessages).sendAwayMessage("this is the message");
        verify(userListController).changeAwayStatus(me.getCode(), true, "this is the message");
    }

    @Test
    public void changeAwayStatusToAwayShouldTrimAwayMessage() throws CommandException {
        when(controller.isLoggedOn()).thenReturn(true);
        final UserListController userListController =
                TestUtils.setFieldValueWithMock(controller, "userListController", UserListController.class);

        controller.changeAwayStatus(me.getCode(), true, "    trim me    ");

        verify(networkMessages).sendAwayMessage("trim me");
        verify(userListController).changeAwayStatus(me.getCode(), true, "trim me");
    }

    @Test
    public void changeAwayStatusToBackWithMeShouldSendBackMessageAndChangeStatus() throws CommandException {
        when(controller.isLoggedOn()).thenReturn(true);
        final UserListController userListController =
                TestUtils.setFieldValueWithMock(controller, "userListController", UserListController.class);

        controller.changeAwayStatus(me.getCode(), false, "");

        verify(networkMessages).sendBackMessage();
        verify(userListController).changeAwayStatus(me.getCode(), false, "");
    }

    @Test
    public void changeAwayStatusToAwayWithSomeoneElseShouldOnlyChangeStatus() throws CommandException {
        final UserListController userListController =
                TestUtils.setFieldValueWithMock(controller, "userListController", UserListController.class);

        controller.changeAwayStatus(654, true, "Away message");

        verifyZeroInteractions(networkMessages);
        verify(userListController).changeAwayStatus(654, true, "Away message");
    }

    @Test
    public void changeAwayStatusToBackWithSomeoneElseShouldOnlyChangeStatus() throws CommandException {
        final UserListController userListController =
                TestUtils.setFieldValueWithMock(controller, "userListController", UserListController.class);

        controller.changeAwayStatus(654, false, "");

        verifyZeroInteractions(networkMessages);
        verify(userListController).changeAwayStatus(654, false, "");
    }

    @Test
    public void goAwayShouldChangeAwayStatusAndUpdateUserInterfaceAndShowSystemMessage() throws CommandException {
        when(controller.isLoggedOn()).thenReturn(true);

        controller.goAway("The away message");

        verify(controller).changeAwayStatus(me.getCode(), true, "The away message");
        verify(ui).changeAway(true);
        verify(messageController).showSystemMessage("You went away: The away message");
    }

    @Test
    public void goAwayShouldThrowExceptionIfAwayMessageIsNull() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not go away without an away message");

        controller.goAway(null);
    }

    @Test
    public void goAwayShouldThrowExceptionIfAwayMessageIsEmpty() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not go away without an away message");

        controller.goAway(" ");
    }

    @Test
    public void comeBackShouldChangeAwayStatusAndUpdateUserInterfaceAndShowSystemMessage() throws CommandException {
        when(controller.isLoggedOn()).thenReturn(true);

        controller.comeBack();

        verify(controller).changeAwayStatus(me.getCode(), false, "");
        verify(ui).changeAway(false);
        verify(messageController).showSystemMessage("You came back");
    }

    @Test
    public void changeMyNickShouldThrowExceptionIfAway() throws CommandException {
        expectedException.expect(CommandException.class);
        expectedException.expectMessage("You can not change nick while away");

        me.setAway(true);

        controller.changeMyNick("nah");
    }

    @Test
    public void changeMyNickShouldSendMessageAndChangeNickAndSave() throws CommandException {
        controller.changeMyNick("kelly");

        verify(networkMessages).sendNickMessage("kelly");
        verify(controller).changeNick(me.getCode(), "kelly");
        verify(controller).saveSettings();
    }

    @Test
    public void saveSettingsShouldUseSettingsSaver() {
        controller.saveSettings();

        verify(settingsSaver).saveSettings();
    }

    private String createStringOfSize(final int size) {
        final StringBuilder sb = new StringBuilder(size);

        for (int i = 0; i < size; i++) {
            sb.append("a");
        }

        return sb.toString();
    }
}
