
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
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.util.TestClient;
import net.usikkert.kouchat.util.TestUtils;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

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
}
