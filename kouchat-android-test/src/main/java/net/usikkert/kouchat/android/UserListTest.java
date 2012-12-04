
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
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.util.TestClient;
import net.usikkert.kouchat.util.TestUtils;

import com.jayway.android.robotium.solo.Solo;

import android.graphics.Typeface;
import android.test.ActivityInstrumentationTestCase2;
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
    private ListView userList;

    public UserListTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        me = Settings.getSettings().getMe();
        me.setNick("Kou");

        final MainChatController activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        client = new TestClient();

        solo.sleep(100);

        userList = solo.getCurrentListViews().get(0);
    }

    public void test01UserListShouldContainMeOnLogon() {
        assertEquals(1, userList.getCount());
        assertSame(me, userList.getItemAtPosition(0));
    }

    public void test02UserListShouldAddNewUser() {
        assertEquals(1, userList.getCount());

        client.logon();
        solo.sleep(500);

        assertEquals(2, userList.getCount());
    }

    public void test03UserListShouldBeSorted() {
        final Messages messages = client.logon();
        solo.sleep(500);

        assertEquals("Kou", getUserNameAtPosition(0));
        assertEquals("Test", getUserNameAtPosition(1));

        messages.sendNickMessage("Ape");
        solo.sleep(500);

        assertEquals("Ape", getUserNameAtPosition(0));
        assertEquals("Kou", getUserNameAtPosition(1));
    }

    public void test04OrientationSwitchShouldKeepSortedUserList() {
        client.logon();
        solo.sleep(500);

        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.sleep(500);

        assertEquals("Kou", getUserNameAtPosition(0));
        assertEquals("Test", getUserNameAtPosition(1));
    }

    public void test05MeShouldBeBold() throws CommandException {
        final Messages messages = client.logon();
        solo.sleep(500);

        // By default
        assertTrue(userIsBold("Kou", 0));
        assertFalse(userIsBold("Test", 1));

        messages.sendNickMessage("Ape");
        solo.sleep(500);

        // After sorting of the user list
        assertFalse(userIsBold("Ape", 0));
        assertTrue(userIsBold("Kou", 1));

        messages.sendPrivateMessage("Look!", me);
        solo.sleep(500);

        // After new private message
        assertFalse(userIsBold("Ape", 0));
        assertTrue(userIsBold("Kou", 1));

        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.sleep(500);

        // After orientation switch
        assertFalse(userIsBold("Ape", 0));
        assertTrue(userIsBold("Kou", 1));
    }

    public void test99Quit() {
        client.logoff();
        TestUtils.quit(solo);
    }

    public void tearDown() {
        client.logoff();
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.sleep(500);
        solo.finishOpenedActivities();
    }

    private String getUserNameAtPosition(final int position) {
        return getUserAtPosition(position).getNick();
    }

    private User getUserAtPosition(final int position) {
        return (User) userList.getItemAtPosition(position);
    }

    private boolean userIsBold(final String nickName, final int userNumber) {
        solo.sleep(500);
        assertEquals(nickName, getUserNameAtPosition(userNumber));

        final LinearLayout row = (LinearLayout) solo.getCurrentListViews().get(0).getChildAt(userNumber);
        final TextView textView = (TextView) row.getChildAt(1);
        final Typeface typeface = textView.getTypeface();

        return typeface != null && typeface.isBold();
    }
}
