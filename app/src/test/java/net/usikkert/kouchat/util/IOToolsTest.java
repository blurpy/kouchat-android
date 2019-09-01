
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

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.util.logging.Logger;

import net.usikkert.kouchat.junit.ExpectedException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link IOTools}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class IOToolsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private IOTools ioTools;

    @Before
    public void setUp() {
        ioTools = new IOTools();

        TestUtils.setFieldValueWithMock(ioTools, "LOG", Logger.class); // To silence log output in tests
    }

    @Test
    public void flushShouldHandleNull() {
        ioTools.flush(null);
    }

    @Test
    public void flushShouldFlush() throws IOException {
        final Flushable flushable = mock(Flushable.class);

        ioTools.flush(flushable);

        verify(flushable).flush();
    }

    @Test
    public void flushShouldIgnoreExceptions() throws IOException {
        final Flushable flushable = mock(Flushable.class);
        doThrow(new IOException("Throw on flush")).when(flushable).flush();

        ioTools.flush(flushable);

        verify(flushable).flush();
    }

    @Test
    public void closeShouldHandleNull() {
        ioTools.close(null);
    }

    @Test
    public void closeShouldClose() throws IOException {
        final Closeable closeable = mock(Closeable.class);

        ioTools.close(closeable);

        verify(closeable).close();
    }

    @Test
    public void closeShouldIgnoreExceptions() throws IOException {
        final Closeable closeable = mock(Closeable.class);
        doThrow(new IOException("Throw on close")).when(closeable).close();

        ioTools.close(closeable);

        verify(closeable).close();
    }

    @Test
    public void createFolderShouldThrowExceptionIfFolderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Folder can not be empty");

        ioTools.createFolder(null);
    }

    @Test
    public void createFolderShouldThrowExceptionIfFolderIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Folder can not be empty");

        ioTools.createFolder(" ");
    }

    @Test
    public void createFolderShouldCreateMissingFolders() {
        final File folder = new File("target/test-folder");
        final File subFolder = new File("target/test-folder/sub-folder");

        assertFalse(folder.exists());

        ioTools.createFolder("target/test-folder/sub-folder");

        assertTrue(folder.exists());
        assertTrue(subFolder.exists());

        folder.deleteOnExit();
        subFolder.deleteOnExit();
    }
}
