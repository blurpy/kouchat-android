
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

package net.usikkert.kouchat.android.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.AndroidUserInterface;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.ServiceConnection;

/**
 * Test of {@link MainChatController}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class MainChatControllerTest {

    private MainChatController controller;

    private AndroidUserInterface ui;
    private UserList userList;
    private ServiceConnection serviceConnection;

    @Before
    public void setUp() {
        controller = new MainChatController();

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        ui = mock(AndroidUserInterface.class);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);

        userList = mock(UserList.class);
        when(ui.getUserList()).thenReturn(userList);

        serviceConnection = mock(ServiceConnection.class);
    }

    @Test
    @Ignore("This does not work with Robolectric yet.") // Sherlock
    public void isVisibleShouldBeTrueOnlyBetweenOnResumeAndOnPause() {
        assertFalse(controller.isVisible());

        controller.onCreate(null);
        assertFalse(controller.isVisible());

        controller.onResume();
        assertTrue(controller.isVisible());

        controller.onPause();
        assertFalse(controller.isVisible());

        controller.onDestroy();
        assertFalse(controller.isVisible());
    }

    @Test
    public void onResumeShouldResetAllNotifications() {
        setupMocks();

        controller.onResume();

        verify(ui).resetAllNotifications();
    }

    @Test
    public void onResumeShouldHandleIfAndroidUserInterfaceIsNotInitializedYet() {
        assertTrue(TestUtils.fieldValueIsNull(controller, "androidUserInterface"));

        controller.onResume();
    }

    @Test
    public void onDestroyShouldUnregister() {
        setupMocks();

        controller.onDestroy();

        verify(userList).removeUserListListener(controller);
        verify(ui).unregisterMainChatController();
        assertEquals(1, Robolectric.getShadowApplication().getUnboundServiceConnections().size());

        assertTrue(TestUtils.fieldValueIsNull(controller, "androidUserInterface"));
        assertTrue(TestUtils.fieldValueIsNull(controller, "userList"));
    }

    @Test
    public void onDestroyShouldNotFailIfServiceHasNotBeenBound() {
        assertTrue(TestUtils.fieldValueIsNull(controller, "androidUserInterface"));

        controller.onDestroy();

        assertEquals(0, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    private void setupMocks() {
        TestUtils.setFieldValue(controller, "userList", userList);
        TestUtils.setFieldValue(controller, "androidUserInterface", ui);
        TestUtils.setFieldValue(controller, "serviceConnection", serviceConnection);
    }
}
