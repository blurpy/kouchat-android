
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

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Test of changing the settings.
 *
 * @author Christian Ihle
 */

public class SettingsTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;

    public SettingsTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testActivity() {
        final MainChatController activity = getActivity();
        assertNotNull(activity);
    }

    public void testNickNameIsSaved() {
        solo.sendKey(Solo.MENU);
        solo.clickOnText("Settings");
        solo.clickOnText("Set nick name");
        assertTrue(solo.searchText("Set nick name"));

        solo.clearEditText(0);
        solo.enterText(0, "ape");
        solo.clickOnButton("OK");
        assertTrue(solo.searchText("ape"));

        solo.goBack();
        assertTrue(solo.searchText("You changed nick to ape")); // This fails for some reason...
    }

    public void testResetNickName() {
        solo.sendKey(Solo.MENU);
        solo.clickOnText("Settings");
        solo.clickOnText("Set nick name");
        solo.clearEditText(0);
        solo.enterText(0, "test");
        solo.clickOnButton("OK");
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
