
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

package net.usikkert.kouchat.util;

import java.net.URL;

/**
 * Loads resources from the classpath.
 *
 * @author Christian Ihle
 */
public class ResourceLoader {

    /**
     * Gets the resource at the specified path. Returns <code>null</code> if the resource doesn't exists.
     *
     * @param path Full path to the resource, on the classpath.
     * @return The {@link URL} to the resource if found, or <code>null</code> if not found.
     */
    public URL getResource(final String path) {
        Validate.notEmpty(path, "Path can not be empty");

        return getClass().getResource(path);
    }
}
