
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

import java.io.File;
import java.io.IOException;

import net.usikkert.kouchat.junit.ExpectedException;

import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link Tools}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
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
        assertEquals(".", Tools.getFileExtension("."));
        assertEquals(".jpg", Tools.getFileExtension("image.txt.jpg"));
        assertEquals(".extension", Tools.getFileExtension("some thing with spaces.extension"));
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
        assertEquals("", Tools.getFileBaseName("."));
        assertEquals("image.txt", Tools.getFileBaseName("image.txt.jpg"));
        assertEquals("some thing with spaces", Tools.getFileBaseName("some thing with spaces.extension"));
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
    public void getFileWithIncrementedNameShouldReturnSameFileIfNoFileWithThatNameExists() {
        final File nonExistingFile = new File("monkeys.jpg");
        assertFalse(nonExistingFile.exists());

        final File file = Tools.getFileWithIncrementedName(nonExistingFile);

        assertEquals("monkeys.jpg", file.getName());
        assertSame(nonExistingFile, file);
    }

    @Test
    public void getFileWithIncrementedNameShouldReturnFileWithOneAppendedIfFileWithOriginalNameExists() throws IOException {
        createTemporaryFile("monkeys.jpg");

        final File file = Tools.getFileWithIncrementedName(new File("monkeys.jpg"));

        assertEquals("monkeys_1.jpg", file.getName());
        assertNull(file.getParent());
    }

    @Test
    public void getFileWithIncrementedNameShouldReturnFileWithTwoAppendedIfFileWithOneAppendedExists() throws IOException {
        createTemporaryFile("bananas.jpg");
        createTemporaryFile("bananas_1.jpg");

        final File file = Tools.getFileWithIncrementedName(new File("bananas.jpg"));

        assertEquals("bananas_2.jpg", file.getName());
    }

    @Test
    public void getFileWithIncrementedNameShouldReturnFileWithFiveAppendedIfFileUpToFourAppendedExists() throws IOException {
        createTemporaryFile("apples.jpg");
        createTemporaryFile("apples_1.jpg");
        createTemporaryFile("apples_2.jpg");
        createTemporaryFile("apples_3.jpg");
        createTemporaryFile("apples_4.jpg");

        final File file = Tools.getFileWithIncrementedName(new File("apples.jpg"));

        assertEquals("apples_5.jpg", file.getName());
    }

    @Test
    public void getFileWithIncrementedNameShouldUseTheSameParent() throws IOException {
        final String home = System.getProperty("user.home");
        final String homeWithSeparator = home + File.separatorChar;

        createTemporaryFile(homeWithSeparator + "donkeys.jpg");
        createTemporaryFile(homeWithSeparator + "donkeys_1.jpg");

        final File file = Tools.getFileWithIncrementedName(new File(homeWithSeparator + "donkeys.jpg"));

        assertEquals("donkeys_2.jpg", file.getName());
        assertEquals(home, file.getParent());
    }

    @Test
    public void getFileWithIncrementedNameShouldHandleMissingExtension() throws IOException {
        createTemporaryFile("STUFF");
        createTemporaryFile("STUFF_1");

        final File file = Tools.getFileWithIncrementedName(new File("STUFF"));

        assertEquals("STUFF_2", file.getName());
    }

    @Test
    public void getFileWithIncrementedNameShouldHandleSpaces() throws IOException {
        createTemporaryFile("this is a movie.mov");
        createTemporaryFile("this is a movie_1.mov");

        final File file = Tools.getFileWithIncrementedName(new File("this is a movie.mov"));

        assertEquals("this is a movie_2.mov", file.getName());
    }

    @Test
    public void getFileWithIncrementedNameShouldHandleDots() throws IOException {
        createTemporaryFile("this.is.a.song.ogg");
        createTemporaryFile("this.is.a.song_1.ogg");

        final File file = Tools.getFileWithIncrementedName(new File("this.is.a.song.ogg"));

        assertEquals("this.is.a.song_2.ogg", file.getName());
    }

    @Test
    public void emptyIfNullShouldReturnAnEmptyStringWhenInputIsNull() {
        assertEquals("", Tools.emptyIfNull(null));
    }

    @Test
    public void emptyIfNullShouldReturnAnEmptyStringWhenInputIsEmpty() {
        assertEquals("", Tools.emptyIfNull(""));
    }

    @Test
    public void emptyIfNullShouldReturnTheInputStringWhenInputIsNotEmpty() {
        assertEquals("hello", Tools.emptyIfNull("hello"));
    }

    /**
     * Creates a file that will be deleted when the jvm exists.
     *
     * @param file The file to create.
     * @return The created file.
     * @throws IOException If something goes wrong.
     */
    public static File createTemporaryFile(final File file) throws IOException {
        return createTemporaryFile(file.getAbsolutePath());
    }

    private static File createTemporaryFile(final String fileName) throws IOException {
        final File file = new File(fileName);

        if (!file.exists()) {
            assertTrue(file.createNewFile());
        }

        file.deleteOnExit();

        return file;
    }
}
