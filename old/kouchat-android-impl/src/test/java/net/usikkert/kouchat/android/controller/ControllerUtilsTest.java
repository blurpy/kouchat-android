
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

package net.usikkert.kouchat.android.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.component.LinkMovementMethodWithSelectSupport;
import net.usikkert.kouchat.android.util.RunRunnableAnswer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.graphics.drawable.Drawable;
import android.text.NoCopySpan;
import android.text.SpanWatcher;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Test of {@link ControllerUtils}.
 *
 * @author Christian Ihle
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class ControllerUtilsTest {

    private ControllerUtils controllerUtils;

    private TextView textView;
    private ScrollView scrollView;

    @Before
    public void setUp() {
        controllerUtils = new ControllerUtils();

        textView = mock(TextView.class);
        scrollView = mock(ScrollView.class);
    }

    @Test
    public void scrollTextViewToBottomShouldSmoothScroll() {
        when(textView.getHeight()).thenReturn(10);
        when(scrollView.getBottom()).thenReturn(25);
        doAnswer(new RunRunnableAnswer()).when(scrollView).post(any(Runnable.class));

        controllerUtils.scrollTextViewToBottom(textView, scrollView);

        verify(scrollView).smoothScrollTo(0, 35);
    }

    @Test
    public void makeLinksClickableShouldUseLinkMovementMethodWithSelectSupport() {
        controllerUtils.makeLinksClickable(textView);

        verify(textView).setMovementMethod(any(LinkMovementMethodWithSelectSupport.class));
    }

    @Test
    public void removeReferencesToTextViewFromTextShouldHandleSpannableString() {
        when(textView.getText()).thenReturn(new SpannableString("SpannableString"));

        controllerUtils.removeReferencesToTextViewFromText(textView);
    }

    @Test
    public void removeReferencesToTextViewFromTextShouldHandleSpannableStringBuilder() {
        when(textView.getText()).thenReturn(new SpannableStringBuilder("SpannableStringBuilder"));

        controllerUtils.removeReferencesToTextViewFromText(textView);
    }

    @Test
    public void removeReferencesToTextViewFromTextShouldRemoveNoCopySpansButNothingElse() {
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("SpannableStringBuilder");

        spannableStringBuilder.setSpan(mock(SpanWatcher.class), 1, 2, 0); // NoCopySpan
        spannableStringBuilder.setSpan(new ImageSpan(mock(Drawable.class)), 3, 4, 0);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(1), 4, 5, 0);
        spannableStringBuilder.setSpan(new BackgroundColorSpan(1), 6, 7, 0);
        spannableStringBuilder.setSpan(mock(TextWatcher.class), 9, 10, 0); // NoCopySpan
        spannableStringBuilder.setSpan(new URLSpan("www.google.com"), 11, 12, 0);

        assertEquals(6, spannableStringBuilder.getSpans(0, 15, Object.class).length);
        assertEquals(2, spannableStringBuilder.getSpans(0, 15, NoCopySpan.class).length);

        when(textView.getText()).thenReturn(spannableStringBuilder);

        controllerUtils.removeReferencesToTextViewFromText(textView);

        final Object[] spans = spannableStringBuilder.getSpans(0, 15, Object.class);
        assertEquals(4, spans.length);

        assertTrue(containsSpan(ImageSpan.class, spans));
        assertTrue(containsSpan(ForegroundColorSpan.class, spans));
        assertTrue(containsSpan(BackgroundColorSpan.class, spans));
        assertTrue(containsSpan(URLSpan.class, spans));

        assertFalse(containsSpan(SpanWatcher.class, spans));
        assertFalse(containsSpan(TextWatcher.class, spans));
        assertFalse(containsSpan(NoCopySpan.class, spans));

        assertEquals("SpannableStringBuilder", spannableStringBuilder.toString()); // Text is still there
    }

    private boolean containsSpan(final Class<?> spanClass, final Object[] spans) {
        for (final Object span : spans) {
            if (spanClass.isInstance(span)) {
                return true;
            }
        }

        return false;
    }
}
