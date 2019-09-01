
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

package net.usikkert.kouchat.android.chatwindow;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;

import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.filetransfer.AndroidFileTransferListener;
import net.usikkert.kouchat.android.filetransfer.AndroidFileUtils;
import net.usikkert.kouchat.android.notification.NotificationService;
import net.usikkert.kouchat.android.settings.AndroidSettings;
import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.event.NetworkConnectionListener;
import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.ChatLogger;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.CommandParser;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.SortedUserList;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.ui.PrivateChatWindow;
import net.usikkert.kouchat.util.Sleeper;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;

import android.content.Context;

/**
 * Test of {@link AndroidUserInterface}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class AndroidUserInterfaceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AndroidUserInterface androidUserInterface;

    private MainChatController mainChatController;
    private Controller controller;
    private NotificationService notificationService;
    private MessageStylerWithHistory messageStyler;
    private MessageController msgController;
    private User me;
    private User testUser;
    private CommandParser commandParser;
    private TransferList transferList;
    private AndroidFileUtils androidFileUtils;
    private Sleeper sleeper;
    private AndroidSettings settings;

    @Before
    public void setUp() {
        settings = mock(AndroidSettings.class);
        me = new User("Me", 1234);
        when(settings.getMe()).thenReturn(me);
        testUser = new User("TestUser", 1235);

        final Context context = Robolectric.application;
        notificationService = mock(NotificationService.class);

        androidUserInterface = spy(new AndroidUserInterface(context, settings, notificationService));

        controller = TestUtils.setFieldValueWithMock(androidUserInterface, "controller", Controller.class);
        messageStyler = TestUtils.setFieldValueWithMock(androidUserInterface, "messageStyler", MessageStylerWithHistory.class);
        msgController = TestUtils.setFieldValueWithMock(androidUserInterface, "msgController", MessageController.class);
        commandParser = TestUtils.setFieldValueWithMock(androidUserInterface, "commandParser", CommandParser.class);
        transferList = TestUtils.setFieldValueWithMock(androidUserInterface, "transferList", TransferList.class);
        androidFileUtils = TestUtils.setFieldValueWithMock(androidUserInterface, "androidFileUtils", AndroidFileUtils.class);
        sleeper = TestUtils.setFieldValueWithMock(androidUserInterface, "sleeper", Sleeper.class);

        mainChatController = mock(MainChatController.class);
        androidUserInterface.registerMainChatController(mainChatController);

        // Reset mocks used in the constructor, to start clean in the tests
        reset(mainChatController, messageStyler);

        when(controller.isConnected()).thenReturn(true);
        when(controller.isLoggedOn()).thenReturn(true);
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new AndroidUserInterface(null, mock(AndroidSettings.class), mock(NotificationService.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new AndroidUserInterface(mock(Context.class), null, mock(NotificationService.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfNotificationServiceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("NotificationService can not be null");

        new AndroidUserInterface(mock(Context.class), mock(AndroidSettings.class), null);
    }

    @Test
    public void showTopicShouldNotSetTitleAndSubtitleOfMainChatIfMainChatIsMissing() {
        androidUserInterface.unregisterMainChatController();

        androidUserInterface.showTopic();

        verify(mainChatController, never()).updateTitleAndSubtitle(anyString(), anyString());
    }

    @Test
    public void showTopicShouldOnlyUpdateTheTitleWhenNoTopicIsSet() {
        when(controller.getTopic()).thenReturn(new Topic());

        androidUserInterface.showTopic();

        verify(mainChatController).updateTitleAndSubtitle("Me - KouChat", null);
    }

    @Test
    public void showTopicShouldUpdateBothTitleAndSubtitleWhenATopicIsSet() {
        when(controller.getTopic()).thenReturn(new Topic("This rocks!", "OtherGuy", System.currentTimeMillis()));

        androidUserInterface.showTopic();

        verify(mainChatController).updateTitleAndSubtitle("Me - KouChat", "This rocks! - OtherGuy");
    }

    @Test
    public void showTopicShouldIncludeAwayIfCurrentlyAway() {
        when(controller.getTopic()).thenReturn(new Topic());
        me.setAway(true);

        androidUserInterface.showTopic();

        verify(mainChatController).updateTitleAndSubtitle("Me (Away) - KouChat", null);
    }

    @Test
    public void showTopicShouldUpdateBothTitleAndSubtitleWhenATopicIsSetAndAway() {
        when(controller.getTopic()).thenReturn(new Topic("This rocks!", "OtherGuy", System.currentTimeMillis()));
        me.setAway(true);

        androidUserInterface.showTopic();

        verify(mainChatController).updateTitleAndSubtitle("Me (Away) - KouChat", "This rocks! - OtherGuy");
    }

    @Test
    public void showTopicShouldIncludeNotConnectedIfNotConnectedAndNotLoggedOn() {
        when(controller.isConnected()).thenReturn(false);
        when(controller.isLoggedOn()).thenReturn(false);

        androidUserInterface.showTopic();

        verify(mainChatController).updateTitleAndSubtitle("Me - Not connected - KouChat", null);
    }

    @Test
    public void showTopicShouldIncludeNotConnectedIfNotConnectedAndNotLoggedOnEvenWhenAwayAndWithTopic() {
        when(controller.isConnected()).thenReturn(false);
        when(controller.isLoggedOn()).thenReturn(false);

        when(controller.getTopic()).thenReturn(new Topic("This rocks!", "OtherGuy", System.currentTimeMillis()));
        me.setAway(true);

        androidUserInterface.showTopic();

        verify(mainChatController).updateTitleAndSubtitle("Me - Not connected - KouChat", null);
    }

    @Test
    public void showTopicShouldIncludeConnectionLostIfNotConnectedAndLoggedOn() {
        when(controller.isConnected()).thenReturn(false);
        when(controller.isLoggedOn()).thenReturn(true);

        androidUserInterface.showTopic();

        verify(mainChatController).updateTitleAndSubtitle("Me - Connection lost - KouChat", null);
    }

    @Test
    public void showTopicShouldIncludeConnectionLostIfNotConnectedAndLoggedOnEvenWhenAwayAndWithTopic() {
        when(controller.isConnected()).thenReturn(false);
        when(controller.isLoggedOn()).thenReturn(true);

        when(controller.getTopic()).thenReturn(new Topic("This rocks!", "OtherGuy", System.currentTimeMillis()));
        me.setAway(true);

        androidUserInterface.showTopic();

        verify(mainChatController).updateTitleAndSubtitle("Me - Connection lost - KouChat", null);
    }

    @Test
    public void updateMeWritingShouldPassTrueToController() {
        androidUserInterface.updateMeWriting(true);

        verify(controller).updateMeWriting(true);
    }

    @Test
    public void updateMeWritingShouldPassFalseToController() {
        androidUserInterface.updateMeWriting(false);

        verify(controller).updateMeWriting(false);
    }

    @Test
    public void notifyMessageArrivedShouldAddNotificationIfMainChatNotVisible() {
        assertFalse(mainChatController.isVisible());

        androidUserInterface.notifyMessageArrived(null);

        verify(notificationService).notifyNewMainChatMessage();
    }

    @Test
    public void notifyMessageArrivedShouldNotAddNotificationIfMainChatVisible() {
        when(mainChatController.isVisible()).thenReturn(true);

        androidUserInterface.notifyMessageArrived(null);

        verifyZeroInteractions(notificationService);
    }

    @Test
    public void notifyPrivateMessageArrivedShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        androidUserInterface.notifyPrivateMessageArrived(null);
    }

    @Test
    public void notifyPrivateMessageArrivedShouldThrowExceptionIfPrivateChatIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Private chat can not be null");

        androidUserInterface.notifyPrivateMessageArrived(testUser);
    }

    @Test
    public void notifyPrivateMessageArrivedShouldAddNotificationIfNotMainChatOrPrivateChatWithSpecifiedUserIsVisible() {
        final AndroidPrivateChatWindow privchat = mock(AndroidPrivateChatWindow.class);
        assertFalse(privchat.isVisible());

        testUser.setPrivchat(privchat);

        assertFalse(mainChatController.isVisible());

        androidUserInterface.notifyPrivateMessageArrived(testUser);

        verify(notificationService).notifyNewPrivateChatMessage(testUser);
    }

    @Test
    public void notifyPrivateMessageArrivedShouldNotAddNotificationIfMainChatIsVisible() {
        final AndroidPrivateChatWindow privchat = mock(AndroidPrivateChatWindow.class);
        assertFalse(privchat.isVisible());

        testUser.setPrivchat(privchat);

        when(mainChatController.isVisible()).thenReturn(true);

        androidUserInterface.notifyPrivateMessageArrived(testUser);

        verifyZeroInteractions(notificationService);
    }

    @Test
    public void notifyPrivateMessageArrivedShouldNotAddNotificationIfPrivateChatWithSpecifiedUserIsVisible() {
        final AndroidPrivateChatWindow privchat = mock(AndroidPrivateChatWindow.class);
        when(privchat.isVisible()).thenReturn(true);

        testUser.setPrivchat(privchat);

        assertFalse(mainChatController.isVisible());

        androidUserInterface.notifyPrivateMessageArrived(testUser);

        verifyZeroInteractions(notificationService);
    }

    @Test
    public void registerMainChatControllerShouldThrowExceptionIfControllerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("MainChatController can not be null");

        androidUserInterface.registerMainChatController(null);
    }

    @Test
    public void registerMainChatControllerShouldSetTheField() {
        TestUtils.setFieldValue(androidUserInterface, "mainChatController", null);

        androidUserInterface.registerMainChatController(mainChatController);

        final MainChatController mainChatControllerFromUI =
                TestUtils.getFieldValue(androidUserInterface, MainChatController.class, "mainChatController");

        assertSame(mainChatController, mainChatControllerFromUI);
    }

    @Test
    public void registerMainChatControllerShouldUpdateChatFromHistory() {
        TestUtils.setFieldValue(androidUserInterface, "mainChatController", null);

        when(messageStyler.getHistory()).thenReturn("History");

        androidUserInterface.registerMainChatController(mainChatController);

        verify(mainChatController).updateChat("History");
        verify(messageStyler).getHistory();
    }

    @Test
    public void unregisterMainChatControllerShouldSetControllerToNull() {
        final String fieldName = "mainChatController";
        final Class<MainChatController> fieldClass = MainChatController.class;

        assertNotNull(TestUtils.getFieldValue(androidUserInterface, fieldClass, fieldName));
        androidUserInterface.unregisterMainChatController();
        assertNull(TestUtils.getFieldValue(androidUserInterface, fieldClass, fieldName));
    }

    @Test
    public void isVisibleAndIsFocusedShouldBeFalseIfMainChatControllerIsNull() {
        androidUserInterface.unregisterMainChatController();

        assertFalse(androidUserInterface.isVisible());
        assertFalse(androidUserInterface.isFocused());
    }

    @Test
    public void isVisibleAndIsFocusedShouldBeFalseIfMainChatControllerIsNotVisible() {
        assertFalse(mainChatController.isVisible());

        assertFalse(androidUserInterface.isVisible());
        assertFalse(androidUserInterface.isFocused());
    }

    @Test
    public void isVisibleAndIsFocusedShouldBeTrueIfMainChatControllerIsVisible() {
        when(mainChatController.isVisible()).thenReturn(true);

        assertTrue(androidUserInterface.isVisible());
        assertTrue(androidUserInterface.isFocused());
    }

    @Test
    public void resetAllNotificationsShouldUseTheNotificationService() {
        androidUserInterface.resetAllNotifications();

        verify(notificationService).resetAllNotifications();
    }

    @Test
    public void activatedPrivChatShouldResetNotificationForTheUser() {
        androidUserInterface.activatedPrivChat(testUser);

        verify(notificationService).resetPrivateChatNotification(testUser);
    }

    @Test
    public void activatedPrivChatShouldResetNewPrivateMessageStatusIfCurrentlyTrue() {
        testUser.setNewPrivMsg(true);

        androidUserInterface.activatedPrivChat(testUser);

        assertFalse(testUser.isNewPrivMsg());
        verify(controller).changeNewMessage(1235, false);
    }

    @Test
    public void activatedPrivChatShouldNotResetNewPrivateMessageStatusIfCurrentlyFalse() {
        androidUserInterface.activatedPrivChat(testUser);

        assertFalse(testUser.isNewPrivMsg());
        verifyZeroInteractions(controller);
    }

    @Test
    public void logOnShouldStartAndLogOnUsingTheController() {
        androidUserInterface.logOn();

        verify(controller).start();
        verify(controller).logOn();
    }

    @Test
    public void logOffShouldUseTheController() {
        androidUserInterface.logOff();

        verify(controller).logOff(false);
        verify(controller).shutdown();
    }

    @Test
    public void createPrivChatShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        androidUserInterface.createPrivChat(null);
    }

    @Test
    public void createPrivChatShouldSetAndroidPrivateChatWindowIfNull() {
        androidUserInterface.createPrivChat(testUser);

        assertNotNull(testUser.getPrivchat());
        assertEquals(AndroidPrivateChatWindow.class, testUser.getPrivchat().getClass());
    }

    @Test
    public void createPrivChatShouldNotSetAndroidPrivateChatWindowIfAlreadySet() {
        final PrivateChatWindow privchat = mock(PrivateChatWindow.class);
        testUser.setPrivchat(privchat);

        androidUserInterface.createPrivChat(testUser);

        assertSame(privchat, testUser.getPrivchat());
    }

    @Test
    public void createPrivChatShouldSetChatLoggerIfNull() {
        androidUserInterface.createPrivChat(testUser);

        assertNotNull(testUser.getPrivateChatLogger());
    }

    @Test
    public void createPrivChatShouldNotSetChatLoggerIfAlreadySet() {
        final ChatLogger chatLogger = mock(ChatLogger.class);
        testUser.setPrivateChatLogger(chatLogger);

        androidUserInterface.createPrivChat(testUser);

        assertSame(chatLogger, testUser.getPrivateChatLogger());
    }

    @Test
    public void appendToChatShouldThrowExceptionIfMessageIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Message can not be empty");

        androidUserInterface.appendToChat(null, 0);
    }

    @Test
    public void appendToChatShouldThrowExceptionIfMessageIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Message can not be empty");

        androidUserInterface.appendToChat(" ", 0);
    }

    @Test
    public void appendToChatShouldAppendToHistoryIfControllerIsNull() {
        TestUtils.setFieldValue(androidUserInterface, "mainChatController", null);

        androidUserInterface.appendToChat("Message", 500);

        verify(messageStyler).styleAndAppend("Message", 500);
        verifyZeroInteractions(mainChatController);
    }

    @Test
    public void appendToChatShouldAppendToHistoryAndController() {
        when(messageStyler.styleAndAppend(anyString(), anyInt())).thenReturn("Styled message");

        androidUserInterface.appendToChat("Message", 500);

        verify(messageStyler).styleAndAppend("Message", 500);
        verify(mainChatController).appendToChat("Styled message");
    }

    @Test
    public void sendMessageShouldThrowExceptionIfMessageIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Message can not be empty");

        androidUserInterface.sendMessage(null);
    }

    @Test
    public void sendMessageShouldThrowExceptionIfMessageIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Message can not be empty");

        androidUserInterface.sendMessage(" ");
    }

    @Test
    public void sendMessageShouldUseTheControllerAndMessageController() throws CommandException {
        androidUserInterface.sendMessage("Hello there");

        verify(controller).sendChatMessage("Hello there");
        verify(msgController).showOwnMessage("Hello there");
    }

    @Test
    public void sendMessageShouldShowSystemMessageIfControllerThrowsException() throws CommandException {
        doThrow(new CommandException("This failed")).when(controller).sendChatMessage(anyString());

        androidUserInterface.sendMessage("Fail now");

        verify(controller).sendChatMessage("Fail now");
        verify(msgController).showSystemMessage("This failed");
        verifyNoMoreInteractions(msgController);
    }

    @Test
    public void sendPrivateMessageShouldThrowExceptionIfMessageIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Private message can not be empty");

        androidUserInterface.sendPrivateMessage(null, testUser);
    }

    @Test
    public void sendPrivateMessageShouldThrowExceptionIfMessageIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Private message can not be empty");

        androidUserInterface.sendPrivateMessage(" ", testUser);
    }

    @Test
    public void sendPrivateMessageShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        androidUserInterface.sendPrivateMessage("Hello", null);
    }

    @Test
    public void sendPrivateMessageShouldUseTheControllerAndMessageController() throws CommandException {
        androidUserInterface.sendPrivateMessage("Hello there", testUser);

        verify(controller).sendPrivateMessage("Hello there", testUser);
        verify(msgController).showPrivateOwnMessage(testUser, "Hello there");
    }

    @Test
    public void sendPrivateMessageShouldShowSystemMessageIfControllerThrowsException() throws CommandException {
        doThrow(new CommandException("This failed")).when(controller).sendPrivateMessage(anyString(), any(User.class));

        androidUserInterface.sendPrivateMessage("Fail now", testUser);

        verify(controller).sendPrivateMessage("Fail now", testUser);
        verify(msgController).showPrivateSystemMessage(testUser, "This failed");
        verifyNoMoreInteractions(msgController);
    }

    @Test
    public void changeNickNameShouldReturnFalseIfNewNickIsSameAsOld() {
        assertFalse(androidUserInterface.changeNickName("Me"));
        assertFalse(androidUserInterface.changeNickName(" Me "));

        verifyZeroInteractions(controller, msgController, mainChatController);
        assertEquals(0, ShadowToast.shownToastCount());
    }

    @Test
    public void changeNickNameShouldReturnFalseAndShowToastIfNewNickIsInvalid() {
        assertFalse(androidUserInterface.changeNickName("123456789012345"));

        verifyZeroInteractions(controller, msgController, mainChatController);
        assertEquals("Invalid nick name", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void changeNickNameShouldReturnFalseAndShowToastIfNewNickIsInUse() {
        when(controller.isNickInUse("Kou")).thenReturn(true);

        assertFalse(androidUserInterface.changeNickName("Kou"));

        verifyZeroInteractions(msgController, mainChatController);
        verify(controller).isNickInUse("Kou");
        verifyNoMoreInteractions(controller);
        assertEquals("The nick name is in use by someone else.", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void changeNickNameShouldReturnFalseAndShowToastIfOperationFails() throws CommandException {
        doThrow(new CommandException("Cant change nick")).when(controller).changeMyNick("Kou");

        assertFalse(androidUserInterface.changeNickName("Kou"));

        verify(controller).isNickInUse("Kou");
        verify(controller).changeMyNick("Kou");
        verifyNoMoreInteractions(controller);
        verifyZeroInteractions(msgController, mainChatController);
        assertEquals("Cant change nick", ShadowToast.getTextOfLatestToast());
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void changeNickNameShouldThrowRuntimeExceptionOnUnexpectedErrors() throws CommandException {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Something went wrong changing nick name to Kou");

        doThrow(new IllegalArgumentException("Something unexpected")).when(controller).changeMyNick("Kou");

        // In the test it fails on asyncTask.execute(), while in reality it fails on asyncTask.get()
        androidUserInterface.changeNickName("Kou");
    }

    @Test
    public void changeNickNameShouldReturnTrueAndShowSystemMessageAndUpdateTitleAndSubtitleIfEverythingOK() throws CommandException {
        when(controller.getTopic()).thenReturn(new Topic());
        doNothing().when(androidUserInterface).showTopic();

        // Makes sure the mocked controller changes the nick name like the real implementation would,
        // so the topic and system message looks more realistic. Would be "Me" instead of "Kou" if not
        doAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocation) {
                me.setNick(invocation.getArguments()[0].toString());
                return null;
            }
        }).when(controller).changeMyNick(anyString());

        assertTrue(androidUserInterface.changeNickName("Kou"));

        verify(controller).isNickInUse("Kou");
        verify(controller).changeMyNick("Kou");
        verifyNoMoreInteractions(controller);

        verify(msgController).showSystemMessage("You changed nick to Kou");
        verify(androidUserInterface).showTopic();
    }

    @Test
    public void getUserShouldCallController() {
        when(controller.getUser(1235)).thenReturn(testUser);

        assertSame(testUser, androidUserInterface.getUser(1235));

        verify(controller).getUser(1235);
    }

    @Test
    public void getUserListShouldReturnCorrectUserList() {
        assertEquals(SortedUserList.class, androidUserInterface.getUserList().getClass());
    }

    @Test
    public void getMeShouldReturnCorrectUser() {
        assertSame(me, androidUserInterface.getMe());
    }

    @Test
    public void askFileSaveShouldReturnTrue() {
        assertTrue(androidUserInterface.askFileSave(null, null, null));
    }

    @Test
    public void showFileSaveShouldShowFileTransferNotificationThenWaitForCancelFromUserThenCancelNotification() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);

        when(fileReceiver.isCanceled()).thenReturn(false, false, true);

        androidUserInterface.showFileSave(fileReceiver);

        verify(notificationService).notifyNewFileTransfer(fileReceiver);
        verify(sleeper, times(2)).sleep(500);
        verify(notificationService).cancelFileTransferNotification(fileReceiver);
    }

    @Test
    public void showFileSaveShouldShowFileTransferNotificationThenWaitForAcceptFromUserThenCancelNotification() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);

        when(fileReceiver.isAccepted()).thenReturn(false, false, true);

        androidUserInterface.showFileSave(fileReceiver);

        verify(notificationService).notifyNewFileTransfer(fileReceiver);
        verify(sleeper, times(2)).sleep(500);
        verify(notificationService).cancelFileTransferNotification(fileReceiver);
    }

    @Test
    public void showFileSaveShouldShowFileTransferNotificationThenWaitForRejectFromUserThenCancelNotification() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);

        when(fileReceiver.isRejected()).thenReturn(false, false, true);

        androidUserInterface.showFileSave(fileReceiver);

        verify(notificationService).notifyNewFileTransfer(fileReceiver);
        verify(sleeper, times(2)).sleep(500);
        verify(notificationService).cancelFileTransferNotification(fileReceiver);
    }

    @Test
    public void showTransferForFileReceiverShouldThrowExceptionIfFileSenderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("FileReceiver can not be null");

        androidUserInterface.showTransfer((FileReceiver) null);
    }

    @Test
    public void showTransferForFileReceiverShouldRegisterListener() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        final ArgumentCaptor<FileTransferListener> argumentCaptor = ArgumentCaptor.forClass(FileTransferListener.class);

        androidUserInterface.showTransfer(fileReceiver);

        verify(fileReceiver).registerListener(argumentCaptor.capture());

        final FileTransferListener listener = argumentCaptor.getValue();
        assertEquals(AndroidFileTransferListener.class, listener.getClass());
    }

    @Test
    public void showTransferForFileReceiverShouldFixFileNameAndPath() {
        final FileReceiver fileReceiver = mock(FileReceiver.class);
        when(fileReceiver.getFileName()).thenReturn("opensuse.iso");

        final File fixedFile = mock(File.class);
        when(androidFileUtils.createFileInDownloadsWithAvailableName("opensuse.iso")).thenReturn(fixedFile);

        androidUserInterface.showTransfer(fileReceiver);

        verify(fileReceiver).setFile(fixedFile);
    }

    @Test
    public void showTransferForFileSenderShouldThrowExceptionIfFileSenderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("FileSender can not be null");

        androidUserInterface.showTransfer((FileSender) null);
    }

    @Test
    public void showTransferForFileSenderShouldRegisterListener() {
        final FileSender fileSender = mock(FileSender.class);
        final ArgumentCaptor<FileTransferListener> argumentCaptor = ArgumentCaptor.forClass(FileTransferListener.class);

        androidUserInterface.showTransfer(fileSender);

        verify(fileSender).registerListener(argumentCaptor.capture());

        final FileTransferListener listener = argumentCaptor.getValue();
        assertEquals(AndroidFileTransferListener.class, listener.getClass());
    }

    @Test
    public void clearChatShouldDoNothing() {
        androidUserInterface.clearChat();
    }

    @Test
    public void changeAwayShouldUpdateTitleAndSubtitle() {
        when(controller.getTopic()).thenReturn(new Topic());

        androidUserInterface.changeAway(true);

        // Details are tested in the showTopic() tests
        verify(mainChatController).updateTitleAndSubtitle(anyString(), anyString());
    }

    @Test
    public void quitShouldDoNothing() {
        androidUserInterface.quit();
    }

    @Test
    public void sendFileShouldUseCommandParser() throws CommandException {
        final File file = mock(File.class);

        androidUserInterface.sendFile(testUser, file);

        verify(commandParser).sendFile(testUser, file);
        assertEquals(0, ShadowToast.shownToastCount());
    }

    @Test
    public void sendFileShouldShowToastIfCommandParserThrowsException() throws CommandException {
        doThrow(new CommandException("Cant send the file"))
                .when(commandParser).sendFile(any(User.class), any(File.class));

        final File file = mock(File.class);

        androidUserInterface.sendFile(testUser, file);

        verify(commandParser).sendFile(testUser, file);
        assertEquals("Cant send the file", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void registerNetworkConnectionListenerShouldUseController() {
        final NetworkConnectionListener listener = mock(NetworkConnectionListener.class);

        androidUserInterface.registerNetworkConnectionListener(listener);

        verify(controller).registerNetworkConnectionListener(listener);
    }

    @Test
    public void getFileReceiverShouldFindUserAndReturnFileReceiverFromTransferList() {
        when(controller.getUser(1235)).thenReturn(testUser);

        final FileReceiver originalFileReceiver = mock(FileReceiver.class);
        when(transferList.getFileReceiver(testUser, 100)).thenReturn(originalFileReceiver);

        final FileReceiver fileReceiver = androidUserInterface.getFileReceiver(1235, 100);

        assertSame(originalFileReceiver, fileReceiver);
        verify(transferList).getFileReceiver(testUser, 100);
        verify(controller).getUser(1235);
    }

    @Test
    public void getSettingsShouldReturnSettingsFromConstructor() {
        assertSame(settings, androidUserInterface.getSettings());
    }

    @Test
    public void getTopicShouldReturnCurrentTopic() {
        when(controller.getTopic()).thenReturn(new Topic("Hey look at the topic", "me", 100));

        assertEquals("Hey look at the topic", androidUserInterface.getTopic());
    }

    @Test
    public void changeTopicShouldUseCommandParser() throws CommandException {
        androidUserInterface.changeTopic("Set this topic");

        verify(commandParser).fixTopic("Set this topic");
        assertEquals(0, ShadowToast.shownToastCount());
    }

    @Test
    public void changeTopicShouldShowToastOnException() throws CommandException {
        doThrow(new CommandException("Can't set topic")).when(commandParser).fixTopic(anyString());

        androidUserInterface.changeTopic("Don't set this topic");

        verify(commandParser).fixTopic("Don't set this topic");
        assertEquals("Can't set topic", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void isAwayShouldBeSameAsMe() {
        me.setAway(true);
        assertTrue(androidUserInterface.isAway());

        me.setAway(false);
        assertFalse(androidUserInterface.isAway());
    }

    @Test
    public void goAwayShouldUseTheController() throws CommandException {
        androidUserInterface.goAway("the message");

        verify(controller).goAway("the message");
        assertEquals(0, ShadowToast.shownToastCount());
    }

    @Test
    public void goAwayShouldShowToastOnException() throws CommandException {
        doThrow(new CommandException("Can't go away")).when(controller).goAway(anyString());

        androidUserInterface.goAway("Don't go away");

        verify(controller).goAway("Don't go away");
        assertEquals("Can't go away", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void comeBackShouldUseTheController() throws CommandException {
        androidUserInterface.comeBack();

        verify(controller).comeBack();
        assertEquals(0, ShadowToast.shownToastCount());
    }

    @Test
    public void comeBackShouldShowToastOnException() throws CommandException {
        doThrow(new CommandException("Can't come back")).when(controller).comeBack();

        androidUserInterface.comeBack();

        verify(controller).comeBack();
        assertEquals("Can't come back", ShadowToast.getTextOfLatestToast());
    }
}
