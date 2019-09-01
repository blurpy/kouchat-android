
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

import android.text.Editable;
import android.text.TextWatcher;

/**
 * A {@link TextWatcher} that removes all new lines. Useful on multi-line text fields.
 *
 * @author Christian Ihle
 */
public class NoNewLineTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

    }

    @Override
    public void afterTextChanged(final Editable text) {
        // Iterates the string backwards and removes "\n"
        for (int i = text.length(); i > 0; i--) {
            final CharSequence character = text.subSequence(i - 1, i);

            if (character.toString().equals("\n")) {
                text.replace(i - 1, i, "");
            }
        }
    }
}
