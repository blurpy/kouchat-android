
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

import java.io.File;
import java.util.Set;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.controller.ReceiveFileController;
import net.usikkert.kouchat.android.notification.NotificationService;
import net.usikkert.kouchat.android.util.AndroidFile;
import net.usikkert.kouchat.android.util.FileUtils;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestClient;
import net.usikkert.kouchat.testclient.TestUtils;

import com.robotium.solo.Solo;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests of file reception that require manual steps.
 *
 * @author Christian Ihle
 */
public class ManualReceiveFileTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient kenny;
    private static AndroidFile image;
    private static File requestedFile;

    private Solo solo;

    private NotificationService notificationService;
    private User me;

    public ManualReceiveFileTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController mainChatController = getActivity();
        final Instrumentation instrumentation = getInstrumentation();

        // To avoid issues with "." and "," in asserts containing file sizes
        RobotiumTestUtils.switchUserInterfaceToEnglish(mainChatController);

        solo = new Solo(instrumentation, mainChatController);
        me = RobotiumTestUtils.getMe(mainChatController);

        final AndroidUserInterface ui =
                TestUtils.getFieldValue(mainChatController, AndroidUserInterface.class, "androidUserInterface");
        notificationService = TestUtils.getFieldValue(ui, NotificationService.class, "notificationService");

        if (kenny == null) {
            kenny = new TestClient("Kenny", 1239);
            kenny.logon();

            // Make sure we have an image to send from a test client to the real client
            FileUtils.copyKouChatImageFromAssetsToInternalStorage(instrumentation, mainChatController);
            image = FileUtils.getKouChatImageFromInternalStorage(mainChatController);
            requestedFile = getLocationToRequestedFile();
        }

        solo.sleep(500);
        checkThatNoFileTransferNotificationsAreActive();
    }

    /**
     * This test requires that the SD card is unmounted.
     *
     * <p>On Android 2.3.3 emulator: go to Settings/Storage and use the option "Unmount SD card".</p>
     *
     * <p>On Android 4.3 emulator: open <code>~/.android/avd/Android-4.3.avd/config.ini</code>
     * and set <code>hw.sdCard=no</code>.</p>
     */
    public void test01AcceptFileTransferRequestWithoutStorage() {
        checkThatTheFileHasNotBeenTransferred();

        kenny.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** Kenny is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotification(1);
        solo.sleep(500);

        openReceiveFileDialog(kenny, 1);
        solo.sleep(500);

        checkThatTheDialogIsInFront();
        checkDialogMessage("Kenny is trying to send you the file ‘kouchat-1600x1600.png’ (67.16KB). " +
                "Do you want to accept the file transfer?");
        solo.sleep(500);

        acceptFileTransfer();
        solo.sleep(1000);

        checkThatTheMainChatIsInFront();
        checkMainChatMessage("*** Failed to receive kouchat-1600x1600.png from Kenny");
        checkThatNoFileTransferNotificationsAreActive();
        checkThatTheFileHasNotBeenTransferred();
    }

    public void test99Quit() {
        kenny.logoff();

        kenny = null;
        image = null;
        requestedFile = null;

        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        solo.finishOpenedActivities();

        solo = null;
        setActivity(null);

        System.gc();
    }

    private void openReceiveFileDialog(final TestClient client, final int fileTransferId) {
        final Intent intent = new Intent();
        intent.putExtra("userCode", client.getUserCode());
        intent.putExtra("fileTransferId", fileTransferId);

        final String packageName = getInstrumentation().getTargetContext().getPackageName();
        launchActivityWithIntent(packageName, ReceiveFileController.class, intent);
    }

    private File getLocationToRequestedFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), image.getName());
    }

    private void checkMainChatMessage(final String textToFind) {
        assertTrue(RobotiumTestUtils.textIsVisible(solo, R.id.mainChatView, R.id.mainChatScroll, textToFind));
    }

    private void checkDialogMessage(final String textToFind) {
        assertTrue(RobotiumTestUtils.searchText(solo, textToFind));
    }

    private void checkThatNoFileTransferNotificationsAreActive() {
        assertTrue(notificationService.getCurrentFileTransferIds().isEmpty());
    }

    private void checkActiveFileTransferNotification(final int expectedFileTransferId) {
        final Set<Integer> currentFileTransferIds = notificationService.getCurrentFileTransferIds();

        assertEquals(1, currentFileTransferIds.size());
        assertTrue(currentFileTransferIds.contains(expectedFileTransferId));
    }

    private void acceptFileTransfer() {
        solo.clickOnText("Accept"); // Button in the popup dialog
    }

    private void checkThatTheDialogIsInFront() {
        assertFalse(getActivity().isVisible()); // The dialog should be in front of the main chat
    }

    private void checkThatTheMainChatIsInFront() {
        assertTrue(getActivity().isVisible()); // The dialog should be closed, and the main chat in front
    }

    private void checkThatTheFileHasNotBeenTransferred() {
        assertFalse(requestedFile.exists());
    }
}
