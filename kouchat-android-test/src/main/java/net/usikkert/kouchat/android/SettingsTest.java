
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
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.TestClient;
import net.usikkert.kouchat.util.TestUtils;

import com.jayway.android.robotium.solo.Solo;

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

    public void test01NickNameCanBeChanged() {
        originalNickName = Settings.getSettings().getMe().getNick();

        clickOnChangeNickNameInTheSettings();
        assertTrue(solo.searchText("Set nick name"));

        changeNickNameTo("Kou");
        assertTrue(solo.searchText("Kou"));

        // Go back to main chat and check result
        TestUtils.goBack(solo);
        assertTrue(solo.searchText("You changed nick to Kou"));
    }

    public void test02RestoreNickName() {
        assertNotNull(originalNickName);

        clickOnChangeNickNameInTheSettings();

        // Change nick name back to the original value in the popup dialog
        changeNickNameTo(originalNickName);
        assertTrue(solo.searchText(originalNickName));
    }

    public void test03ChangingToNickNameInUseShouldOnlyShowToast() {
        final TestClient client = new TestClient();
        client.logon();

        clickOnChangeNickNameInTheSettings();
        changeNickNameTo("Test");

        assertTrue(solo.searchText("The nick name is in use by someone else.")); // Toast
        assertFalse(solo.searchText("Test"));

        client.logoff();
    }

    public void test99Quit() {
        TestUtils.quit(solo);
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }

    private void clickOnChangeNickNameInTheSettings() {
        // Go to the Settings menu item and choose to set nick name
        solo.sendKey(Solo.MENU);
        solo.clickOnText("Settings");
        solo.clickOnText("Set nick name");
    }

    private void changeNickNameTo(final String nickName) {
        TestUtils.hideSoftwareKeyboard(solo);
        solo.clearEditText(0);
        solo.enterText(0, nickName);
        solo.clickOnButton("OK");
    }
}
