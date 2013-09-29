
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
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
import net.usikkert.kouchat.android.controller.ReceiveFileController;
import net.usikkert.kouchat.android.util.AndroidFile;
import net.usikkert.kouchat.android.util.FileUtils;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestClient;

import com.jayway.android.robotium.solo.Solo;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests file reception.
 *
 * @author Christian Ihle
 */
public class ReceiveFileTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient albert;
    private static TestClient tina;
    private static TestClient xen;

    private static AndroidFile image;

    private Solo solo;

    public ReceiveFileTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());

        if (albert == null) {
            albert = new TestClient("Albert", 1234);
            tina = new TestClient("Tina", 1235);
            xen = new TestClient("Xen", 1236);

            albert.logon();
            tina.logon();
            xen.logon();
        }
    }

    public void test01ShouldShowMissingFileDialogIfNoFileTransferRequestAvailable() {
        solo.sleep(1000);

        // Make sure we have an image to send from a test client to the real client
        final MainChatController mainChatController = getActivity();
        FileUtils.copyKouChatImageFromAssetsToSdCard(getInstrumentation(), mainChatController);
        image = FileUtils.getKouChatImage(mainChatController);

        openReceiveFileController();

        solo.sleep(1000);
        solo.searchText("Unable to find the specified file transfer request");
        assertFalse(getActivity().isVisible()); // The dialog should be in front of the main chat

        solo.clickOnText("OK"); // Close dialog

        solo.sleep(1000);
        assertTrue(getActivity().isVisible()); // The dialog should be closed, and the main chat in front
    }

    public void test02RejectFileTransferRequest() {
        solo.sleep(1000);
        final User me = RobotiumTestUtils.getMe(getActivity());

        tina.sendFile(me, image.getFile());
        solo.sleep(1000);

        // Message in the main chat
        solo.searchText("*** Tina is trying to send the file kouchat-1600x1600.png");
        solo.sleep(1000);

        openReceiveFileController(1235, 1);
        solo.sleep(1000);

        // Message in the popup dialog
        assertFalse(getActivity().isVisible()); // The dialog should be in front of the main chat
        solo.searchText("Tina is trying to send you the file ‘kouchat-1600x1600.png’ (67.16KB). Do you want to accept the file transfer?");
        solo.sleep(1000);

        // Button in the popup dialog
        solo.clickOnText("Reject");
        solo.sleep(1000);

        // Message in the main chat
        assertTrue(getActivity().isVisible()); // The dialog should be closed, and the main chat in front
        solo.searchText("*** You declined to receive kouchat-1600x1600.png from Tina");
    }

    public void test99Quit() {
        albert.logoff();
        tina.logoff();
        xen.logoff();

        albert = null;
        tina = null;
        xen = null;
        image = null;

        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        solo.finishOpenedActivities();

        solo = null;
        setActivity(null);

        System.gc();
    }

    private void openReceiveFileController(final int userCode, final int fileTransferId) {
        final Intent intent = new Intent();
        intent.putExtra("userCode", userCode);
        intent.putExtra("fileTransferId", fileTransferId);

        final String packageName = getInstrumentation().getTargetContext().getPackageName();
        launchActivityWithIntent(packageName, ReceiveFileController.class, intent);
    }

    private void openReceiveFileController() {
        final String packageName = getInstrumentation().getTargetContext().getPackageName();
        launchActivity(packageName, ReceiveFileController.class, null);
    }
}