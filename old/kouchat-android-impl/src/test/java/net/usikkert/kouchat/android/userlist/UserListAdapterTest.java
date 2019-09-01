
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
import net.usikkert.kouchat.misc.UserList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test of {@link UserListAdapter}.
 *
 * @author Christian Ihle
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class UserListAdapterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UserListAdapter adapter;

    private UserList userList;

    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {
        adapter = new UserListAdapter(Robolectric.application);

        userList = mock(UserList.class);

        user1 = new User("User1", 1);
        user2 = new User("User2", 2);
        user3 = new User("User3", 3);
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(NullPointerException.class); // Happens in Android superclass

        new UserListAdapter(null);
    }

    @Test
    public void addShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        adapter.add(null);
    }

    @Test
    public void addShouldSortUsers() {
        adapter.add(user3);
        assertOrder(user3);

        adapter.add(user1);
        assertOrder(user1, user3);

        adapter.add(user2);
        assertOrder(user1, user2, user3);
    }

    @Test
    public void addUsersShouldThrowExceptionIfUserListIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("UserList can not be null");

        adapter.addUsers(null);
    }

    @Test
    public void addUsersShouldAddAllUsersInTheList() {
        when(userList.get(0)).thenReturn(user1);
        when(userList.get(1)).thenReturn(user2);
        when(userList.get(2)).thenReturn(user3);
        when(userList.size()).thenReturn(3);

        assertEquals(0, adapter.getCount());

        adapter.addUsers(userList);

        assertEquals(3, adapter.getCount());
        assertOrder(user1, user2, user3);
    }

    @Test
    public void addUsersShouldSortUsers() {
        when(userList.get(0)).thenReturn(user2);
        when(userList.get(1)).thenReturn(user1);
        when(userList.get(2)).thenReturn(user3);
        when(userList.size()).thenReturn(3);

        adapter.addUsers(userList);

        assertOrder(user1, user2, user3);
    }

    @Test
    public void sortShouldSortUsers() {
        adapter.add(user1);
        adapter.add(user2);
        adapter.add(user3);

        user1.setNick("Xyz");
        user2.setNick("Abc");

        assertOrder(user1, user2, user3);

        adapter.sort();

        assertOrder(user2, user3, user1);
    }

    @Test
    public void onDestroyShouldClearTheList() {
        assertEquals(0, adapter.getCount());

        adapter.add(user1);
        adapter.add(user2);
        adapter.add(user3);

        assertEquals(3, adapter.getCount());

        adapter.onDestroy();

        assertEquals(0, adapter.getCount());
    }

    private void assertOrder(final User... users) {
        for (int i = 0; i < users.length; i++) {
            final User user = users[i];
            assertSame(user, adapter.getItem(i));
        }
    }
}
