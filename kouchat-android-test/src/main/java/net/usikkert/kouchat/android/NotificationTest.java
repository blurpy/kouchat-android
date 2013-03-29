
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
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestClient;
import net.usikkert.kouchat.util.TestUtils;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests notifications.
 *
 * @author Christian Ihle
 */
public class NotificationTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient client;

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

        me = TestUtils.getMe(activity);

        // Making sure the test client only logs on once during all the tests
        if (client == null) {
            client = new TestClient();
            client.logon();
        }
    }

    public void test01ShouldShowNotificationWhenKouChatIsHiddenAndSomeoneWritesInTheMainChat() {
        solo.sleep(1500);
        assertDefaultNotification();

        TestUtils.closeMainChat(this);
        solo.sleep(1500);
        assertDefaultNotification();

        client.sendChatMessage("You have a new hidden message!");
        solo.sleep(1500);
        assertNewMessageNotification();

        TestUtils.launchMainChat(this);
        solo.sleep(1500);
        assertDefaultNotification();
    }

    public void test02ShouldShowNotificationWhenKouChatIsHiddenAndSomeoneWritesInThePrivateChat() {
        solo.sleep(1500);
        assertDefaultNotification();

        TestUtils.closeMainChat(this);
        solo.sleep(1500);
        assertDefaultNotification();

        client.sendPrivateChatMessage("You have a new hidden private message!", me);
        solo.sleep(1500);
        assertNewMessageNotification();

        TestUtils.launchMainChat(this);
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
        final TestClient otherUser = new TestClient("OtherUser", 12345);
        otherUser.logon();

        solo.sleep(1500);
        assertDefaultNotification();

        TestUtils.openPrivateChat(solo, 3, 3, "Test");

        otherUser.sendPrivateChatMessage("You should get a notification now!", me);
        solo.sleep(1500);
        assertNewMessageNotification();

        TestUtils.goHome(solo);
        solo.sleep(1500);
        assertDefaultNotification();

        otherUser.logoff();
    }

    public void test07ShouldShowNotificationWhenPrivateChatIsVisibleAndSomeUserWritesInTheMainChat() {
        solo.sleep(1500);
        assertDefaultNotification();

        openPrivateChat();

        client.sendChatMessage("You have been notified!");
        solo.sleep(1500);
        assertNewMessageNotification();

        TestUtils.goHome(solo);
        solo.sleep(1500);
        assertDefaultNotification();
    }

    public void test99Quit() {
        client.logoff();
        TestUtils.quit(solo);
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }

    private void assertDefaultNotification() {
        assertEquals(R.string.notification_running, notificationService.getCurrentLatestInfoTextId());
        assertEquals(R.drawable.kou_icon_24x24, notificationService.getCurrentIconId());
    }

    private void assertNewMessageNotification() {
        assertEquals(R.string.notification_new_message, notificationService.getCurrentLatestInfoTextId());
        assertEquals(R.drawable.kou_icon_activity_24x24, notificationService.getCurrentIconId());
    }

    private void openPrivateChat() {
        TestUtils.openPrivateChat(solo, 2, 2, "Test");
    }
}
