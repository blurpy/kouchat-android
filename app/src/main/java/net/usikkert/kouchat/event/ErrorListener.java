
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

package net.usikkert.kouchat.event;

/**
 * This is the listener interface used by the ErrorHandler.
 * The implementing class should be able to show the error message
 * to the user of the application.
 *
 * @author Christian Ihle
 */
public interface ErrorListener {

    /**
     * This method is called when an error occurs.
     *
     * @param errorMsg The message to show.
     */
    void errorReported(String errorMsg);

    /**
     * This method is called when a critical error occurs.
     *
     * @param criticalErrorMsg The message to show.
     */
    void criticalErrorReported(String criticalErrorMsg);
}
