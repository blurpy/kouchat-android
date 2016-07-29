/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
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
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.EditText;

import net.usikkert.kouchat.android.R;

/**
 * An {@link EditText} that uses the default dialog theme of the platform.
 *
 * <p>This is necessary to make the dialog use correct default dialog colors on Android 4.</p>
 *
 * <p>Inspired by http://stackoverflow.com/questions/14032977/correct-text-color-appearance-in-alertdialog</p>
 *
 * @author Christian Ihle
 */
public class ThemedEditText extends EditText {

    public ThemedEditText(final Context context, final AttributeSet attrs, final int defStyle) {
        super(createContextThemeWrapper(context), attrs, defStyle);
    }

    public ThemedEditText(final Context context, final AttributeSet attrs) {
        super(createContextThemeWrapper(context), attrs);
    }

    public ThemedEditText(final Context context) {
        super(createContextThemeWrapper(context));
    }

    private static ContextThemeWrapper createContextThemeWrapper(final Context originalContext) {
        return new ContextThemeWrapper(originalContext, R.style.Theme_KouChat_Dialog);
    }
}
