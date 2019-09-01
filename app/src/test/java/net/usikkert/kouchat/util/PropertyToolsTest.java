
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import net.usikkert.kouchat.junit.ExpectedException;

import org.jetbrains.annotations.NonNls;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test of {@link PropertyTools}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class PropertyToolsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private PropertyTools propertyTools;

    private IOTools ioTools;

    @Before
    public void setUp() {
        propertyTools = new PropertyTools();

        ioTools = TestUtils.setFieldValueWithMock(propertyTools, "ioTools", IOTools.class);
    }

    @Test
    public void loadPropertiesShouldThrowExceptionIfFilePathIsNull() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File path can not be empty");

        propertyTools.loadProperties(null);
    }

    @Test
    public void loadPropertiesShouldThrowExceptionIfFilePathIsEmpty() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File path can not be empty");

        propertyTools.loadProperties(" ");
    }

    @Test
    public void loadPropertiesShouldThrowExceptionIfFileNotFound() throws IOException {
        expectedException.expect(FileNotFoundException.class);
        // Linux: (No such file or directory) || Windows: (The system cannot find the file specified)
        expectedException.expectMessageContaining("unknown.properties (");

        propertyTools.loadProperties("unknown.properties");
    }

    @Test
    @Ignore("Looks in the Android sdk folder for some reason")
    public void loadPropertiesShouldSuccessfullyLoadAllPropertiesInFileFromFullFileSystemPath() throws IOException {
        final File filePath = getPathTo("test-messages.properties");

        final Properties properties = propertyTools.loadProperties(filePath.getAbsolutePath());

        assertEquals(3, properties.size());

        assertEquals("This is the first string", properties.getProperty("test.string1"));
        assertEquals("This is the second string", properties.getProperty("test.string2"));
        assertEquals("Say hello to {0} from {1}!", properties.getProperty("test.hello"));
    }

    @Test
    @Ignore("Looks in the Android sdk folder for some reason")
    public void loadPropertiesShouldCloseInputStreamWhenDoneLoading() throws IOException {
        final File filePath = getPathTo("test-messages.properties");

        propertyTools.loadProperties(filePath.getAbsolutePath());

        verify(ioTools).close(any(InputStream.class));
    }

    @Test
    public void loadPropertiesShouldCloseInputStreamEvenOnException() {
        try {
            propertyTools.loadProperties("nothing");
            fail("Should fail to load properties");
        }

        catch (final IOException e) {
            verify(ioTools).close(nullable(InputStream.class));
        }
    }

    @Test
    public void savePropertiesShouldThrowExceptionIfFilePathIsNull() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File path can not be empty");

        propertyTools.saveProperties(null, new Properties(), null);
    }

    @Test
    public void savePropertiesShouldThrowExceptionIfFilePathIsEmpty() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("File path can not be empty");

        propertyTools.saveProperties(" ", new Properties(), null);
    }

    @Test
    public void savePropertiesShouldThrowExceptionIfPropertiesIsNull() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Properties can not be null");

        propertyTools.saveProperties("file", null, null);
    }

    @Test
    @Ignore("Looks in the Android sdk folder for some reason")
    public void savePropertiesShouldThrowExceptionIfFileCouldNotBeSaved() throws IOException {
        expectedException.expect(FileNotFoundException.class);
        // Linux: (Is a directory) || Windows: (Access is denied)
        expectedException.expectMessageContaining("test-classes (");

        final File filePath = getPathTo("");

        propertyTools.saveProperties(filePath.getAbsolutePath(), new Properties(), null);
    }

    @Test
    public void savePropertiesShouldSavePropertiesToNewFile() throws IOException {
        final File filePath = getTempPathTo("temp1.properties");

        assertFalse(filePath.exists());

        final Properties properties = new Properties();
        properties.put("key1", "value1");
        properties.put("key2", "value2");

        propertyTools.saveProperties(filePath.getAbsolutePath(), properties, null);

        assertTrue(filePath.exists());

        final Properties loadedProperties = propertyTools.loadProperties(filePath.getAbsolutePath());
        assertEquals(2, loadedProperties.size());
        assertEquals("value1", loadedProperties.getProperty("key1"));
        assertEquals("value2", loadedProperties.getProperty("key2"));
    }

    @Test
    public void savePropertiesShouldSavePropertiesToExistingFile() throws IOException {
        final File filePath = getTempPathTo("temp2.properties");

        assertFalse(filePath.exists());

        final Properties firstProperties = new Properties();
        firstProperties.put("key1", "value1");
        firstProperties.put("key2", "value2");

        propertyTools.saveProperties(filePath.getAbsolutePath(), firstProperties, null);

        assertTrue(filePath.exists());

        final Properties secondProperties = new Properties();
        secondProperties.put("key2", "new value");
        secondProperties.put("key3", "new key");

        propertyTools.saveProperties(filePath.getAbsolutePath(), secondProperties, null);

        final Properties loadedProperties = propertyTools.loadProperties(filePath.getAbsolutePath());
        assertEquals(2, loadedProperties.size());
        assertEquals("new value", loadedProperties.getProperty("key2"));
        assertEquals("new key", loadedProperties.getProperty("key3"));
    }

    @Test
    public void savePropertiesShouldHandlePathCharacters() throws IOException {
        final File filePath = getTempPathTo("temp3.properties");

        assertFalse(filePath.exists());

        final Properties properties = new Properties();
        properties.put("linux", "/opt/opera/the bin/opera");
        properties.put("windows", "C:\\Programs and features\\Opera\\opera.exe");

        propertyTools.saveProperties(filePath.getAbsolutePath(), properties, null);

        assertTrue(filePath.exists());

        final Properties loadedProperties = propertyTools.loadProperties(filePath.getAbsolutePath());
        assertEquals(2, loadedProperties.size());
        assertEquals("/opt/opera/the bin/opera", loadedProperties.getProperty("linux"));
        assertEquals("C:\\Programs and features\\Opera\\opera.exe", loadedProperties.getProperty("windows"));
    }

    @Test
    public void savePropertiesShouldFlushAndCloseFileWriterWhenDone() throws IOException {
        final File filePath = getTempPathTo("temp4.properties");

        assertFalse(filePath.exists());

        final Properties properties = new Properties();
        properties.put("key", "value");

        propertyTools.saveProperties(filePath.getAbsolutePath(), properties, null);

        assertTrue(filePath.exists());

        verify(ioTools).flush(any(FileWriter.class));
        verify(ioTools).close(any(FileWriter.class));
    }

    @Test
    public void savePropertiesShouldFlushAndCloseWriterEvenOnException() {
        final File filePath = getPathTo("");

        try {
            propertyTools.saveProperties(filePath.getAbsolutePath(), new Properties(), null);
            fail("Should fail to save properties");
        }

        catch (final IOException e) {
            verify(ioTools).flush(nullable(FileWriter.class));
            verify(ioTools).close(nullable(FileWriter.class));
        }
    }

    private File getPathTo(@NonNls final String fileName) {
        final URL classpathUrl = getClass().getResource("/");

        return new File(classpathUrl.getPath(), fileName);
    }

    private File getTempPathTo(@NonNls final String fileName) {
        return new File(temporaryFolder.getRoot(), fileName);
    }
}
