
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

package net.usikkert.kouchat.android.notification;

import android.app.Notification;
import android.app.NotificationManager;
import java.util.HashSet;
import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowPendingIntent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

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

    @Before
    public void setUp() {
        notificationService = new NotificationService(Robolectric.application.getApplicationContext());

        notificationManager = mock(NotificationManager.class);
        TestUtils.setFieldValue(notificationService, "notificationManager", notificationManager);
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
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.notifyNewMainChatMessage();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatActivityIconHasBeenSet(notification);
    }

    @Test
    public void notifyNewMainChatMessageShouldSetNotificationTextForTheDrawer() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.notifyNewMainChatMessage();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatNotificationTextIsNewUnreadMessages(notification);
    }

    @Test
    public void notifyNewMainChatMessageShouldCreatePendingIntentForOpeningTheMainChat() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

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
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.notifyNewPrivateChatMessage(new User("Test", 1234));

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatActivityIconHasBeenSet(notification);
    }

    @Test
    public void notifyNewPrivateChatMessageShouldSetNotificationTextForTheDrawer() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.notifyNewPrivateChatMessage(new User("Test", 1234));

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatNotificationTextIsNewUnreadMessages(notification);
    }

    @Test
    public void notifyNewPrivateChatMessageShouldCreatePendingIntentForOpeningTheMainChat() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

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
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.resetAllNotifications();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatRegularIconHasBeenSet(notification);
    }

    @Test
    public void resetAllNotificationsShouldResetNotificationTextForTheDrawer() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.resetAllNotifications();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatNotificationTextIsRunning(notification);
    }

    @Test
    public void resetAllNotificationsShouldCreatePendingIntentForOpeningTheMainChat() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

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
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.resetPrivateChatNotification(new User("Test", 1234));

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatRegularIconHasBeenSet(notification);
    }

    @Test
    public void resetPrivateChatNotificationShouldResetNotificationTextForTheDrawer() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.resetPrivateChatNotification(new User("Test", 1234));

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        verifyThatNotificationTextIsRunning(notification);
    }

    @Test
    public void resetPrivateChatNotificationShouldCreatePendingIntentForOpeningTheMainChat() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

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
        assertEquals(R.drawable.kou_icon_24x24, notification.icon);
        assertEquals(R.drawable.kou_icon_24x24, notificationService.getCurrentIconId());
    }

    private void verifyThatActivityIconHasBeenSet(final Notification notification) {
        assertEquals(R.drawable.kou_icon_activity_24x24, notification.icon);
        assertEquals(R.drawable.kou_icon_activity_24x24, notificationService.getCurrentIconId());
    }

    private ShadowNotification.LatestEventInfo getLatestEventInfo(final Notification notification) {
        final ShadowNotification shadowNotification = Robolectric.shadowOf_(notification);
        return shadowNotification.getLatestEventInfo();
    }

    private ShadowIntent getPendingIntent(final ShadowNotification.LatestEventInfo latestEventInfo) {
        final ShadowPendingIntent shadowPendingIntent = Robolectric.shadowOf_(latestEventInfo.getContentIntent());
        return Robolectric.shadowOf_(shadowPendingIntent.getSavedIntent());
    }
}
