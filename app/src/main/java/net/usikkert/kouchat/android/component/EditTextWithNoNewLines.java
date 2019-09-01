
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

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * An {@link EditText} that blocks 'enter'. This is an improvement to just setting
 * max lines to 1, because pressing enter usually moves focus away from the text field.
 *
 * <p>This subclass is for cases where it's easier to choose the class, than to access the
 * instance, such as preferences.</p>
 *
 * @author Christian Ihle
 */
public class EditTextWithNoNewLines extends AppCompatEditText {

    public EditTextWithNoNewLines(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        addTextChangedListener(new NoNewLineTextWatcher());
    }

    public EditTextWithNoNewLines(final Context context, final AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.editTextStyle);
    }

    public EditTextWithNoNewLines(final Context context) {
        this(context, null);
    }
}
