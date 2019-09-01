
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

import net.usikkert.kouchat.android.R;

import android.content.Context;
import android.support.v7.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * An {@link EditTextPreference} for nick name that sets it's own dialog layout.
 *
 * <p>This is necessary because the support library version of preferences
 * won't allow any customization anymore, like setting max length and block
 * 'enter'.</p>
 *
 * @author Christian Ihle
 */
public class NickNameEditTextPreference extends EditTextPreference {

    public NickNameEditTextPreference(final Context context, final AttributeSet attrs,
                                      final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setDialogLayoutResource(R.layout.preference_dialog_nickname);
    }

    public NickNameEditTextPreference(final Context context, final AttributeSet attrs,
                                      final int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NickNameEditTextPreference(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.attr.editTextPreferenceStyle);
    }

    public NickNameEditTextPreference(final Context context) {
        this(context, null);
    }
}
