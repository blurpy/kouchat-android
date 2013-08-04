
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

/**
 * Test of topic handling.
 *
 * <p>These tests must run in order to pass.</p>
 *
 * @author Christian Ihle
 */
public class TopicTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient client;

    private Solo solo;
    private User me;
    private int defaultOrientation;

    public TopicTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);

        if (client == null) {
            client = new TestClient();
            client.logon();
        }

        me = RobotiumTestUtils.getMe(activity);
        defaultOrientation = RobotiumTestUtils.getCurrentOrientation(solo);
    }

    public void test01TopicShouldBeEmptyOnStart() {
        solo.sleep(500);
        assertEquals(me.getNick() + " - KouChat", getCurrentTitle());
    }

    public void test02OtherClientChangingTopicIsShownInChatAndTitle() {
        client.changeTopic("New topic");
        solo.sleep(500);

        assertTrue(solo.searchText("Test changed the topic to: New topic"));
        assertEquals(me.getNick() + " - Topic: New topic (Test) - KouChat", getCurrentTitle());
    }

    public void test03OrientationSwitchShouldKeepTopic() {
        assertEquals(me.getNick() + " - Topic: New topic (Test) - KouChat", getCurrentTitle());

        RobotiumTestUtils.switchOrientation(solo);
        solo.sleep(500);

        assertEquals(me.getNick() + " - Topic: New topic (Test) - KouChat", getCurrentTitle());
    }

    public void test04RemovingTheTopicShouldRemoveTopicFromTitle() {
        assertEquals(me.getNick() + " - Topic: New topic (Test) - KouChat", getCurrentTitle());

        client.changeTopic("");
        solo.sleep(500);

        assertTrue(solo.searchText("Test removed the topic"));
        assertEquals(me.getNick() + " - KouChat", getCurrentTitle());
    }

    public void test05SettingTopicAndLoggingOffToPrepareForTest06() {
        client.changeTopic("Original topic");
        solo.sleep(500);

        RobotiumTestUtils.quit(solo);
    }

    public void test06OtherClientSettingTopicBeforeStartIsShownInChatAndTitle() {
        solo.sleep(500);

        assertTrue(solo.searchText("Topic is: Original topic"));
        assertEquals(me.getNick() + " - Topic: Original topic (Test) - KouChat", getCurrentTitle());
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
        me = null;
        setActivity(null);

        System.gc();
    }

    private String getCurrentTitle() {
        return solo.getCurrentActivity().getTitle().toString(); // getActivity() returns the old activity after rotate
    }
}
