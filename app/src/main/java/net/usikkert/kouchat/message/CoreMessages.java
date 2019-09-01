
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

import org.jetbrains.annotations.PropertyKey;

/**
 * Contains all the messages for the core application, independent of user interface.
 *
 * <p>Loaded from <code>resources/messages/core.properties</code></p>
 *
 * @author Christian Ihle
 */
public class CoreMessages implements Messages {

    private static final String CORE = "messages.core";

    private final PropertyFileMessages messages;

    public CoreMessages() {
        messages = new PropertyFileMessages(CORE);
    }

    @Override
    public String getMessage(@PropertyKey(resourceBundle = CORE) final String key, final Object... arguments) {
        return messages.getMessage(key, arguments);
    }

    @Override
    public boolean hasMessage(final String key) {
        return messages.hasMessage(key);
    }
}
