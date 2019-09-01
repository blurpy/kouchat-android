
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

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.ConnectionWorker;
import net.usikkert.kouchat.net.NetworkService;
import net.usikkert.kouchat.testclient.TestClient;
import net.usikkert.kouchat.testclient.TestUtils;

import com.robotium.solo.Solo;

import android.app.Instrumentation;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

/**
 * Test of network connection handling.
 *
 * @author Christian Ihle
 */
public class ConnectionTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient client;

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

        if (client == null) {
            client = new TestClient();
            client.logon();
        }
    }

    public void test01ShouldSetNotConnectedInActionBarWhenNeverConnected() {
        solo.sleep(500);
        checkTitle(me.getNick() + " - KouChat");

        controller.logOff(true); // Simulate never having connected
        solo.sleep(500);

        checkTitle(me.getNick() + " - Not connected - KouChat");
        assertTrue(solo.searchText("You logged off"));
    }

    public void test02ShouldNotBeAbleToSendMessageWhenNeverConnected() {
        solo.sleep(500);
        checkTitle(me.getNick() + " - Not connected - KouChat");

        RobotiumTestUtils.writeLine(solo, "Don't send this");
        solo.sleep(500);
        assertTrue(solo.searchText("You can not send a chat message without being connected"));
    }

    public void test03ShouldResetActionBarWhenConnectionEstablished() {
        solo.sleep(500);
        checkTitle(me.getNick() + " - Not connected - KouChat");

        clearMainChat(); // Remove the first logon text
        controller.logOn(); // Simulate getting a connection for the first time
        solo.sleep(500);

        checkTitle(me.getNick() + " - KouChat");
        assertTrue(solo.searchText("You logged on as " + me.getNick()));
    }

    public void test04ShouldSetConnectionLostInActionBarWhenConnectionLost() {
        solo.sleep(500);
        checkTitle(me.getNick() + " - KouChat");

        connectionWorker.stop(); // Simulate losing a connection
        solo.sleep(500);

        checkTitle(me.getNick() + " - Connection lost - KouChat");
        assertTrue(solo.searchText("You lost contact with the network"));
    }

    public void test05ShouldNotBeAbleToSendMessageWhenConnectionLost() {
        solo.sleep(500);
        checkTitle(me.getNick() + " - Connection lost - KouChat");

        clearMainChat(); // Remove the first "can't send without being connected" message
        RobotiumTestUtils.writeLine(solo, "Don't send this");
        solo.sleep(500);
        assertTrue(solo.searchText("You can not send a chat message without being connected"));

        RobotiumTestUtils.openPrivateChat(solo, getInstrumentation(), 2, 2, "Test");

        RobotiumTestUtils.writeLine(solo, "Don't send this");
        solo.sleep(500);
        assertTrue(solo.searchText("You can not send a private chat message without being connected"));
    }

    public void test06ShouldResetActionBarWhenConnectionBack() {
        solo.sleep(500);
        checkTitle(me.getNick() + " - Connection lost - KouChat");

        connectionWorker.start(); // Simulate getting back a lost connection
        solo.sleep(500);

        checkTitle(me.getNick() + " - KouChat");
        assertTrue(solo.searchText("You are connected to the network again"));
    }

    public void test07ShouldOnlyShowDetailsAboutAwayAndTopicWhenConnected() {
        solo.sleep(500);
        checkTitleAndTopic(me.getNick() + " - KouChat", null);

        RobotiumTestUtils.changeTopicTo(solo, getInstrumentation(), "Such a nice day");
        RobotiumTestUtils.goAway(solo, getInstrumentation(), "Away we go");
        checkTitleAndTopic(me.getNick() + " (Away) - KouChat", "Such a nice day - " + me.getNick());

        connectionWorker.stop(); // Lost connection
        solo.sleep(500);
        checkTitleAndTopic(me.getNick() + " - Connection lost - KouChat", null);

        connectionWorker.start(); // Connection returned
        solo.sleep(500);
        checkTitleAndTopic(me.getNick() + " (Away) - KouChat", "Such a nice day - " + me.getNick());
    }

    public void test08QuitShouldWorkWhenNeverConnected() {
        solo.sleep(500);
        controller.logOff(true);

        RobotiumTestUtils.quit(solo); // Just to test that it doesn't crash
    }

    public void test09QuitShouldWorkWhenConnectionLost() {
        solo.sleep(500);
        connectionWorker.stop();

        RobotiumTestUtils.quit(solo); // Just to test that it doesn't crash
    }

    public void test99Quit() {
        client.logoff();

        RobotiumTestUtils.quit(solo);

        client = null;
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
        checkTitleAndTopic(title, null);
    }

    private void checkTitleAndTopic(final String title, final String topic) {
        // getActivity() returns the old activity after rotate
        final AppCompatActivity currentActivity = (AppCompatActivity) solo.getCurrentActivity();
        final ActionBar supportActionBar = currentActivity.getSupportActionBar();

        assertEquals(title, supportActionBar.getTitle());
        assertEquals(topic, supportActionBar.getSubtitle());
    }

    // This only works temporarily, as the text will be reloaded in the next test from the backend
    private void clearMainChat() {
        final TextView mainChatView = getActivity().findViewById(R.id.mainChatView);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mainChatView.setText("");
            }
        });
    }
}
