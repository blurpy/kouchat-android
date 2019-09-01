
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

import android.graphics.Paint;
import android.text.TextPaint;
import android.text.style.LineHeightSpan;

/**
 * A type of span that adjusts the line height in the text view to be the default of the text,
 * to avoid line height increasing when adding image spans.
 *
 * <p>Example:</p>
 *
 * <ul>
 *   <li>Text only: <code>FontMetricsInt: top=-17 ascent=-15 descent=4 bottom=5 leading=0</code></li>
 *   <li>Text and images: <code>FontMetricsInt: top=-20 ascent=-20 descent=4 bottom=5 leading=0 bottom</code></li>
 * </ul>
 *
 * <p>This span will set the font metrics the way it is when there is text only, even with images.</p>
 *
 * @author Christian Ihle
 */
public class DefaultLineHeightSpan implements LineHeightSpan.WithDensity {

    @Override
    public void chooseHeight(final CharSequence text, final int start, final int end, final int spanstartv,
                             final int v, final Paint.FontMetricsInt fm, final TextPaint paint) {
        paint.getFontMetricsInt(fm); // Resets font metrics to the default
    }

    @Override
    public void chooseHeight(final CharSequence text, final int start, final int end, final int spanstartv,
                             final int v, final Paint.FontMetricsInt fm) {

    }
}
