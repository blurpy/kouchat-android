
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

package net.usikkert.kouchat.android.userlist;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;

/**
 * Test of {@link UserListAdapterWithoutMe}.
 *
 * @author Christian Ihle
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class UserListAdapterWithoutMeTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UserListAdapterWithoutMe adapter;

    private User me;

    @Before
    public void setUp() {
        me = new User("Me", 123);

        adapter = new UserListAdapterWithoutMe(Robolectric.application, me);
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(NullPointerException.class); // Happens in Android superclass

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
    public void getCountShouldBeMinus1WhenEmpty() {
        assertEquals(-1, adapter.getCount()); // Looks strange, but should never happen
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

    @Test
    public void onDestroyShouldClearTheListIncludingMe() {
        final User penny = new User("Penny", 1);
        final User amy = new User("Amy", 2);

        adapter.add(me);
        adapter.add(penny);
        adapter.add(amy);

        assertEquals(2, adapter.getCount());

        adapter.onDestroy();

        assertEquals(-1, adapter.getCount()); // -1 since "me" is removed as well
    }

    @Test
    public void onDestroyShouldSetAllFieldsToNull() {
        assertTrue(TestUtils.allFieldsHaveValue(adapter));

        adapter.onDestroy();

        assertTrue(TestUtils.allFieldsAreNull(adapter));
    }
}
