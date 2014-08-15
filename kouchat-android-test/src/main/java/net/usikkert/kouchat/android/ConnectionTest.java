
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

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.ConnectionWorker;
import net.usikkert.kouchat.net.NetworkService;
import net.usikkert.kouchat.testclient.TestUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.robotium.solo.Solo;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Test of network connection handling.
 *
 * @author Christian Ihle
 */
public class ConnectionTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;
    private Instrumentation instrumentation;

    private int defaultOrientation;

    private User me;
    private ConnectionWorker connectionWorker;
    private Controller controller;

    public ConnectionTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        instrumentation = getInstrumentation();
        final MainChatController activity = getActivity();

        solo = new Solo(instrumentation, activity);

        me = RobotiumTestUtils.getMe(activity);
        defaultOrientation = RobotiumTestUtils.getCurrentOrientation(solo);

        final AndroidUserInterface androidUserInterface = RobotiumTestUtils.getAndroidUserInterface(activity);
        controller = TestUtils.getFieldValue(androidUserInterface, Controller.class, "controller");
        final NetworkService networkService = TestUtils.getFieldValue(controller, NetworkService.class, "networkService");
        connectionWorker = networkService.getConnectionWorker();
    }

    public void test01ShouldSetNotConnectedInActionBarWhenNeverConnected() {
        checkTitle(me.getNick() + " - KouChat");
        solo.sleep(500);

        controller.logOff(true); // Simulate never having connected
        solo.sleep(500);

        checkTitle(me.getNick() + " - Not connected - KouChat");
        assertTrue(solo.searchText("You logged off"));

        me.setNick(me.getNick() + "A"); // Need to identify the second logon in the next test, so changing nick in memory
    }

    public void test02ShouldResetActionBarWhenConnectionEstablished() {
        checkTitle(me.getNick() + " - Not connected - KouChat");
        solo.sleep(500);

        controller.logOn(); // Simulate getting a connection for the first time
        solo.sleep(500);

        checkTitle(me.getNick() + " - KouChat");
        assertTrue(solo.searchText("You logged on as " + me.getNick()));
    }

    public void test03ShouldSetConnectionLostInActionBarWhenConnectionLost() {
        checkTitle(me.getNick() + " - KouChat");
        solo.sleep(500);

        connectionWorker.stop(); // Simulate losing a connection
        solo.sleep(500);

        checkTitle(me.getNick() + " - Connection lost - KouChat");
        assertTrue(solo.searchText("You lost contact with the network"));
    }

    public void test04ShouldResetActionBarWhenConnectionBack() {
        checkTitle(me.getNick() + " - Connection lost - KouChat");
        solo.sleep(500);

        connectionWorker.start(); // Simulate getting back a lost connection
        solo.sleep(500);

        checkTitle(me.getNick() + " - KouChat");
        assertTrue(solo.searchText("You are connected to the network again"));
    }

    public void test99Quit() {
        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        RobotiumTestUtils.setOrientation(solo, defaultOrientation);
        solo.finishOpenedActivities();

        solo = null;
        instrumentation = null;
        me = null;
        connectionWorker = null;
        controller = null;
        setActivity(null);

        System.gc();
    }

    private void checkTitle(final String title) {
        // getActivity() returns the old activity after rotate
        final SherlockActivity currentActivity = (SherlockActivity) solo.getCurrentActivity();
        final ActionBar supportActionBar = currentActivity.getSupportActionBar();

        assertEquals(title, supportActionBar.getTitle());
        assertNull(supportActionBar.getSubtitle());
    }
}
