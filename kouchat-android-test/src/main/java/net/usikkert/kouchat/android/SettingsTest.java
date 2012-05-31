
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

import com.jayway.android.robotium.solo.Solo;

import android.graphics.Rect;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Test of changing the settings.
 *
 * @author Christian Ihle
 */
public class SettingsTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static String originalNickName;

    private Solo solo;

    public SettingsTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testNickNameCanBeChanged() {
        originalNickName = Settings.getSettings().getMe().getNick();

        // Go to the Settings menu item and choose to set nick name
        solo.sendKey(Solo.MENU);
        solo.clickOnText("Settings");
        solo.clickOnText("Set nick name");
        assertTrue(solo.searchText("Set nick name"));

        // Change nick name to Testing in the popup dialog
        hideSoftKeyboard();
        solo.clearEditText(0);
        solo.enterText(0, "Testing");
        solo.clickOnButton("OK");
        assertTrue(solo.searchText("Testing"));

        // Go back to main chat and check result
        solo.goBack();
        assertTrue(solo.searchText("You changed nick to Testing"));
    }

    public void testRestoreNickName() {
        assertNotNull(originalNickName);

        // Go to the Settings menu item and choose to set nick name
        solo.sendKey(Solo.MENU);
        solo.clickOnText("Settings");
        solo.clickOnText("Set nick name");

        // Change nick name back to the original value in the popup dialog
        hideSoftKeyboard();
        solo.clearEditText(0);
        solo.enterText(0, originalNickName);
        solo.clickOnButton("OK");
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }

    private void hideSoftKeyboard() {
        if (softKeyboardIsVisible()) {
            solo.goBack();
        }
    }

    private boolean softKeyboardIsVisible() {
        final MainChatController activity = getActivity();

        final Rect visibleDisplayFrame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(visibleDisplayFrame);

        final int statusBarHeight = visibleDisplayFrame.top;
        final int activityHeight = visibleDisplayFrame.height();
        final int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();

        final int diff = (screenHeight - statusBarHeight) - activityHeight;

        return diff > screenHeight / 3;
    }
}
