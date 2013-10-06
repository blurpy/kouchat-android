
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
    private static File requestedFile1;
    private static File requestedFile2;

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
            image = FileUtils.getKouChatImageFromSdCard(mainChatController);

            requestedFile = getLocationToRequestedFile("");
            requestedFile1 = getLocationToRequestedFile("_1");
            requestedFile2 = getLocationToRequestedFile("_2");
        }

        solo.sleep(500);
        checkThatNoFileTransferNotificationsAreActive();
    }

    public void test01ShouldShowMissingFileDialogIfNoFileTransferRequestAvailable() {
        openReceiveFileDialog();

        solo.sleep(500);
        checkDialogMessage("Unable to find the specified file transfer request");
        checkThatTheDialogIsInFront();

        solo.clickOnText("OK"); // Close dialog

        solo.sleep(500);
        checkThatTheMainChatIsInFront();
        checkThatNoFileTransferNotificationsAreActive();
    }

    public void test02RejectFileTransferRequest() {
        checkThatTheFilesHaveNotBeenNotTransferred();

        tina.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** Tina is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotifications(1);
        solo.sleep(500);

        openReceiveFileDialog(tina, 1);
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
        checkThatTheFilesHaveNotBeenNotTransferred();
    }

    public void test03AcceptFileTransferRequest() throws IOException {
        checkThatTheFilesHaveNotBeenNotTransferred();

        albert.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** Albert is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotifications(2);
        solo.sleep(500);

        openReceiveFileDialog(albert, 2);
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
        checkThatTheFilesHaveNotBeenNotTransferred();

        xen.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** Xen is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotifications(3);
        solo.sleep(500);

        xen.cancelFileSending(me, image.getFile());

        solo.sleep(500);
        checkMainChatMessage("*** Xen aborted sending of kouchat-1600x1600.png");
        checkThatNoFileTransferNotificationsAreActive();
        checkThatTheFilesHaveNotBeenNotTransferred();
    }

    public void test05CancelFileTransferRequestBeforeRejecting() {
        checkThatTheFilesHaveNotBeenNotTransferred();

        xen.changeNickName("XenMaster");
        solo.sleep(500);

        xen.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** XenMaster is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotifications(4);
        solo.sleep(500);

        openReceiveFileDialog(xen, 4);
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
        checkThatTheFilesHaveNotBeenNotTransferred();
    }

    public void test06CancelFileTransferRequestBeforeAccepting() {
        checkThatTheFilesHaveNotBeenNotTransferred();

        tina.changeNickName("SuperTina");
        solo.sleep(500);

        tina.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** SuperTina is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotifications(5);
        solo.sleep(500);

        openReceiveFileDialog(tina, 5);
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
        checkThatTheFilesHaveNotBeenNotTransferred();
    }

    public void test07CloseAndReopenDialog() {
        checkThatTheFilesHaveNotBeenNotTransferred();

        albert.changeNickName("Alban");
        solo.sleep(500);

        albert.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** Alban is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotifications(6);
        solo.sleep(500);

        // First try
        openReceiveFileDialog(albert, 6);
        solo.sleep(500);

        checkThatTheDialogIsInFront();
        checkDialogMessage("Alban is trying to send you the file ‘kouchat-1600x1600.png’ (67.16KB). " +
                "Do you want to accept the file transfer?");
        solo.sleep(500);

        // Close dialog without accepting or rejecting
        solo.goBack();
        solo.sleep(500);

        checkThatTheMainChatIsInFront();
        checkActiveFileTransferNotifications(6); // The notification should still be there

        // Second try
        openReceiveFileDialog(albert, 6);
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
        checkThatTheFilesHaveNotBeenNotTransferred();
    }

    public void test08ConcurrentFileTransfers() throws IOException {
        checkThatTheFilesHaveNotBeenNotTransferred();

        albert.changeNickName("Albino");
        tina.changeNickName("TinaBurger");
        xen.changeNickName("XenXei");
        solo.sleep(500);

        albert.sendFile(me, image.getFile());
        solo.sleep(100);
        checkActiveFileTransferNotifications(7);

        xen.sendFile(me, image.getFile());
        solo.sleep(100);
        checkActiveFileTransferNotifications(7, 8);

        tina.sendFile(me, image.getFile());
        solo.sleep(100);
        checkActiveFileTransferNotifications(7, 8, 9);

        checkMainChatMessage("*** Albino is trying to send the file kouchat-1600x1600.png");
        checkMainChatMessage("*** TinaBurger is trying to send the file kouchat-1600x1600.png");
        checkMainChatMessage("*** XenXei is trying to send the file kouchat-1600x1600.png");

        openReceiveFileDialog(albert, 7);
        acceptFileTransfer();

        openReceiveFileDialog(xen, 8);
        acceptFileTransfer();

        openReceiveFileDialog(tina, 9);
        acceptFileTransfer();

        solo.sleep(1000);

        checkThatTheMainChatIsInFront();
        checkThatNoFileTransferNotificationsAreActive();

        checkMainChatMessage("*** Successfully received kouchat-1600x1600.png from Albino, and saved as kouchat-1600x1600.png");
        checkMainChatMessage("*** Successfully received kouchat-1600x1600.png from XenXei, and saved as kouchat-1600x1600_1.png");
        checkMainChatMessage("*** Successfully received kouchat-1600x1600.png from TinaBurger, and saved as kouchat-1600x1600_2.png");

        checkThatTheFileWasReceivedSuccessfully(requestedFile);
        checkThatTheFileWasReceivedSuccessfully(requestedFile1);
        checkThatTheFileWasReceivedSuccessfully(requestedFile2);
    }

    public void test09UserLoggingOffShouldCancelFileTransferRequest() {
        checkThatTheFilesHaveNotBeenNotTransferred();

        tina.changeNickName("TinaTurner");
        solo.sleep(500);

        tina.sendFile(me, image.getFile());
        solo.sleep(500);

        checkMainChatMessage("*** TinaTurner is trying to send the file kouchat-1600x1600.png");
        checkActiveFileTransferNotifications(10);
        solo.sleep(500);

        tina.logoff();
        solo.sleep(1000);

        checkMainChatMessage("*** TinaTurner logged off");
        checkThatNoFileTransferNotificationsAreActive();
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
        requestedFile1 = null;
        requestedFile2 = null;

        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        deleteFile(requestedFile);
        deleteFile(requestedFile1);
        deleteFile(requestedFile2);

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

    private void openReceiveFileDialog() {
        final String packageName = getInstrumentation().getTargetContext().getPackageName();
        launchActivity(packageName, ReceiveFileController.class, null);
    }

    private File getLocationToRequestedFile(final String postfix) {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                image.getBaseName() + postfix + image.getExtension());
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

    private void checkActiveFileTransferNotifications(final int... expectedFileTransferIds) {
        final Set<Integer> currentFileTransferIds = notificationService.getCurrentFileTransferIds();
        assertEquals(expectedFileTransferIds.length, currentFileTransferIds.size());

        for (final Integer expectedFileTransferId : expectedFileTransferIds) {
            assertTrue(currentFileTransferIds.contains(expectedFileTransferId));
        }
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

    private void checkThatTheFilesHaveNotBeenNotTransferred() {
        assertFalse(requestedFile.exists());
        assertFalse(requestedFile1.exists());
        assertFalse(requestedFile2.exists());
    }

    private void checkThatTheFileWasReceivedSuccessfully(final File file) throws IOException {
        assertTrue("Should exist: " + file, file.exists());

        final ByteSource originalFile = Files.asByteSource(image.getFile());
        final ByteSource savedFile = Files.asByteSource(file);

        assertTrue(originalFile.contentEquals(savedFile));
    }

    private void deleteFile(final File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }
}
