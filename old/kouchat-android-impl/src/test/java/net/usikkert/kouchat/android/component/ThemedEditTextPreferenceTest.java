
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;

/**
 * Test of {@link ThemedEditTextPreference}.
 *
 * @author Christian Ihle
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class ThemedEditTextPreferenceTest {

    private ThemedEditTextPreference preferenceFromOneParameterConstructor;
    private ThemedEditTextPreference preferenceFromTwoParameterConstructor;
    private ThemedEditTextPreference preferenceFromThreeParameterConstructor;

    @Before
    public void setUp() {
        preferenceFromOneParameterConstructor =
                new ThemedEditTextPreference(Robolectric.application);
        preferenceFromTwoParameterConstructor =
                new ThemedEditTextPreference(Robolectric.application, mock(AttributeSet.class));
        preferenceFromThreeParameterConstructor =
                new ThemedEditTextPreference(Robolectric.application, mock(AttributeSet.class), 0);
    }

    @Test
    public void getContextShouldReturnThemeWrapper() {
        verifyContext(preferenceFromOneParameterConstructor);
        verifyContext(preferenceFromTwoParameterConstructor);
        verifyContext(preferenceFromThreeParameterConstructor);
    }

    @Test
    public void editTextShouldAddNoNewLineTextWatcher() {
        verifyNoNewLineTextWatcher(preferenceFromOneParameterConstructor);
        verifyNoNewLineTextWatcher(preferenceFromTwoParameterConstructor);
        verifyNoNewLineTextWatcher(preferenceFromThreeParameterConstructor);
    }

    private void verifyContext(final ThemedEditTextPreference preference) {
        final Context context = preference.getContext();

        assertEquals(ContextThemeWrapper.class, context.getClass());
        assertEquals(Integer.valueOf(R.style.Theme_Default_Dialog), getThemeResourceId(context));
    }

    private Integer getThemeResourceId(final Context context) {
        return TestUtils.getFieldValue(context, Integer.class, "mThemeResource");
    }

    private void verifyNoNewLineTextWatcher(final ThemedEditTextPreference preference) {
        final List<TextWatcher> textWatchers = getTextWatchers(preference);

        assertEquals(1, textWatchers.size());
        assertEquals(NoNewLineTextWatcher.class, textWatchers.get(0).getClass());
    }

    @SuppressWarnings("unchecked")
    private List<TextWatcher> getTextWatchers(final ThemedEditTextPreference preference) {
        return TestUtils.getFieldValue(preference.getEditText(), List.class, "mListeners");
    }
}
