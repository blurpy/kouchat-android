
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

package net.usikkert.kouchat.android.settings;

import net.usikkert.kouchat.settings.Setting;

import org.jetbrains.annotations.NonNls;

/**
 * An "enum" that adds the Android settings that will be notified when changed.
 *
 * @author Christian Ihle
 */
public class AndroidSetting extends Setting {

    /** Maps to {@link AndroidSettings#isWakeLockEnabled()}. */
    public static final AndroidSetting WAKE_LOCK = new AndroidSetting("WAKE_LOCK");

    protected AndroidSetting(@NonNls final String name) {
        super(name);
    }
}
