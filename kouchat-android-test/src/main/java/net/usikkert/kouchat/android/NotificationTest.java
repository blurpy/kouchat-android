
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
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.net.Messages;
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
    private static Messages messages;

    private Solo solo;
    private NotificationService notificationService;

    public NotificationTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);

        final AndroidUserInterface ui =
                TestUtils.getFieldValue(activity, AndroidUserInterface.class, "androidUserInterface");
        notificationService = TestUtils.getFieldValue(ui, NotificationService.class, "notificationService");

        // Making sure the test client only logs on once during all the tests
        if (client == null) {
            client = new TestClient();
            messages = client.logon();
        }
    }

    public void test01ShouldShowNotificationWhenKouChatIsHiddenAndSomeoneWritesInTheMainChat() {
        solo.sleep(1500);
        assertDefaultNotification();

        TestUtils.closeMainChat(this);
        solo.sleep(1500);
        assertDefaultNotification();

        sendChatMessageFromTestClient("You have a new message!");
        solo.sleep(1500);
        assertNewMessageNotification();

        TestUtils.launchMainChat(this);
        solo.sleep(1500);
        assertDefaultNotification();
    }

//    public void test02ShouldShowNotificationWhenKouChatIsHiddenAndSomeoneWritesInThePrivateChat() {
//
//    }
//
//    public void test03ShouldNotShowNotificationWhenMainChatIsVisibleAndSomeUserWritesInTheMainChat() {
//
//    }
//
//    public void test04ShouldNotShowNotificationWhenMainChatIsVisibleAndSomeUserWritesInThePrivateChat() {
//
//    }
//
//    public void test05ShouldNotShowNotificationWhenPrivateChatIsVisibleAndCurrentUserWritesInThePrivateChat() {
//
//    }
//
//    public void test06ShouldShowNotificationWhenPrivateChatIsVisibleAndOtherUserWritesInAnotherPrivateChat() {
//
//    }
//
//    public void test07ShouldShowNotificationWhenPrivateChatIsVisibleAndSomeUserWritesInTheMainChat() {
//
//    }

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

    private void sendChatMessageFromTestClient(final String message) {
        try {
            messages.sendChatMessage(message);
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }
    }
}
