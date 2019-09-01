
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

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import android.graphics.Paint;
import android.text.TextPaint;

/**
 * Test of {@link DefaultLineHeightSpan}
 *
 * @author Christian Ihle
 */
public class DefaultLineHeightSpanTest {

    private DefaultLineHeightSpan span;

    @Before
    public void setUp() {
        span = new DefaultLineHeightSpan();
    }

    @Test // This is not implemented by Robolectric yet, so using mocks
    public void chooseHeightWithPaintShouldCallGetFontMetricsInt() {
        final Paint.FontMetricsInt fm = mock(Paint.FontMetricsInt.class);
        final TextPaint textPaint = mock(TextPaint.class);

        span.chooseHeight(null, 0, 0, 0, 0, fm, textPaint);

        verify(textPaint).getFontMetricsInt(fm);
    }

    @Test
    public void chooseHeightWithoutPaintShouldDoNothing() {
        span.chooseHeight(null, 0, 0, 0, 0, null);
    }
}
