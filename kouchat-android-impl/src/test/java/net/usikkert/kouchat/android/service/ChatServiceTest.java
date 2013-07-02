
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

package net.usikkert.kouchat.android.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.android.AndroidUserInterface;
import net.usikkert.kouchat.android.notification.NotificationService;
import net.usikkert.kouchat.android.util.RobolectricTestUtils;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;

import android.app.Notification;
import android.os.IBinder;

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
    private MulticastLockHandler multicastLockHandler;

    @Before
    public void setUp() {
        chatService = new ChatService();

        ui = mock(AndroidUserInterface.class);
        notificationService = mock(NotificationService.class);
        multicastLockHandler = mock(MulticastLockHandler.class);

        System.clearProperty(Constants.PROPERTY_CLIENT_UI);
    }

    @Test
    public void onCreateShouldSetClientProperty() {
        chatService.onCreate();

        assertEquals("Android", System.getProperty(Constants.PROPERTY_CLIENT_UI));
    }

    @Test
    public void omCreateShouldSetNickNameFromAndroidSettings() {
        RobolectricTestUtils.setNickNameInTheAndroidSettingsTo("Kou");

        chatService.onCreate();

        final User me = getMe();
        assertEquals("Kou", me.getNick());
    }

    @Test
    public void onCreateShouldCreateMulticastLockHandler() {
        assertTrue(TestUtils.fieldValueIsNull(chatService, "multicastLockHandler"));

        chatService.onCreate();

        assertFalse(TestUtils.fieldValueIsNull(chatService, "multicastLockHandler"));
    }

    @Test
    public void onStartShouldLogOn() {
        setMockedFields();

        chatService.onStart(null, 0);

        verify(ui).logOn();
    }

    @Test
    public void onStartShouldNotLogOnInAlreadyLoggedOn() {
        setMockedFields();

        when(ui.isLoggedOn()).thenReturn(true);

        chatService.onStart(null, 0);

        verify(ui, never()).logOn();
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
    public void onBindShouldReturnChatServiceBinderWithUserInterface() {
        setMockedFields();

        final IBinder binder = chatService.onBind(null);

        assertEquals(ChatServiceBinder.class, binder.getClass());

        final ChatServiceBinder chatServiceBinder = (ChatServiceBinder) binder;
        assertSame(ui, chatServiceBinder.getAndroidUserInterface());
    }

    @Test
    public void onDestroyShouldLogOff() {
        setMockedFields();

        chatService.onDestroy();

        verify(ui).logOff();
    }

    @Test
    public void onDestroyShouldReleaseMulticastLock() {
        setMockedFields();

        chatService.onDestroy();

        verify(multicastLockHandler).release();
    }

    private User getMe() {
        final AndroidUserInterface androidUserInterface =
                TestUtils.getFieldValue(chatService, AndroidUserInterface.class, "androidUserInterface");

        return TestUtils.getFieldValue(androidUserInterface, User.class, "me");
    }

    private void setMockedFields() {
        TestUtils.setFieldValue(chatService, "androidUserInterface", ui);
        TestUtils.setFieldValue(chatService, "notificationService", notificationService);
        TestUtils.setFieldValue(chatService, "multicastLockHandler", multicastLockHandler);
    }
}
