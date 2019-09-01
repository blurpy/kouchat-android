
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

package net.usikkert.kouchat.android;

import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestClient;

import com.robotium.solo.Solo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Test of private chat.
 *
 * @author Christian Ihle
 */
public class PrivateChatStateTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient client;

    private TestClient client2;

    private Solo solo;

    private Bitmap envelope;
    private Bitmap dot;

    private User me;

    private int defaultOrientation;

    public PrivateChatStateTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController activity = getActivity();

        solo = new Solo(getInstrumentation(), activity);
        me = RobotiumTestUtils.getMe(activity);
        envelope = getBitmap(R.drawable.ic_envelope);
        dot = getBitmap(R.drawable.ic_dot);
        defaultOrientation = RobotiumTestUtils.getCurrentOrientation(solo);

        // Making sure the test client only logs on once during all the tests
        if (client == null) {
            client = new TestClient();
            client.logon();
        }

        client.resetPrivateMessages();
    }

    public void test001NoOp() {
        // The first test seems to fail on the newest 2.3.3 emulator for some reason.
        // Adding an empty test to work around the issue.
    }

    public void test01ShouldShowNotificationOnNewPrivateMessageAndRemoveNotificationWhenMessageIsSeen() {
        // Starts with a dot
        assertEquals(dot, getBitmapForTestUser());

        // Then the envelope for a new message
        client.sendPrivateChatMessage("Hello there!", me);
        assertEquals(envelope, getBitmapForTestUser());

        // Look at the message, and receive a new one
        openPrivateChat();
        client.sendPrivateChatMessage("Look at me", me);

        // Go back. The envelope should be gone.
        RobotiumTestUtils.goBack(solo);
        assertEquals(dot, getBitmapForTestUser());

        // New message. The envelope returns.
        client.sendPrivateChatMessage("Don't leave", me);
        assertEquals(envelope, getBitmapForTestUser());

        // Read message and make envelope go away for the next tests
        openPrivateChat();
    }

    public void test02ShouldNotBeAbleToOpenPrivateChatWithYourself() {
        solo.sleep(500);
        solo.clickInList(1);
        solo.sleep(500);

        solo.assertCurrentActivity("Should have stayed in the main chat", MainChatController.class);
    }

    public void test03GettingPrivateMessageWhileKouChatIsHiddenShouldShowNotificationWhenVisibleAgain() {
        // Verify fresh start
        assertEquals(dot, getBitmapForTestUser());

        // Hide KouChat
        getActivity().finish();
        solo.sleep(500);

        // Receive private message while hidden
        client.sendPrivateChatMessage("You can't see me!", me);
        solo.sleep(500);

        // Reopen the main chat
        RobotiumTestUtils.launchMainChat(this);

        // Should have a notification about the new message
        assertEquals(envelope, getBitmapForTestUser());

        // Read message and make envelope go away for the next tests
        openPrivateChat();
    }

    public void test04GettingPrivateMessageWhilePrivateChatIsHiddenAndReturningToMainChatShouldShowNotification() {
        openPrivateChat();

        // Pretend to click "home" while in the private chat
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                getInstrumentation().callActivityOnPause(solo.getCurrentActivity());
            }
        });

        // Receive private message while "home"
        client.sendPrivateChatMessage("You are not looking!", me);
        solo.sleep(500);

        // Pretend to open the main chat from the list of running applications. This does not "resume" the private chat
        RobotiumTestUtils.goBack(solo);

        // There should be a notification about the new private message
        assertEquals(envelope, getBitmapForTestUser());
    }

    public void test05GettingPrivateMessageWhilePrivateChatIsHiddenAndReturningToPrivateChatShouldHideNotification() {
        openPrivateChat();

        // Pretend to turn off the screen while in the private chat
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                getInstrumentation().callActivityOnPause(solo.getCurrentActivity());
            }
        });

        // Receive private message while the screen is "off"
        client.sendPrivateChatMessage("You are still not looking!", me);
        solo.sleep(500);

        // Turn the screen back "on" again and return to the private chat
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                getInstrumentation().callActivityOnResume(solo.getCurrentActivity());
            }
        });

        // Go back to the main chat
        RobotiumTestUtils.goBack(solo);

        // The notification about the new private message should be gone
        assertEquals(dot, getBitmapForTestUser());
    }

    // A more complicated scenario
    public void test06PrivateChattingWithSeveralUsersShouldCommunicateCorrectly() {
        client2 = new TestClient("Test2", 12345679);
        client2.logon();
        solo.sleep(1000);

        // New message from first user
        client.sendPrivateChatMessage("First message from user 1", me);
        assertEquals(dot, getBitmapForUser(3, 1)); // Me
        assertEquals(envelope, getBitmapForUser(3, 2)); // Test
        assertEquals(dot, getBitmapForUser(3, 3)); // Test2

        // New message from second user
        client2.sendPrivateChatMessage("First message from user 2", me);
        assertEquals(envelope, getBitmapForUser(3, 3));

        // Chat with first user
        RobotiumTestUtils.openPrivateChat(solo, getInstrumentation(), 3, 2, "Test");
        assertTrue(solo.searchText("First message from user 1"));
        RobotiumTestUtils.writeLine(solo, "Hello user 1");
        solo.sleep(500);
        assertTrue(client.gotPrivateMessage(me, "Hello user 1"));
        client.sendPrivateChatMessage("Second message from user 1", me);
        solo.sleep(500);
        assertTrue(solo.searchText("Second message from user 1"));

        // Check that the messages from the first user has been read
        RobotiumTestUtils.goBack(solo);
        assertEquals(dot, getBitmapForUser(3, 2));
        assertEquals(envelope, getBitmapForUser(3, 3));

        // Chat with second user
        RobotiumTestUtils.openPrivateChat(solo, getInstrumentation(), 3, 3, "Test2");
        assertTrue(solo.searchText("First message from user 2"));
        RobotiumTestUtils.writeLine(solo, "Hello user 2");
        solo.sleep(500);
        assertTrue(client2.gotPrivateMessage(me, "Hello user 2"));
        client2.sendPrivateChatMessage("Second message from user 2", me);
        solo.sleep(500);
        assertTrue(solo.searchText("Second message from user 2"));

        // Get another message from the first user, while still in the chat with the second
        client.sendPrivateChatMessage("Third message from user 1", me);
        solo.sleep(500);

        // Check that the messages from the second user has been read, and that a new has arrived from the first
        RobotiumTestUtils.goBack(solo);
        assertEquals(envelope, getBitmapForUser(3, 2));
        assertEquals(dot, getBitmapForUser(3, 3));

        // Check message from first user
        RobotiumTestUtils.openPrivateChat(solo, getInstrumentation(), 3, 2, "Test");
        assertTrue(solo.searchText("Third message from user 1"));

        // Check that the message has been read
        RobotiumTestUtils.goBack(solo);
        assertEquals(dot, getBitmapForUser(3, 2));

        solo.sleep(500);
    }

    public void test07SuspendingPrivateChatWithOneUserAndStartingANewChatWithAnotherUserShouldShownTheCorrectMessage() {
        client2 = new TestClient("Test2", 12345679);
        client2.logon();
        solo.sleep(1000);

        // Get message from first user, and open the chat
        client.sendPrivateChatMessage("Message from user 1", me);
        RobotiumTestUtils.openPrivateChat(solo, getInstrumentation(), 3, 2, "Test");
        assertTrue(solo.searchText("Message from user 1"));

        // Pretend to click "home" while in the private chat
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                getInstrumentation().callActivityOnPause(solo.getCurrentActivity());
            }
        });

        // Pretend to open the main chat from the list of running applications. This does not "resume" the private chat
        RobotiumTestUtils.goBack(solo);

        // Get message from the second user, and open the chat
        client2.sendPrivateChatMessage("Message from user 2", me);
        RobotiumTestUtils.openPrivateChat(solo, getInstrumentation(), 3, 3, "Test2");
        assertTrue(solo.searchText("Message from user 2"));

        // Get new message from the first user, while still in the chat with the second user
        client.sendPrivateChatMessage("New message from user 1", me);

        // Go back and look at the new message from the first user
        RobotiumTestUtils.goBack(solo);
        RobotiumTestUtils.openPrivateChat(solo, getInstrumentation(), 3, 2, "Test");
        assertTrue(solo.searchText("New message from user 1"));

        solo.sleep(500);
    }

    public void test08ShouldNotBeAbleToSendPrivateMessageToUserThatGoesAway() {
        openPrivateChat();

        solo.sleep(500);
        client.goAway("Going away now");
        solo.sleep(500);

        checkTitle("Test (Away)", "Going away now");

        RobotiumTestUtils.writeLine(solo, "Don't leave me!");
        solo.sleep(500);

        assertTrue(solo.searchText("You can not send a private chat message to a user that is away"));
        assertFalse(client.gotAnyPrivateMessages());

        solo.sleep(500);
        client.comeBack();
        solo.sleep(500);

        checkTitle("Test", null);

        RobotiumTestUtils.writeLine(solo, "You are back!");
        solo.sleep(500);
        assertTrue(client.gotPrivateMessage(me, "You are back!"));
    }

    public void test09ShouldNotBeAbleToSendPrivateMessageToUserThatIsAway() {
        solo.sleep(500);
        client.goAway("Going away again");
        solo.sleep(500);

        RobotiumTestUtils.openPrivateChat(solo, getInstrumentation(), 2, 2, "Test (Away)", "Going away again");

        RobotiumTestUtils.writeLine(solo, "Don't leave me!");
        solo.sleep(500);

        assertTrue(solo.searchText("You can not send a private chat message to a user that is away"));
        assertFalse(client.gotAnyPrivateMessages());

        client.comeBack();
        solo.sleep(500);
    }

    public void test10ShouldNotBeAbleToSendPrivateMessageToUserThatIsOffline() {
        openPrivateChat();

        solo.sleep(500);
        client.logoff();
        solo.sleep(500);

        checkTitle("Test (Offline)", null);

        RobotiumTestUtils.writeLine(solo, "Don't leave me!");
        solo.sleep(500);

        assertTrue(solo.searchText("You can not send a private chat message to a user that is offline"));
        assertFalse(client.gotAnyPrivateMessages());
    }

    public void test99Quit() {
        client.logoff();
        RobotiumTestUtils.quit(solo);

        client = null;
    }

    public void tearDown() {
        if (client2 != null) {
            client2.logoff();
        }

        RobotiumTestUtils.setOrientation(solo, defaultOrientation);
        solo.finishOpenedActivities();

        client2 = null;
        solo = null;
        envelope = null;
        dot = null;
        me = null;
        setActivity(null);

        System.gc();
    }

    private void openPrivateChat() {
        RobotiumTestUtils.openPrivateChat(solo, getInstrumentation(), 2, 2, "Test");
    }

    private Bitmap getBitmapForTestUser() {
        return getBitmapForUser(2, 2);
    }

    private Bitmap getBitmapForUser(final int numberOfUsers, final int userNumber) {
        solo.sleep(1000);
        assertEquals(numberOfUsers, solo.getCurrentViews(ListView.class).get(0).getCount());
        final LinearLayout row = (LinearLayout) solo.getCurrentViews(ListView.class).get(0).getChildAt(userNumber - 1);
        final ImageView imageAtRow = (ImageView) row.getChildAt(0);
        final BitmapDrawable drawable = (BitmapDrawable) imageAtRow.getDrawable();

        return drawable.getBitmap();
    }

    private Bitmap getBitmap(final int resourceId) {
        final BitmapDrawable drawable = (BitmapDrawable) solo.getCurrentActivity().getResources().getDrawable(resourceId);

        return drawable.getBitmap();
    }

    private void checkTitle(final String title, final String subtitle) {
        final AppCompatActivity currentActivity = (AppCompatActivity) solo.getCurrentActivity();
        final ActionBar actionBar = currentActivity.getSupportActionBar();

        getInstrumentation().waitForIdleSync();
        solo.sleep(500);

        assertEquals(title, actionBar.getTitle());
        assertEquals(subtitle, actionBar.getSubtitle());
    }
}
