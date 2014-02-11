
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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.text.SpannableStringBuilder;

/**
 * Test of {@link NoNewLineTextWatcher}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class NoNewLineTextWatcherTest {

    private NoNewLineTextWatcher textWatcher;

    @Before
    public void setUp() {
        textWatcher = new NoNewLineTextWatcher();
    }

    @Test
    public void beforeTextChangedShouldDoNothing() {
        textWatcher.beforeTextChanged(null, 0, 0, 0);
    }

    @Test
    public void onTextChangedShouldDoNothing() {
        textWatcher.onTextChanged(null, 0, 0, 0);
    }

    @Test
    public void afterTextChangedShouldRemoveAllNewLines() {
        final SpannableStringBuilder stringBuilder = new SpannableStringBuilder("Line1\nLine2\nLine3");

        textWatcher.afterTextChanged(stringBuilder);

        assertEquals("Line1Line2Line3", stringBuilder.toString());
    }

    @Test
    public void afterTextChangedShouldHandleNoNewLines() {
        final SpannableStringBuilder stringBuilder = new SpannableStringBuilder("Line1 Line2 Line3");

        textWatcher.afterTextChanged(stringBuilder);

        assertEquals("Line1 Line2 Line3", stringBuilder.toString());
    }

    @Test
    public void afterTextChangedShouldHandleEmptyString() {
        final SpannableStringBuilder stringBuilder = new SpannableStringBuilder("");

        textWatcher.afterTextChanged(stringBuilder);

        assertEquals("", stringBuilder.toString());
    }
}
