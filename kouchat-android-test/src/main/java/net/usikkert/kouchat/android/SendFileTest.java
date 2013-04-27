
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

import net.usikkert.kouchat.android.controller.SendFileController;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.testclient.TestClient;

import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests send file functionality. Requires at least one image in the gallery.
 *
 * @author Christian Ihle
 */
public class SendFileTest extends ActivityInstrumentationTestCase2<SendFileController> {

    private static Uri uri;

    private Solo solo;

    public SendFileTest() {
        super(SendFileController.class);
    }

    public void test01NoFile() {
        final SendFileController activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        solo.sleep(2000);

        uri = getUriForAnImage(activity);
    }

    public void test02FileNotFound() {
        final Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File("afile.txt")));
        setActivityIntent(intent);

        solo = new Solo(getInstrumentation(), getActivity());
        solo.sleep(2000);
    }

    public void test03NoUsersAndSelectUser() {
        final TestClient albert = new TestClient("Albert", 1234);
        final TestClient tina = new TestClient("Tina", 1235);
        final TestClient xen = new TestClient("Xen", 1236);

        final Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_STREAM, uri);
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
    }

    // TODO assert users
    // TODO test real file transfer
    // getInstrumentation().getTargetContext().getResources().getAssets().open(testFile);

    public void test99Quit() {
        solo = new Solo(getInstrumentation(), getActivity());
        solo.clickOnButton("Cancel");

        RobotiumTestUtils.launchMainChat(this);
        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }

    private Uri getUriForAnImage(final Activity activity) {
        final ContentResolver contentResolver = activity.getContentResolver();
        final Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, "_id asc limit 1");

        cursor.moveToFirst();

        final String uris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI +
                "/" +
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));

        return Uri.parse(uris);
    }
}
