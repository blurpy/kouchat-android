
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

import static org.mockito.Mockito.*;

import java.io.PrintStream;

import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A junit rule that mocks {@link System#out}, to avoid output and to verify usage.
 *
 * <p>Use regular verify methods directly on {@link System#out} during tests.</p>
 *
 * <p>This is better than {@link StandardOutputStreamLog}, which registers as an additional stream,
 * keeping the output to the console.</p>
 *
 * @author Christian Ihle
 */
public class ExpectedSystemOut implements TestRule {

    private PrintStream originalSystemOut;

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                beforeSetUp();

                try {
                    base.evaluate();
                } finally {
                    afterTearDown();
                }
            }
        };
    }

    private void beforeSetUp() {
        originalSystemOut = System.out;

        System.setOut(mock(PrintStream.class));
    }

    private void afterTearDown() {
        System.setOut(originalSystemOut);
    }
}
