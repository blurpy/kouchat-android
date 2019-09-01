
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

package net.usikkert.kouchat.android.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.android.userlist.UserListAdapter;
import net.usikkert.kouchat.misc.SortedUserList;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Ignore;
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
import com.actionbarsherlock.internal.view.menu.ActionMenu;
import com.actionbarsherlock.internal.view.menu.ActionMenuItem;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Test of {@link MainChatController}.
 *
 * @author Christian Ihle
 */
@Config(qualifiers = "v10")
@RunWith(RobolectricTestRunner.class)
public class MainChatControllerTest {

    private ActivityController<MainChatController> activityController;
    private MainChatController controller;

    private AndroidUserInterface ui;
    private SortedUserList userList;
    private ControllerUtils controllerUtils;

    @Before
    public void setUp() {
        activityController = Robolectric.buildActivity(MainChatController.class);
        controller = activityController.get();

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        ui = mock(AndroidUserInterface.class);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);

        userList = new SortedUserList();
        when(ui.getUserList()).thenReturn(userList);

        controllerUtils = TestUtils.setFieldValueWithMock(controller, "controllerUtils", ControllerUtils.class);
    }

    @Test
    public void onCreateShouldRegisterKeyListenerThatSendsMessageAndClearsInputOnEnter() {
        activityController.create();

        final EditText mainChatInput = (EditText) controller.findViewById(R.id.mainChatInput);
        mainChatInput.setText("Hello");

        mainChatInput.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));

        verify(ui).sendMessage("Hello");
        assertEquals("", mainChatInput.getText().toString());
    }

    @Test
    public void onCreateShouldRegisterKeyListenerThatIgnoresOtherEvents() {
        activityController.create();

        final EditText mainChatInput = (EditText) controller.findViewById(R.id.mainChatInput);
        mainChatInput.setText("Hello");

        mainChatInput.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        mainChatInput.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE));

        verify(ui, never()).sendMessage(anyString());
        assertEquals("Hello", mainChatInput.getText().toString());
    }

    @Test
    public void onCreateShouldRegisterTextListenerThatUpdatesWriteStatus() {
        activityController.create();

        final EditText mainChatInput = (EditText) controller.findViewById(R.id.mainChatInput);

        mainChatInput.setText("Hello");
        verify(ui).updateMeWriting(true);

        mainChatInput.setText("");
        verify(ui).updateMeWriting(false);
    }

    @Test
    public void onCreateShouldRegisterTextListenerThatHandlesMissingDependenciesOnRotation() {
        activityController.create();
        TestUtils.setFieldValue(controller, "androidUserInterface", null);

        final EditText mainChatInput = (EditText) controller.findViewById(R.id.mainChatInput);

        mainChatInput.setText("Hello");
        mainChatInput.setText("");

        verify(ui, never()).updateMeWriting(anyBoolean());
    }

    @Test
    public void onCreateShouldRegisterOnClickListenerThatStartsPrivateChatWithClickedUser() {
        userList.add(new User("Ally", 1234));
        userList.add(new User("Molly", 2345));
        userList.add(new User("Wanda", 3456));

        activityController.create();

        final ListView mainChatUserList = (ListView) controller.findViewById(R.id.mainChatUserList);
        final AdapterView.OnItemClickListener listener = mainChatUserList.getOnItemClickListener();

        listener.onItemClick(mainChatUserList, null, 1, 100); // Should be Molly at position 1

        final Intent nextStartedActivity = Robolectric.getShadowApplication().getNextStartedActivity();
        final ShadowIntent nextStartedActivityShadow = Robolectric.shadowOf(nextStartedActivity);

        assertEquals(PrivateChatController.class, nextStartedActivityShadow.getIntentClass());
        assertEquals(2345, nextStartedActivity.getIntExtra("userCode", 0));
    }

    @Test
    public void onCreateShouldRegisterOnClickListenerThatIgnoresClickOnMe() {
        final User me = new User("Me", 1234);
        me.setMe(true);
        userList.add(me);

        activityController.create();

        final ListView mainChatUserList = (ListView) controller.findViewById(R.id.mainChatUserList);
        final AdapterView.OnItemClickListener listener = mainChatUserList.getOnItemClickListener();

        listener.onItemClick(mainChatUserList, null, 0, 100);

        assertNull(Robolectric.getShadowApplication().getNextStartedActivity());
    }

    @Test
    public void onCreateShouldMakeLinksClickable() {
        activityController.create();

        final TextView mainChatView = (TextView) controller.findViewById(R.id.mainChatView);

        verify(controllerUtils).makeLinksClickable(mainChatView);
    }

    @Test
    public void onCreateShouldRequestFocusOnInputToOpenKeyboard() {
        activityController.create();

        final EditText mainChatInput = (EditText) controller.findViewById(R.id.mainChatInput);

        assertTrue(mainChatInput.hasFocus());
    }

    @Test
    public void onCreateShouldStartService() {
        activityController.create();

        final ShadowIntent startedServiceIntent =
                Robolectric.shadowOf(Robolectric.getShadowApplication().getNextStartedService());

        assertEquals(ChatService.class, startedServiceIntent.getIntentClass());
    }

    @Test
    public void onCreateShouldRegisterWithAndroidUserInterfaceAndShowTopic() {
        activityController.create();

        verify(ui).registerMainChatController(controller);
        verify(ui).showTopic();
    }

    @Test
    public void onCreateShouldRegisterControllerAsUserListListener() {
        assertEquals(0, userList.getListeners().size());

        activityController.create();

        assertEquals(1, userList.getListeners().size());
        assertTrue(userList.getListeners().contains(controller));
    }

    @Test
    public void onCreateShouldAddSortedUsers() {
        final User penny = new User("Penny", 126);
        final User xing = new User("Xing", 127);
        final User cecilia = new User("Cecilia", 128);

        userList.add(penny);
        userList.add(xing);
        userList.add(cecilia);

        activityController.create();

        final ListView userListView = (ListView) controller.findViewById(R.id.mainChatUserList);
        final ListAdapter adapter = userListView.getAdapter();

        assertEquals(3, adapter.getCount());
        assertSame(cecilia, adapter.getItem(0));
        assertSame(penny, adapter.getItem(1));
        assertSame(xing, adapter.getItem(2));
    }

    @Test
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
        activityController.create();

        activityController.resume();

        verify(ui).resetAllNotifications();
    }

    @Test
    public void onResumeShouldHandleIfAndroidUserInterfaceIsNotInitializedYet() {
        assertTrue(TestUtils.fieldValueIsNull(controller, "androidUserInterface"));

        controller.onResume();

        verify(ui, never()).resetAllNotifications();
    }

    @Test
    public void onDestroyShouldUnregister() {
        activityController.create();

        // Replacing with mocks for easier verification
        final TextView mainChatView = TestUtils.setFieldValueWithMock(controller, "mainChatView", TextView.class);
        final EditText mainChatInput = TestUtils.setFieldValueWithMock(controller, "mainChatInput", EditText.class);
        final ListView mainChatUserList = TestUtils.setFieldValueWithMock(controller, "mainChatUserList", ListView.class);
        final TextWatcher textWatcher = TestUtils.setFieldValueWithMock(controller, "textWatcher", TextWatcher.class);
        final UserListAdapter userListAdapter = TestUtils.setFieldValueWithMock(controller, "userListAdapter", UserListAdapter.class);
        final UserList userListMock = TestUtils.setFieldValueWithMock(controller, "userList", UserList.class);

        activityController.destroy();

        verify(userListMock).removeUserListListener(controller);
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
        activityController.create();
        TestUtils.setFieldValueWithMock(controller, "actionBar", ActionBar.class); // getSupportActionBar() returns null

        assertTrue(TestUtils.allFieldsHaveValue(controller));

        activityController.destroy();

        assertTrue(TestUtils.allFieldsAreNull(controller));
    }

    @Test
    public void onDestroyShouldSetDestroyedToTrue() {
        activityController.create();

        assertFalse(TestUtils.getFieldValue(controller, Boolean.class, "destroyed"));

        activityController.destroy();

        assertTrue(TestUtils.getFieldValue(controller, Boolean.class, "destroyed"));
    }

    @Test
    public void onDestroyShouldNotFailIfServiceHasNotBeenBound() {
        activityController.create();

        TestUtils.setFieldValue(controller, "androidUserInterface", null);
        TestUtils.setFieldValue(controller, "userList", null);
        TestUtils.setFieldValue(controller, "serviceConnection", null);

        activityController.destroy();

        assertEquals(0, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void appendToChatShouldAppendAndScrollToBottomIfInputHasFocus() {
        activityController.create();

        final EditText mainChatInput = (EditText) controller.findViewById(R.id.mainChatInput);
        final TextView mainChatView = (TextView) controller.findViewById(R.id.mainChatView);
        final ScrollView mainChatScroll = (ScrollView) controller.findViewById(R.id.mainChatScroll);

        mainChatView.setText("Original text");
        mainChatInput.requestFocus();
        assertTrue(mainChatInput.hasFocus());

        controller.appendToChat("\nSome text");

        assertEquals("Original text\nSome text", mainChatView.getText().toString());
        verify(controllerUtils).scrollTextViewToBottom(mainChatView, mainChatScroll);
    }

    @Test
    public void appendToChatShouldOnlyAppendIfInputLacksFocus() {
        activityController.create();

        final EditText mainChatInput = (EditText) controller.findViewById(R.id.mainChatInput);
        final TextView mainChatView = (TextView) controller.findViewById(R.id.mainChatView);

        mainChatView.setText("Original text");
        mainChatView.requestFocus();
        assertFalse(mainChatInput.hasFocus());

        controller.appendToChat("\nSome other text");

        assertEquals("Original text\nSome other text", mainChatView.getText().toString());
        verify(controllerUtils, never()).scrollTextViewToBottom(any(TextView.class), any(ScrollView.class));
    }

    @Test
    public void appendToChatShouldDoNothingIfDestroyed() {
        activityController.create();

        final TextView mainChatView = (TextView) controller.findViewById(R.id.mainChatView);
        mainChatView.setText("Original text");

        activityController.destroy();

        controller.appendToChat("\nDon't append");

        assertEquals("Original text", mainChatView.getText().toString());
        verify(controllerUtils, never()).scrollTextViewToBottom(any(TextView.class), any(ScrollView.class));
    }

    @Test
    public void updateChatShouldSetTextAndScrollToBottomIfNotDestroyed() {
        activityController.create();

        final TextView mainChatView = (TextView) controller.findViewById(R.id.mainChatView);
        final ScrollView mainChatScroll = (ScrollView) controller.findViewById(R.id.mainChatScroll);
        mainChatView.setText("Original text");

        controller.updateChat("Set this text");

        ShadowHandler.runMainLooperOneTask();

        assertEquals("Set this text", mainChatView.getText().toString());
        verify(controllerUtils).scrollTextViewToBottom(mainChatView, mainChatScroll);
    }

    @Test
    public void updateChatShouldSetTextAndNotScrollToBottomIfDestroyed() {
        activityController.create();

        final TextView mainChatView = (TextView) controller.findViewById(R.id.mainChatView);
        mainChatView.setText("Original text");

        controller.updateChat("Set this text");

        activityController.destroy(); // onDestroy() runs between setting the text and the delayed handler that scrolls
        ShadowHandler.runMainLooperOneTask();

        assertEquals("Set this text", mainChatView.getText().toString());
        verify(controllerUtils, never()).scrollTextViewToBottom(any(TextView.class), any(ScrollView.class));
    }

    @Test
    public void userAddedShouldAddUserToAdapter() {
        activityController.create();

        final ListView mainChatUserList = (ListView) controller.findViewById(R.id.mainChatUserList);
        final ListAdapter adapter = mainChatUserList.getAdapter();

        assertEquals(0, adapter.getCount());

        controller.userAdded(0, new User("Lilly", 125));

        assertEquals(1, adapter.getCount());
    }

    @Test
    public void userAddedShouldNotAddUserIfDestroyed() {
        activityController.create();

        final ListView mainChatUserList = (ListView) controller.findViewById(R.id.mainChatUserList);
        final ListAdapter adapter = mainChatUserList.getAdapter();

        activityController.destroy();

        assertEquals(0, adapter.getCount());

        controller.userAdded(0, new User("Lilly", 125));

        assertEquals(0, adapter.getCount());
    }

    @Test
    public void userAddedShouldSortUsers() {
        final User xing = new User("Xing", 127);
        final User cecilia = new User("Cecilia", 128);

        userList.add(xing);
        userList.add(cecilia);

        activityController.create();

        final ListView mainChatUserList = (ListView) controller.findViewById(R.id.mainChatUserList);
        final ListAdapter adapter = mainChatUserList.getAdapter();

        assertEquals(2, adapter.getCount());
        assertSame(cecilia, adapter.getItem(0));
        assertSame(xing, adapter.getItem(1));

        final User penny = new User("Penny", 126);

        controller.userAdded(0, penny);

        assertEquals(3, adapter.getCount());
        assertSame(cecilia, adapter.getItem(0));
        assertSame(penny, adapter.getItem(1));
        assertSame(xing, adapter.getItem(2));
    }

    @Test
    public void userRemovedShouldRemoveUserFromUserListAdapter() {
        final User lilly = new User("Lilly", 125);
        userList.add(lilly);

        activityController.create();

        final ListView mainChatUserList = (ListView) controller.findViewById(R.id.mainChatUserList);
        final ListAdapter adapter = mainChatUserList.getAdapter();

        assertEquals(1, adapter.getCount());

        controller.userRemoved(0, lilly);

        assertEquals(0, adapter.getCount());
    }

    @Test
    public void userRemovedShouldNotFailIfDestroyed() {
        final User lilly = new User("Lilly", 125);
        userList.add(lilly);

        activityController.create();

        final ListView mainChatUserList = (ListView) controller.findViewById(R.id.mainChatUserList);
        final ListAdapter adapter = mainChatUserList.getAdapter();

        assertEquals(1, adapter.getCount());
        activityController.destroy();
        assertEquals(0, adapter.getCount());

        controller.userRemoved(0, lilly);

        assertEquals(0, adapter.getCount());
    }

    @Test
    public void userChangedShouldSortUserListAdapter() {
        final User penny = new User("Penny", 126);
        final User xing = new User("Xing", 127);
        final User cecilia = new User("Cecilia", 128);

        userList.add(penny);
        userList.add(xing);
        userList.add(cecilia);

        activityController.create();

        final ListView mainChatUserList = (ListView) controller.findViewById(R.id.mainChatUserList);
        final ListAdapter adapter = mainChatUserList.getAdapter();

        assertEquals(3, adapter.getCount());
        assertSame(cecilia, adapter.getItem(0));
        assertSame(penny, adapter.getItem(1));
        assertSame(xing, adapter.getItem(2));

        penny.setNick("Amy");

        controller.userChanged(0, null); // Doesn't use any of the parameters

        assertEquals(3, adapter.getCount());
        assertSame(penny, adapter.getItem(0)); // Now Amy
        assertSame(cecilia, adapter.getItem(1));
        assertSame(xing, adapter.getItem(2));
    }

    @Test
    public void userChangedShouldNotFailIfDestroyed() {
        final User penny = new User("Penny", 126);
        final User xing = new User("Xing", 127);
        final User cecilia = new User("Cecilia", 128);

        userList.add(penny);
        userList.add(xing);
        userList.add(cecilia);

        activityController.create();
        activityController.destroy();

        controller.userChanged(0, null);
    }

    @Test
    public void sendMessageShouldSendUsingAndroidUserInterface() {
        activityController.create();

        controller.sendMessage("A message");

        verify(ui).sendMessage("A message");
    }

    @Test
    public void sendMessageShouldNotSendIfMessageIsNull() {
        activityController.create();

        controller.sendMessage(null);

        verify(ui, never()).sendMessage(anyString());
    }

    @Test
    public void sendMessageShouldNotSendIfMessageIsWhitespace() {
        activityController.create();

        controller.sendMessage(" ");

        verify(ui, never()).sendMessage(anyString());
    }

    @Test
    public void updateTitleAndSubtitleShouldSetTitleAsTitleAndTopicAsSubtitle() {
        activityController.create();

        final ActionBar actionBar = TestUtils.setFieldValueWithMock(controller, "actionBar", ActionBar.class);

        controller.updateTitleAndSubtitle("The title", "The topic");

        verify(actionBar).setTitle("The title");
        verify(actionBar).setSubtitle("The topic");
    }

    @Test
    public void onOptionsItemSelectedWithQuitShouldFinishAndStopService() {
        activityController.create();

        final boolean selected = controller.onOptionsItemSelected(createMenuItem(R.id.mainChatMenuQuit));

        assertTrue(selected);
        assertTrue(controller.isFinishing());

        final Intent stoppedServiceIntent = Robolectric.getShadowApplication().getNextStoppedService();
        final ShadowIntent stoppedServiceShadowIntent = Robolectric.shadowOf(stoppedServiceIntent);
        assertEquals(ChatService.class, stoppedServiceShadowIntent.getIntentClass());
    }

    @Test
    public void onOptionsItemSelectedWithSettingsShouldOpenSettingsController() {
        activityController.create();

        final boolean selected = controller.onOptionsItemSelected(createMenuItem(R.id.mainChatMenuSettings));

        assertTrue(selected);

        final Intent startedActivityIntent = Robolectric.getShadowApplication().getNextStartedActivity();
        final ShadowIntent startedActivityShadowIntent = Robolectric.shadowOf(startedActivityIntent);
        assertEquals(SettingsController.class, startedActivityShadowIntent.getIntentClass());
    }

    @Test
    public void onOptionsItemSelectedWithAboutShouldOpenAboutDialog() {
        activityController.create();

        final boolean selected = controller.onOptionsItemSelected(createMenuItem(R.id.mainChatMenuAbout));

        assertTrue(selected);

        final ShadowAlertDialog latestDialog = Robolectric.getShadowApplication().getLatestAlertDialog();
        assertEquals("KouChat v" + Constants.APP_VERSION, latestDialog.getTitle());
    }

    @Test
    public void onOptionsItemSelectedWithTopicShouldOpenTopicDialog() {
        activityController.create();

        final boolean selected = controller.onOptionsItemSelected(createMenuItem(R.id.mainChatMenuTopic));

        assertTrue(selected);

        final ShadowAlertDialog latestDialog = Robolectric.getShadowApplication().getLatestAlertDialog();
        assertEquals("Topic", latestDialog.getTitle());
    }

    @Test
    public void onOptionsItemSelectedWithAwayMenuItemWhenNotAwayShouldShowGoAwayDialog() {
        activityController.create();

        when(ui.isAway()).thenReturn(false);

        final boolean selected = controller.onOptionsItemSelected(createMenuItem(R.id.mainChatMenuAway));

        assertTrue(selected);

        final ShadowAlertDialog latestDialog = Robolectric.getShadowApplication().getLatestAlertDialog();
        assertNotNull(latestDialog.getView().findViewById(R.id.goAwayDialogMessage));
    }

    @Test
    public void onOptionsItemSelectedWithAwayMenuItemWhenAwayShouldShowComeBackDialog() {
        activityController.create();

        when(ui.isAway()).thenReturn(true);
        when(ui.getMe()).thenReturn(new User("Me", 123));

        final boolean selected = controller.onOptionsItemSelected(createMenuItem(R.id.mainChatMenuAway));

        assertTrue(selected);

        final ShadowAlertDialog latestDialog = Robolectric.getShadowApplication().getLatestAlertDialog();
        assertNotNull(latestDialog.getView().findViewById(R.id.comeBackDialogMessage));
    }

    @Test
    public void onOptionsItemSelectedWithUnknownMenuItemShouldReturnFalse() {
        activityController.create();

        final boolean selected = controller.onOptionsItemSelected(createMenuItem(0));

        assertFalse(selected);
    }

    @Test
    public void dispatchKeyEventShouldDelegateToSuperClassFirst() {
        activityController.create();
        final EditText mainChatInput = TestUtils.setFieldValueWithMock(controller, "mainChatInput", EditText.class);

        // Force ActionBarSherlock to respond to the back event
        controller.startActionMode(new ActionMode.Callback() {
            public boolean onCreateActionMode(final ActionMode mode, final Menu menu) { return true; }
            public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) { return false; }
            public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) { return false; }
            public void onDestroyActionMode(final ActionMode mode) { }
        });

        assertTrue(controller.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK)));

        verifyZeroInteractions(mainChatInput); // Not delegating, and not requesting focus
    }

    @Test
    public void dispatchKeyEventShouldDelegateToMainChatInputSecond() {
        activityController.create();
        final EditText mainChatInput = TestUtils.setFieldValueWithMock(controller, "mainChatInput", EditText.class);

        final KeyEvent event1 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A);
        when(mainChatInput.dispatchKeyEvent(event1)).thenReturn(false);
        assertFalse(controller.dispatchKeyEvent(event1));
        verify(mainChatInput).dispatchKeyEvent(event1);

        final KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_B);
        when(mainChatInput.dispatchKeyEvent(event2)).thenReturn(true);
        assertTrue(controller.dispatchKeyEvent(event2));
        verify(mainChatInput).dispatchKeyEvent(event2);
    }

    @Test
    public void dispatchKeyEventShouldRequestFocusIfFocusIsMissingIfDelegatingToMainChatInput() {
        activityController.create();
        final EditText mainChatInput = TestUtils.setFieldValueWithMock(controller, "mainChatInput", EditText.class);

        when(mainChatInput.hasFocus()).thenReturn(true);
        controller.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A));
        verify(mainChatInput, never()).requestFocus();

        when(mainChatInput.hasFocus()).thenReturn(false);
        controller.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A));
        verify(mainChatInput).requestFocus();
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void onCreateOptionsMenuShouldLoadTheMainChatMenu() {
        activityController.create();
        final ActionMenu menu = new ActionMenu(controller);

        // Fails with: android.content.res.Resources$NotFoundException: Resource ID #0x7f0c0000
        controller.onCreateOptionsMenu(menu);

        assertNotNull(menu.findItem(R.id.mainChatMenu));
    }

    private ActionMenuItem createMenuItem(final int menuItemId) {
        return new ActionMenuItem(null, 0, menuItemId, 0, 0, "");
    }
}
