
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
import java.io.IOException;

import net.usikkert.kouchat.android.controller.SendFileController;
import net.usikkert.kouchat.android.util.AndroidFile;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestClient;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests send file functionality. Requires at least one image in the gallery.
 *
 * @author Christian Ihle
 */
public class SendFileTest extends ActivityInstrumentationTestCase2<SendFileController> {

    private static AndroidFile image;

    private Solo solo;

    public SendFileTest() {
        super(SendFileController.class);
    }

    public void test01NoSelectedFile() {
        final SendFileController activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        solo.sleep(2000);

        image = getRandomImage(activity);
    }

    public void test02SelectedFileNotFound() {
        final Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File("afile.txt")));
        setActivityIntent(intent);

        solo = new Solo(getInstrumentation(), getActivity());
        solo.sleep(2000);
    }

    public void test03UsersLoggingOnAndOff() {
        final TestClient albert = new TestClient("Albert", 1234);
        final TestClient tina = new TestClient("Tina", 1235);
        final TestClient xen = new TestClient("Xen", 1236);

        final Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_STREAM, image.getUri());
        setActivityIntent(intent);

        solo = new Solo(getInstrumentation(), getActivity());
        solo.sleep(1000);

        albert.logon();
        solo.sleep(1000);

        xen.logon();
        solo.sleep(1000);

        tina.logon();
        solo.sleep(2000);

        tina.logoff();
        solo.sleep(1000);

        xen.logoff();
        solo.sleep(1000);

        albert.logoff();
        solo.sleep(1000);

        // TODO assert users
    }

    public void test04FileTransferAccepted() throws IOException {
        final TestClient albert = new TestClient("Albert", 1234);

        final Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_STREAM, image.getUri());
        setActivityIntent(intent);

        final SendFileController activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        solo.sleep(1000);

        albert.logon();
        solo.sleep(1000);

        solo.clickInList(1); // Click on Albert
        solo.sleep(1000);

        RobotiumTestUtils.launchMainChat(this);
        solo.sleep(1000);

        final User me = RobotiumTestUtils.getMe(activity);
        final File newFile = createNewFile();
        assertFalse("Should not exist: " + newFile, newFile.exists());

        albert.acceptFile(me, image.getName(), newFile);
        solo.sleep(2000);

        RobotiumTestUtils.searchText(solo, image.getName() + "successfully sent to Albert");
        assertTrue("Should exist: " + newFile, newFile.exists());
        final ByteSource originalFile = Files.asByteSource(image.getFile());
        final ByteSource savedFile = Files.asByteSource(newFile);
        assertTrue(originalFile.contentEquals(savedFile));

        solo.sleep(2000);
        albert.logoff();
    }

    public void test99Quit() {
        solo = new Solo(getInstrumentation(), getActivity());
        solo.clickOnButton("Cancel");

        RobotiumTestUtils.launchMainChat(this);
        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }

    private AndroidFile getRandomImage(final Activity activity) {
        final ContentResolver contentResolver = activity.getContentResolver();
        final Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, "_id asc limit 1");

        cursor.moveToFirst();

        return new AndroidFile(cursor);
    }

    private File createNewFile() throws IOException {
        final File externalStorageDirectory = Environment.getExternalStorageDirectory();
        final String fileName = "kouchat-" + System.currentTimeMillis() + image.getExtension();

        return new File(externalStorageDirectory, fileName);
    }
}
