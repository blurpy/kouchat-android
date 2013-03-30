
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowIntent;
import com.xtremelabs.robolectric.shadows.ShadowNotification;
import com.xtremelabs.robolectric.shadows.ShadowPendingIntent;

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

    @Before
    public void setUp() {
        notificationService = new NotificationService(Robolectric.application.getApplicationContext());

        notificationManager = mock(NotificationManager.class);
        TestUtils.setFieldValue(notificationService, "notificationManager", notificationManager);
    }

    @Test
    public void constructoShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new NotificationService(null);
    }

    @Test
    public void createServiceNotificationShouldSetIconAndStartupMessage() {
        final Notification notification = notificationService.createServiceNotification();

        assertEquals(R.drawable.kou_icon_24x24, notification.icon);
        assertEquals(R.drawable.kou_icon_24x24, notificationService.getCurrentIconId());
        assertEquals("KouChat is up and running", notification.tickerText);
    }

    @Test
    public void createServiceNotificationShouldSetNotificationTextForTheDrawer() {
        final Notification notification = notificationService.createServiceNotification();
        final ShadowNotification.LatestEventInfo latestEventInfo = getLatestEventInfo(notification);

        assertEquals("KouChat", latestEventInfo.getContentTitle());
        assertEquals("Running", latestEventInfo.getContentText());
        assertEquals(R.string.notification_running, notificationService.getCurrentLatestInfoTextId());
    }

    @Test
    public void createServiceNotificationShouldCreatePendingIntentForOpeningTheMainChat() {
        final Notification notification = notificationService.createServiceNotification();
        final ShadowNotification.LatestEventInfo latestEventInfo = getLatestEventInfo(notification);
        final ShadowIntent pendingIntent = getPendingIntent(latestEventInfo);

        assertEquals(MainChatController.class, pendingIntent.getIntentClass());
    }

    @Test
    public void notifyNewMessageShouldSetActivityIcon() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.notifyNewMessage();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        assertEquals(R.drawable.kou_icon_activity_24x24, notification.icon);
        assertEquals(R.drawable.kou_icon_activity_24x24, notificationService.getCurrentIconId());
    }

    @Test
    public void notifyNewMessageShouldSetNotificationTextForTheDrawer() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.notifyNewMessage();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        final ShadowNotification.LatestEventInfo latestEventInfo = getLatestEventInfo(notification);

        assertEquals("KouChat", latestEventInfo.getContentTitle());
        assertEquals("New unread messages", latestEventInfo.getContentText());
        assertEquals(R.string.notification_new_message, notificationService.getCurrentLatestInfoTextId());
    }

    @Test
    public void notifyNewMessageShouldCreatePendingIntentForOpeningTheMainChat() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.notifyNewMessage();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        final ShadowNotification.LatestEventInfo latestEventInfo = getLatestEventInfo(notification);
        final ShadowIntent pendingIntent = getPendingIntent(latestEventInfo);

        assertEquals(MainChatController.class, pendingIntent.getIntentClass());
    }

    @Test
    public void resetAllNotificationsShouldSetRegularIcon() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.resetAllNotifications();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        assertEquals(R.drawable.kou_icon_24x24, notification.icon);
        assertEquals(R.drawable.kou_icon_24x24, notificationService.getCurrentIconId());
    }

    @Test
    public void resetAllNotificationsShouldResetNotificationTextForTheDrawer() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.resetAllNotifications();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        final ShadowNotification.LatestEventInfo latestEventInfo = getLatestEventInfo(notification);

        assertEquals("KouChat", latestEventInfo.getContentTitle());
        assertEquals("Running", latestEventInfo.getContentText());
        assertEquals(R.string.notification_running, notificationService.getCurrentLatestInfoTextId());
    }

    @Test
    public void resetAllNotificationsShouldCreatePendingIntentForOpeningTheMainChat() {
        final ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.resetAllNotifications();

        verify(notificationManager).notify(eq(1001), argumentCaptor.capture());

        final Notification notification = argumentCaptor.getValue();
        final ShadowNotification.LatestEventInfo latestEventInfo = getLatestEventInfo(notification);
        final ShadowIntent pendingIntent = getPendingIntent(latestEventInfo);

        assertEquals(MainChatController.class, pendingIntent.getIntentClass());
    }

    private ShadowNotification.LatestEventInfo getLatestEventInfo(final Notification notification) {
        final ShadowNotification shadowNotification = Robolectric.shadowOf(notification);
        return shadowNotification.getLatestEventInfo();
    }

    private ShadowIntent getPendingIntent(final ShadowNotification.LatestEventInfo latestEventInfo) {
        final ShadowPendingIntent shadowPendingIntent = Robolectric.shadowOf(latestEventInfo.getContentIntent());
        return Robolectric.shadowOf(shadowPendingIntent.getSavedIntent());
    }
}
