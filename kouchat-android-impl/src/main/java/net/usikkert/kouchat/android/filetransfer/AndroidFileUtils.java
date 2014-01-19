
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
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

import java.io.File;
import java.util.logging.Logger;

import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * Utility methods for handling files on Android.
 *
 * @author Christian Ihle
 */
public class AndroidFileUtils {

    private static final Logger LOG = Logger.getLogger(AndroidFileUtils.class.getName());

    /**
     * Gets a {@link File} reference to the file represented by the {@link Uri}.
     *
     * <p>The uri is expected to be in the following format: <code>content://media/external/images/media/22</code></p>
     *
     * <p>The <code>content</code> protocol is the only protocol supported, and is used to find files
     * in the Android media database, using a {@link ContentResolver}.</p>
     *
     * @param uri Content uri to the file to return. Can be <code>null</code>.
     * @param contentResolver The content resolver, from a context.
     * @return The file, if it's found, or <code>null</code> if not found.
     */
    public File getFileFromContentUri(final Uri uri, final ContentResolver contentResolver) {
        Validate.notNull(contentResolver, "ContentResolver can not be null");

        if (uri == null || !uri.getScheme().equals("content")) {
            return null;
        }

        final String[] columns = new String[] {MediaStore.MediaColumns.DATA};
        final Cursor cursor = contentResolver.query(uri, columns, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        final String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));

        return new File(path);
    }

    /**
     * Adds the file to the media database in Android.
     *
     * <p>It's an important step after adding a file to the file system. Without doing this, the
     * added file will not be visible in apps (like the gallery) without a reboot.</p>
     *
     * @param context A context.
     * @param fileToAdd The file to add to the database.
     */
    public void addFileToMediaDatabase(final Context context, final File fileToAdd) {
        Validate.notNull(context, "Context can not be null");
        Validate.notNull(fileToAdd, "File to add can not be null");

        final Intent scanMediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanMediaIntent.setData(Uri.fromFile(fileToAdd));

        context.sendBroadcast(scanMediaIntent);
    }

    /**
     * Creates a new unique file in the public downloads directory of the device, with the given file
     * name as a suggestion. If the name is in use, it gets appended by a counter.
     *
     * <p>If the downloads directory is missing, it will be created.</p>
     *
     * @param fileName The suggested file name to use on the file.
     * @return A new unique file.
     */
    public File createFileInDownloadsWithAvailableName(final String fileName) {
        Validate.notEmpty(fileName, "File name can not be empty");

        final File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Not sure if this is supposed to be missing, but it happens on the Android 2.3.3 emulator.
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                LOG.warning(String.format(
                        "Unable to create the public download directory. Saving here will probably fail. path=%s",
                        directory));
            }
        }

        return Tools.getFileWithIncrementedName(new File(directory, fileName));
    }
}
