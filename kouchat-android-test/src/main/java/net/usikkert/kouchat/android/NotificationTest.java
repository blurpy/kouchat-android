
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

package net.usikkert.kouchat.android;

import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.notification.NotificationService;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestClient;
import net.usikkert.kouchat.testclient.TestUtils;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests notifications.
 *
 * @author Christian Ihle
 */
public class NotificationTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient client;
    private TestClient otherUser;

    private Solo solo;
    private NotificationService notificationService;
    private User me;

    public NotificationTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);

        final AndroidUserInterface ui =
                TestUtils.getFieldValue(activity, AndroidUserInterface.class, "androidUserInterface");
        notificationService = TestUtils.getFieldValue(ui, NotificationService.class, "notificationService");

        me = RobotiumTestUtils.getMe(activity);

        // Making sure the test client only logs on once during all the tests
        if (client == null) {
            client = new TestClient();
            client.logon();
        }
    }

    public void test01ShouldShowNotificationWhenKouChatIsHiddenAndSomeoneWritesInTheMainChat() {
        solo.sleep(1500);
        assertDefaultNotification();

        RobotiumTestUtils.closeMainChat(this);
        solo.sleep(1500);
        assertDefaultNotification();

        client.sendChatMessage("You have a new hidden message!");
        solo.sleep(1500);
        assertNewMessageNotification();

        RobotiumTestUtils.launchMainChat(this);
        solo.sleep(1500);
        assertDefaultNotification();
    }

    public void test02ShouldShowNotificationWhenKouChatIsHiddenAndSomeoneWritesInThePrivateChat() {
        solo.sleep(1500);
        assertDefaultNotification();

        RobotiumTestUtils.closeMainChat(this);
        solo.sleep(1500);
        assertDefaultNotification();

        client.sendPrivateChatMessage("You have a new hidden private message!", me);
        solo.sleep(1500);
        assertNewMessageNotification();

        RobotiumTestUtils.launchMainChat(this);
        solo.sleep(1500);
        assertDefaultNotification();

        openPrivateChat(); // To reset envelope
    }

    public void test03ShouldNotShowNotificationWhenMainChatIsVisibleAndSomeUserWritesInTheMainChat() {
        solo.sleep(1500);
        assertDefaultNotification();

        client.sendChatMessage("You have a new visible message!");
        solo.sleep(1500);
        assertDefaultNotification();
    }

    public void test04ShouldNotShowNotificationWhenMainChatIsVisibleAndSomeUserWritesInThePrivateChat() {
        solo.sleep(1500);
        assertDefaultNotification();

        client.sendPrivateChatMessage("You have another hidden private message!", me);
        solo.sleep(1500);
        assertDefaultNotification();

        openPrivateChat(); // To reset envelope
    }

    public void test05ShouldNotShowNotificationWhenPrivateChatIsVisibleAndCurrentUserWritesInThePrivateChat() {
        solo.sleep(1500);
        assertDefaultNotification();

        openPrivateChat();

        client.sendPrivateChatMessage("You have a new visible private message!", me);
        solo.sleep(1500);
        assertDefaultNotification();
    }

    public void test06ShouldShowNotificationWhenPrivateChatIsVisibleAndOtherUserWritesInAnotherPrivateChat() {
        otherUser = new TestClient("OtherUser", 12345);
        otherUser.logon();

        solo.sleep(1500);
        assertDefaultNotification();

        RobotiumTestUtils.openPrivateChat(solo, 3, 3, "Test");

        otherUser.sendPrivateChatMessage("You should get a notification now!", me);
        solo.sleep(1500);
        assertNewMessageNotification();

        RobotiumTestUtils.goHome(solo);
        solo.sleep(1500);
        assertDefaultNotification();
    }

    public void test07ShouldShowNotificationWhenPrivateChatIsVisibleAndSomeUserWritesInTheMainChat() {
        solo.sleep(1500);
        assertDefaultNotification();

        openPrivateChat();

        client.sendChatMessage("You have been notified!");
        solo.sleep(1500);
        assertNewMessageNotification();

        RobotiumTestUtils.goHome(solo);
        solo.sleep(1500);
        assertDefaultNotification();
    }

    public void test08ShouldNotRemoveNotificationWhenReturningToPrivateChatFromPauseAfterNewMessageInMainChat() {
        solo.sleep(1500);
        assertDefaultNotification();

        openPrivateChat();
        solo.sleep(500);

        // Pretend to start another application while in the private chat
        getInstrumentation().callActivityOnPause(solo.getCurrentActivity());
        solo.sleep(500);

        client.sendChatMessage("You should stay notified until you see this!");
        solo.sleep(500);
        assertNewMessageNotification();

        // Pretend to return to the private chat using the list of running applications
        getInstrumentation().callActivityOnResume(solo.getCurrentActivity());

        solo.sleep(1500);
        assertNewMessageNotification();

        RobotiumTestUtils.goHome(solo);
        solo.sleep(1500);
        assertDefaultNotification();
    }

    public void test09ShouldNotRemoveNotificationWhenReturningToPrivateChatFromPauseAfterNewPrivateMessageFromOtherUser() {
        otherUser = new TestClient("OtherUser", 12345);
        otherUser.logon();

        solo.sleep(1500);
        assertDefaultNotification();

        RobotiumTestUtils.openPrivateChat(solo, 3, 3, "Test");
        solo.sleep(500);

        // Pretend to start another application while in the private chat
        getInstrumentation().callActivityOnPause(solo.getCurrentActivity());
        solo.sleep(500);

        otherUser.sendPrivateChatMessage("You should stay notified until you are back in the main chat!", me);
        solo.sleep(500);
        assertNewMessageNotification();

        // Pretend to return to the private chat using the list of running applications
        getInstrumentation().callActivityOnResume(solo.getCurrentActivity());

        solo.sleep(1500);
        assertNewMessageNotification();

        RobotiumTestUtils.goHome(solo);
        solo.sleep(1500);
        assertDefaultNotification();
    }

    public void test10ShouldRemoveNotificationWhenReturningToPrivateChatFromPauseAfterNewPrivateMessageFromCurrentUser() {
        solo.sleep(1500);
        assertDefaultNotification();

        openPrivateChat();
        solo.sleep(500);

        // Pretend to start another application while in the private chat
        getInstrumentation().callActivityOnPause(solo.getCurrentActivity());
        solo.sleep(500);

        client.sendPrivateChatMessage("The notification should be reset soon!", me);
        solo.sleep(500);
        assertNewMessageNotification();

        // Pretend to return to the private chat using the list of running applications
        getInstrumentation().callActivityOnResume(solo.getCurrentActivity());

        solo.sleep(1500);
        assertDefaultNotification();
    }

    public void test99Quit() {
        client.logoff();
        RobotiumTestUtils.quit(solo);
        System.gc();
    }

    public void tearDown() {
        if (otherUser != null) {
            otherUser.logoff();
        }

        solo.finishOpenedActivities();
    }

    private void assertDefaultNotification() {
        assertEquals(R.string.notification_running, notificationService.getCurrentLatestInfoTextId());
        assertEquals(R.drawable.notification_icon_default, notificationService.getCurrentIconId());
    }

    private void assertNewMessageNotification() {
        assertEquals(R.string.notification_new_message, notificationService.getCurrentLatestInfoTextId());
        assertEquals(R.drawable.notification_icon_activity, notificationService.getCurrentIconId());
    }

    private void openPrivateChat() {
        RobotiumTestUtils.openPrivateChat(solo, 2, 2, "Test");
    }
}
