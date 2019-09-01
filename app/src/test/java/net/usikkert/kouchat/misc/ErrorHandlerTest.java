
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

import static org.mockito.Mockito.*;

import net.usikkert.kouchat.event.ErrorListener;
import net.usikkert.kouchat.junit.ExpectedException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link ErrorHandler}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ErrorHandlerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ErrorHandler errorHandler;

    @Before
    public void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    public void showErrorShouldDoNothingIfNoListeners() {
        errorHandler.showError("error");
    }

    @Test
    public void showErrorShouldNotifyListeners() {
        final ErrorListener listener1 = mock(ErrorListener.class);
        final ErrorListener listener2 = mock(ErrorListener.class);

        errorHandler.addErrorListener(listener1);
        errorHandler.addErrorListener(listener2);

        errorHandler.showError("error");

        verify(listener1).errorReported("error");
        verify(listener2).errorReported("error");
    }

    @Test
    public void showCriticalErrorShouldDoNothingIfNoListeners() {
        errorHandler.showCriticalError("error");
    }

    @Test
    public void showCriticalErrorShouldNotifyListeners() {
        final ErrorListener listener1 = mock(ErrorListener.class);
        final ErrorListener listener2 = mock(ErrorListener.class);

        errorHandler.addErrorListener(listener1);
        errorHandler.addErrorListener(listener2);

        errorHandler.showCriticalError("error");

        verify(listener1).criticalErrorReported("error");
        verify(listener2).criticalErrorReported("error");
    }

    @Test
    public void addErrorListenerShouldThrowExceptionIfListenerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error listener can not be null");

        errorHandler.addErrorListener(null);
    }

    @Test
    public void removeErrorListenerShouldThrowExceptionIfListenerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error listener can not be null");

        errorHandler.removeErrorListener(null);
    }

    @Test
    public void removeErrorListenerShouldRemoveListener() {
        final ErrorListener listener = mock(ErrorListener.class);

        errorHandler.addErrorListener(listener);
        errorHandler.removeErrorListener(listener);

        errorHandler.showError("error");

        verifyZeroInteractions(listener);
    }
}
