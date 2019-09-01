
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

package net.usikkert.kouchat.util;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.Nullable;

/**
 * Tools for working with IO.
 *
 * @author Christian Ihle
 */
public class IOTools {

    private static final Logger LOG = Logger.getLogger(IOTools.class.getName());

    /**
     * Flushes the parameter, and ignores any errors.
     *
     * @param flushable The object to flush. Handles <code>null</code>.
     */
    public void flush(@Nullable final Flushable flushable) {
        if (flushable == null) {
            return;
        }

        try {
            flushable.flush();
        }

        catch (final IOException e) {
            // Don't care
            LOG.log(Level.WARNING, "Flush failed", e);
        }
    }

    /**
     * Closes the parameter, and ignores any errors.
     *
     * @param closeable The object to close. Handles <code>null</code>.
     */
    public void close(@Nullable final Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        }

        catch (final IOException e) {
            // Don't care
            LOG.log(Level.WARNING, "Close failed", e);
        }
    }

    /**
     * Creates the specified folder, including all missing folders in the path.
     *
     * @param folder The folder to create.
     */
    public void createFolder(final String folder) {
        Validate.notEmpty(folder, "Folder can not be empty");

        final File theFolder = new File(folder);

        if (!theFolder.exists()) {
            final boolean success = theFolder.mkdirs();

            if (!success) {
                LOG.log(Level.INFO, "Failed to create folder: " + folder);
            }
        }
    }
}
