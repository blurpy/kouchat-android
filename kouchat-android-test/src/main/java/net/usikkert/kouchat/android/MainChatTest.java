
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
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestClient;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Tests sending and receiving messages in the main chat.
 *
 * @author Christian Ihle
 */
public class MainChatTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;
    private int defaultOrientation;
    private TestClient client;
    private User me;

    public MainChatTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController activity = getActivity();

        solo = new Solo(getInstrumentation(), activity);
        defaultOrientation = RobotiumTestUtils.getCurrentOrientation(solo);
        me = RobotiumTestUtils.getMe(activity);
    }

    public void test01OwnMessageIsShownInChat() {
        RobotiumTestUtils.writeLine(solo, "This is a new message from myself");
        solo.sleep(500);

        assertTrue(RobotiumTestUtils.searchText(solo, "This is a new message from myself"));
    }

    public void test02OwnMessageShouldArriveAtOtherClient() {
        client = new TestClient();
        client.logon();

        RobotiumTestUtils.writeLine(solo, "This is the second message");
        solo.sleep(500);

        assertTrue(client.gotMessage(me, "This is the second message"));
    }

    public void test03OtherClientMessageIsShownInChat() {
        client = new TestClient();
        client.logon();

        client.sendChatMessage("Hello, this is a message from someone else");
        assertTrue(solo.searchText("Hello, this is a message from someone else"));
    }

    public void test04OrientationSwitchShouldKeepText() {
        assertTrue(solo.searchText("Welcome to KouChat"));

        RobotiumTestUtils.switchOrientation(solo);

        assertTrue(solo.searchText("Welcome to KouChat"));
    }

    // Must be verified manually
    public void test05OrientationSwitchShouldKeepSmileys() {
        RobotiumTestUtils.writeLine(solo, ":) :( :p :D ;) :O :@ :S ;( :$ 8)");

        solo.sleep(2000);
        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(2000);
    }

    // Must be verified manually. I haven't found an automated way to verify scrolling yet.
    public void test06OrientationSwitchShouldScrollToBottom() {
        for (int i = 1; i <= 30; i++) {
            RobotiumTestUtils.writeLine(solo,
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

        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(3000); // See if message number 30 is visible
    }

    public void test07InputFieldShouldAlwaysGetKeyEventsAndFocus() {
        // Starts with focus
        final EditText mainChatInput = (EditText) solo.getView(R.id.mainChatInput);
        assertTrue(mainChatInput.hasFocus());

        // Keeps focus after "enter"
        RobotiumTestUtils.writeLine(solo, "Keep me focused!");
        solo.sleep(500);
        assertTrue(mainChatInput.hasFocus());

        // Keeps focus after clicking in the user list
        solo.sleep(500);
        solo.clickInList(0);
        solo.sleep(500);
        assertTrue(mainChatInput.hasFocus());

        // Need to support losing focus when clicking in the main chat to support text selection
        solo.clickOnView(solo.getView(R.id.mainChatScroll));
        solo.sleep(500);
        assertFalse(mainChatInput.hasFocus());

        // Let's enter a few key strokes when the input field lacks focus
        solo.sendKey(KeyEvent.KEYCODE_A);
        solo.sendKey(KeyEvent.KEYCODE_B);
        solo.sendKey(KeyEvent.KEYCODE_C);

        // The focus should now be back, and the keys entered should be in the input field
        solo.sleep(500);
        assertTrue(mainChatInput.hasFocus());
        assertEquals("abc", mainChatInput.getText().toString());
    }

    // Must be verified manually
    public void test08ShouldBeAbleToSelectText() {
        solo.sleep(500);

        RobotiumTestUtils.writeLine(solo, "Lets select something");
        solo.sleep(500);

        solo.clickLongOnView(solo.getView(R.id.mainChatScroll));
        solo.sleep(500);
    }

    public void test99Quit() {
        RobotiumTestUtils.quit(solo);
        System.gc();
    }

    public void tearDown() {
        if (client != null) {
            client.logoff();
        }

        RobotiumTestUtils.setOrientation(solo, defaultOrientation);
        solo.finishOpenedActivities();
    }
}
