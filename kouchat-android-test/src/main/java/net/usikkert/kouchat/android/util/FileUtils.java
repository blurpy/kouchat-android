
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
     * Copies the file <code>kouchat-1600x1600.png</code> from assets to the root of the SD card,
     * if it's not already there.
     *
     * <p>This will fail if the SD card is unmounted.</p>
     *
     * @param instrumentation Test instrumentation.
     * @param activity The activity under test.
     */
    public static void copyKouChatImageFromAssetsToSdCard(final Instrumentation instrumentation, final Activity activity) {
        final File externalStorageDirectory = Environment.getExternalStorageDirectory();

        copyKouChatImageFromAssetsToStorage(instrumentation, activity, externalStorageDirectory);
    }

    /**
     * Copies the file <code>kouchat-1600x1600.png</code> from assets to the cache directory of the internal storage,
     * if it's not already there.
     *
     * <p>The internal storage should be available even if the SD card is unmounted.</p>
     *
     * @param instrumentation Test instrumentation.
     * @param activity The activity under test.
     */
    public static void copyKouChatImageFromAssetsToInternalStorage(final Instrumentation instrumentation, final Activity activity) {
        final File cacheDir = activity.getCacheDir();

        copyKouChatImageFromAssetsToStorage(instrumentation, activity, cacheDir);
    }

    /**
     * Returns a representation of <code>kouchat-1600x1600.png</code> than can be used to get the
     * actual file on the SD card.
     *
     * @param activity The activity under test.
     * @return <code>kouchat-1600x1600.png</code>.
     */
    public static AndroidFile getKouChatImageFromSdCard(final Activity activity) {
        final Cursor cursor = getCursorForKouChatImageFromExternalStorage(activity);

        return getKouChatImageFromStorage(cursor);
    }

    /**
     * Returns a representation of <code>kouchat-1600x1600.png</code> than can be used to get the
     * actual file on the internal storage.
     *
     * @param activity The activity under test.
     * @return <code>kouchat-1600x1600.png</code>.
     */
    public static AndroidFile getKouChatImageFromInternalStorage(final Activity activity) {
        final Cursor cursor = getCursorForKouChatImageFromInternalStorage(activity);

        return getKouChatImageFromStorage(cursor);
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

    private static void copyKouChatImageFromAssetsToStorage(final Instrumentation instrumentation,
                                                            final Activity activity,
                                                            final File storageDirectory) {
        final File fileToStore = new File(storageDirectory, KOUCHAT_FILE);

        if (!fileToStore.exists()) {
            copyFileToDevice(fileToStore, instrumentation);
            addFileToDatabase(activity, fileToStore);
        }
    }

    private static AndroidFile getKouChatImageFromStorage(final Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            throw new RuntimeException("No files in the database");
        }

        cursor.moveToFirst();

        return new AndroidFile(cursor);
    }

    private static Cursor getCursorForKouChatImageFromExternalStorage(final Activity activity) {
        return getCursorForKouChatImageFromStorage(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    private static Cursor getCursorForKouChatImageFromInternalStorage(final Activity activity) {
        return getCursorForKouChatImageFromStorage(activity, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    }

    private static Cursor getCursorForKouChatImageFromStorage(final Activity activity, final Uri from) {
        final ContentResolver contentResolver = activity.getContentResolver();

        final String where = MediaStore.Images.Media.DISPLAY_NAME + " = ?";
        final String[] whereArguments = {KOUCHAT_FILE};
        final String orderBy = MediaStore.Images.Media._ID + " ASC LIMIT 1";

        // SELECT * FROM images WHERE (_display_name = ?) ORDER BY _id ASC LIMIT 1
        return contentResolver.query(from, null, where, whereArguments, orderBy);
    }

    private static void copyFileToDevice(final File fileToStore, final Instrumentation instrumentation) {
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
