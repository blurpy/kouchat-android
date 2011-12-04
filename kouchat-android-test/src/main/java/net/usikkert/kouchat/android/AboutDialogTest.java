
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
 * Test of the about dialog.
 *
 * @author Christian Ihle
 */

public class AboutDialogTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;

    public AboutDialogTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testAboutInMenuShouldOpenDialog() {
        solo.sendKey(Solo.MENU);
        solo.clickOnText("About");
        assertTrue(solo.searchText("KouChat v"));
        assertTrue(solo.searchText("Copyright"));
        assertTrue(solo.searchText("GPLv3"));

        solo.clickOnButton("OK");
        assertTrue(solo.searchText("Welcome to KouChat"));
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
