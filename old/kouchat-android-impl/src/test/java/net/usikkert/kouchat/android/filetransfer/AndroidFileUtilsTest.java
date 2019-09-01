
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

package net.usikkert.kouchat.android.filetransfer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.util.ToolsTest;

import org.fest.util.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.tester.android.database.SimpleTestCursor;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

/**
 * Test of {@link AndroidFileUtils}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class AndroidFileUtilsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AndroidFileUtils androidFileUtils;

    private ContentResolver contentResolver;
    private SimpleTestCursor cursor;
    private ShadowContentResolver shadowContentResolver;

    @Before
    public void setUp() {
        androidFileUtils = spy(new AndroidFileUtils());

        contentResolver = Robolectric.application.getContentResolver();
        shadowContentResolver = Robolectric.shadowOf(contentResolver);

        cursor = new SimpleTestCursor() {
            @Override
            public int getCount() {
                return results.length; // Not implemented in SimpleTestCursor
            }

            @Override
            public boolean moveToFirst() {
                return moveToNext(); // Not implemented in SimpleTestCursor
            }
        };
    }

    @Test
    public void getFileFromUriShouldReturnNullIfUriIsNull() {
        assertNull(androidFileUtils.getFileFromUri(null, contentResolver));
    }

    @Test
    public void getFileFromUriShouldReturnNullIfUriIsOfUnsupportedType() {
        assertNull(androidFileUtils.getFileFromUri(Uri.parse("ftp://ftp.google.com"), contentResolver));
    }

    @Test
    public void getFileFromUriShouldReturnResultFromContentUriIfContentScheme() {
        final Uri uri = Uri.parse("content://contacts/photos/253");
        final File tempFile = new File("tmp.txt");

        when(androidFileUtils.getFileFromContentUri(uri, contentResolver)).thenReturn(tempFile);

        final File fileFromUri = androidFileUtils.getFileFromUri(uri, contentResolver);

        assertSame(tempFile, fileFromUri);
        verify(androidFileUtils).getFileFromContentUri(uri, contentResolver);
    }

    @Test
    public void getFileFromUriShouldReturnResultFromGetFromFileUriIfFileScheme() {
        final Uri uri = Uri.parse("file:///storage/emulated/0/kouchat-1600x1600.png");
        final File tempFile = new File("tmp.txt");

        when(androidFileUtils.getFileFromFileUri(uri)).thenReturn(tempFile);

        final File fileFromUri = androidFileUtils.getFileFromUri(uri, contentResolver);

        assertSame(tempFile, fileFromUri);
        verify(androidFileUtils).getFileFromFileUri(uri);
    }

    @Test
    public void getFileFromContentUriShouldThrowExceptionIfContentResolverIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("ContentResolver can not be null");

        androidFileUtils.getFileFromContentUri(Uri.EMPTY, null);
    }

    @Test
    public void getFileFromContentUriShouldThrowExceptionIfUriIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Content uri can not be null");

        assertNull(androidFileUtils.getFileFromContentUri(null, contentResolver));
    }

    @Test
    public void getFileFromContentUriShouldReturnNullIfCursorIsNullAfterQuery() {
        shadowContentResolver.setCursor(null);

        assertNull(androidFileUtils.getFileFromContentUri(Uri.parse("content://contacts/photos/253"), contentResolver));
    }

    @Test
    public void getFileFromContentUriShouldReturnNullIfNoResults() {
        shadowContentResolver.setCursor(cursor);

        assertNull(androidFileUtils.getFileFromContentUri(Uri.parse("content://contacts/photos/253"), contentResolver));
    }

    @Test
    public void getFileFromContentUriShouldReturnFirstResult() {
        shadowContentResolver.setCursor(cursor);
        cursor.setColumnNames(Lists.newArrayList("_data"));
        cursor.setResults(new String[][] {{"/home/user/pictures/dsc0001.jpg", "/home/user/pictures/dsc0002.jpg"}});

        final File fileFromUri = androidFileUtils.getFileFromContentUri(Uri.parse("content://contacts/photos/253"), contentResolver);

        assertNotNull(fileFromUri);
        assertEquals("dsc0001.jpg", fileFromUri.getName());
    }

    @Test
    public void getFileFromFileUriShouldThrowExceptionIfUriIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File uri can not be null");

        androidFileUtils.getFileFromFileUri(null);
    }

    @Test
    public void getFileFromFileUriShouldReturnFileWithUriPathIfFileExists() throws IOException {
        final File tempFile = File.createTempFile("getFileFromFileUri", ".tmp");
        assertTrue(tempFile.exists());

        final Uri uri = Uri.fromFile(tempFile);
        assertTrue(uri.getScheme().equals("file"));

        final File file = androidFileUtils.getFileFromFileUri(uri);

        assertEquals(tempFile, file);
    }

    @Test
    public void getFileFromFileUriShouldReturnNullIfFileDoesNotExist() throws IOException {
        final Uri uri = Uri.parse("file:///storage/emulated/0/kouchat-1600x1600.png");
        assertTrue(uri.getScheme().equals("file"));

        final File file = androidFileUtils.getFileFromFileUri(uri);

        assertNull(file);
    }

    @Test
    public void addFileToMediaDatabaseShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        androidFileUtils.addFileToMediaDatabase(null, mock(File.class));
    }

    @Test
    public void addFileToMediaDatabaseShouldThrowExceptionIfFileIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File to add can not be null");

        androidFileUtils.addFileToMediaDatabase(mock(Context.class), null);
    }

    @Test
    public void addFileToMediaDatabaseShouldSendBroadcast() {
        final Context context = Robolectric.application;
        final File file = new File("kouchat.png");

        // Hack around the fact that this needs to final in order to be modified from an anonymous class
        final boolean[] broadcastReceived = {false};

        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                assertEquals(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, intent.getAction());
                assertEquals(Uri.fromFile(file), intent.getData());
                broadcastReceived[0] = true;
            }
        };

        final IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        context.registerReceiver(broadcastReceiver, intentFilter);

        androidFileUtils.addFileToMediaDatabase(context, file);

        assertTrue(broadcastReceived[0]);
    }

    @Test
    public void createFileInDownloadsWithAvailableNameShouldThrowExceptionIfNameIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File name can not be empty");

        androidFileUtils.createFileInDownloadsWithAvailableName(null);
    }

    @Test
    public void createFileInDownloadsWithAvailableNameShouldThrowExceptionIfNameIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File name can not be empty");

        androidFileUtils.createFileInDownloadsWithAvailableName(" ");
    }

    @Test
    public void createFileInDownloadsWithAvailableNameShouldKeepFileNameAndUseDownloadDirectory() {
        final File file = androidFileUtils.createFileInDownloadsWithAvailableName("file.txt");

        assertNotNull(file);
        assertFalse(file.exists());
        assertEquals("file.txt", file.getName());
        assertEquals(getDownloadsDirectory().getAbsolutePath(), file.getParent());
    }

    @Test
    public void createFileInDownloadsWithAvailableNameShouldCreateMissingDirectoryStructure() {
        // Note: this test doesn't actually verify anything, because Robolectric creates the directory structure
        // before my code (unlike real Android). I don't know how to avoid that. Keeping the test anyway.

        final File downloadsDirectory = getDownloadsDirectory();

        // Just trying to make sure this is an actual temporary directory before it's deleted.
        // Example: /tmp/android-external-files744975316838robolectric/e12c13f2-1135-4519/Download
        assertTrue(downloadsDirectory.getAbsolutePath().matches(".+android-external-files.+robolectric.+Download"));

        assertTrue(downloadsDirectory.delete());
        assertFalse(downloadsDirectory.exists());

        final File file = androidFileUtils.createFileInDownloadsWithAvailableName("file.txt");

        assertNotNull(file);
        assertFalse(file.exists());
        assertTrue(downloadsDirectory.exists());
    }

    @Test
    public void createFileInDownloadsWithAvailableNameShouldIncrementFileNameIfFileAlreadyExists() throws IOException {
        final File existingFile = new File(getDownloadsDirectory(), "file.txt");
        ToolsTest.createTemporaryFile(existingFile);

        final File file = androidFileUtils.createFileInDownloadsWithAvailableName("file.txt");

        assertNotNull(file);
        assertFalse(file.exists());
        assertEquals("file_1.txt", file.getName());
    }

    private File getDownloadsDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }
}
