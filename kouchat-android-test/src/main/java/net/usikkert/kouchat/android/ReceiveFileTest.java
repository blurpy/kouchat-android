
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

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

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

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.jayway.android.robotium.solo.Solo;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Environment;
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
    private static File requestedFile;

    private Solo solo;

    private NotificationService notificationService;
    private User me;

    public ReceiveFileTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        Locale.setDefault(Locale.US); // To avoid issues with "." and "," in asserts containing file sizes

        final MainChatController mainChatController = getActivity();
        final Instrumentation instrumentation = getInstrumentation();

        solo = new Solo(instrumentation, mainChatController);
        me = RobotiumTestUtils.getMe(mainChatController);

        final AndroidUserInterface ui =
                TestUtils.getFieldValue(mainChatController, AndroidUserInterface.class, "androidUserInterface");
        notificationService = TestUtils.getFieldValue(ui, NotificationService.class, "notificationService");

        if (albert == null) {
            albert = new TestClient("Albert", 1234);
            tina = new TestClient("Tina", 1235);
            xen = new TestClient("Xen", 1236);

            albert.logon();
            tina.logon();
            xen.logon();

            // Make sure we have an image to send from a test client to the real client
            FileUtils.copyKouChatImageFromAssetsToSdCard(instrumentation, mainChatController);
            image = FileUtils.getKouChatImage(mainChatController);
            requestedFile = getLocationToRequestedFile();
        }

        solo.sleep(500);
        checkThatTheFileHasNotBeenNotTransferred();
        checkThatNoFileTransferNotificationsAreActive();
    }

    public void test01ShouldShowMissingFileDialogIfNoFileTransferRequestAvailable() {
        openReceiveFileController();

        solo.sleep(500);
        checkDialogMessage("Unable to find the specified file transfer request");
        checkThatTheDialogIsInFront();

        solo.clickOnText("OK"); // Close dialog

        solo.sleep(500);
        checkThatTheMainChatIsInFront();
        checkThatNoFileTransferNotificationsAreActive();
    }

    public void test02RejectFileTransferRequest() {
        tina.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** Tina is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotification(1);
        solo.sleep(500);

        openReceiveFileController(tina, 1);
        solo.sleep(500);

        checkThatTheDialogIsInFront();
        checkDialogMessage("Tina is trying to send you the file ‘kouchat-1600x1600.png’ (67.16KB). " +
                "Do you want to accept the file transfer?");
        solo.sleep(500);

        rejectFileTransfer();
        solo.sleep(500);

        checkThatTheMainChatIsInFront();
        checkMainChatMessage("*** You declined to receive kouchat-1600x1600.png from Tina");
        checkThatNoFileTransferNotificationsAreActive();
        checkThatTheFileHasNotBeenNotTransferred();
    }

    public void test03AcceptFileTransferRequest() throws IOException {
        albert.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** Albert is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotification(2);
        solo.sleep(500);

        openReceiveFileController(albert, 2);
        solo.sleep(500);

        checkThatTheDialogIsInFront();
        checkDialogMessage("Albert is trying to send you the file ‘kouchat-1600x1600.png’ (67.16KB). " +
                "Do you want to accept the file transfer?");
        solo.sleep(500);

        acceptFileTransfer();
        solo.sleep(1000);

        checkThatTheMainChatIsInFront();
        checkMainChatMessage("*** Successfully received kouchat-1600x1600.png from Albert, and saved as kouchat-1600x1600.png");
        checkThatNoFileTransferNotificationsAreActive();
        checkThatTheFileWasReceivedSuccessfully(requestedFile);
    }

    public void test04CancelFileTransferRequestBeforeOpeningActivity() {
        xen.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** Xen is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotification(3);
        solo.sleep(500);

        xen.cancelFileSending(me, image.getFile());

        solo.sleep(500);
        checkMainChatMessage("*** Xen aborted sending of kouchat-1600x1600.png");
        checkThatNoFileTransferNotificationsAreActive();
        checkThatTheFileHasNotBeenNotTransferred();
    }

    public void test05CancelFileTransferRequestBeforeRejecting() {
        xen.changeNickName("XenMaster");
        solo.sleep(500);

        xen.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** XenMaster is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotification(4);
        solo.sleep(500);

        openReceiveFileController(xen, 4);
        solo.sleep(500);

        checkThatTheDialogIsInFront();
        checkDialogMessage("XenMaster is trying to send you the file ‘kouchat-1600x1600.png’ (67.16KB). " +
                "Do you want to accept the file transfer?");
        solo.sleep(500);

        xen.cancelFileSending(me, image.getFile());
        solo.sleep(500);

        rejectFileTransfer();
        solo.sleep(500);

        checkThatTheMainChatIsInFront();
        checkMainChatMessage("*** XenMaster aborted sending of kouchat-1600x1600.png");
        checkThatNoFileTransferNotificationsAreActive();
        checkThatTheFileHasNotBeenNotTransferred();
    }

    public void test06CancelFileTransferRequestBeforeAccepting() {
        tina.changeNickName("SuperTina");
        solo.sleep(500);

        tina.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** SuperTina is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotification(5);
        solo.sleep(500);

        openReceiveFileController(tina, 5);
        solo.sleep(500);

        checkThatTheDialogIsInFront();
        checkDialogMessage("SuperTina is trying to send you the file ‘kouchat-1600x1600.png’ (67.16KB). " +
                "Do you want to accept the file transfer?");
        solo.sleep(500);

        tina.cancelFileSending(me, image.getFile());
        solo.sleep(500);

        acceptFileTransfer();
        solo.sleep(500);

        checkThatTheMainChatIsInFront();
        checkMainChatMessage("*** SuperTina aborted sending of kouchat-1600x1600.png");
        checkThatNoFileTransferNotificationsAreActive();
        checkThatTheFileHasNotBeenNotTransferred();
    }

    public void test07CloseAndReopenDialog() {
        albert.changeNickName("Alban");
        solo.sleep(500);

        albert.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** Alban is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotification(6);
        solo.sleep(500);

        // First try
        openReceiveFileController(albert, 6);
        solo.sleep(500);

        checkThatTheDialogIsInFront();
        checkDialogMessage("Alban is trying to send you the file ‘kouchat-1600x1600.png’ (67.16KB). " +
                "Do you want to accept the file transfer?");
        solo.sleep(500);

        // Close dialog without accepting or rejecting
        solo.goBack();
        solo.sleep(500);

        checkThatTheMainChatIsInFront();
        checkActiveFileTransferNotification(6); // The notification should still be there

        // Second try
        openReceiveFileController(albert, 6);
        solo.sleep(500);

        checkThatTheDialogIsInFront();
        checkDialogMessage("Alban is trying to send you the file ‘kouchat-1600x1600.png’ (67.16KB). " +
                "Do you want to accept the file transfer?");
        solo.sleep(500);

        rejectFileTransfer();
        solo.sleep(500);

        checkThatTheMainChatIsInFront();
        checkMainChatMessage("*** You declined to receive kouchat-1600x1600.png from Alban");
        checkThatNoFileTransferNotificationsAreActive();
        checkThatTheFileHasNotBeenNotTransferred();
    }

    public void test99Quit() {
        albert.logoff();
        tina.logoff();
        xen.logoff();

        albert = null;
        tina = null;
        xen = null;
        image = null;
        requestedFile = null;

        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        if (requestedFile != null && requestedFile.exists()) {
            requestedFile.delete();
        }

        solo.finishOpenedActivities();

        solo = null;
        setActivity(null);

        System.gc();
    }

    private void openReceiveFileController(final TestClient client, final int fileTransferId) {
        final Intent intent = new Intent();
        intent.putExtra("userCode", client.getUserCode());
        intent.putExtra("fileTransferId", fileTransferId);

        final String packageName = getInstrumentation().getTargetContext().getPackageName();
        launchActivityWithIntent(packageName, ReceiveFileController.class, intent);
    }

    private void openReceiveFileController() {
        final String packageName = getInstrumentation().getTargetContext().getPackageName();
        launchActivity(packageName, ReceiveFileController.class, null);
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

    private void checkActiveFileTransferNotification(final int fileTransferId) {
        final Set<Integer> currentFileTransferIds = notificationService.getCurrentFileTransferIds();

        assertTrue(currentFileTransferIds.contains(fileTransferId));
        assertEquals(1, currentFileTransferIds.size());
    }

    private void rejectFileTransfer() {
        solo.clickOnText("Reject"); // Button in the popup dialog
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

    private void checkThatTheFileHasNotBeenNotTransferred() {
        assertFalse(requestedFile.exists());
    }

    private void checkThatTheFileWasReceivedSuccessfully(final File file) throws IOException {
        assertTrue("Should exist: " + file, file.exists());

        final ByteSource originalFile = Files.asByteSource(image.getFile());
        final ByteSource savedFile = Files.asByteSource(file);

        assertTrue(originalFile.contentEquals(savedFile));
    }
}
