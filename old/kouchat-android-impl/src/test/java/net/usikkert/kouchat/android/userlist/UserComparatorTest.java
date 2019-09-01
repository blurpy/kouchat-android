
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

import java.util.ArrayList;
import java.util.Collections;

import net.usikkert.kouchat.misc.User;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link UserComparator}.
 *
 * @author Christian Ihle
 */
public class UserComparatorTest {

    private UserComparator comparator;

    @Before
    public void setUp() {
        comparator = new UserComparator();
    }

    @Test
    public void compareShouldConsiderTheSameUserToBeEqual() {
        final User user = new User("Test", 123);

        assertEquals(0, comparator.compare(user, user));
    }

    @Test
    public void compareShouldConsiderTheSameUserOfDifferentObjectsToBeEqual() {
        final User user1 = new User("Test", 123);
        final User user2 = new User("Test", 123);

        assertEquals(0, comparator.compare(user1, user2));
    }

    @Test
    public void compareShouldConsiderTheSameUserOfDifferentCasingToBeEqual() {
        final User user1 = new User("Test", 123);
        final User user2 = new User("test", 123);

        assertEquals(0, comparator.compare(user1, user2));
        assertEquals(0, comparator.compare(user2, user1));
    }

    @Test
    public void compareShouldSortAlphabetically() {
        final User user1 = new User("aa", 123);
        final User user2 = new User("ab", 124);

        assertEquals(-1, comparator.compare(user1, user2));
        assertEquals(1, comparator.compare(user2, user1));
    }

    @Test
    public void compareShouldSortAlphabeticallyIgnoringCase() {
        final User user1 = new User("aa", 123);
        final User user2 = new User("AB", 124);

        assertEquals(-1, comparator.compare(user1, user2));
        assertEquals(1, comparator.compare(user2, user1));
    }

    @Test
    public void compareShouldSortAlphabeticallyInCollectionsSort() {
        final User user1 = new User("ABd", 121);
        final User user2 = new User("def", 122);
        final User user3 = new User("Abc", 123);
        final User user4 = new User("XYZ", 124);

        final ArrayList<User> users = new ArrayList<User>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

        Collections.sort(users, comparator);

        assertSame(user3, users.get(0));
        assertSame(user1, users.get(1));
        assertSame(user2, users.get(2));
        assertSame(user4, users.get(3));
    }
}
