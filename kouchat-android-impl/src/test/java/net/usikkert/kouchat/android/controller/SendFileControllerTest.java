
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

import java.io.File;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.android.userlist.UserListAdapter;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.ServiceConnection;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Test of {@link SendFileController}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class SendFileControllerTest {

    private SendFileController controller;

    private AndroidUserInterface ui;
    private UserList userList;
    private ServiceConnection serviceConnection;

    @Before
    public void setUp() {
        controller = new SendFileController();

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        ui = mock(AndroidUserInterface.class);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);

        userList = mock(UserList.class);
        when(ui.getUserList()).thenReturn(userList);

        serviceConnection = mock(ServiceConnection.class);
    }

    @Test
    public void onDestroyShouldUnregister() {
        setupMocks();

        controller.onDestroy();

        verify(userList).removeUserListListener(controller);
        assertEquals(1, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void onDestroyShouldSetAllFieldsToNull() {
        setupMocks();
        assertTrue(TestUtils.allFieldsHaveValue(controller));

        controller.onDestroy();

        assertTrue(TestUtils.allFieldsAreNull(controller));
    }

    @Test
    public void onDestroyShouldNotFailIfServiceHasNotBeenBound() {
        assertTrue(TestUtils.fieldValueIsNull(controller, "userList"));

        controller.onDestroy();

        assertEquals(0, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    private void setupMocks() {
        TestUtils.setFieldValue(controller, "userList", userList);
        TestUtils.setFieldValue(controller, "androidUserInterface", ui);
        TestUtils.setFieldValue(controller, "serviceConnection", serviceConnection);

        TestUtils.setFieldValue(controller, "fileToSend", mock(File.class));
        TestUtils.setFieldValue(controller, "userListAdapter", mock(UserListAdapter.class));
        TestUtils.setFieldValue(controller, "line2TextView", mock(TextView.class));
        TestUtils.setFieldValue(controller, "userListView", mock(ListView.class));
    }
}
