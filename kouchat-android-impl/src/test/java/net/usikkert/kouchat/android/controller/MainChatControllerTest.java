
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
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

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.android.userlist.UserListAdapter;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowHandler;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.util.ActivityController;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.view.menu.ActionMenuItem;

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

    private ActivityController<MainChatController> activityController;

    private MainChatController controller;

    private AndroidUserInterface ui;
    private UserList userList;
    private TextView mainChatView;
    private EditText mainChatInput;
    private ScrollView mainChatScroll;
    private ControllerUtils controllerUtils;
    private ListView mainChatUserList;
    private TextWatcher textWatcher;
    private UserListAdapter userListAdapter;
    private ActionBar actionBar;
    private Intent chatServiceIntent;

    @Before
    public void setUp() {
        activityController = Robolectric.buildActivity(MainChatController.class);
        controller = activityController.get();

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        ui = TestUtils.setFieldValueWithMock(controller, "androidUserInterface", AndroidUserInterface.class);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);

        userList = TestUtils.setFieldValueWithMock(controller, "userList", UserList.class);
        when(ui.getUserList()).thenReturn(userList);

        mainChatView = TestUtils.setFieldValueWithMock(controller, "mainChatView", TextView.class);
        mainChatInput = TestUtils.setFieldValueWithMock(controller, "mainChatInput", EditText.class);
        mainChatScroll = TestUtils.setFieldValueWithMock(controller, "mainChatScroll", ScrollView.class);
        controllerUtils = TestUtils.setFieldValueWithMock(controller, "controllerUtils", ControllerUtils.class);
        mainChatUserList = TestUtils.setFieldValueWithMock(controller, "mainChatUserList", ListView.class);
        textWatcher = TestUtils.setFieldValueWithMock(controller, "textWatcher", TextWatcher.class);
        userListAdapter = TestUtils.setFieldValueWithMock(controller, "userListAdapter", UserListAdapter.class);
        actionBar = TestUtils.setFieldValueWithMock(controller, "actionBar", ActionBar.class);
        chatServiceIntent = TestUtils.setFieldValueWithMock(controller, "chatServiceIntent", Intent.class);

        TestUtils.setFieldValueWithMock(controller, "serviceConnection", ServiceConnection.class);
    }

    @Test
    @Config(qualifiers = "v10")
    public void isVisibleShouldBeTrueOnlyBetweenOnResumeAndOnPause() {
        assertFalse(controller.isVisible());

        activityController.create();
        assertFalse(controller.isVisible());

        activityController.resume();
        assertTrue(controller.isVisible());

        activityController.pause();
        assertFalse(controller.isVisible());

        activityController.destroy();
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
        verify(userListAdapter).onDestroy();
        verify(controllerUtils).removeReferencesToTextViewFromText(mainChatView);
        verify(controllerUtils).removeReferencesToTextViewFromText(mainChatInput);
        assertEquals(1, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void onDestroyShouldSetAllFieldsToNull() {
        assertTrue(TestUtils.allFieldsHaveValue(controller));

        controller.onDestroy();

        assertTrue(TestUtils.allFieldsAreNull(controller));
    }

    @Test
    public void onDestroyShouldSetDestroyedToTrue() {
        assertFalse(TestUtils.getFieldValue(controller, Boolean.class, "destroyed"));

        controller.onDestroy();

        assertTrue(TestUtils.getFieldValue(controller, Boolean.class, "destroyed"));
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
    public void appendToChatShouldDoNothingIfDestroyed() {
        TestUtils.setFieldValue(controller, "destroyed", true);

        controller.appendToChat("Don't append");

        verifyZeroInteractions(controllerUtils, mainChatView);
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
        TestUtils.setFieldValue(controller, "destroyed", true);

        controller.updateChat("Set this text");

        ShadowHandler.runMainLooperOneTask();

        verify(mainChatView).setText("Set this text");
        verifyZeroInteractions(controllerUtils);
    }

    @Test
    public void userAddedShouldAddUserWithUserListAdapter() {
        final User user = new User("User", 1234);

        controller.userAdded(0, user);

        verify(userListAdapter).add(user);
    }

    @Test
    public void userAddedShouldNotAddUserIfDestroyed() {
        TestUtils.setFieldValue(controller, "destroyed", true);

        controller.userAdded(0, new User("User", 1234));

        verifyZeroInteractions(userListAdapter);
    }

    @Test
    public void userRemovedShouldRemoveUserWithUserListAdapter() {
        final User user = new User("User", 1234);

        controller.userRemoved(0, user);

        verify(userListAdapter).remove(user);
    }

    @Test
    public void userRemovedShouldNotRemoveUserIfDestroyed() {
        TestUtils.setFieldValue(controller, "destroyed", true);

        controller.userRemoved(0, new User("User", 1234));

        verifyZeroInteractions(userListAdapter);
    }

    @Test
    public void userChangedShouldSortWithUserListAdapter() {
        final User user = new User("User", 1234);

        controller.userChanged(0, user);

        verify(userListAdapter).sort();
    }

    @Test
    public void userChangedShouldNotSortIfDestroyed() {
        TestUtils.setFieldValue(controller, "destroyed", true);

        controller.userChanged(0, new User("User", 1234));

        verifyZeroInteractions(userListAdapter);
    }

    @Test
    public void sendMessageShouldSendUsingAndroidUserInterface() {
        controller.sendMessage("A message");

        verify(ui).sendMessage("A message");
    }

    @Test
    public void sendMessageShouldNotSendIfMessageIsNull() {
        controller.sendMessage(null);

        verifyZeroInteractions(ui);
    }

    @Test
    public void sendMessageShouldNotSendIfMessageIsWhitespace() {
        controller.sendMessage(" ");

        verifyZeroInteractions(ui);
    }

    @Test
    public void updateTitleAndTopicShouldSetTitleAsTitleAndTopicAsSubtitle() {
        assertNull(controller.getTitle());

        controller.updateTitleAndTopic("The title", "The topic");

        verify(actionBar).setTitle("The title");
        verify(actionBar).setSubtitle("The topic");
    }

    @Test
    public void onOptionsItemSelectedWithQuitShouldFinishAndStopService() {
        final boolean selected = controller.onOptionsItemSelected(createMenuItem(R.id.mainChatMenuQuit));

        assertTrue(selected);
        assertTrue(controller.isFinishing());

        final Intent stoppedService = Robolectric.getShadowApplication().getNextStoppedService();
        assertSame(chatServiceIntent, stoppedService);
    }

    @Test
    public void onOptionsItemSelectedWithSettingsShouldOpenSettingsController() {
        final boolean selected = controller.onOptionsItemSelected(createMenuItem(R.id.mainChatMenuSettings));

        assertTrue(selected);

        final Intent startedActivityIntent = Robolectric.getShadowApplication().getNextStartedActivity();
        final ShadowIntent startedActivityShadowIntent = Robolectric.shadowOf(startedActivityIntent);
        assertEquals(SettingsController.class, startedActivityShadowIntent.getIntentClass());
    }

    @Test
    @Config(qualifiers = "v10")
    public void onOptionsItemSelectedWithAboutShouldOpenAboutDialog() {
        activityController.create();

        final boolean selected = controller.onOptionsItemSelected(createMenuItem(R.id.mainChatMenuAbout));

        assertTrue(selected);

        final ShadowAlertDialog latestDialog = Robolectric.getShadowApplication().getLatestAlertDialog();
        assertEquals("KouChat v" + Constants.APP_VERSION, latestDialog.getTitle());
    }

    @Test
    @Config(qualifiers = "v10")
    public void onOptionsItemSelectedWithTopicShouldOpenTopicDialog() {
        activityController.create();

        final boolean selected = controller.onOptionsItemSelected(createMenuItem(R.id.mainChatMenuTopic));

        assertTrue(selected);

        final ShadowAlertDialog latestDialog = Robolectric.getShadowApplication().getLatestAlertDialog();
        assertEquals("Topic", latestDialog.getTitle());
    }

    private ActionMenuItem createMenuItem(final int menuItemId) {
        return new ActionMenuItem(null, 0, menuItemId, 0, 0, "");
    }
}
