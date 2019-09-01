
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

import java.util.ArrayList;
import java.util.List;

import net.usikkert.kouchat.event.ErrorListener;
import net.usikkert.kouchat.util.Validate;

/**
 * This is a class for reporting errors to listeners.
 *
 * <p>These errors will be shown to the user of the application.</p>
 *
 * @author Christian Ihle
 */
public class ErrorHandler {

    /** The error listeners. */
    private final List<ErrorListener> listeners;

    public ErrorHandler() {
        listeners = new ArrayList<>();
    }

    /**
     * This method notifies the listeners that an error has occurred.
     *
     * @param errorMsg The message to deliver to the listeners.
     */
    public void showError(final String errorMsg) {
        for (final ErrorListener listener : listeners) {
            listener.errorReported(errorMsg);
        }
    }

    /**
     * This method notifies the listeners that a critical error has occurred.
     *
     * @param criticalErrorMsg The message to deliver to the listeners.
     */
    public void showCriticalError(final String criticalErrorMsg) {
        for (final ErrorListener listener : listeners) {
            listener.criticalErrorReported(criticalErrorMsg);
        }
    }

    /**
     * Adds a new error listener.
     *
     * @param listener The class to add as a listener.
     */
    public void addErrorListener(final ErrorListener listener) {
        Validate.notNull(listener, "Error listener can not be null");

        listeners.add(listener);
    }

    /**
     * Removes an error listener.
     *
     * @param listener The class to remove as a listener.
     */
    public void removeErrorListener(final ErrorListener listener) {
        Validate.notNull(listener, "Error listener can not be null");

        listeners.remove(listener);
    }
}
