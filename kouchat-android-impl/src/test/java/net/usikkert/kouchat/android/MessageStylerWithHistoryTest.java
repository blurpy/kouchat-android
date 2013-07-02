
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

package net.usikkert.kouchat.android;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import net.usikkert.kouchat.android.controller.MainChatController;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test of {@link MessageStylerWithHistory}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class MessageStylerWithHistoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MessageStylerWithHistory messageStyler;

    @Before
    public void setUp() {
        messageStyler = new MessageStylerWithHistory(new MainChatController());
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new MessageStylerWithHistory(null);
    }

    @Test
    public void styleAndAppendShouldReturnMessage() {
        final CharSequence message = messageStyler.styleAndAppend("Hello, this is a test", 0);

        assertEquals("Hello, this is a test\n", message.toString());
    }

    @Test
    public void getHistoryShouldReturnAllAppendedMessages() {
        messageStyler.styleAndAppend("Message 1", 0);
        messageStyler.styleAndAppend("Message 2", 0);
        messageStyler.styleAndAppend("Message 3", 0);

        assertEquals("Message 1\nMessage 2\nMessage 3\n", messageStyler.getHistory().toString());
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void styleAndAppendShouldAddColor() {
        final CharSequence message = messageStyler.styleAndAppend("Color me!", 50);

        checkColor((SpannableStringBuilder) message);
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void getHistoryShouldRememberColor() {
        messageStyler.styleAndAppend("Color me!", 50);

        checkColor((SpannableStringBuilder) messageStyler.getHistory());
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void styleAndAppendShouldAddLinks() {
        final CharSequence message = messageStyler.styleAndAppend("http://kouchat.googlecode.com/", 0);

        checkLinks((SpannableStringBuilder) message);
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void getHistoryShouldRememberLinks() {
        messageStyler.styleAndAppend("http://kouchat.googlecode.com/", 0);

        checkLinks((SpannableStringBuilder) messageStyler.getHistory());
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void styleAndAppendShouldAddSmileys() {
        final CharSequence message = messageStyler.styleAndAppend(":)", 0);

        checkSmileys((SpannableStringBuilder) message);
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void getHistoryShouldRememberSmileys() {
        messageStyler.styleAndAppend(":)", 0);

        checkSmileys((SpannableStringBuilder) messageStyler.getHistory());
    }

    private void checkColor(final SpannableStringBuilder builder) {
        final ForegroundColorSpan[] spans = builder.getSpans(0, builder.length(), ForegroundColorSpan.class);

        assertNotNull(spans);
        assertEquals(1, spans.length);
        assertEquals(50, spans[0].getForegroundColor());
    }

    private void checkLinks(final SpannableStringBuilder builder) {
        final URLSpan[] spans = builder.getSpans(0, builder.length(), URLSpan.class);

        assertNotNull(spans);
        assertEquals(1, spans.length);
        assertEquals("http://kouchat.googlecode.com/", spans[0].getURL());
    }

    private void checkSmileys(final SpannableStringBuilder builder) {
        final ImageSpan[] spans = builder.getSpans(0, builder.length(), ImageSpan.class);

        assertNotNull(spans);
        assertEquals(1, spans.length);
        assertEquals(":)", spans[0].getSource());
    }
}
