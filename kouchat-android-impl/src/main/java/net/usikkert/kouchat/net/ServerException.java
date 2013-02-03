
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

package net.usikkert.kouchat.net;

/**
 * This exception is used when there are problems with starting a server.
 *
 * @author Christian Ihle
 */
public class ServerException extends Exception {

    /** Standard serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a ServerException with no message or cause.
     */
    public ServerException() {
        super();
    }

    /**
     * Creates a ServerException with the specified message and cause.
     *
     * @param message The exception message to use.
     * @param cause The cause of the exception.
     */
    public ServerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a ServerException with the specified message.
     *
     * @param message The exception message to use.
     */
    public ServerException(final String message) {
        super(message);
    }

    /**
     * Creates a ServerException with the specified cause.
     *
     * @param cause The cause of the exception.
     */
    public ServerException(final Throwable cause) {
        super(cause);
    }
}
