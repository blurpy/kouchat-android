
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
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.util.TestClient;
import net.usikkert.kouchat.util.TestUtils;

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

    private Solo solo;
    private TestClient client;
    private Messages messages;
    private MainChatController activity;
    private User me;
    private int defaultOrientation;

    public TopicTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        client = new TestClient();
        messages = client.logon();
        me = Settings.getSettings().getMe();
        defaultOrientation = solo.getCurrentActivity().getRequestedOrientation();
    }

    public void test01OtherClientSettingTopicIsShownInChatAndTitle() throws CommandException {
        messages.sendTopicChangeMessage(new Topic("Original topic", "Test", System.currentTimeMillis()));
        solo.sleep(500);

        assertTrue(solo.searchText("Topic is: Original topic"));
        assertEquals(me.getNick() + " - Topic: Original topic (Test) - KouChat", activity.getTitle());
    }

    public void test02OtherClientChangingTopicIsShownInChatAndTitle() throws CommandException {
        messages.sendTopicChangeMessage(new Topic("New topic", "Test", System.currentTimeMillis()));
        solo.sleep(500);

        assertTrue(solo.searchText("Test changed the topic to: New topic"));
        assertEquals(me.getNick() + " - Topic: New topic (Test) - KouChat", activity.getTitle());
    }

    public void test03OrientationSwitchShouldKeepTopic() {
        assertEquals(me.getNick() + " - Topic: New topic (Test) - KouChat", activity.getTitle());

        TestUtils.switchOrientation(solo);

        assertEquals(me.getNick() + " - Topic: New topic (Test) - KouChat", activity.getTitle());
    }

    public void test04RemovingTheTopicShouldRemoveTopicFromTitle() {
        assertEquals(me.getNick() + " - Topic: New topic (Test) - KouChat", activity.getTitle());

        messages.sendTopicChangeMessage(new Topic("", "Test", System.currentTimeMillis()));

        solo.sleep(500);

        assertTrue(solo.searchText("Test removed the topic"));
        assertEquals(me.getNick() + " - KouChat", activity.getTitle());
    }

    public void test99Quit() {
        client.logoff();
        TestUtils.quit(solo);
    }

    public void tearDown() {
        client.logoff();
        TestUtils.resetOrientation(solo, defaultOrientation);
        solo.finishOpenedActivities();
    }
}
