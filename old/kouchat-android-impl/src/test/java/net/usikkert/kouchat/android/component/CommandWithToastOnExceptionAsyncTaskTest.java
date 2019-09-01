
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

package net.usikkert.kouchat.android.component;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ExecutionException;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.CommandException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

/**
 * Test of {@link CommandWithToastOnExceptionAsyncTask}.
 *
 * @author Christian Ihle
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class CommandWithToastOnExceptionAsyncTaskTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Command command;
    private CommandWithToastOnExceptionAsyncTask asyncTask;

    @Before
    public void setUp() {
        command = mock(Command.class);
        asyncTask = new CommandWithToastOnExceptionAsyncTask(Robolectric.application, command);
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new CommandWithToastOnExceptionAsyncTask(null, command);
    }

    @Test
    public void constructorShouldThrowExceptionIfCommandIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Command can not be null");

        new CommandWithToastOnExceptionAsyncTask(Robolectric.application, null);
    }

    @Test
    public void executeShouldRunCommand() throws CommandException {
        asyncTask.execute();

        verify(command).runCommand();
        assertEquals(0, ShadowToast.shownToastCount());
    }

    @Test
    public void executeShouldShowToastOnException() throws CommandException {
        doThrow(new CommandException("Don't run")).when(command).runCommand();

        asyncTask.execute();

        verify(command).runCommand();
        assertEquals("Don't run", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void getShouldReturnTrueAfterSuccessfulCommand() throws ExecutionException, InterruptedException {
        asyncTask.execute();

        assertTrue(asyncTask.get());
    }

    @Test
    public void getShouldReturnFalseOnException() throws CommandException, ExecutionException, InterruptedException {
        doThrow(new CommandException("Don't run")).when(command).runCommand();

        asyncTask.execute();

        assertFalse(asyncTask.get());
    }
}
