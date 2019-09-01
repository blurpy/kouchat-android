
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

import android.graphics.Typeface;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Tests the user list.
 *
 * @author Christian Ihle
 */
public class UserListTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;
    private TestClient client;
    private User me;
    private int defaultOrientation;

    public UserListTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController activity = getActivity();

        me = RobotiumTestUtils.getMe(activity);
        me.setNick("Kou");

        solo = new Solo(getInstrumentation(), activity);
        client = new TestClient();

        solo.sleep(100);

        defaultOrientation = RobotiumTestUtils.getCurrentOrientation(solo);
    }

    public void test01UserListShouldContainMeOnLogon() {
        final ListView userList = getUserList();

        assertEquals(1, userList.getCount());
        assertSame(me, userList.getItemAtPosition(0));
        assertTrue(me.getClient().endsWith("Android"));
    }

    public void test02UserListShouldAddNewUser() {
        final ListView userList = getUserList();

        assertEquals(1, userList.getCount());

        client.logon();
        solo.sleep(500);

        assertEquals(2, userList.getCount());
    }

    public void test03UserListShouldBeSorted() {
        client.logon();
        solo.sleep(500);

        assertEquals("Kou", getUserNameAtPosition(0));
        assertEquals("Test", getUserNameAtPosition(1));

        client.changeNickName("Ape");
        solo.sleep(500);

        assertEquals("Ape", getUserNameAtPosition(0));
        assertEquals("Kou", getUserNameAtPosition(1));
    }

    public void test04OrientationSwitchShouldKeepSortedUserList() {
        client.logon();
        solo.sleep(500);

        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(1000);

        assertEquals("Kou", getUserNameAtPosition(0));
        assertEquals("Test", getUserNameAtPosition(1));
    }

    public void test05MeShouldBeBold() {
        client.logon();
        solo.sleep(500);

        // By default
        assertTrue(userIsBold("Kou", 0));
        assertFalse(userIsBold("Test", 1));

        client.changeNickName("Ape");
        solo.sleep(500);

        // After sorting of the user list
        assertFalse(userIsBold("Ape", 0));
        assertTrue(userIsBold("Kou", 1));

        client.sendPrivateChatMessage("Look!", me);
        solo.sleep(500);

        // After new private message
        assertFalse(userIsBold("Ape", 0));
        assertTrue(userIsBold("Kou", 1));

        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(500);

        // After orientation switch
        assertFalse(userIsBold("Ape", 0));
        assertTrue(userIsBold("Kou", 1));
    }

    public void test06ShouldShowStarOnOtherUserThatIsWriting() {
        client.logon();
        solo.sleep(500);

        assertFalse(userIsWriting("Kou", 0));
        assertFalse(userIsWriting("Test", 1));

        client.startWriting();
        solo.sleep(500);

        assertFalse(userIsWriting("Kou", 0));
        assertTrue(userIsWriting("Test", 1));

        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(500);

        assertFalse(userIsWriting("Kou", 0));
        assertTrue(userIsWriting("Test", 1));

        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(500);

        client.stopWriting();
        solo.sleep(500);

        assertFalse(userIsWriting("Kou", 0));
        assertFalse(userIsWriting("Test", 1));
    }

    public void test07ShouldShowStarOnMeWhenWriting() {
        client.logon();
        solo.sleep(500);

        assertFalse(userIsWriting("Kou", 0));
        assertFalse(userIsWriting("Test", 1));

        solo.enterText(0, "h"); // Write a single character
        solo.sleep(500);

        assertTrue(userIsWriting("Kou", 0));
        assertFalse(userIsWriting("Test", 1));

        solo.clearEditText(0); // Remove the character without sending (KeyEvent.KEYCODE_DEL does not seem to work)
        solo.sleep(500);

        assertFalse(userIsWriting("Kou", 0));
        assertFalse(userIsWriting("Test", 1));

        solo.enterText(0, "hello"); // Write a word
        solo.sleep(500);

        assertTrue(userIsWriting("Kou", 0));
        assertFalse(userIsWriting("Test", 1));

        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(500);

        assertTrue(userIsWriting("Kou", 0));
        assertFalse(userIsWriting("Test", 1));

        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(500);

        solo.sendKey(KeyEvent.KEYCODE_ENTER); // Send the word
        solo.sleep(500);

        assertFalse(userIsWriting("Kou", 0));
        assertFalse(userIsWriting("Test", 1));
    }

    public void test08ShouldShowUsersAsDisabledWhileAway() {
        client.logon();

        assertTrue(userIsEnabled("Kou", 0));
        assertTrue(userIsEnabled("Test", 1));

        client.goAway("Going away");

        assertTrue(userIsEnabled("Kou", 0));
        assertFalse(userIsEnabled("Test", 1));

        goAway("Also going away");

        assertFalse(userIsEnabled("Kou", 0));
        assertFalse(userIsEnabled("Test", 1));

        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(500);

        assertFalse(userIsEnabled("Kou", 0));
        assertFalse(userIsEnabled("Test", 1));

        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(500);

        comeBack("Also going away");

        assertTrue(userIsEnabled("Kou", 0));
        assertFalse(userIsEnabled("Test", 1));

        client.comeBack();

        assertTrue(userIsEnabled("Kou", 0));
        assertTrue(userIsEnabled("Test", 1));
    }

    public void test99Quit() {
        client.logoff();
        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        client.logoff();
        RobotiumTestUtils.setOrientation(solo, defaultOrientation);
        solo.finishOpenedActivities();

        solo = null;
        client = null;
        me = null;
        setActivity(null);

        System.gc();
    }

    private String getUserNameAtPosition(final int position) {
        return getUserAtPosition(position).getNick();
    }

    private User getUserAtPosition(final int position) {
        final ListView userList = getUserList();
        return (User) userList.getItemAtPosition(position);
    }

    private boolean userIsBold(final String nickName, final int userNumber) {
        solo.sleep(500);
        assertEquals(nickName, getUserNameAtPosition(userNumber));

        final LinearLayout row = (LinearLayout) getUserList().getChildAt(userNumber);
        final TextView textView = (TextView) row.getChildAt(1);
        final Typeface typeface = textView.getTypeface();

        return typeface != null && typeface.isBold();
    }

    private boolean userIsWriting(final String nickName, final int userNumber) {
        solo.sleep(500);
        assertEquals(nickName, getUserNameAtPosition(userNumber));

        final LinearLayout row = (LinearLayout) getUserList().getChildAt(userNumber);
        final TextView textView = (TextView) row.getChildAt(1);
        final CharSequence displayText = textView.getText();

        if (displayText.equals(nickName + " *")) {
            return true;
        } else if (displayText.equals(nickName)) {
            return false;
        }

        fail("Invalid display text of user in user list: " + displayText);
        return false;
    }

    private boolean userIsEnabled(final String nickName, final int userNumber) {
        solo.sleep(500);
        assertEquals(nickName, getUserNameAtPosition(userNumber));

        final LinearLayout row = (LinearLayout) getUserList().getChildAt(userNumber);
        final TextView textView = (TextView) row.getChildAt(1);

        return textView.isEnabled();
    }

    private ListView getUserList() {
        solo.sleep(100);
        return solo.getCurrentViews(ListView.class).get(0);
    }

    private void goAway(final String awayMessage) {
        RobotiumTestUtils.goAway(solo, getInstrumentation(), awayMessage);
    }

    private void comeBack(final String awayMessage) {
        RobotiumTestUtils.openMenu(solo);
        solo.sleep(500);
        RobotiumTestUtils.clickMenuItem(solo, "Away");
        solo.sleep(500);

        assertTrue(solo.searchText("Come back from '" + awayMessage + "'?"));
        solo.clickOnText("OK");
    }
}
