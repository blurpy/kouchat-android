
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

package net.usikkert.kouchat.android.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.misc.User;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;

/**
 * Test of {@link UserListAdapterWithoutMe}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class UserListAdapterWithoutMeTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UserListAdapterWithoutMe adapter;

    private User me;

    @Before
    public void setUp() {
        me = new User("Me", 123);

        adapter = new UserListAdapterWithoutMe(Robolectric.application.getApplicationContext(), me);
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new UserListAdapterWithoutMe(null, me);
    }

    @Test
    public void constructorShouldThrowExceptionIfMeIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Me can not be null");

        new UserListAdapterWithoutMe(mock(Context.class), null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getItem0ShouldGiveIndexOutOfBoundsExceptionWhenOnlyMe() {
        adapter.add(me);

        assertNull(adapter.getItem(0));
    }

    @Test
    public void getCountShouldBe0WhenOnlyMe() {
        adapter.add(me);

        assertEquals(0, adapter.getCount());
    }

    @Test
    public void getItemShouldIgnoreMe() {
        final User test1 = new User("Test1", 1);
        final User test2 = new User("Test2", 2);
        final User test3 = new User("Test3", 3);
        final User test4 = new User("Test4", 4);

        adapter.add(test1);
        adapter.add(test2);
        adapter.add(me);
        adapter.add(test3);
        adapter.add(test4);

        assertEquals(4, adapter.getCount());

        assertSame(test1, adapter.getItem(0));
        assertSame(test2, adapter.getItem(1));
        assertSame(test3, adapter.getItem(2));
        assertSame(test4, adapter.getItem(3));
    }

    @Test
    @Ignore("This does not work with Robolectric yet.") // sort is not implemented in shadow
    public void getItemShouldIgnoreMeAfterSort() {
        final User penny = new User("Penny", 1);
        final User amy = new User("Amy", 2);
        final User que = new User("Que", 3);
        final User fido = new User("Fido", 4);

        adapter.add(me);
        adapter.add(penny);
        adapter.add(amy);
        adapter.add(que);
        adapter.add(fido);

        assertEquals(4, adapter.getCount());

        assertSame(amy, adapter.getItem(0));
        assertSame(fido, adapter.getItem(1));
        assertSame(penny, adapter.getItem(2));
        assertSame(que, adapter.getItem(3));
    }
}
