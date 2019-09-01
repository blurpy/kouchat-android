
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

package net.usikkert.kouchat.android.manual;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.testclient.TestClient;

import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests links in the main and private chat. Needs manual attention.
 *
 * @author Christian Ihle
 */
public class ManualLinkTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static final String URL = Constants.APP_WEB;

    private static TestClient client;

    private Solo solo;
    private int defaultOrientation;

    public ManualLinkTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController activity = getActivity();

        solo = new Solo(getInstrumentation(), activity);
        defaultOrientation = RobotiumTestUtils.getCurrentOrientation(solo);

        // Making sure the test client only logs on once during all the tests
        if (client == null) {
            client = new TestClient();
            client.logon();
        }
    }

    public void test01LinksShouldWorkInTheMainChat() {
        RobotiumTestUtils.writeLine(solo, URL);

        solo.sleep(500);
        assertTrue(solo.getCurrentActivity().hasWindowFocus()); // KouChat is in focus

        RobotiumTestUtils.clickOnText(solo, R.id.mainChatView, R.id.mainChatScroll, URL);
        solo.sleep(1000);
        assertFalse(solo.getCurrentActivity().hasWindowFocus()); // Browser is in focus

        solo.sleep(3000); // Close browser manually now!
        RobotiumTestUtils.switchOrientation(solo);

        solo.sleep(2000);
        assertTrue(solo.getCurrentActivity().hasWindowFocus()); // KouChat is in focus
        RobotiumTestUtils.clickOnText(solo, R.id.mainChatView, R.id.mainChatScroll, URL);
        solo.sleep(1000);
        assertFalse(solo.getCurrentActivity().hasWindowFocus()); // Browser is in focus
    }

    public void test02LinksShouldWorkInThePrivateChat() {
        RobotiumTestUtils.openPrivateChat(solo, getInstrumentation(), 2, 2, "Test");

        RobotiumTestUtils.writeLine(solo, URL);

        solo.sleep(500);
        assertTrue(solo.getCurrentActivity().hasWindowFocus()); // KouChat is in focus

        RobotiumTestUtils.clickOnText(solo, R.id.privateChatView, R.id.privateChatScroll, URL);
        solo.sleep(1000);
        assertFalse(solo.getCurrentActivity().hasWindowFocus()); // Browser is in focus

        solo.sleep(3000); // Close browser manually now!
        RobotiumTestUtils.switchOrientation(solo);

        solo.sleep(2000);
        assertTrue(solo.getCurrentActivity().hasWindowFocus()); // KouChat is in focus
        RobotiumTestUtils.clickOnText(solo, R.id.privateChatView, R.id.privateChatScroll, URL);
        solo.sleep(1000);
        assertFalse(solo.getCurrentActivity().hasWindowFocus()); // Browser is in focus
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
        setActivity(null);

        System.gc();
    }
}
