
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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

/**
 * This is a singleton class for reporting errors to listeners.
 * These errors will be shown to the user of the application.
 *
 * @author Christian Ihle
 */
public final class ErrorHandler {

    /** The single instance of this class. */
    private static final ErrorHandler INSTANCE = new ErrorHandler();

    /** The error listeners. */
    private final List<ErrorListener> listeners;

    /**
     * Private constructor.
     */
    private ErrorHandler() {
        listeners = new ArrayList<ErrorListener>();
    }

    /**
     * Will return the only instance of this class.
     *
     * @return The only instance of ErrorHandler.
     */
    public static ErrorHandler getErrorHandler() {
        return INSTANCE;
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
        listeners.add(listener);
    }

    /**
     * Removes an error listener.
     *
     * @param listener The class to remove as a listener.
     */
    public void removeErrorListener(final ErrorListener listener) {
        listeners.remove(listener);
    }
}
