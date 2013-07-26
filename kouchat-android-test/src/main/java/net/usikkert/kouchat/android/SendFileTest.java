
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.usikkert.kouchat.android.controller.SendFileController;
import net.usikkert.kouchat.android.util.AndroidFile;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestClient;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;
import com.google.common.io.Files;
import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

/**
 * Tests send file functionality. Requires at least one image in the gallery.
 * If the gallery is empty, then the image kouchat-512x512.png will be added.
 *
 * @author Christian Ihle
 */
public class SendFileTest extends ActivityInstrumentationTestCase2<SendFileController> {

    private static AndroidFile image;

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
        solo = new Solo(getInstrumentation(), activity);
        solo.sleep(2000);

        copyImageFromAssetsIfSdCardIsEmpty(activity);
        image = getRandomImage(activity);

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
        setActivityIntent(image.getUri());

        solo = new Solo(getInstrumentation(), getActivity());
        solo.sleep(1000);

        assertTrue(RobotiumTestUtils.searchText(solo, "File name: " + image.getName()));
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

    public void test04FileTransferAccepted() throws IOException {
        setActivityIntent(image.getUri());

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

        final File newFile = createNewFile();
        assertFalse("Should not exist: " + newFile, newFile.exists());

        albert.acceptFile(me, image.getName(), newFile);
        solo.sleep(2000);

        assertTrue(RobotiumTestUtils.searchText(solo, image.getName() + " successfully sent to Albert"));
        assertTrue("Should exist: " + newFile, newFile.exists());
        final ByteSource originalFile = Files.asByteSource(image.getFile());
        final ByteSource savedFile = Files.asByteSource(newFile);
        assertTrue(originalFile.contentEquals(savedFile));
    }

    public void test05FileTransferRejected() {
        setActivityIntent(image.getUri());

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

        tina.rejectFile(me, image.getName());
        solo.sleep(2000);

        assertTrue(RobotiumTestUtils.textIsVisible(solo, R.id.mainChatView, R.id.mainChatScroll,
                "Tina aborted reception of " + image.getName()));
    }

    public void test99Quit() {
        solo = new Solo(getInstrumentation(), getActivity());
        solo.clickOnButton("Cancel");

        RobotiumTestUtils.launchMainChat(this);
        solo.sleep(500);
        RobotiumTestUtils.quit(solo);

        image = null;
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

        System.gc();
    }

    private void setActivityIntent(final Uri uri) {
        final Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        setActivityIntent(intent);
    }

    private void copyImageFromAssetsIfSdCardIsEmpty(final Activity activity) {
        final Cursor cursor = getCursor(activity);

        if (cursor.getCount() == 0) {
            final File externalStorageDirectory = Environment.getExternalStorageDirectory();
            final File fileToStore = new File(externalStorageDirectory, "kouchat-512x512.png");

            copyFileToSdCard(fileToStore);
            addFileToDatabase(activity, fileToStore);
        }
    }

    private AndroidFile getRandomImage(final Activity activity) {
        final Cursor cursor = getCursor(activity);

        if (cursor.getCount() == 0) {
            throw new RuntimeException("No files in the database");
        }

        cursor.moveToFirst();

        return new AndroidFile(cursor);
    }

    private Cursor getCursor(final Activity activity) {
        final ContentResolver contentResolver = activity.getContentResolver();
        return contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, "_id asc limit 1");
    }

    private void copyFileToSdCard(final File fileToStore) {
        final Closer closer = Closer.create();
        final AssetManager assets = getInstrumentation().getContext().getResources().getAssets();

        try {
            final InputStream inputStream = closer.register(assets.open(fileToStore.getName()));
            final FileOutputStream outputStream = closer.register(new FileOutputStream(fileToStore));

            ByteStreams.copy(inputStream, outputStream);
            outputStream.flush();
            assertTrue("Should exist: " + fileToStore, fileToStore.exists());
        }

        catch (IOException e) {
            throw new RuntimeException(e);
        }

        finally {
            try {
                closer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addFileToDatabase(final Activity activity, final File fileToScan) {
        MediaScannerConnection.scanFile(
                activity,
                new String[] {fileToScan.toString()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(final String path, final Uri uri) { }
                });

        solo.sleep(1000); // To give the scanner time to finish
    }

    private File createNewFile() {
        final File externalStorageDirectory = Environment.getExternalStorageDirectory();
        final String fileName = "kouchat-" + System.currentTimeMillis() + image.getExtension();

        return new File(externalStorageDirectory, fileName);
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
}
