
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

package net.usikkert.kouchat.android.component;

import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;

/**
 * Link movement method with support for text selection as well, on Android 3.0 or newer.
 *
 * <p>Usually you have to choose between {@link LinkMovementMethod} to get link support,
 * or {@link android.text.method.ArrowKeyMovementMethod} to get selection support.</p>
 *
 * <p>Or you could set the autoLink property on the {@link android.widget.TextView}.
 * But it seems to be a bit buggy. It's looks like it's random which url you get to when you click on a link.
 * And it doesn't work on Android 2.3.3, at least not in the emulator.</p>
 *
 * <p>The selection support is disabled on Android 2.3.3, because it doesn't work. Trying to select using a regular
 * {@link android.text.method.ArrowKeyMovementMethod} gives a popup to select, but it's not possible to
 * actually select anything. Trying to select using this class only leads to:
 * <code>java.lang.IndexOutOfBoundsException: setSpan (-1 ... -1) starts before 0</code></p>
 *
 * @author Christian Ihle
 */
public class LinkMovementMethodWithSelectSupport extends LinkMovementMethod {

    private static LinkMovementMethodWithSelectSupport instance;

    @Override
    public boolean canSelectArbitrarily() {
        return true;
    }

    public static MovementMethod getInstance() {
        if (instance == null) {
            instance = new LinkMovementMethodWithSelectSupport();
        }

        return instance;
    }
}
