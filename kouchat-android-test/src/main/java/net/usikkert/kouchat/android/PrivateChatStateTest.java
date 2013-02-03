
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
import net.usikkert.kouchat.android.testcase.PrivateChatTestCase;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.net.PrivateMessageResponderMock;
import net.usikkert.kouchat.util.TestClient;
import net.usikkert.kouchat.util.TestUtils;

/**
 * Test of private chat.
 *
 * @author Christian Ihle
 */
public class PrivateChatStateTest extends PrivateChatTestCase {

    public void test01ShouldShowNotificationOnNewPrivateMessageAndRemoveNotificationWhenMessageIsSeen() {
        // Starts with a dot
        assertEquals(dot, getBitmapForTestUser());

        // Then the envelope for a new message
        sendPrivateMessage("Hello there!");
        assertEquals(envelope, getBitmapForTestUser());

        // Look at the message, and receive a new one
        openPrivateChat();
        sendPrivateMessage("Look at me");

        // Go back. The envelope should be gone.
        TestUtils.goBack(solo);
        assertEquals(dot, getBitmapForTestUser());

        // New message. The envelope returns.
        sendPrivateMessage("Don't leave");
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
        sendPrivateMessage("You can't see me!");
        solo.sleep(500);

        // Reopen the main chat
        reopenMainChat();

        // Should have a notification about the new message
        assertEquals(envelope, getBitmapForTestUser());

        // Read message and make envelope go away for the next tests
        openPrivateChat();
    }

    public void test04GettingPrivateMessageWhilePrivateChatIsHiddenAndReturningToMainChatShouldShowNotification() {
        openPrivateChat();

        // Pretend to click "home" while in the private chat
        getInstrumentation().callActivityOnPause(solo.getCurrentActivity());

        // Receive private message while "home"
        sendPrivateMessage("You are not looking!");
        solo.sleep(500);

        // Pretend to open the main chat from the list of running applications. This does not "resume" the private chat
        TestUtils.goBack(solo);

        // There should be a notification about the new private message
        assertEquals(envelope, getBitmapForTestUser());
    }

    public void test05GettingPrivateMessageWhilePrivateChatIsHiddenAndReturningToPrivateChatShouldHideNotification() {
        openPrivateChat();

        // Pretend to turn off the screen while in the private chat
        getInstrumentation().callActivityOnPause(solo.getCurrentActivity());

        // Receive private message while the screen is "off"
        sendPrivateMessage("You are still not looking!");
        solo.sleep(500);

        // Turn the screen back "on" again and return to the private chat
        getInstrumentation().callActivityOnResume(solo.getCurrentActivity());

        // Go back to the main chat
        TestUtils.goBack(solo);

        // The notification about the new private message should be gone
        assertEquals(dot, getBitmapForTestUser());
    }

    // A more complicated scenario
    public void test06PrivateChattingWithSeveralUsersShouldCommunicateCorrectly() {
        final TestClient client2 = new TestClient("Test2", 12345679);
        final Messages messages2 = client2.logon();
        final PrivateMessageResponderMock privateMessageResponder2 = client2.getPrivateMessageResponderMock();
        solo.sleep(1000);

        // New message from first user
        sendPrivateMessage("First message from user 1");
        assertEquals(dot, getBitmapForUser(3, 1)); // Me
        assertEquals(envelope, getBitmapForUser(3, 2)); // Test
        assertEquals(dot, getBitmapForUser(3, 3)); // Test2

        // New message from second user
        sendPrivateMessage("First message from user 2", messages2);
        assertEquals(envelope, getBitmapForUser(3, 3));

        // Chat with first user
        openPrivateChat(3, 2, "Test");
        assertTrue(solo.searchText("First message from user 1"));
        TestUtils.writeLine(solo, "Hello user 1");
        solo.sleep(500);
        assertTrue(privateMessageResponder.gotMessageArrived("Hello user 1"));
        sendPrivateMessage("Second message from user 1");
        solo.sleep(500);
        assertTrue(solo.searchText("Second message from user 1"));

        // Check that the messages from the first user has been read
        TestUtils.goBack(solo);
        assertEquals(dot, getBitmapForUser(3, 2));
        assertEquals(envelope, getBitmapForUser(3, 3));

        // Chat with second user
        openPrivateChat(3, 3, "Test2");
        assertTrue(solo.searchText("First message from user 2"));
        TestUtils.writeLine(solo, "Hello user 2");
        solo.sleep(500);
        assertTrue(privateMessageResponder2.gotMessageArrived("Hello user 2"));
        sendPrivateMessage("Second message from user 2", messages2);
        solo.sleep(500);
        assertTrue(solo.searchText("Second message from user 2"));

        // Get another message from the first user, while still in the chat with the second
        sendPrivateMessage("Third message from user 1");
        solo.sleep(500);

        // Check that the messages from the second user has been read, and that a new has arrived from the first
        TestUtils.goBack(solo);
        assertEquals(envelope, getBitmapForUser(3, 2));
        assertEquals(dot, getBitmapForUser(3, 3));

        // Check message from first user
        openPrivateChat(3, 2, "Test");
        assertTrue(solo.searchText("Third message from user 1"));

        // Check that the message has been read
        TestUtils.goBack(solo);
        assertEquals(dot, getBitmapForUser(3, 2));

        solo.sleep(500);
        client2.logoff();
    }

    public void test07SuspendingPrivateChatWithOneUserAndStartingANewChatWithAnotherUserShouldShownTheCorrectMessage() {
        final TestClient client2 = new TestClient("Test2", 12345679);
        final Messages messages2 = client2.logon();
        solo.sleep(1000);

        // Get message from first user, and open the chat
        sendPrivateMessage("Message from user 1");
        openPrivateChat(3, 2, "Test");
        assertTrue(solo.searchText("Message from user 1"));

        // Pretend to click "home" while in the private chat
        getInstrumentation().callActivityOnPause(solo.getCurrentActivity());

        // Pretend to open the main chat from the list of running applications. This does not "resume" the private chat
        TestUtils.goBack(solo);

        // Get message from the second user, and open the chat
        sendPrivateMessage("Message from user 2", messages2);
        openPrivateChat(3, 3, "Test2");
        assertTrue(solo.searchText("Message from user 2"));

        // Get new message from the first user, while still in the chat with the second user
        sendPrivateMessage("New message from user 1");

        // Go back and look at the new message from the first user
        TestUtils.goBack(solo);
        openPrivateChat(3, 2, "Test");
        assertTrue(solo.searchText("New message from user 1"));

        solo.sleep(500);
        client2.logoff();
    }

    public void test08ShouldNotBeAbleToSendPrivateMessageToUserThatGoesAway() {
        openPrivateChat();

        solo.sleep(500);
        messages.sendAwayMessage("Going away now");
        solo.sleep(500);

        assertEquals("Test (away: Going away now) - KouChat", solo.getCurrentActivity().getTitle());

        TestUtils.writeLine(solo, "Don't leave me!");
        solo.sleep(500);

        assertTrue(solo.searchText("You can not send a private chat message to a user that is away"));
        assertFalse(privateMessageResponder.gotAnyMessage());

        solo.sleep(500);
        messages.sendBackMessage();
        solo.sleep(500);

        assertEquals("Test - KouChat", solo.getCurrentActivity().getTitle());

        TestUtils.writeLine(solo, "You are back!");
        solo.sleep(500);
        assertTrue(privateMessageResponder.gotMessageArrived("You are back!"));
    }

    public void test09ShouldNotBeAbleToSendPrivateMessageToUserThatIsAway() {
        solo.sleep(500);
        messages.sendAwayMessage("Going away now");
        solo.sleep(500);

        openPrivateChat(2, 2, "Test (away: Going away now)");

        TestUtils.writeLine(solo, "Don't leave me!");
        solo.sleep(500);

        assertTrue(solo.searchText("You can not send a private chat message to a user that is away"));
        assertFalse(privateMessageResponder.gotAnyMessage());

        messages.sendBackMessage();
        solo.sleep(500);
    }

    public void test10ShouldNotBeAbleToSendPrivateMessageToUserThatIsOffline() {
        openPrivateChat();

        solo.sleep(500);
        client.logoff();
        solo.sleep(500);

        assertEquals("Test (offline) - KouChat", solo.getCurrentActivity().getTitle());

        TestUtils.writeLine(solo, "Don't leave me!");
        solo.sleep(500);

        assertTrue(solo.searchText("You can not send a private chat message to a user that is offline"));
        assertFalse(privateMessageResponder.gotAnyMessage());
    }
}
