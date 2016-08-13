
/***************************************************************************
 *   Copyright 2006-2016 by Christian Ihle                                 *
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
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;

/**
 * An {@link EditTextPreference} that uses the default dialog theme of the platform.
 *
 * <p>This is necessary because KouChat is using a light theme from ActionBarSherlock,
 * but the theme doesn't apply correctly to dialogs on Android 2.3.3. The dialogs in that version
 * are still dark, but the text changes to a dark color. So it's difficult to read.</p>
 *
 * <p>Inspired by http://stackoverflow.com/questions/14032977/correct-text-color-appearance-in-alertdialog</p>
 *
 * @author Christian Ihle
 */
public class ThemedEditTextPreference extends EditTextPreference {

    public ThemedEditTextPreference(final Context context, final AttributeSet attrs, final int defStyle) {
        super(createContextThemeWrapper(context), attrs, defStyle);
        blockNewLines();
    }

    public ThemedEditTextPreference(final Context context, final AttributeSet attrs) {
        super(createContextThemeWrapper(context), attrs);
        blockNewLines();
    }

    public ThemedEditTextPreference(final Context context) {
        super(createContextThemeWrapper(context));
        blockNewLines();
    }

    /**
     * Avoid getting new lines when pressing 'enter'.
     */
    private void blockNewLines() {
        getEditText().addTextChangedListener(new NoNewLineTextWatcher());
    }

    private static ContextThemeWrapper createContextThemeWrapper(final Context originalContext) {
        return new ContextThemeWrapper(originalContext, R.style.Theme_KouChat_Dialog);
    }
}
