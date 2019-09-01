
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.text.SpannableStringBuilder;

/**
 * Test of {@link NoNewLineTextWatcher}.
 *
 * @author Christian Ihle
 */
@Config(manifest = Config.NONE)
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

    /**
     * equals() is not implemented in the real Android class, but it is implemented in the Robolectric shadow,
     * meaning that the tests above can go green even when it doesn't work in practice.
     *
     * It's important to use toString() on the stringBuilder before equals().
     * It's not possible to mock equals() and toString(), so have to provoke the behaviour with a subclass.
     */
    @Test
    public void afterTextChangedShouldUseEqualsOnTheStringAndNotTheStringBuilder() {
        final SpannableStringBuilder stringBuilder = new SpannableStringBuilderThatNeverEquals("\n");

        textWatcher.afterTextChanged(stringBuilder);

        assertEquals("", stringBuilder.toString()); // Will fail if equals is used on the stringBuilder
    }

    // CHECKSTYLE:OFF
    private class SpannableStringBuilderThatNeverEquals extends SpannableStringBuilder {

        private SpannableStringBuilderThatNeverEquals(final CharSequence text) {
            super(text);
        }

        @Override
        public CharSequence subSequence(final int start, final int end) {
            return new SpannableStringBuilderThatNeverEquals(super.subSequence(start, end));
        }

        @Override
        public boolean equals(final Object o) {
            return false;
        }
    }
    // CHECKSTYLE:ON
}
