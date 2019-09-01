
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

package net.usikkert.kouchat.ui;

/**
 * Used in the ui factory to notify the main class
 * if there is a problem with the loading of the ui.
 *
 * @author Christian Ihle
 */
public class UIException extends Exception {

    /**
     * Creates a UIException with no message or cause.
     */
    public UIException() {
        super();
    }

    /**
     * Creates a UIException with the specified message and cause.
     *
     * @param message The exception message to use.
     * @param cause The cause of the exception.
     */
    public UIException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a UIException with the specified message.
     *
     * @param message The exception message to use.
     */
    public UIException(final String message) {
        super(message);
    }

    /**
     * Creates a UIException with the specified cause.
     *
     * @param cause The cause of the exception.
     */
    public UIException(final Throwable cause) {
        super(cause);
    }
}
