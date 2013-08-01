
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

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.android.userlist.UserListAdapter;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowHandler;

import android.content.Intent;
import android.content.ServiceConnection;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

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
    private TextView mainChatView;
    private EditText mainChatInput;
    private ScrollView mainChatScroll;
    private ControllerUtils controllerUtils;
    private ListView mainChatUserList;
    private TextWatcher textWatcher;

    @Before
    public void setUp() {
        controller = new MainChatController();

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        ui = mock(AndroidUserInterface.class);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);

        userList = mock(UserList.class);
        when(ui.getUserList()).thenReturn(userList);

        final ServiceConnection serviceConnection = mock(ServiceConnection.class);
        mainChatView = mock(TextView.class);
        mainChatInput = mock(EditText.class);
        mainChatScroll = mock(ScrollView.class);
        controllerUtils = mock(ControllerUtils.class);
        mainChatUserList = mock(ListView.class);
        textWatcher = mock(TextWatcher.class);

        TestUtils.setFieldValue(controller, "userList", userList);
        TestUtils.setFieldValue(controller, "androidUserInterface", ui);
        TestUtils.setFieldValue(controller, "serviceConnection", serviceConnection);
        TestUtils.setFieldValue(controller, "mainChatView", mainChatView);
        TestUtils.setFieldValue(controller, "mainChatInput", mainChatInput);
        TestUtils.setFieldValue(controller, "mainChatScroll", mainChatScroll);
        TestUtils.setFieldValue(controller, "controllerUtils", controllerUtils);
        TestUtils.setFieldValue(controller, "chatServiceIntent", mock(Intent.class));
        TestUtils.setFieldValue(controller, "userListAdapter", mock(UserListAdapter.class));
        TestUtils.setFieldValue(controller, "mainChatUserList", mainChatUserList);
        TestUtils.setFieldValue(controller, "textWatcher", textWatcher);
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
        controller.onResume();

        verify(ui).resetAllNotifications();
    }

    @Test
    public void onResumeShouldHandleIfAndroidUserInterfaceIsNotInitializedYet() {
        TestUtils.setFieldValue(controller, "androidUserInterface", null);

        controller.onResume();
    }

    @Test
    public void onDestroyShouldUnregister() {
        controller.onDestroy();

        verify(userList).removeUserListListener(controller);
        verify(ui).unregisterMainChatController();
        verify(mainChatInput).removeTextChangedListener(textWatcher);
        verify(mainChatInput).setOnKeyListener(null);
        verify(mainChatUserList).setOnItemClickListener(null);
        verify(mainChatUserList).setAdapter(null);
        assertEquals(1, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void onDestroyShouldSetAllFieldsToNull() {
        assertTrue(TestUtils.allFieldsHaveValue(controller));

        controller.onDestroy();

        assertTrue(TestUtils.allFieldsAreNull(controller));
    }

    @Test
    public void onDestroyShouldNotFailIfServiceHasNotBeenBound() {
        TestUtils.setFieldValue(controller, "androidUserInterface", null);
        TestUtils.setFieldValue(controller, "userList", null);
        TestUtils.setFieldValue(controller, "serviceConnection", null);

        controller.onDestroy();

        assertEquals(0, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void appendToChatShouldAppendAndScrollToBottomIfInputHasFocus() {
        when(mainChatInput.hasFocus()).thenReturn(true);

        controller.appendToChat("Some text");

        verify(mainChatView).append("Some text");
        verify(controllerUtils).scrollTextViewToBottom(mainChatView, mainChatScroll);
    }

    @Test
    public void appendToChatShouldOnlyAppendIfInputLacksFocus() {
        when(mainChatInput.hasFocus()).thenReturn(false);

        controller.appendToChat("Some other text");

        verify(mainChatView).append("Some other text");
        verifyZeroInteractions(controllerUtils);
    }

    @Test
    public void updateChatShouldSetTextAndScrollToBottomIfNotDestroyed() {
        controller.updateChat("Set this text");

        ShadowHandler.runMainLooperOneTask();

        verify(mainChatView).setText("Set this text");
        verify(controllerUtils).scrollTextViewToBottom(mainChatView, mainChatScroll);
    }

    @Test
    public void updateChatShouldSetTextAndNotScrollToBottomIfDestroyed() {
        TestUtils.setFieldValue(controller, "controllerUtils", null);

        controller.updateChat("Set this text");

        ShadowHandler.runMainLooperOneTask(); // Should not give NullPointerException

        verify(mainChatView).setText("Set this text");
        verifyZeroInteractions(controllerUtils);
    }
}
