
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

import static org.hamcrest.CoreMatchers.*;

import org.jetbrains.annotations.NonNls;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A custom variant of {@link org.junit.rules.ExpectedException} that verifies that the whole exception message
 * is identical to the expected message instead of just if the message contains the expected message.
 *
 * @author Christian Ihle
 */
public class ExpectedException implements TestRule {

    private final org.junit.rules.ExpectedException expectedException = org.junit.rules.ExpectedException.none();

    public static ExpectedException none() {
        return new ExpectedException();
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return expectedException.apply(base, description);
    }

    /**
     * Which exception to expect.
     */
    public void expect(final Class<? extends Throwable> expectedExceptionClass) {
        expectedException.expect(expectedExceptionClass);
    }

    /**
     * The exact exception message to expect.
     */
    public void expectMessage(@NonNls final String expectedExceptionMessage) {
        expectedException.expectMessage(equalTo(expectedExceptionMessage));
    }

    /**
     * A part of the exception message to expect.
     */
    public void expectMessageContaining(@NonNls final String substring) {
        expectedException.expectMessage(substring);
    }
}
