
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
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

package net.usikkert.kouchat.android.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.chatwindow.AndroidPrivateChatWindow;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.misc.User;
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
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Test of {@link PrivateChatController}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class PrivateChatControllerTest {

    private PrivateChatController controller;

    private AndroidPrivateChatWindow privateChatWindow;
    private TextView privateChatView;
    private EditText privateChatInput;
    private ScrollView privateChatScroll;
    private ControllerUtils controllerUtils;

    @Before
    public void setUp() {
        controller = new PrivateChatController();

        final User user = new User("User", 1234);
        privateChatWindow = mock(AndroidPrivateChatWindow.class);
        user.setPrivchat(privateChatWindow);

        final AndroidUserInterface ui = mock(AndroidUserInterface.class);
        when(ui.getUser(1234)).thenReturn(user);

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        final ServiceConnection serviceConnection = mock(ServiceConnection.class);

        final Intent intent = new Intent();
        intent.putExtra("userCode", 1234);
        controller.setIntent(intent);

        privateChatView = mock(TextView.class);
        privateChatInput = mock(EditText.class);
        privateChatScroll = mock(ScrollView.class);
        controllerUtils = mock(ControllerUtils.class);

        TestUtils.setFieldValue(controller, "privateChatWindow", privateChatWindow);
        TestUtils.setFieldValue(controller, "user", user);
        TestUtils.setFieldValue(controller, "androidUserInterface", ui);
        TestUtils.setFieldValue(controller, "serviceConnection", serviceConnection);
        TestUtils.setFieldValue(controller, "privateChatView", privateChatView);
        TestUtils.setFieldValue(controller, "privateChatInput", privateChatInput);
        TestUtils.setFieldValue(controller, "privateChatScroll", privateChatScroll);
        TestUtils.setFieldValue(controller, "controllerUtils", controllerUtils);
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
    public void onDestroyShouldUnregister() {
        controller.onDestroy();

        verify(privateChatWindow).unregisterPrivateChatController();
        verify(privateChatInput).setOnKeyListener(null);
        verify(controllerUtils).removeReferencesToTextViewFromText(privateChatView);
        verify(controllerUtils).removeReferencesToTextViewFromText(privateChatInput);
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
        TestUtils.setFieldValue(controller, "privateChatWindow", null);
        TestUtils.setFieldValue(controller, "user", null);
        TestUtils.setFieldValue(controller, "androidUserInterface", null);

        controller.onDestroy();

        assertEquals(0, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void appendToPrivateChatShouldAppendAndScrollToBottomIfInputHasFocus() {
        when(privateChatInput.hasFocus()).thenReturn(true);

        controller.appendToPrivateChat("Some text");

        verify(privateChatView).append("Some text");
        verify(controllerUtils).scrollTextViewToBottom(privateChatView, privateChatScroll);
    }

    @Test
    public void appendToPrivateChatShouldOnlyAppendIfInputLacksFocus() {
        when(privateChatInput.hasFocus()).thenReturn(false);

        controller.appendToPrivateChat("Some other text");

        verify(privateChatView).append("Some other text");
        verifyZeroInteractions(controllerUtils);
    }

    @Test
    public void updatePrivateChatShouldSetTextAndScrollToBottomIfNotDestroyed() {
        controller.updatePrivateChat("Set this text");

        ShadowHandler.runMainLooperOneTask();

        verify(privateChatView).setText("Set this text");
        verify(controllerUtils).scrollTextViewToBottom(privateChatView, privateChatScroll);
    }

    @Test
    public void updatePrivateChatShouldSetTextAndNotScrollToBottomIfDestroyed() {
        TestUtils.setFieldValue(controller, "controllerUtils", null);

        controller.updatePrivateChat("Set this text");

        ShadowHandler.runMainLooperOneTask(); // Should not give NullPointerException

        verify(privateChatView).setText("Set this text");
        verifyZeroInteractions(controllerUtils);
    }
}
