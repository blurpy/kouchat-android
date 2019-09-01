
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

import net.usikkert.kouchat.junit.ExpectedException;

import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link Validate}.
 *
 * @author Christian Ihle
 */
public class ValidateTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void notNullShouldDoNothingIfObjectIsNotNull() {
        Validate.notNull(100, "No exception");
    }

    @Test
    public void notNullShouldThrowExceptionIfObjectIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("This is the message");

        Validate.notNull(null, "This is the message");
    }

    @Test
    public void notEmptyShouldDoNothingIfStringHasValue() {
        Validate.notEmpty("a", "No exception");
    }

    @Test
    public void notEmptyShouldThrowExceptionIfStringIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("This is the message");

        Validate.notEmpty(null, "This is the message");
    }

    @Test
    public void notEmptyShouldThrowExceptionIfStringIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("This is the message");

        Validate.notEmpty("", "This is the message");
    }

    @Test
    public void notEmptyShouldThrowExceptionIfStringIsSpace() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("This is the message");

        Validate.notEmpty(" ", "This is the message");
    }
}
