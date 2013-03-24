
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

package net.usikkert.kouchat.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import net.usikkert.kouchat.Constants;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link Tools}.
 *
 * @author Christian Ihle
 */
public class ToolsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Tests that capitalization of the first letter in a word works as expected.
     */
    @Test
    public void testCapitalizeFirstLetter() {
        assertNull(Tools.capitalizeFirstLetter(null));
        assertEquals("Monkey", Tools.capitalizeFirstLetter("monkey"));
        assertEquals("Kou", Tools.capitalizeFirstLetter("kou"));
        assertEquals("Up", Tools.capitalizeFirstLetter("up"));
        assertEquals("O", Tools.capitalizeFirstLetter("o"));
        assertEquals("-", Tools.capitalizeFirstLetter("-"));
        assertEquals("", Tools.capitalizeFirstLetter(""));
        assertEquals("CAKE", Tools.capitalizeFirstLetter("CAKE"));
        assertEquals("123", Tools.capitalizeFirstLetter("123"));
    }

    /**
     * Tests the shortening of words.
     */
    @Test
    public void testShorten() {
        assertNull(Tools.shorten(null, 5));
        assertEquals("Monkey", Tools.shorten("Monkey", 12));
        assertEquals("Monkey", Tools.shorten("Monkey", 6));
        assertEquals("Monke", Tools.shorten("Monkey", 5));
        assertEquals("M", Tools.shorten("Monkey", 1));
        assertEquals("", Tools.shorten("Monkey", 0));
        assertEquals("", Tools.shorten("Monkey", -5));
    }

    /**
     * Tests getting the file extension from a file name.
     */
    @Test
    public void testGetFileExtension() {
        assertNull(Tools.getFileExtension(null));
        assertEquals("", Tools.getFileExtension("file"));
        assertEquals(".txt", Tools.getFileExtension("file.txt"));
        assertEquals(".", Tools.getFileExtension("file."));
        assertEquals(".txt", Tools.getFileExtension(".txt"));
        assertEquals(".jpg", Tools.getFileExtension("image.txt.jpg"));
    }

    /**
     * Tests getting the base name from a file name.
     */
    @Test
    public void testGetFileBaseName() {
        assertNull(Tools.getFileBaseName(null));
        assertEquals("file", Tools.getFileBaseName("file"));
        assertEquals("file", Tools.getFileBaseName("file.txt"));
        assertEquals("file", Tools.getFileBaseName("file."));
        assertEquals("", Tools.getFileBaseName(".txt"));
        assertEquals("image.txt", Tools.getFileBaseName("image.txt.jpg"));
    }

    /**
     * Test finding how many percent a fraction is of the total.
     */
    @Test
    public void testPercent() {
        assertEquals(0.08, Tools.percent(1, 1250), 10);
        assertEquals(25, Tools.percent(50, 200), 10);
        assertEquals(50, Tools.percent(5, 10),  10);
        assertEquals(100, Tools.percent(10, 10), 10);
        assertEquals(200, Tools.percent(60, 30), 10);
    }

    /**
     * Test finding the fraction from the percent of the total.
     */
    @Test
    public void testPercentOf() {
        assertEquals(1, Tools.percentOf(0.08, 1250), 10);
        assertEquals(50, Tools.percentOf(25, 200),  10);
        assertEquals(5, Tools.percentOf(50, 10), 10);
        assertEquals(10, Tools.percentOf(100, 10), 10);
        assertEquals(60, Tools.percentOf(200, 30), 10);
    }

    @Test
    public void postPadString() {
        assertEquals("Hello", Tools.postPadString("Hello", 0));
        assertEquals("Hello", Tools.postPadString("Hello", 5));
        assertEquals("Hello ", Tools.postPadString("Hello", 6));
        assertEquals("Hello  ", Tools.postPadString("Hello", 7));
        assertEquals("Hello       ", Tools.postPadString("Hello", 12));
    }

    @Test
    public void isEmpty() {
        assertFalse(Tools.isEmpty("a"));
        assertFalse(Tools.isEmpty(" a "));
        assertFalse(Tools.isEmpty("hello you"));

        assertTrue(Tools.isEmpty(null));
        assertTrue(Tools.isEmpty(""));
        assertTrue(Tools.isEmpty(" "));
    }

    @Test
    public void appendSlashShouldWorkWithLinuxPaths() {
        System.setProperty("file.separator", "/");
        assertEquals("/var/log/", Tools.appendSlash("/var/log"));
        assertEquals("/var/log/", Tools.appendSlash("/var/log/"));
    }

    @Test
    public void appendSlashShouldWorkWithWindowsPaths() {
        System.setProperty("file.separator", "\\");
        assertEquals("C:\\some folder\\logs\\", Tools.appendSlash("C:\\some folder\\logs"));
        assertEquals("C:\\some folder\\logs\\", Tools.appendSlash("C:\\some folder\\logs\\"));
    }

    @Test
    public void getFileWithIncrementedNameShouldThrowExceptionIfFileIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The existing file to increment the name of can not be null");

        Tools.getFileWithIncrementedName(null);
    }

    @Test
    public void getFileWithIncrementedNameShouldReturnFileWithOneAppendedIfNoFileWithThatNameExists() {
        final File file = Tools.getFileWithIncrementedName(new File("monkeys.jpg"));

        assertEquals("monkeys.jpg.1", file.getName());
        assertNull(file.getParent());
    }

    @Test
    public void getFileWithIncrementedNameShouldReturnFileWithTwoAppendedIfFileWithOneAppendedExists() throws IOException {
        createTemporaryFile("bananas.jpg.1");

        final File file = Tools.getFileWithIncrementedName(new File("bananas.jpg"));

        assertEquals("bananas.jpg.2", file.getName());
    }

    @Test
    public void getFileWithIncrementedNameShouldReturnFileWithFiveAppendedIfFileUpToFourAppendedExists() throws IOException {
        createTemporaryFile("apples.jpg.1");
        createTemporaryFile("apples.jpg.2");
        createTemporaryFile("apples.jpg.3");
        createTemporaryFile("apples.jpg.4");

        final File file = Tools.getFileWithIncrementedName(new File("apples.jpg"));

        assertEquals("apples.jpg.5", file.getName());
    }

    @Test
    public void getFileWithIncrementedNameShouldUseTheSameParent() throws IOException {
        final String home = System.getProperty("user.home");
        final String homeWithSeparator = home + File.separatorChar;

        createTemporaryFile(homeWithSeparator + "donkeys.jpg.1");

        final File file = Tools.getFileWithIncrementedName(new File(homeWithSeparator + "donkeys.jpg"));

        assertEquals("donkeys.jpg.2", file.getName());
        assertEquals(home, file.getParent());
    }

    @Test
    public void isAndroidShouldBeFalseByDefault() {
        assertFalse(Tools.isAndroid());
    }

    @Test
    public void isAndroidShouldBeFalseIfSwing() {
        System.setProperty(Constants.PROPERTY_CLIENT_UI, "Swing");

        assertFalse(Tools.isAndroid());
    }

    @Test
    public void isAndroidShouldBeFalseIfConsole() {
        System.setProperty(Constants.PROPERTY_CLIENT_UI, "Console");

        assertFalse(Tools.isAndroid());
    }

    @Test
    public void isAndroidShouldBeTrueIfAndroid() {
        System.setProperty(Constants.PROPERTY_CLIENT_UI, "Android");

        assertTrue(Tools.isAndroid());
    }

    private File createTemporaryFile(final String fileName) throws IOException {
        final File file = new File(fileName);

        if (!file.exists()) {
            assertTrue(file.createNewFile());
        }

        file.deleteOnExit();

        return file;
    }
}
