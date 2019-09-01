
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

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.chatwindow.AndroidPrivateChatWindow;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowHandler;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.util.ActivityController;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.view.menu.ActionMenuItem;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Test of {@link PrivateChatController}.
 *
 * @author Christian Ihle
 */
@Config(reportSdk = 10)
@RunWith(RobolectricTestRunner.class)
public class PrivateChatControllerTest {

    private ActivityController<PrivateChatController> activityController;
    private PrivateChatController controller;

    private ControllerUtils controllerUtils;
    private User vivi;
    private AndroidUserInterface ui;
    private AndroidPrivateChatWindow chatWindow;

    @Before
    public void setUp() {
        activityController = Robolectric.buildActivity(PrivateChatController.class);
        controller = activityController.get();

        vivi = new User("Vivi", 1234);
        ui = mock(AndroidUserInterface.class);
        chatWindow = mock(AndroidPrivateChatWindow.class);

        when(ui.getUser(1234)).thenReturn(vivi);
        doAnswer(withChatWindowForVivi(chatWindow)).when(ui).createPrivChat(vivi);

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        final Intent intent = new Intent(Robolectric.application, PrivateChatController.class);
        intent.putExtra("userCode", 1234);
        activityController.withIntent(intent);

        controllerUtils = TestUtils.setFieldValueWithMock(controller, "controllerUtils", ControllerUtils.class);
    }

    @Test
    public void onCreateShouldBindChatServiceToSetAndroidUserInterface() {
        activityController.create();

        assertSame(ui, TestUtils.getFieldValue(controller, AndroidUserInterface.class, "androidUserInterface"));

        final ShadowIntent startedServiceIntent =
                Robolectric.shadowOf(Robolectric.getShadowApplication().getNextStartedService());
        assertEquals(ChatService.class, startedServiceIntent.getIntentClass());
    }

    @Test
    public void onCreateShouldEnableUpInActionBar() {
        activityController.create();

        final ActionBar actionBar = controller.getSupportActionBar();
        final int displayOptions = actionBar.getDisplayOptions();

        assertTrue((displayOptions & ActionBar.DISPLAY_HOME_AS_UP) != 0);
    }

    @Test
    public void onCreateShouldMakeLinksClickable() {
        activityController.create();

        final TextView privateChatView = (TextView) controller.findViewById(R.id.privateChatView);

        verify(controllerUtils).makeLinksClickable(privateChatView);
    }

    @Test
    public void onCreateShouldRequestFocusOnInputToOpenKeyboard() {
        activityController.create();

        final EditText privateChatInput = (EditText) controller.findViewById(R.id.privateChatInput);

        assertTrue(privateChatInput.hasFocus());
    }

    @Test
    public void onCreateWithNoUserShouldSetDefaultTitleAndNoSubtitle() {
        activityController.withIntent(null);

        activityController.create();

        final ActionBar actionBar = controller.getSupportActionBar();

        assertEquals("User not found - KouChat", actionBar.getTitle());
        assertNull(actionBar.getSubtitle());
        verifyZeroInteractions(chatWindow);
    }

    @Test
    public void onCreateWithUserShouldSetTitleAndSubtitleFromChatWindow() {
        doAnswer(withTitle("Title from chat window", "Subtitle from chat window")).when(chatWindow).updateTitle();

        activityController.create();

        final ActionBar actionBar = controller.getSupportActionBar();

        assertEquals("Title from chat window", actionBar.getTitle());
        assertEquals("Subtitle from chat window", actionBar.getSubtitle());
        verify(chatWindow).updateTitle();
    }

    @Test
    public void onCreateShouldRegisterKeyListenerThatSendsMessageAndClearsInputOnEnter() {
        activityController.create();

        final EditText privateChatInput = (EditText) controller.findViewById(R.id.privateChatInput);
        privateChatInput.setText("Hello");

        privateChatInput.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));

        verify(ui).sendPrivateMessage("Hello", vivi);
        assertEquals("", privateChatInput.getText().toString());
    }

    @Test
    public void onCreateShouldRegisterKeyListenerThatIgnoresOtherEvents() {
        activityController.create();

        final EditText privateChatInput = (EditText) controller.findViewById(R.id.privateChatInput);
        privateChatInput.setText("Hello");

        privateChatInput.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        privateChatInput.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE));

        verify(ui, never()).sendPrivateMessage(anyString(), any(User.class));
        assertEquals("Hello", privateChatInput.getText().toString());
    }

    @Test
    public void onCreateShouldNotRegisterKeyListenerWhenUnknownUser() {
        activityController.withIntent(null);

        activityController.create();

        final EditText privateChatInput = (EditText) controller.findViewById(R.id.privateChatInput);
        privateChatInput.setText("Hello");

        privateChatInput.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));

        verify(ui, never()).sendPrivateMessage(anyString(), any(User.class));
        assertEquals("Hello", privateChatInput.getText().toString());
    }

    @Test
    public void onCreateShouldSetupUserAndRegisterControllerWithChatWindow() {
        assertNull(vivi.getPrivchat());

        activityController.create();

        verify(ui).getUser(1234);
        verify(ui).createPrivChat(vivi);

        final AndroidPrivateChatWindow viviChatWindow = (AndroidPrivateChatWindow) vivi.getPrivchat();
        verify(viviChatWindow).registerPrivateChatController(controller);
        assertSame(chatWindow, viviChatWindow);
    }

    @Test
    public void onCreateShouldNotSetupUserAndRegisterControllerWithChatWindowWhenUnknownUser() {
        activityController.withIntent(null);

        activityController.create();

        verify(ui).getUser(-1);
        verify(ui, never()).createPrivChat(any(User.class));
        assertNull(vivi.getPrivchat());
    }

    @Test
    public void onCreateShouldNotifyAndroidUserInterfaceToResetNewPrivateMessageIcon() {
        activityController.create();

        verify(ui).activatedPrivChat(vivi);
    }

    @Test
    public void onCreateShouldNotNotifyAndroidUserInterfaceWhenUnknownUser() {
        activityController.withIntent(null);

        activityController.create();

        verify(ui, never()).activatedPrivChat(any(User.class));
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
    public void onResumeShouldNotifyAndroidUserInterfaceToResetNewPrivateMessageIcon() {
        activityController.create();
        reset(ui); // Also happens during onCreate()

        activityController.resume();

        verify(ui).activatedPrivChat(vivi);
    }

    @Test
    public void onResumeShouldNotNotifyAndroidUserInterfaceWhenUnknownUser() {
        activityController.withIntent(null);
        activityController.create();

        activityController.resume();

        verify(ui, never()).activatedPrivChat(any(User.class));
    }

    @Test
    public void onResumeShouldNotFailIfServiceHasNotBeenBoundYet() {
        controller.onResume();

        verify(ui, never()).activatedPrivChat(any(User.class));
    }

    @Test
    public void onDestroyShouldUnregister() {
        activityController.create();

        // Replacing with mocks for easier verification
        final AndroidPrivateChatWindow privateChatWindow =
                TestUtils.setFieldValueWithMock(controller, "privateChatWindow", AndroidPrivateChatWindow.class);
        final EditText privateChatInput = TestUtils.setFieldValueWithMock(controller, "privateChatInput", EditText.class);
        final TextView privateChatView = TestUtils.setFieldValueWithMock(controller, "privateChatView", TextView.class);

        activityController.destroy();

        verify(privateChatWindow).unregisterPrivateChatController();
        verify(privateChatInput).setOnKeyListener(null);
        verify(controllerUtils).removeReferencesToTextViewFromText(privateChatView);
        verify(controllerUtils).removeReferencesToTextViewFromText(privateChatInput);
        assertEquals(1, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void onDestroyShouldSetAllFieldsToNull() {
        activityController.create();

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

        TestUtils.setFieldValue(controller, "privateChatWindow", null);
        TestUtils.setFieldValue(controller, "user", null);
        TestUtils.setFieldValue(controller, "androidUserInterface", null);

        activityController.destroy();

        assertEquals(0, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void appendToPrivateChatShouldAppendAndScrollToBottomIfInputHasFocus() {
        activityController.create();

        final EditText privateChatInput = (EditText) controller.findViewById(R.id.privateChatInput);
        final TextView privateChatView = (TextView) controller.findViewById(R.id.privateChatView);
        final ScrollView privateChatScroll = (ScrollView) controller.findViewById(R.id.privateChatScroll);

        privateChatView.setText("Original text");
        privateChatInput.requestFocus();
        assertTrue(privateChatInput.hasFocus());

        controller.appendToPrivateChat("\nSome text");

        assertEquals("Original text\nSome text", privateChatView.getText().toString());
        verify(controllerUtils).scrollTextViewToBottom(privateChatView, privateChatScroll);
    }

    @Test
    public void appendToPrivateChatShouldOnlyAppendIfInputLacksFocus() {
        activityController.create();

        final EditText privateChatInput = (EditText) controller.findViewById(R.id.privateChatInput);
        final TextView privateChatView = (TextView) controller.findViewById(R.id.privateChatView);

        privateChatView.setText("Original text");
        privateChatView.requestFocus();
        assertFalse(privateChatInput.hasFocus());

        controller.appendToPrivateChat("\nSome other text");

        assertEquals("Original text\nSome other text", privateChatView.getText().toString());
        verify(controllerUtils, never()).scrollTextViewToBottom(any(TextView.class), any(ScrollView.class));
    }

    @Test
    public void appendToPrivateChatShouldDoNothingIfDestroyed() {
        activityController.create();

        final TextView privateChatView = (TextView) controller.findViewById(R.id.privateChatView);
        privateChatView.setText("Original text");

        activityController.destroy();

        controller.appendToPrivateChat("\nDon't append");

        assertEquals("Original text", privateChatView.getText().toString());
        verify(controllerUtils, never()).scrollTextViewToBottom(any(TextView.class), any(ScrollView.class));
    }

    @Test
    public void updatePrivateChatShouldSetTextAndScrollToBottomIfNotDestroyed() {
        activityController.create();

        final TextView privateChatView = (TextView) controller.findViewById(R.id.privateChatView);
        final ScrollView privateChatScroll = (ScrollView) controller.findViewById(R.id.privateChatScroll);
        privateChatView.setText("Original text");

        controller.updatePrivateChat("Set this text");

        ShadowHandler.runMainLooperOneTask();

        assertEquals("Set this text", privateChatView.getText().toString());
        verify(controllerUtils).scrollTextViewToBottom(privateChatView, privateChatScroll);
    }

    @Test
    public void updatePrivateChatShouldSetTextAndNotScrollToBottomIfDestroyed() {
        activityController.create();

        final TextView privateChatView = (TextView) controller.findViewById(R.id.privateChatView);
        privateChatView.setText("Original text");

        controller.updatePrivateChat("Set this text");

        activityController.destroy(); // onDestroy() runs between setting the text and the delayed handler that scrolls
        ShadowHandler.runMainLooperOneTask();

        assertEquals("Set this text", privateChatView.getText().toString());
        verify(controllerUtils, never()).scrollTextViewToBottom(any(TextView.class), any(ScrollView.class));
    }

    @Test
    public void updateTitleAndSubtitleShouldSetTheSpecifiedTitleAndSubtitle() {
        activityController.create();
        final ActionBar actionBar = controller.getSupportActionBar();

        controller.updateTitleAndSubtitle("This is the title", "This is the subtitle");

        assertEquals("This is the title", actionBar.getTitle());
        assertEquals("This is the subtitle", actionBar.getSubtitle());
    }

    @Test
    public void dispatchKeyEventShouldDelegateToSuperClassFirst() {
        activityController.create();
        final EditText privateChatInput = TestUtils.setFieldValueWithMock(controller, "privateChatInput", EditText.class);

        // Force ActionBarSherlock to respond to the back event
        controller.startActionMode(new ActionMode.Callback() {
            public boolean onCreateActionMode(final ActionMode mode, final Menu menu) { return true; }
            public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) { return false; }
            public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) { return false; }
            public void onDestroyActionMode(final ActionMode mode) { }
        });

        assertTrue(controller.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK)));
        verifyZeroInteractions(privateChatInput); // Not delegating, and not requesting focus
    }

    @Test
    public void dispatchKeyEventShouldDelegateToPrivateChatInputSecond() {
        activityController.create();
        final EditText privateChatInput = TestUtils.setFieldValueWithMock(controller, "privateChatInput", EditText.class);

        final KeyEvent event1 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A);
        when(privateChatInput.dispatchKeyEvent(event1)).thenReturn(false);
        assertFalse(controller.dispatchKeyEvent(event1));
        verify(privateChatInput).dispatchKeyEvent(event1);

        final KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_B);
        when(privateChatInput.dispatchKeyEvent(event2)).thenReturn(true);
        assertTrue(controller.dispatchKeyEvent(event2));
        verify(privateChatInput).dispatchKeyEvent(event2);
    }

    @Test
    public void dispatchKeyEventShouldRequestFocusIfFocusIsMissingIfDelegatingToPrivateChatInput() {
        activityController.create();
        final EditText privateChatInput = TestUtils.setFieldValueWithMock(controller, "privateChatInput", EditText.class);

        when(privateChatInput.hasFocus()).thenReturn(true);
        controller.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A));
        verify(privateChatInput, never()).requestFocus();

        when(privateChatInput.hasFocus()).thenReturn(false);
        controller.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A));
        verify(privateChatInput).requestFocus();
    }

    @Test
    public void onOptionsItemSelectedWithUpShouldGoBackToMainChat() {
        activityController.create();

        final boolean selected = controller.onOptionsItemSelected(createMenuItem(android.R.id.home));

        assertTrue(selected);

        final Intent startedActivityIntent = Robolectric.getShadowApplication().getNextStartedActivity();
        final ShadowIntent startedActivityShadowIntent = Robolectric.shadowOf(startedActivityIntent);
        assertEquals(MainChatController.class, startedActivityShadowIntent.getIntentClass());
    }

    @Test
    public void onOptionsItemSelectedWithUnknownMenuItemShouldReturnFalse() {
        activityController.create();

        final boolean selected = controller.onOptionsItemSelected(createMenuItem(0));

        assertFalse(selected);
    }

    @Test
    public void sendPrivateMessageShouldSendToUserUsingAndroidUserInterface() {
        activityController.create();

        controller.sendPrivateMessage("A private message");

        verify(ui).sendPrivateMessage("A private message", vivi);
    }

    @Test
    public void sendPrivateMessageShouldNotSendIfMessageIsNull() {
        activityController.create();

        controller.sendPrivateMessage(null);

        verify(ui, never()).sendPrivateMessage(anyString(), any(User.class));
    }

    @Test
    public void sendPrivateMessageShouldNotSendIfMessageIsWhitespace() {
        activityController.create();

        controller.sendPrivateMessage(" ");

        verify(ui, never()).sendPrivateMessage(anyString(), any(User.class));
    }

    private ActionMenuItem createMenuItem(final int menuItemId) {
        return new ActionMenuItem(null, 0, menuItemId, 0, 0, "");
    }

    private Answer<Void> withChatWindowForVivi(final AndroidPrivateChatWindow privateChatWindow) {
        return new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) {
                vivi.setPrivchat(privateChatWindow);
                return null;
            }
        };
    }

    private Answer withTitle(final String title, final String subtitle) {
        return new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) {
                controller.updateTitleAndSubtitle(title, subtitle);
                return null;
            }
        };
    }
}
