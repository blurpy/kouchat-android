
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

package net.usikkert.kouchat.message;

/**
 * Interface for getting messages in the correct language.
 *
 * @author Christian Ihle
 */
public interface Messages {

    /**
     * Checks if a message with the given key exists.
     *
     * @param key The message key to lookup.
     * @return If the key exists.
     */
    boolean hasMessage(String key);

    /**
     * Gets a message with the given key, merged with the given arguments.
     *
     * @param key The message key to lookup.
     * @param arguments Arguments to merge with a message with parameters.
     * @return The message.
     * @throws RuntimeException If the message doesn't exist.
     */
    String getMessage(String key, Object... arguments);
}
