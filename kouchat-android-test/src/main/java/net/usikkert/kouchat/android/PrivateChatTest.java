
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
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
import net.usikkert.kouchat.android.controller.PrivateChatController;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.net.PrivateMessageResponderMock;
import net.usikkert.kouchat.util.TestClient;
import net.usikkert.kouchat.util.TestUtils;

import com.jayway.android.robotium.solo.Solo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Test of private chat.
 *
 * @author Christian Ihle
 */
public class PrivateChatTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;

    private static TestClient client;
    private static PrivateMessageResponderMock privateMessageResponder;
    private static Messages messages;
    private User me;

    public PrivateChatTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
        me = Settings.getSettings().getMe();

        // Making sure the test client only logs on once during all the tests
        if (client == null) {
            client = new TestClient();
            privateMessageResponder = client.getPrivateMessageResponderMock();
            messages = client.logon();
        }
    }

    public void test01OwnMessageIsShownInChat() {
        openPrivateChat();

        TestUtils.writeLine(solo, "This is a new message from myself");
        solo.sleep(500);

        assertTrue(TestUtils.searchText(solo, "This is a new message from myself"));
    }

    public void test02OwnMessageShouldArriveAtOtherClient() {
        openPrivateChat();

        TestUtils.writeLine(solo, "This is the second message");
        solo.sleep(500);

        assertTrue(privateMessageResponder.gotMessageArrived("This is the second message"));
    }

    public void test03OtherClientMessageIsShownInChat() {
        openPrivateChat();

        sendPrivateMessage("Hello, this is a message from someone else");

        assertTrue(solo.searchText("Hello, this is a message from someone else"));
    }

    public void test04OrientationSwitchShouldKeepText() {
        openPrivateChat();

        TestUtils.writeLine(solo, "This is the third message");

        solo.sleep(500);
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.sleep(500);

        assertTrue(solo.searchText("This is the third message"));
    }

    // Must be verified manually. I haven't found an automated way to verify scrolling yet.
    public void test05OrientationSwitchShouldScrollToBottom() {
        openPrivateChat();

        for (int i = 1; i <= 30; i++) {
            TestUtils.writeLine(solo,
                    "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! " +
                            "This is message number " + i + "! ");
        }

        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.sleep(3000); // See if message number 30 is visible
    }

    // This test actually fails if scrolling doesn't work,
    // as the link to click is out of sight because of the previous test.
    public void test06OrientationSwitchShouldKeepLinks() {
        openPrivateChat();

        TestUtils.writeLine(solo, "http://kouchat.googlecode.com/");

        solo.sleep(500);
        assertTrue(solo.getCurrentActivity().hasWindowFocus()); // KouChat is in focus
        TestUtils.clickOnText(solo, "http://kouchat.googlecode.com/");
        solo.sleep(1000);
        assertFalse(solo.getCurrentActivity().hasWindowFocus()); // Browser is in focus

        solo.sleep(3000); // Close browser manually now!
        solo.setActivityOrientation(Solo.PORTRAIT);

        solo.sleep(500);
        assertTrue(solo.getCurrentActivity().hasWindowFocus()); // KouChat is in focus
        TestUtils.clickOnText(solo, "http://kouchat.googlecode.com/");
        solo.sleep(1000);
        assertFalse(solo.getCurrentActivity().hasWindowFocus()); // Browser is in focus
    }

    // Manual step: verify that notifications work when application is hidden. Both from main and private chat.
    public void test07ShouldShowNotificationOnNewPrivateMessageAndRemoveNotificationWhenMessageSeen() {
        solo.sleep(500);
        final Bitmap envelope = getBitmap(R.drawable.envelope);
        final Bitmap dot = getBitmap(R.drawable.dot);

        // Starts with a dot
        assertEquals(dot, getBitmapForTestUser());

        // Then the envelope for a new message
        sendPrivateMessage("Hello there!");
        assertEquals(envelope, getBitmapForTestUser());

        // Look at the message, and receive a new one
        openPrivateChat();
        sendPrivateMessage("Look at me");

        // Go back. The envelope should be gone.
        solo.goBack();
        assertEquals(dot, getBitmapForTestUser());

        // New message. The envelope returns.
        sendPrivateMessage("Don't leave");
        assertEquals(envelope, getBitmapForTestUser());
    }

    public void test08ShouldNotBeAbleToChatWithSelf() {
        solo.sleep(500);
        solo.clickInList(1);
        solo.sleep(500);

        solo.assertCurrentActivity("Should have stayed in the main chat", MainChatController.class);
    }

    // TODO test other user going away
    // TODO test other user going offline

    public void test99Quit() {
        client.logoff();
        TestUtils.quit(solo);
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }

    private void openPrivateChat() {
        solo.sleep(500);
        solo.clickInList(2);
        solo.sleep(500);

        solo.assertCurrentActivity("Should have opened the private chat", PrivateChatController.class);
        assertEquals("Test - KouChat", solo.getCurrentActivity().getTitle()); // To be sure we are chatting with the right user
    }

    private void sendPrivateMessage(final String privMsg) {
        try {
            messages.sendPrivateMessage(privMsg, me);
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }

        solo.sleep(500);
    }

    private Bitmap getBitmap(final int resourceId) {
        final BitmapDrawable drawable = (BitmapDrawable) getActivity().getResources().getDrawable(resourceId);

        return drawable.getBitmap();
    }

    private Bitmap getBitmapForTestUser() {
        final LinearLayout row = (LinearLayout) solo.getCurrentListViews().get(0).getChildAt(1);
        final ImageView imageAtRow = (ImageView) row.getChildAt(0);
        final BitmapDrawable drawable = (BitmapDrawable) imageAtRow.getDrawable();

        return drawable.getBitmap();
    }
}
