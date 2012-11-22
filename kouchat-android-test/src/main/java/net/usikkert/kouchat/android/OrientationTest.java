
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
import net.usikkert.kouchat.util.TestUtils;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

/**
 * Tests how the application handles orientation changes.
 *
 * @author Christian Ihle
 */
public class OrientationTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;
    private MainChatController activity;
    private User me;

    public OrientationTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        me = Settings.getSettings().getMe();
    }

    public void test01OrientationSwitchShouldKeepState() {
        verifyWelcomeMessage();
        verifyTopic();
        verifyUserInUserList();

        solo.setActivityOrientation(Solo.PORTRAIT);

        verifyWelcomeMessage();
        verifyTopic();
        verifyUserInUserList();

        solo.setActivityOrientation(Solo.LANDSCAPE);

        verifyWelcomeMessage();
        verifyTopic();
        verifyUserInUserList();
    }

    // Must be verified manually. I haven't found an automated way to verify scrolling yet.
    public void test02OrientationSwitchShouldScrollToBottom() {
        for (int i = 1; i <= 30; i++) {
            solo.enterText(0,
                    "This is message number " + i + "! " +
                    "This is message number " + i + "! " +
                    "This is message number " + i + "! " +
                    "This is message number " + i + "! " +
                    "This is message number " + i + "! " +
                    "This is message number " + i + "! " +
                    "This is message number " + i + "! " +
                    "This is message number " + i + "! " +
                    "This is message number " + i + "! " +
                    "This is message number " + i + "! " +
                    "This is message number " + i + "! " +
                    "This is message number " + i + "! ");
            solo.sendKey(KeyEvent.KEYCODE_ENTER);
        }

        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.sleep(3000); // See if message number 30 is visible

        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.sleep(3000); // See if message number 30 is visible
    }

    public void test99Quit() {
        TestUtils.quit(solo);
    }

    private void verifyUserInUserList() {
        assertEquals(me, solo.getCurrentListViews().get(0).getItemAtPosition(0));
    }

    private void verifyWelcomeMessage() {
        assertTrue(solo.searchText("Welcome to KouChat"));
    }

    private void verifyTopic() {
        final String topic = activity.getTitle().toString();
        assertTrue("wrong topic: " + topic, topic.contains("Topic:  () - KouChat"));
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
