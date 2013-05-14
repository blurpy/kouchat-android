
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

package net.usikkert.kouchat.android.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.AndroidUserInterface;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;

/**
 * Test of {@link ChatServiceBinder}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class ChatServiceBinderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructorShouldThrowExceptionIfAndroidUserInterfaceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("AndroidUserInterface can not be null");

        new ChatServiceBinder(null);
    }

    @Test
    public void getAndroidUserInterfaceShouldReturnObjectFromConstructor() {
        final AndroidUserInterface ui = mock(AndroidUserInterface.class);

        final ChatServiceBinder binder = new ChatServiceBinder(ui);

        assertSame(ui, binder.getAndroidUserInterface());
    }
}
