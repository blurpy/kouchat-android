
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

package net.usikkert.kouchat.android.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.notification.NotificationService;
import net.usikkert.kouchat.android.util.RobolectricTestUtils;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;

/**
 * Test of {@link ChatService}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class ChatServiceTest {

    private ChatService chatService;

    private AndroidUserInterface ui;
    private NotificationService notificationService;
    private LockHandler lockHandler;
    private ChatServiceBinder chatServiceBinder;

    @Before
    public void setUp() {
        chatService = new ChatService();

        ui = mock(AndroidUserInterface.class);
        notificationService = mock(NotificationService.class);
        lockHandler = mock(LockHandler.class);
        chatServiceBinder = mock(ChatServiceBinder.class);
    }

    @Test
    public void omCreateShouldSetNickNameFromAndroidSettings() {
        mockSystemServices();
        RobolectricTestUtils.setNickNameInTheAndroidSettingsTo("Kou");

        chatService.onCreate();

        final User me = getMe();
        assertEquals("Kou", me.getNick());
    }

    @Test
    public void onCreateShouldCreateLockHandler() {
        mockSystemServices();

        assertTrue(TestUtils.fieldValueIsNull(chatService, "lockHandler"));

        chatService.onCreate();

        assertFalse(TestUtils.fieldValueIsNull(chatService, "lockHandler"));
    }

    @Test
    public void onCreateShouldCreateChatServiceBinderWithUserInterfaceForUsageInOnBind() {
        mockSystemServices();

        chatService.onCreate();

        final IBinder binder = chatService.onBind(null);
        assertEquals(ChatServiceBinder.class, binder.getClass());

        final ChatServiceBinder binderAsChatServiceBinder = (ChatServiceBinder) binder;
        assertNotNull(binderAsChatServiceBinder.getAndroidUserInterface());
    }

    @Test
    public void onBindShouldReturnTheSameInstanceOfChatServiceBinderEachTime() {
        setMockedFields();

        final IBinder binder1 = chatService.onBind(null);
        final IBinder binder2 = chatService.onBind(null);

        assertSame(binder1, binder2);
    }

    @Test
    public void onStartShouldLogOn() {
        setMockedFields();

        chatService.onStart(null, 0);

        verify(ui).logOn();
    }

    @Test
    public void onStartShouldOnlyLogOnOnce() {
        // Logging on starts the controller, and the controller only supports being started once.
        // DayTimer throws "java.lang.IllegalStateException: TimerTask is scheduled already" when started twice.
        // This used to be an issue when starting KouChat without a network connection, since it tried
        // to log on each time the main chat was hidden and shown again, resulting in the exception.
        setMockedFields();

        chatService.onStart(null, 0);
        chatService.onStart(null, 0);
        chatService.onStart(null, 0);

        verify(ui, times(1)).logOn();
    }

    @Test
    public void onStartShouldStartInForegroundWithNotification() {
        setMockedFields();

        chatService = spy(chatService); // To be able to verify startForeground

        final Notification notification = new Notification();
        when(notificationService.createServiceNotification()).thenReturn(notification);

        chatService.onStart(null, 0);

        verify(notificationService).createServiceNotification();
        verify(chatService).startForeground(1001, notification);
    }

    @Test
    public void onDestroyShouldLogOff() {
        setMockedFields();

        chatService.onDestroy();

        verify(ui).logOff();
    }

    @Test
    public void onDestroyShouldReleaseAllLocks() {
        setMockedFields();

        chatService.onDestroy();

        verify(lockHandler).releaseAllLocks();
    }

    @Test
    public void onDestroyShouldDestroyBinder() {
        setMockedFields();

        chatService.onDestroy();

        verify(chatServiceBinder).onDestroy();
    }

    @Test
    public void onDestroyShouldSetAllFieldsToNull() {
        setMockedFields();
        assertTrue(TestUtils.allFieldsHaveValue(chatService));

        chatService.onDestroy();

        assertTrue(TestUtils.allFieldsAreNull(chatService));
    }

    private User getMe() {
        final AndroidUserInterface androidUserInterface =
                TestUtils.getFieldValue(chatService, AndroidUserInterface.class, "androidUserInterface");

        return TestUtils.getFieldValue(androidUserInterface, User.class, "me");
    }

    private void setMockedFields() {
        TestUtils.setFieldValue(chatService, "androidUserInterface", ui);
        TestUtils.setFieldValue(chatService, "notificationService", notificationService);
        TestUtils.setFieldValue(chatService, "lockHandler", lockHandler);
        TestUtils.setFieldValue(chatService, "chatServiceBinder", chatServiceBinder);
    }

    private void mockSystemServices() {
        final Context context = mock(Context.class);
        TestUtils.setFieldValue(chatService, "mBase", context); // In superclass android.content.ContextWrapper

        doReturn(mock(NotificationManager.class)).when(context).getSystemService(Context.NOTIFICATION_SERVICE);
        doReturn(mock(WifiManager.class)).when(context).getSystemService(Context.WIFI_SERVICE);
        doReturn(mock(PowerManager.class)).when(context).getSystemService(Context.POWER_SERVICE);
    }
}
