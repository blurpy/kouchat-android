
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
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

import com.actionbarsherlock.app.ActionBar;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Testing away and back functionality.
 *
 * @author Christian Ihle
 */
public class AwayTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient client;

    private Solo solo;
    private User me;

    public AwayTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController activity = getActivity();

        RobotiumTestUtils.switchUserInterfaceToEnglish(activity); // To get the buttons in English

        solo = new Solo(getInstrumentation(), activity);
        me = RobotiumTestUtils.getMe(activity);

        if (client == null) {
            client = new TestClient();
            client.logon();
        }
    }

    public void test01ShouldNotGoAwayOnCancel() {
        solo.sleep(500);
        checkIfBack();

        RobotiumTestUtils.openMenu(solo);
        solo.clickOnText("Away");
        solo.sleep(500);

        assertTrue(solo.searchText("Go away?"));

        RobotiumTestUtils.writeText(getInstrumentation(), "Not going away");
        solo.sleep(500);
        solo.clickOnText("Cancel");

        solo.sleep(500);
        checkIfBack();
    }

    public void test02ShouldGoAwayOnOK() {
        solo.sleep(500);
        checkIfBack();

        RobotiumTestUtils.openMenu(solo);
        solo.clickOnText("Away");
        solo.sleep(500);

        assertTrue(solo.searchText("Go away?"));

        RobotiumTestUtils.writeText(getInstrumentation(), "Going for a walk");
        solo.sleep(500);
        solo.clickOnText("OK");

        solo.sleep(500);
        assertTrue(solo.searchText("You went away: Going for a walk"));
        checkIfAway();
    }

    public void test03ShouldNotComeBackOnCancel() {
        solo.sleep(500);
        checkIfAway();

        RobotiumTestUtils.openMenu(solo);
        solo.clickOnText("Away");
        solo.sleep(500);

        assertTrue(solo.searchText("Come back from 'Going for a walk'?"));
        solo.clickOnText("Cancel");

        solo.sleep(500);
        checkIfAway();
    }

    public void test04ShouldComeBackOnOK() {
        solo.sleep(500);
        checkIfAway();

        RobotiumTestUtils.openMenu(solo);
        solo.clickOnText("Away");
        solo.sleep(500);

        assertTrue(solo.searchText("Come back from 'Going for a walk'?"));
        solo.clickOnText("OK");

        solo.sleep(500);
        assertTrue(solo.searchText("You came back"));
        checkIfBack();
    }

    public void test99Quit() {
        client.logoff();
        RobotiumTestUtils.quit(solo);

        client = null;
    }

    public void tearDown() {
        solo.finishOpenedActivities();

        solo = null;
        me = null;
        setActivity(null);

        System.gc();
    }

    private void checkIfAway() {
        final ActionBar supportActionBar = getActivity().getSupportActionBar();

        assertTrue(me.isAway());
        assertEquals(me.getNick() + " (Away) - KouChat", supportActionBar.getTitle());
        // TODO check client?
    }

    private void checkIfBack() {
        final ActionBar supportActionBar = getActivity().getSupportActionBar();

        assertFalse(me.isAway());
        assertEquals(me.getNick() + " - KouChat", supportActionBar.getTitle());
    }
}
