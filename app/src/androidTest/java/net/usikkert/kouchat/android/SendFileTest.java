
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

package net.usikkert.kouchat.android;

import java.io.File;
import java.io.IOException;

import net.usikkert.kouchat.android.controller.SendFileController;
import net.usikkert.kouchat.android.util.AndroidFile;
import net.usikkert.kouchat.android.util.FileUtils;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestClient;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.robotium.solo.Solo;

import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

/**
 * Tests send file functionality.
 * The image kouchat-1600x1600.png will be added to the gallery if it's missing.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class SendFileTest extends ActivityInstrumentationTestCase2<SendFileController> {

    private static AndroidFile imageWithContentUri;
    private static AndroidFile imageWithFileUri;

    private Solo solo;

    private TestClient albert;
    private TestClient tina;
    private TestClient xen;

    public SendFileTest() {
        super(SendFileController.class);
    }

    public void setUp() {
        albert = new TestClient("Albert", 1234);
        tina = new TestClient("Tina", 1235);
        xen = new TestClient("Xen", 1236);
    }

    public void test01NoSelectedFile() {
        final SendFileController activity = getActivity();
        final Instrumentation instrumentation = getInstrumentation();

        solo = new Solo(instrumentation, activity);
        solo.sleep(2000);

        if (imageWithContentUri == null) {
            FileUtils.copyKouChatImageFromAssetsToSdCard(instrumentation, activity);
            imageWithContentUri = FileUtils.getKouChatImageFromSdCardWithContentUri(activity);
            imageWithFileUri = FileUtils.getKouChatImageFromSdCardWithFileUri();
        }

        assertTrue(RobotiumTestUtils.searchText(solo, "Unable to locate the file to send."));
    }

    public void test02SelectedFileNotFound() {
        setActivityIntent(Uri.fromFile(new File("afile.txt")));

        solo = new Solo(getInstrumentation(), getActivity());
        solo.sleep(2000);

        assertTrue(RobotiumTestUtils.searchText(solo, "Unable to locate the file to send."));
        assertTrue(RobotiumTestUtils.searchText(solo, "afile.txt"));
    }

    public void test03UsersLoggingOnAndOff() {
        setActivityIntent(imageWithContentUri.getUri());

        solo = new Solo(getInstrumentation(), getActivity());
        solo.sleep(1000);

        assertTrue(RobotiumTestUtils.searchText(solo, "File name: " + imageWithContentUri.getName()));
        assertTrue(RobotiumTestUtils.searchText(solo, "File size:"));

        assertUsers();

        albert.logon();
        solo.sleep(1000);

        assertUsers("Albert");

        xen.logon();
        solo.sleep(1000);

        assertUsers("Albert", "Xen");

        tina.logon();
        solo.sleep(2000);

        assertUsers("Albert", "Tina", "Xen");

        tina.logoff();
        solo.sleep(1000);

        assertUsers("Albert", "Xen");

        xen.logoff();
        solo.sleep(1000);

        assertUsers("Albert");

        albert.logoff();
        solo.sleep(1000);

        assertUsers();
    }

    public void test04FileTransferAcceptedWithContentUri() throws IOException {
        assertEquals("content", imageWithContentUri.getUri().getScheme());
        setActivityIntent(imageWithContentUri.getUri());

        final SendFileController activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        solo.sleep(1000);

        final User me = RobotiumTestUtils.getMe(activity);

        albert.logon();
        solo.sleep(1000);

        solo.clickInList(1); // Click on Albert
        solo.sleep(1000);

        RobotiumTestUtils.launchMainChat(this);
        solo.sleep(1000);

        final File newFile = FileUtils.createNewFile(imageWithContentUri);
        assertFalse("Should not exist: " + newFile, newFile.exists());

        albert.acceptFile(me, imageWithContentUri.getName(), newFile);
        solo.sleep(2000);

        assertTrue(textIsVisible(imageWithContentUri.getName() + " successfully sent to Albert"));
        assertTrue("Should exist: " + newFile, newFile.exists());
        final ByteSource originalFile = Files.asByteSource(imageWithContentUri.getFile());
        final ByteSource savedFile = Files.asByteSource(newFile);
        assertTrue(originalFile.contentEquals(savedFile));

        assertTrue("Should be able to delete temporary file: " + newFile, newFile.delete()); // Cleanup
    }

    public void test05FileTransferAcceptedWithFileUri() throws IOException {
        assertEquals("file", imageWithFileUri.getUri().getScheme());
        setActivityIntent(imageWithFileUri.getUri());

        final SendFileController activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        solo.sleep(1000);

        final User me = RobotiumTestUtils.getMe(activity);

        albert.logon();
        solo.sleep(1000);

        solo.clickInList(1); // Click on Albert
        solo.sleep(1000);

        RobotiumTestUtils.launchMainChat(this);
        solo.sleep(1000);

        final File newFile = FileUtils.createNewFile(imageWithFileUri);
        assertFalse("Should not exist: " + newFile, newFile.exists());

        albert.acceptFile(me, imageWithFileUri.getName(), newFile);
        solo.sleep(2000);

        assertTrue(textIsVisible(imageWithFileUri.getName() + " successfully sent to Albert"));
        assertTrue("Should exist: " + newFile, newFile.exists());
        final ByteSource originalFile = Files.asByteSource(imageWithFileUri.getFile());
        final ByteSource savedFile = Files.asByteSource(newFile);
        assertTrue(originalFile.contentEquals(savedFile));

        assertTrue("Should be able to delete temporary file: " + newFile, newFile.delete()); // Cleanup
    }

    public void test06FileTransferRejected() {
        setActivityIntent(imageWithContentUri.getUri());

        final SendFileController activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        solo.sleep(1000);

        final User me = RobotiumTestUtils.getMe(activity);

        tina.logon();
        solo.sleep(1000);

        solo.clickInList(1); // Click on Tina
        solo.sleep(1000);

        RobotiumTestUtils.launchMainChat(this);
        solo.sleep(1000);

        tina.rejectFile(me, imageWithContentUri.getName());
        solo.sleep(2000);

        assertTrue(textIsVisible("Tina aborted reception of " + imageWithContentUri.getName()));
    }

    public void test07ShouldNotBeAbleToSendFileToUserThatIsAway() {
        setActivityIntent(imageWithContentUri.getUri());

        final SendFileController activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        solo.sleep(1000);

        tina.logon();
        solo.sleep(100);

        tina.goAway("Don't want your files");
        solo.sleep(1000);

        solo.clickInList(1); // Click on Tina
        solo.sleep(1000);

        assertTrue(solo.searchText("You can not send a file to a user that is away"));
    }

    public void test99Quit() {
        solo = new Solo(getInstrumentation(), getActivity());
        solo.clickOnButton("Cancel");

        RobotiumTestUtils.launchMainChat(this);
        solo.sleep(1000);
        RobotiumTestUtils.quit(solo);

        imageWithContentUri = null;
        imageWithFileUri = null;
    }

    public void tearDown() {
        albert.logoff();
        tina.logoff();
        xen.logoff();

        solo.finishOpenedActivities();

        solo = null;
        albert = null;
        tina = null;
        xen = null;
        setActivity(null);

        System.gc();
    }

    private void setActivityIntent(final Uri uri) {
        final Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        setActivityIntent(intent);
    }

    private void assertUsers(final String... users) {
        assertEquals(users.length, solo.getCurrentViews(ListView.class).get(0).getCount());

        if (users.length == 0) {
            assertTrue(RobotiumTestUtils.searchText(solo, "-- No connected users."));
            assertFalse(RobotiumTestUtils.searchText(solo, "Please select the user to send the file to."));
        }

        else {
            assertFalse(RobotiumTestUtils.searchText(solo, "-- No connected users."));
            assertTrue(RobotiumTestUtils.searchText(solo, "Please select the user to send the file to."));

            for (int i = 0; i < users.length; i++) {
                final String user = users[i];
                assertEquals(user, solo.getCurrentViews(ListView.class).get(0).getItemAtPosition(i).toString());
            }
        }
    }

    private boolean textIsVisible(final String textToFind) {
        return RobotiumTestUtils.textIsVisible(solo, R.id.mainChatView, R.id.mainChatScroll, textToFind);
    }
}
