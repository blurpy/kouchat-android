
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU General Public License as               *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with KouChat. If not, see <http://www.gnu.org/licenses/>.       *
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
 * TODO
 *
 * @author Christian Ihle
 */
public class UserListTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;
    private TestClient client;
    private MainChatController activity;
    private User me;

    public UserListTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        client = new TestClient();
        me = Settings.getSettings().getMe();
    }

    public void test01UserListShouldContainMeOnLogon() {
        final ListView userList = solo.getCurrentListViews().get(0);
        assertEquals(1, userList.getCount());
        assertSame(me, userList.getItemAtPosition(0));
    }

    public void test02UserListShouldAddNewUser() {
        final ListView userList = solo.getCurrentListViews().get(0);
        assertEquals(1, userList.getCount());

        final Messages messages = client.logon();

        sleep(500);

        assertEquals(2, userList.getCount());
        final User item1 = (User) userList.getItemAtPosition(0);
        final User item2 = (User) userList.getItemAtPosition(1);

        // TODO verify order
    }

    public void test99Quit() {
        client.logoff();
        TestUtils.quit(solo);
    }

    public void tearDown() {
        client.logoff();
        solo.finishOpenedActivities();
    }

    private void sleep(final int time) {
        try {
            Thread.sleep(time);
        }

        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
