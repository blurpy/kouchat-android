
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

package net.usikkert.kouchat.android.util;

import static junit.framework.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.usikkert.kouchat.android.filetransfer.AndroidFileUtils;
import net.usikkert.kouchat.util.Tools;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * Android file related utility methods.
 *
 * @author Christian Ihle
 */
public final class FileUtils {

    private static final String KOUCHAT_FILE = "kouchat-1600x1600.png";

    private FileUtils() {
        // Only static methods here
    }

    /**
     * Copies the file <code>kouchat-1600x1600.png</code> from assets to the SD card, if it's not already there.
     *
     * @param instrumentation Test instrumentation.
     * @param activity The activity under test.
     */
    public static void copyKouChatImageFromAssetsToSdCard(final Instrumentation instrumentation, final Activity activity) {
        final File externalStorageDirectory = Environment.getExternalStorageDirectory();
        final File fileToStore = new File(externalStorageDirectory, KOUCHAT_FILE);

        if (!fileToStore.exists()) {
            copyFileToSdCard(fileToStore, instrumentation);
            addFileToDatabase(activity, fileToStore);
        }
    }

    /**
     * Returns a representation of <code>kouchat-1600x1600.png</code> than can be used to get the actual file.
     *
     * @param activity The activity under test.
     * @return <code>kouchat-1600x1600.png</code>.
     */
    public static AndroidFile getKouChatImage(final Activity activity) {
        final Cursor cursor = getCursorForKouChatImage(activity);

        if (cursor.getCount() == 0) {
            throw new RuntimeException("No files in the database");
        }

        cursor.moveToFirst();

        return new AndroidFile(cursor);
    }

    /**
     * Creates a new unique file with the following pattern: <code>kouchat-current_time.extension</code>.
     *
     * @param image The image to get the extension from.
     * @return A new unique file. Should not exist on the file system.
     */
    public static File createNewFile(final AndroidFile image) {
        final File externalStorageDirectory = Environment.getExternalStorageDirectory();
        final String fileName = "kouchat-" + System.currentTimeMillis() + image.getExtension();

        return new File(externalStorageDirectory, fileName);
    }

    private static Cursor getCursorForKouChatImage(final Activity activity) {
        final ContentResolver contentResolver = activity.getContentResolver();

        final Uri from = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final String where = MediaStore.Images.Media.DISPLAY_NAME + " = ?";
        final String[] whereArguments = {KOUCHAT_FILE};
        final String orderBy = MediaStore.Images.Media._ID + " ASC LIMIT 1";

        // SELECT * FROM images WHERE (_display_name = ?) ORDER BY _id ASC LIMIT 1
        return contentResolver.query(from, null, where, whereArguments, orderBy);
    }

    private static void copyFileToSdCard(final File fileToStore, final Instrumentation instrumentation) {
        final Closer closer = Closer.create();
        final AssetManager assets = instrumentation.getContext().getResources().getAssets();

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

    private static void addFileToDatabase(final Activity activity, final File fileToScan) {
        new AndroidFileUtils().addFileToMediaDatabase(activity, fileToScan);
        Tools.sleep(1000); // To give the scanner time to finish
    }
}
