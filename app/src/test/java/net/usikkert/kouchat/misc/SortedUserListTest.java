
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

package net.usikkert.kouchat.misc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import net.usikkert.kouchat.event.UserListListener;
import net.usikkert.kouchat.junit.ExpectedException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link SortedUserList}.
 *
 * @author Christian Ihle
 */
public class SortedUserListTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SortedUserList userList;

    private UserListListener listener;

    private User niles;
    private User john;
    private User lenny;
    private User annie;

    @Before
    public void setUp() {
        userList = new SortedUserList();

        listener = mock(UserListListener.class);
        userList.addUserListListener(listener);

        niles = new User("Niles", 1);
        john = new User("John", 2);
        lenny = new User("Lenny", 3);
        annie = new User("Annie", 4);
    }

    @Test
    public void addShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        userList.add(null);
    }

    @Test
    public void addShouldReturnTrueWhenUserHasBeenAddedToTheList() {
        assertEquals(0, userList.size());

        assertTrue(userList.add(niles));
        assertEquals(1, userList.size());

        assertTrue(userList.add(lenny));
        assertEquals(2, userList.size());

        assertTrue(userList.add(annie));
        assertEquals(3, userList.size());
    }

    @Test
    public void addShouldSortUsersByName() {
        addAllUsers();

        assertEquals("Annie", userList.get(0).getNick());
        assertEquals("John", userList.get(1).getNick());
        assertEquals("Lenny", userList.get(2).getNick());
        assertEquals("Niles", userList.get(3).getNick());
    }

    @Test
    public void addShouldNotifyListenersAboutPosition() {
        userList.add(annie);
        userList.add(john);
        userList.add(lenny);
        userList.add(niles);

        verify(listener).userAdded(0, annie);
        verify(listener).userAdded(1, john);
        verify(listener).userAdded(2, lenny);
        verify(listener).userAdded(3, niles);
    }

    @Test
    public void addShouldNotifyListenersAboutPositionAfterSort() {
        userList.add(niles);
        userList.add(lenny);
        userList.add(john);
        userList.add(annie);

        verify(listener).userAdded(0, niles);
        verify(listener).userAdded(0, lenny);
        verify(listener).userAdded(0, john);
        verify(listener).userAdded(0, annie);
    }

    @Test
    public void getShouldReturnNullIfNoElementFound() {
        assertNull(userList.get(0));
        assertNull(userList.get(1));
    }

    @Test
    public void indexOfShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        userList.indexOf(null);
    }

    @Test
    public void indexOfShouldReturnCorrectIndexOfUserInList() {
        addAllUsers();

        assertEquals(0, userList.indexOf(annie));
        assertEquals(1, userList.indexOf(john));
        assertEquals(2, userList.indexOf(lenny));
        assertEquals(3, userList.indexOf(niles));
    }

    @Test
    public void indexOfShouldReturnMinusOneIfUserDoesNotExist() {
        assertEquals(-1, userList.indexOf(annie));
    }

    @Test
    public void removeShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        userList.remove(null);
    }

    @Test
    public void removeShouldReturnTrueAndRemoveTheCorrectUser() {
        addAllUsers();

        assertEquals(4, userList.size());

        assertTrue(userList.remove(annie));

        assertEquals(3, userList.size());
        assertEquals(-1, userList.indexOf(annie));
    }

    @Test
    public void removeShouldNotifyListeners() {
        addAllUsers();

        userList.remove(john);

        verify(listener).userRemoved(1, john);
    }

    @Test
    public void removeShouldReturnFalseIfUserDoesNotExist() {
        assertFalse(userList.remove(annie));
    }

    @Test
    public void removeShouldNotNotifyListenersIfUserDoesNotExist() {
        userList.remove(annie);

        verifyZeroInteractions(listener);
    }

    @Test
    public void addUserListListenerShouldThrowExceptionIfListenerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("UserListListener can not be null");

        userList.addUserListListener(null);
    }

    @Test
    public void removeUserListListenerShouldThrowExceptionIfListenerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("UserListListener can not be null");

        userList.removeUserListListener(null);
    }

    @Test
    public void removeUserListListenerShouldRemoveTheListener() {
        userList.removeUserListListener(listener);

        addAllUsers();

        verifyZeroInteractions(listener);
    }

    @Test
    public void setShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        userList.set(0, null);
    }

    @Test
    public void setShouldReplaceUserAtPositionAndReturnTheReplacedUser() {
        final User test1 = new User("Test1", 10);
        final User test2 = new User("Test2", 11);

        userList.add(test1);
        assertEquals(1, userList.size());

        assertSame(test1, userList.set(0, test2));

        assertEquals(1, userList.size());

        assertEquals(-1, userList.indexOf(test1));
        assertEquals(0, userList.indexOf(test2));
    }

    @Test
    public void setShouldSortListAfterReplace() {
        addAllUsers();

        final User test1 = new User("Test1", 10);

        userList.set(0, test1); // Replacing Annie at position 0 with test1, which should be put at position 3

        assertSame(john, userList.get(0));
        assertSame(lenny, userList.get(1));
        assertSame(niles, userList.get(2));
        assertSame(test1, userList.get(3));
    }

    @Test
    public void setShouldNotifyListenersAboutTheNewPosition() {
        addAllUsers();

        final User test1 = new User("Test1", 10);

        userList.set(0, test1);

        verify(listener).userChanged(3, test1);
    }

    @Test
    public void setShouldFailIfTryingToReplaceUserThatDoesNotExist() {
        expectedException.expect(IndexOutOfBoundsException.class);
//        expectedException.expectMessage("Index: 0, Size: 0"); // Different on JDK 11

        userList.set(0, new User("Test1", 10));
    }

    @Test
    public void getListenersShouldReturnImmutableList() {
        expectedException.expect(UnsupportedOperationException.class); // No message

        userList.getListeners().add(listener);
    }

    @Test
    public void getListenersShouldReturnNewListEveryTime() {
        assertNotSame(userList.getListeners(), userList.getListeners());
    }

    @Test
    public void getListenersShouldReturnTheCurrentListeners() {
        // Just listener from setUp()
        final List<UserListListener> listeners1 = userList.getListeners();
        assertEquals(1, listeners1.size());
        assertTrue(listeners1.contains(listener));

        // Adding another listener
        final UserListListener listener2 = mock(UserListListener.class);
        userList.addUserListListener(listener2);

        final List<UserListListener> listeners2 = userList.getListeners();
        assertEquals(2, listeners2.size());
        assertTrue(listeners2.contains(listener));
        assertTrue(listeners2.contains(listener2));

        // Removing the first listener
        userList.removeUserListListener(listener);

        final List<UserListListener> listeners3 = userList.getListeners();
        assertEquals(1, listeners3.size());
        assertTrue(listeners3.contains(listener2));
    }

    private void addAllUsers() {
        userList.add(niles);
        userList.add(john);
        userList.add(lenny);
        userList.add(annie);
    }
}
