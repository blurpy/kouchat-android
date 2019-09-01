
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

package net.usikkert.kouchat.junit;

import static org.junit.Assert.*;

import org.jetbrains.annotations.NonNls;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.Statement;

/**
 * Test of {@link ExpectedException}.
 *
 * @author Christian Ihle
 */
public class ExpectedExceptionTest {

    private ExpectedException expectedException;

    @Before
    public void setUp() {
        expectedException = new ExpectedException();
    }

    @Test
    public void checkingWithExpectMessageShouldNotThrowAssertionErrorIfExpectedMessageIsIdentical() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("the error message");

        final AssertionError error = runTestAndThrow("the error message");

        assertNull(error);
    }

    @Test
    public void checkingWithExpectMessageShouldThrowAssertionErrorIfExpectedMessageIsDifferent() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("something else");

        final AssertionError error = runTestAndThrow("the error message");

        assertNotNull(error);
    }

    @Test
    public void checkingWithExpectMessageShouldThrowAssertionErrorIfExpectedMessageOnlyContainsTheLastPart() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("the error message");

        final AssertionError error = runTestAndThrow("this is the error message");

        assertNotNull(error);
    }

    @Test
    public void checkingWithExpectMessageShouldThrowAssertionErrorIfExpectedMessageOnlyContainsTheFirstPart() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("the error message");

        final AssertionError error = runTestAndThrow("the error message this is");

        assertNotNull(error);
    }

    @Test
    public void checkingWithExpectMessageShouldThrowAssertionErrorIfExpectedMessageOnlyContainsTheMiddlePart() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("the error message");

        final AssertionError error = runTestAndThrow("hello the error message this is");

        assertNotNull(error);
    }

    @Test
    public void checkingWithExpectShouldThrowAssertionErrorIfExceptionIsDifferent() {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("the error message");

        final AssertionError error = runTestAndThrow("the error message");

        assertNotNull(error);
    }

    @Test
    public void checkingWithExpectMessageContainingShouldNotThrowAssertionErrorIfExpectedMessageIsIdentical() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessageContaining("the error message");

        final AssertionError error = runTestAndThrow("the error message");

        assertNull(error);
    }

    @Test
    public void checkingWithExpectMessageContainingShouldThrowAssertionErrorIfExpectedMessageIsDifferent() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessageContaining("something else");

        final AssertionError error = runTestAndThrow("the error message");

        assertNotNull(error);
    }

    @Test
    public void checkingWithExpectMessageContainingShouldNotThrowAssertionErrorIfExpectedMessageOnlyContainsTheText() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessageContaining("the error message");

        final AssertionError error = runTestAndThrow("hello the error message this is");

        assertNull(error);
    }

    private AssertionError runTestAndThrow(@NonNls final String messageToThrow) {
        final Statement statement = expectedException.apply(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                throw new IllegalArgumentException(messageToThrow);
            }
        }, null);

        try {
            statement.evaluate();
        }

        catch (final Throwable throwable) {
            if (throwable instanceof AssertionError) {
                return (AssertionError) throwable;
            }
        }

        return null;
    }
}
