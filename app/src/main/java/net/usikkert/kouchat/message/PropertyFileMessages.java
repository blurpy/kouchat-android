
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

import java.text.MessageFormat;
import java.util.ResourceBundle;

import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.NonNls;

/**
 * Implementation of {@link Messages} using a {@link ResourceBundle} to load a property file with messages.
 *
 * @author Christian Ihle
 */
public class PropertyFileMessages implements Messages {

    private final ResourceBundle bundle;

    /**
     * Constructs an instance that loads a property-file with the given base name.
     *
     * @param baseName The base name of the property-file.
     *                 Example: the base name of <code>messages_en.properties</code> is <code>messages</code>.
     */
    public PropertyFileMessages(@NonNls final String baseName) {
        Validate.notEmpty(baseName, "Base name can not be empty");

        bundle = ResourceBundle.getBundle(baseName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasMessage(final String key) {
        Validate.notEmpty(key, "Key can not be empty");

        return bundle.containsKey(key);
    }

    /**
     * Gets a message with the given key, merged with the given arguments.
     *
     * <p>Supports messages in property files of the following format:<br/>
     * <code>Message with arguments {0}, {1} and {2}</code>.</p>
     */
    @Override
    public String getMessage(final String key, final Object... arguments) {
        Validate.notEmpty(key, "Key can not be empty");

        final String message = bundle.getString(key);

        return MessageFormat.format(message, arguments); // Merges arguments
    }
}
