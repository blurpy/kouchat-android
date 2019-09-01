
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

package net.usikkert.kouchat.settings;

import net.usikkert.kouchat.event.SettingsListener;

import org.jetbrains.annotations.NonNls;

/**
 * An "enum" representing the different types of settings that can be changed.
 *
 * <p>Contains only the settings that can be used with {@link SettingsListener}.</p>
 *
 * <p>This is not a real enum because of the need to support inheritance. Use {@link #equals(Object)}
 * instead of <code>==</code> for comparison, to avoid issues with class loaders and serialization.</p>
 *
 * @author Christian Ihle
 */
public class Setting {

    /** Maps to {@link Settings#isLogging()}. */
    public static final Setting LOGGING = new Setting("LOGGING");

    /** Maps to {@link Settings#isSystemTray()}. */
    public static final Setting SYSTEM_TRAY = new Setting("SYSTEM_TRAY");

    private final String name; // Must be unique

    protected Setting(@NonNls final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Setting setting = (Setting) o;

        return name.equals(setting.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
