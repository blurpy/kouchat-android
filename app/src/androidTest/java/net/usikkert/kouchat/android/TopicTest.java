
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

import android.app.Instrumentation;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

/**
 * Test of topic handling.
 *
 * @author Christian Ihle
 */
public class TopicTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient client;

    private Solo solo;
    private Instrumentation instrumentation;

    private User me;
    private int defaultOrientation;

    public TopicTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        instrumentation = getInstrumentation();
        final MainChatController activity = getActivity();

        RobotiumTestUtils.switchUserInterfaceToEnglish(activity); // To get the buttons in English

        solo = new Solo(instrumentation, activity);

        if (client == null) {
            client = new TestClient();
            client.logon();
        }

        me = RobotiumTestUtils.getMe(activity);
        defaultOrientation = RobotiumTestUtils.getCurrentOrientation(solo);
    }

    public void test01TopicShouldBeEmptyOnStart() {
        solo.sleep(500);
        checkTopic(null);
    }

    public void test02OtherClientChangingTopicIsShownInChatAndTitle() {
        client.changeTopic("New topic");
        solo.sleep(500);

        assertTrue(solo.searchText("Test changed the topic to: New topic"));
        checkTopic("New topic - Test");
    }

    public void test03OrientationSwitchShouldKeepTopic() {
        checkTopic("New topic - Test");

        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(500);

        checkTopic("New topic - Test");
    }

    public void test04OtherClientRemovingTheTopicShouldRemoveTopicFromTitle() {
        checkTopic("New topic - Test");

        client.changeTopic("");
        solo.sleep(500);

        assertTrue(solo.searchText("Test removed the topic"));
        checkTopic(null);
    }

    public void test05SettingTopicAndLoggingOffToPrepareForTest06() {
        client.changeTopic("Original topic");
        solo.sleep(500);

        RobotiumTestUtils.quit(solo);
    }

    public void test06OtherClientSettingTopicBeforeStartIsShownInChatAndTitle() {
        solo.sleep(500);

        assertTrue(solo.searchText("Topic is: Original topic"));
        checkTopic("Original topic - Test");
    }

    public void test07MeChangingTopicIsShownInChatAndTitle() {
        solo.sleep(500);

        RobotiumTestUtils.openMenu(solo);
        solo.sleep(500);
        RobotiumTestUtils.clickMenuItem(solo, "Topic");
        solo.sleep(500);

        assertTrue(solo.searchText("Set or change the current topic."));

        RobotiumTestUtils.writeText(instrumentation, "This is my topic");
        solo.sleep(500);
        solo.clickOnText("OK");

        solo.sleep(500);
        assertTrue(solo.searchText("You changed the topic to: This is my topic"));
        checkTopic("This is my topic - " + me.getNick());
    }

    public void test08MeRemovingTheTopicShouldRemoveTopicFromTitle() {
        solo.sleep(500);

        RobotiumTestUtils.openMenu(solo);
        solo.sleep(500);
        RobotiumTestUtils.clickMenuItem(solo, "Topic");
        solo.sleep(500);

        solo.sendKey(KeyEvent.KEYCODE_DEL);
        solo.sleep(500);
        solo.clickOnText("OK");

        solo.sleep(500);
        assertTrue(solo.searchText("You removed the topic"));
        checkTopic(null);
    }

    public void test09PressingEnterShouldNotAddANewLine() {
        solo.sleep(500);

        RobotiumTestUtils.openMenu(solo);
        solo.sleep(500);
        RobotiumTestUtils.clickMenuItem(solo, "Topic");
        solo.sleep(500);

        RobotiumTestUtils.writeText(instrumentation, "Line1");
        solo.sendKey(KeyEvent.KEYCODE_ENTER);
        RobotiumTestUtils.writeText(instrumentation, "Line2");

        solo.sleep(500);
        assertTrue(solo.searchText("Line1Line2"));
    }

    public void test10ClickingCancelShouldNotChangeTopic() {
        solo.sleep(500);

        RobotiumTestUtils.openMenu(solo);
        solo.sleep(500);
        RobotiumTestUtils.clickMenuItem(solo, "Topic");
        solo.sleep(500);

        RobotiumTestUtils.writeText(instrumentation, "Don't set this topic");
        solo.sleep(500);
        solo.clickOnText("Cancel");

        solo.sleep(500);
        checkTopic(null);
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
        setActivity(null);

        System.gc();
    }

    private void checkTopic(final String topic) {
        // getActivity() returns the old activity after rotate
        final AppCompatActivity currentActivity = (AppCompatActivity) solo.getCurrentActivity();
        final ActionBar supportActionBar = currentActivity.getSupportActionBar();

        assertEquals(me.getNick() + " - KouChat", supportActionBar.getTitle());
        assertEquals(topic, supportActionBar.getSubtitle());
    }
}
