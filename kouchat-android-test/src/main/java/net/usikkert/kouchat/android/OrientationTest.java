
/***************************************************************************
 *   Copyright 2006-2011 by Christian Ihle                                 *
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

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

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

    public void testOrientationSwitchShouldKeepState() {
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
