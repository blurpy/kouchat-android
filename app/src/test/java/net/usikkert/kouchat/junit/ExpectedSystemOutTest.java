
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import net.usikkert.kouchat.util.Tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.mockito.internal.util.DefaultMockingDetails;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test of {@link ExpectedSystemOut}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ExpectedSystemOutTest {

    private ExpectedSystemOut expectedSystemOut;

    @Before
    public void setUp() {
        expectedSystemOut = new ExpectedSystemOut();
    }

    @Test(timeout = 5000)
    public void applyShouldReplaceSystemOutWithMockDuringTestAndResetWhenDone() throws Throwable {
        final boolean[] testShouldWait = new boolean[1];
        testShouldWait[0] = true;

        final boolean[] testIsRunning = new boolean[1];
        testIsRunning[0] = false;

        final Statement realTest = mock(Statement.class);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) {
                testIsRunning[0] = true;

                while (testShouldWait[0]) {
                    Tools.sleep(1);
                }

                return null;
            }
        }).when(realTest).evaluate();

        final Statement wrappedTest = expectedSystemOut.apply(realTest, null);

        // Run the test in a different thread that waits until told otherwise, to be able to verify state during run
        final Thread wrappedTestRunner = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wrappedTest.evaluate();
                }
                catch (final Throwable throwable) {
                    throwable.printStackTrace();
                    fail("Should not get exception on evaluate()");
                }
            }
        });

        // Checking that a real System.out is used before starting the test
        assertFalse(isMock(System.out));

        wrappedTestRunner.start(); // Run @Before and @Test

        // Wait for @Test to start
        while (!testIsRunning[0]) {
            Tools.sleep(1);
        }

        // Inside @Test now. System.out should be mocked
        assertTrue(isMock(System.out));

        testShouldWait[0] = false;
        wrappedTestRunner.join(); // Wait for @Test and @After to finish

        // Test is done, System.out should be back to the real value
        assertFalse(isMock(System.out));
    }

    @Test
    public void applyShouldResetMockedSystemOutAlsoOnFailure() throws Throwable {
        assertFalse(isMock(System.out));

        final Statement realTest = mock(Statement.class);
        doThrow(new Throwable("Something failed")).when(realTest).evaluate();

        final Statement wrappedTest = expectedSystemOut.apply(realTest, null);

        try {
            wrappedTest.evaluate();
            fail("Should have failed to evaluate()");
        }

        catch (final Throwable throwable) {
            assertEquals("Something failed", throwable.getMessage());
            // Test failed, but System.out should be reset to real value
            assertFalse(isMock(System.out));
        }
    }

    private boolean isMock(final Object o) {
        return new DefaultMockingDetails(o).isMock();
    }
}
