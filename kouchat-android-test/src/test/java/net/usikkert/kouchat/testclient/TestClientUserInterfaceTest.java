
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

package net.usikkert.kouchat.testclient;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link TestClientUserInterface}.
 *
 * @author Christian Ihle
 */
public class TestClientUserInterfaceTest {

    private TestClientUserInterface ui;

    @Before
    public void setUp() {
        ui = new TestClientUserInterface(mock(Settings.class));
    }

    @Test
    public void gotMessageShouldFindTheMessageWithTheExactSameNickNameAndMessage() {
        ui.appendToChat("[14:29:21] <Christian>: hello there", 1234);

        final User christian = new User("Christian", 123);
        final User christian1 = new User("Christian1", 123);

        assertTrue(ui.gotMessage(christian, "hello there"));
        assertFalse(ui.gotMessage(christian1, "hello there"));
        assertFalse(ui.gotMessage(christian, "hello there1"));
    }
}
