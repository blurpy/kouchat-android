
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

package net.usikkert.kouchat.android.notification;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.controller.ReceiveFileController;
import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowPendingIntent;

import android.app.Notification;
import android.app.NotificationManager;

/**
 * Test of {@link NotificationService}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class NotificationServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private NotificationService notificationService;

    private NotificationManager notificationManager;
    private ArgumentCaptor<Notification> argumentCaptor;
    private FileReceiver fileReceiver;

    @Before
    public void setUp() {
        notificationService = new NotificationService(Robolectric.application);

        notificationManager =
                TestUtils.setFieldValueWithMock(notificationService, "notificationManager", NotificationManager.class);

        argumentCaptor = ArgumentCaptor.forClass(Notification.class);
        fileReceiver = new FileReceiver(new User("Niles", 1234), new File("picture.png"), 0, 12);
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new NotificationService(null);
    }

    @Test
    public void createServiceNotificationShouldSetIconAndStartupMessage() {
        final Notification notification = notificationService.createServiceNotification();

        verifyThatRegularIconHasBeenSet(notification);
        assertEquals("KouChat is up and running", notification.tickerText);
    }

    @Test
    public void createServiceNotificationShouldFlagNotificationWithNoClearToAvoidSwipeToCancel() {
        final Notification notification = notificationService.createServiceNotification();

        assertEquals(Notification.FLAG_NO_CLEAR, notification.flags);
    }

    @Test
    public void createServiceNotificationShouldSetNotificationTextForTheDrawer() {
        final Notification notification = notificationService.createServiceNotification();
        verifyThatNotificationTextIsRunning(notification);
    }

    @Test
    public void createServiceNotificationShouldCreatePendingIntentForOpeningTheMainChat() {
        final Notification notification = notificationService.createServiceNotification();
        verifyThatPendingIntentOpensMainChat(notification);
    }

    @Test
    public void createServiceNotificationShouldNotSetAnyChatActivity() {
        notificationService.createServiceNotification();

        assertFalse(notificationService.isMainChatActivity());
        assertFalse(notificationService.isPrivateChatActivity());
    }

    @Test
    public void notifyNewMainChatMessageShouldSetActivityIcon() {
        notificationService.notifyNewMainChatMessage();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatActivityIconHasBeenSet(notification);
    }

    @Test
    public void notifyNewMainChatMessageShouldFlagNotificationWithNoClearToAvoidSwipeToCancel() {
        notificationService.notifyNewMainChatMessage();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        assertEquals(Notification.FLAG_NO_CLEAR, notification.flags);
    }

    @Test
    public void notifyNewMainChatMessageShouldSetNotificationTextForTheDrawer() {
        notificationService.notifyNewMainChatMessage();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatNotificationTextIsNewUnreadMessages(notification);
    }

    @Test
    public void notifyNewMainChatMessageShouldCreatePendingIntentForOpeningTheMainChat() {
        notificationService.notifyNewMainChatMessage();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatPendingIntentOpensMainChat(notification);
    }

    @Test
    public void notifyNewMainChatMessageShouldOnlySetMainChatActivityToTrue() {
        notificationService.notifyNewMainChatMessage();

        assertTrue(notificationService.isMainChatActivity());
        assertFalse(notificationService.isPrivateChatActivity());
    }

    @Test
    public void notifyNewPrivateChatMessageShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        notificationService.notifyNewPrivateChatMessage(null);
    }

    @Test
    public void notifyNewPrivateChatMessageShouldSetActivityIcon() {
        notificationService.notifyNewPrivateChatMessage(new User("Test", 1234));

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatActivityIconHasBeenSet(notification);
    }

    @Test
    public void notifyNewPrivateChatMessageShouldFlagNotificationWithNoClearToAvoidSwipeToCancel() {
        notificationService.notifyNewPrivateChatMessage(new User("Test", 1234));

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        assertEquals(Notification.FLAG_NO_CLEAR, notification.flags);
    }

    @Test
    public void notifyNewPrivateChatMessageShouldSetNotificationTextForTheDrawer() {
        notificationService.notifyNewPrivateChatMessage(new User("Test", 1234));

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatNotificationTextIsNewUnreadMessages(notification);
    }

    @Test
    public void notifyNewPrivateChatMessageShouldCreatePendingIntentForOpeningTheMainChat() {
        notificationService.notifyNewPrivateChatMessage(new User("Test", 1234));

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatPendingIntentOpensMainChat(notification);
    }

    @Test
    public void notifyNewPrivateChatMessageShouldOnlySetPrivateChatActivityToTrue() {
        notificationService.notifyNewPrivateChatMessage(new User("Test", 1234));

        assertFalse(notificationService.isMainChatActivity());
        assertTrue(notificationService.isPrivateChatActivity());
    }

    @Test
    public void resetAllNotificationsShouldSetRegularIcon() {
        notificationService.resetAllNotifications();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatRegularIconHasBeenSet(notification);
    }

    @Test
    public void resetAllNotificationsShouldResetNotificationTextForTheDrawer() {
        notificationService.resetAllNotifications();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatNotificationTextIsRunning(notification);
    }

    @Test
    public void resetAllNotificationsShouldCreatePendingIntentForOpeningTheMainChat() {
        notificationService.resetAllNotifications();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatPendingIntentOpensMainChat(notification);
    }

    @Test
    public void resetAllNotificationsShouldSetMainChatActivityToFalse() {
        TestUtils.setFieldValue(notificationService, "mainChatActivity", true);
        assertTrue(notificationService.isMainChatActivity());

        notificationService.resetAllNotifications();
        assertFalse(notificationService.isMainChatActivity());
    }

    @Test
    public void resetAllNotificationsShouldSetPrivateChatActivityToFalse() {
        final User user = new User("Test", 1234);
        final HashSet<User> users = new HashSet<User>();
        users.add(user);

        TestUtils.setFieldValue(notificationService, "privateChatActivityUsers", users);
        assertTrue(notificationService.isPrivateChatActivity());

        notificationService.resetAllNotifications();
        assertFalse(notificationService.isPrivateChatActivity());
    }

    @Test
    public void resetPrivateChatNotificationShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        notificationService.resetPrivateChatNotification(null);
    }

    @Test
    public void resetPrivateChatNotificationShouldSetRegularIcon() {
        notificationService.resetPrivateChatNotification(new User("Test", 1234));

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatRegularIconHasBeenSet(notification);
    }

    @Test
    public void resetPrivateChatNotificationShouldResetNotificationTextForTheDrawer() {
        notificationService.resetPrivateChatNotification(new User("Test", 1234));

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatNotificationTextIsRunning(notification);
    }

    @Test
    public void resetPrivateChatNotificationShouldCreatePendingIntentForOpeningTheMainChat() {
        notificationService.resetPrivateChatNotification(new User("Test", 1234));

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatPendingIntentOpensMainChat(notification);
    }

    @Test
    public void resetPrivateChatNotificationShouldNotTouchMainChatActivity() {
        TestUtils.setFieldValue(notificationService, "mainChatActivity", true);
        assertTrue(notificationService.isMainChatActivity());

        notificationService.resetPrivateChatNotification(new User("Test", 1234));
        assertTrue(notificationService.isMainChatActivity());
    }

    @Test
    public void resetPrivateChatNotificationShouldSetPrivateChatActivityToFalse() {
        final User user = new User("Test", 1234);
        final HashSet<User> users = new HashSet<User>();
        users.add(user);

        TestUtils.setFieldValue(notificationService, "privateChatActivityUsers", users);
        assertTrue(notificationService.isPrivateChatActivity());

        notificationService.resetPrivateChatNotification(user);
        assertFalse(notificationService.isPrivateChatActivity());
    }

    @Test
    public void resetPrivateChatNotificationShouldNotSendNotificationInThereIsMainChatActivity() {
        TestUtils.setFieldValue(notificationService, "mainChatActivity", true);
        assertTrue(notificationService.isMainChatActivity());
        assertFalse(notificationService.isPrivateChatActivity());

        notificationService.resetPrivateChatNotification(new User("Test", 1234));
        verifyZeroInteractions(notificationManager);

        TestUtils.setFieldValue(notificationService, "mainChatActivity", false);

        notificationService.resetPrivateChatNotification(new User("Test", 1234));
        verify(notificationManager).notify(anyInt(), any(Notification.class));
    }

    @Test
    public void resetPrivateChatNotificationShouldNotSendNotificationIfThereIsPrivateChatActivity() {
        final User user1 = new User("Test1", 1234);
        final User user2 = new User("Test2", 1235);
        final User user3 = new User("Test3", 1236);

        final HashSet<User> users = new HashSet<User>();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        TestUtils.setFieldValue(notificationService, "privateChatActivityUsers", users);
        assertTrue(notificationService.isPrivateChatActivity());
        assertFalse(notificationService.isMainChatActivity());

        notificationService.resetPrivateChatNotification(user1);
        verifyZeroInteractions(notificationManager);
        assertTrue(notificationService.isPrivateChatActivity());

        notificationService.resetPrivateChatNotification(user2);
        verifyZeroInteractions(notificationManager);
        assertTrue(notificationService.isPrivateChatActivity());

        notificationService.resetPrivateChatNotification(user3);
        verify(notificationManager).notify(anyInt(), any(Notification.class));
        assertFalse(notificationService.isPrivateChatActivity());
    }

    @Test
    public void resetPrivateChatNotificationShouldNotSendNotificationIfThereIsBothPrivateChatAndMainChatActivity() {
        final User user1 = new User("Test1", 1234);
        final User user2 = new User("Test2", 1235);

        final HashSet<User> users = new HashSet<User>();
        users.add(user1);
        users.add(user2);

        TestUtils.setFieldValue(notificationService, "privateChatActivityUsers", users);
        assertTrue(notificationService.isPrivateChatActivity());

        TestUtils.setFieldValue(notificationService, "mainChatActivity", true);
        assertTrue(notificationService.isMainChatActivity());

        notificationService.resetPrivateChatNotification(user1);

        verifyZeroInteractions(notificationManager);
        assertTrue(notificationService.isPrivateChatActivity());
        assertTrue(notificationService.isMainChatActivity());
    }

    @Test
    public void notifyNewFileTransferShouldThrowExceptionIfFileReceiverIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("FileReceiver can not be null");

        notificationService.notifyNewFileTransfer(null);
    }

    @Test
    public void notifyNewFileTransferShouldSetActivityIconAndStartupMessage() {
        notificationService.notifyNewFileTransfer(fileReceiver);

        verify(notificationManager).notify(eq(10012), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        assertEquals(R.drawable.ic_stat_notify_activity, notification.icon);
        assertEquals("New file transfer request", notification.tickerText);
    }

    @Test
    public void notifyNewFileTransferShouldSetNotificationTextForTheDrawer() {
        notificationService.notifyNewFileTransfer(fileReceiver);

        verify(notificationManager).notify(eq(10012), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        final ShadowNotification.LatestEventInfo latestEventInfo = getLatestEventInfo(notification);

        assertEquals("File transfer from Niles", latestEventInfo.getContentTitle());
        assertEquals("picture.png", latestEventInfo.getContentText());
    }

    @Test
    public void notifyNewFileTransferShouldCreatePendingIntentForOpeningTheReceiveFileController() {
        notificationService.notifyNewFileTransfer(fileReceiver);

        verify(notificationManager).notify(eq(10012), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        final ShadowNotification.LatestEventInfo latestEventInfo = getLatestEventInfo(notification);
        final ShadowIntent pendingIntent = getPendingIntent(latestEventInfo);

        assertEquals(ReceiveFileController.class, pendingIntent.getIntentClass());
        assertEquals(1234, pendingIntent.getIntExtra("userCode", -1));
        assertEquals(12, pendingIntent.getIntExtra("fileTransferId", -1));
        assertTrue(pendingIntent.getAction().matches("openReceiveFileDialog \\d{13}"));
    }

    @Test
    public void notifyNewFileTransferShouldFlagNotificationWithNoClearToAvoidSwipeToCancel() {
        notificationService.notifyNewFileTransfer(fileReceiver);

        verify(notificationManager).notify(eq(10012), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        assertEquals(Notification.FLAG_NO_CLEAR, notification.flags);
    }

    @Test
    public void notifyNewFileTransferShouldCreatePendingIntentWithUniqueRequestCodeToAvoidIntentExtrasFromBeingCached() {
        notificationService.notifyNewFileTransfer(fileReceiver);

        verify(notificationManager).notify(eq(10012), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        final ShadowNotification.LatestEventInfo latestEventInfo = getLatestEventInfo(notification);
        final ShadowPendingIntent shadowPendingIntent = Robolectric.shadowOf(latestEventInfo.getContentIntent());

        assertEquals(10012, shadowPendingIntent.getRequestCode());
    }

    @Test
    public void notifyNewFileTransferShouldAddFileTransferIdToCurrentFileTransfers() {
        final FileReceiver fileReceiver1 = createFileReceiver(101);
        final FileReceiver fileReceiver2 = createFileReceiver(102);
        final FileReceiver fileReceiver3 = createFileReceiver(103);

        assertTrue(notificationService.getCurrentFileTransferIds().isEmpty());

        notificationService.notifyNewFileTransfer(fileReceiver1);
        verifyCurrentFileTransferIds(101);

        notificationService.notifyNewFileTransfer(fileReceiver2);
        verifyCurrentFileTransferIds(101, 102);

        notificationService.notifyNewFileTransfer(fileReceiver3);
        verifyCurrentFileTransferIds(101, 102, 103);
    }

    @Test
    public void cancelFileTransferNotificationShouldThrowExceptionInFileReceiverIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("FileReceiver can not be null");

        notificationService.cancelFileTransferNotification(null);
    }

    @Test
    public void cancelFileTransferNotificationShouldCancelUsingFileReceiverId() {
        notificationService.cancelFileTransferNotification(fileReceiver);

        verify(notificationManager).cancel(10012);
    }

    @Test
    public void cancelFileTransferNotificationShouldRemoveFileTransferIdFromCurrentFileTransfers() {
        final FileReceiver fileReceiver1 = createFileReceiver(101);
        final FileReceiver fileReceiver2 = createFileReceiver(102);
        final FileReceiver fileReceiver3 = createFileReceiver(103);

        assertTrue(notificationService.getCurrentFileTransferIds().isEmpty());

        notificationService.notifyNewFileTransfer(fileReceiver1);
        notificationService.notifyNewFileTransfer(fileReceiver2);
        notificationService.notifyNewFileTransfer(fileReceiver3);

        verifyCurrentFileTransferIds(101, 102, 103);

        notificationService.cancelFileTransferNotification(fileReceiver2);
        verifyCurrentFileTransferIds(101, 103);

        notificationService.cancelFileTransferNotification(fileReceiver1);
        verifyCurrentFileTransferIds(103);

        notificationService.cancelFileTransferNotification(fileReceiver3);
        assertTrue(notificationService.getCurrentFileTransferIds().isEmpty());
    }

    @Test
    public void getCurrentFileTransferIdsShouldNotReturnAModifiableSet() {
        expectedException.expect(UnsupportedOperationException.class);

        final Set<Integer> set = notificationService.getCurrentFileTransferIds();

        set.add(10);
    }

    private void verifyThatNotificationTextIsRunning(final Notification notification) {
        final ShadowNotification.LatestEventInfo latestEventInfo = getLatestEventInfo(notification);

        assertEquals("KouChat", latestEventInfo.getContentTitle());
        assertEquals("Running", latestEventInfo.getContentText());
        assertEquals(R.string.notification_running, notificationService.getCurrentLatestInfoTextId());
    }

    private void verifyThatNotificationTextIsNewUnreadMessages(final Notification notification) {
        final ShadowNotification.LatestEventInfo latestEventInfo = getLatestEventInfo(notification);

        assertEquals("KouChat", latestEventInfo.getContentTitle());
        assertEquals("New unread messages", latestEventInfo.getContentText());
        assertEquals(R.string.notification_new_message, notificationService.getCurrentLatestInfoTextId());
    }

    private void verifyThatPendingIntentOpensMainChat(final Notification notification) {
        final ShadowNotification.LatestEventInfo latestEventInfo = getLatestEventInfo(notification);
        final ShadowIntent pendingIntent = getPendingIntent(latestEventInfo);

        assertEquals(MainChatController.class, pendingIntent.getIntentClass());
    }

    private void verifyThatRegularIconHasBeenSet(final Notification notification) {
        assertEquals(R.drawable.ic_stat_notify_default, notification.icon);
        assertEquals(R.drawable.ic_stat_notify_default, notificationService.getCurrentIconId());
    }

    private void verifyThatActivityIconHasBeenSet(final Notification notification) {
        assertEquals(R.drawable.ic_stat_notify_activity, notification.icon);
        assertEquals(R.drawable.ic_stat_notify_activity, notificationService.getCurrentIconId());
    }

    private ShadowNotification.LatestEventInfo getLatestEventInfo(final Notification notification) {
        final ShadowNotification shadowNotification = Robolectric.shadowOf(notification);
        return shadowNotification.getLatestEventInfo();
    }

    private ShadowIntent getPendingIntent(final ShadowNotification.LatestEventInfo latestEventInfo) {
        final ShadowPendingIntent shadowPendingIntent = Robolectric.shadowOf(latestEventInfo.getContentIntent());
        return Robolectric.shadowOf(shadowPendingIntent.getSavedIntent());
    }

    private FileReceiver createFileReceiver(final int fileTransferId) {
        return new FileReceiver(new User("Holly", 567), new File("nothing.png"), 0, fileTransferId);
    }

    private void verifyCurrentFileTransferIds(final int... fileTransferIds) {
        assertEquals(fileTransferIds.length, notificationService.getCurrentFileTransferIds().size());

        for (final int fileTransferId : fileTransferIds) {
            assertTrue(notificationService.getCurrentFileTransferIds().contains(fileTransferId));
        }
    }
}
