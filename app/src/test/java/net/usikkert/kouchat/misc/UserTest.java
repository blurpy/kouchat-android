
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

import org.junit.Test;

/**
 * Test of {@link User}.
 *
 * @author Christian Ihle
 */
public class UserTest {

    @Test
    public void equalsAndHashCodeShouldBeTrueWhenSameCode() {
        final User test1 = new User("Test1", 123);
        final User test2 = new User("Test2", 123);

        assertTrue(test1.equals(test2));
        assertTrue(test2.equals(test1));

        assertEquals(test1.hashCode(), test2.hashCode());
    }

    @Test
    public void equalsAndHashCodeShouldBeFalseWhenDifferentCode() {
        final User test1 = new User("Test1", 123);
        final User test2 = new User("Test2", 124);

        assertFalse(test1.equals(test2));
        assertFalse(test2.equals(test1));

        assertNotEquals(test1.hashCode(), test2.hashCode());
    }

    @Test
    @SuppressWarnings({ "ObjectEqualsNull", "EqualsBetweenInconvertibleTypes" })
    public void equalsShouldBeFalseWhenWrongObjects() {
        final User test1 = new User("Test1", 123);

        assertFalse(test1.equals(null));
        assertFalse(test1.equals("No user"));
    }
}
