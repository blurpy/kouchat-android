
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;

/**
 * Test of {@link ThemedEditTextPreference}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class ThemedEditTextPreferenceTest {

    @Test
    public void getContextShouldReturnThemeWrapperWithOneParameterConstructor() {
        final ThemedEditTextPreference preference = new ThemedEditTextPreference(Robolectric.application);

        verifyContext(preference);
    }

    @Test
    public void getContextShouldReturnThemeWrapperWithTwoParameterConstructor() {
        final ThemedEditTextPreference preference =
                new ThemedEditTextPreference(Robolectric.application, mock(AttributeSet.class));

        verifyContext(preference);
    }

    @Test
    public void getContextShouldReturnThemeWrapperWithThreeParameterConstructor() {
        final ThemedEditTextPreference preference =
                new ThemedEditTextPreference(Robolectric.application, mock(AttributeSet.class), 0);

        verifyContext(preference);
    }

    private void verifyContext(final ThemedEditTextPreference preference) {
        final Context context = preference.getContext();

        assertEquals(ContextThemeWrapper.class, context.getClass());
        assertEquals(Integer.valueOf(R.style.Theme_Default_Dialog), getThemeResourceId(context));
    }

    private Integer getThemeResourceId(final Context context) {
        return TestUtils.getFieldValue(context, Integer.class, "mThemeResource");
    }
}
