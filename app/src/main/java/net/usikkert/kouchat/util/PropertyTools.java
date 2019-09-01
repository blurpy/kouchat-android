
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jetbrains.annotations.NonNls;

/**
 * Utility methods for handling {@link Properties}.
 *
 * @author Christian Ihle
 */
public class PropertyTools {

    private final IOTools ioTools = new IOTools();

    /**
     * Loads the contents of a properties file from the given file path.
     *
     * @param filePath The full file system path of the properties file to load.
     * @return The properties of the given file.
     * @throws FileNotFoundException If the file doesn't exist.
     * @throws IOException If something goes wrong loading the properties from file.
     */
    public Properties loadProperties(@NonNls final String filePath) throws IOException {
        Validate.notEmpty(filePath, "File path can not be empty");

        final Properties properties = new Properties();
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(filePath);
            properties.load(inputStream);

            return properties;
        }

        finally {
            ioTools.close(inputStream);
        }
    }

    /**
     * Saves the specified properties in a file at the specified path, with an optional comment at the top.
     *
     * @param filePath The full file system path to the properties file to save.
     * @param properties The properties to save in the file.
     * @param comment Optional comment to put at the top of the properties file.
     * @throws IOException If the properties could not be saved.
     */
    public void saveProperties(@NonNls final String filePath, final Properties properties,
                               final String comment) throws IOException {
        Validate.notEmpty(filePath, "File path can not be empty");
        Validate.notNull(properties, "Properties can not be null");

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(filePath);
            properties.store(fileWriter, comment);
        }

        finally {
            ioTools.flush(fileWriter);
            ioTools.close(fileWriter);
        }
    }
}
