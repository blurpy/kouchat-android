
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

package net.usikkert.kouchat.android.chatwindow;

import java.util.List;

import net.usikkert.kouchat.android.component.DefaultLineHeightSpan;
import net.usikkert.kouchat.android.smiley.Smiley;
import net.usikkert.kouchat.android.smiley.SmileyLocator;
import net.usikkert.kouchat.android.smiley.SmileyMap;
import net.usikkert.kouchat.util.Validate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.util.Linkify;

/**
 * Builds the messages that are shown in the chat, with support for smileys, links and colors.
 * All the messages are stored for later use.
 *
 * @author Christian Ihle
 */
public class MessageStylerWithHistory {

    private final SmileyLocator smileyLocator;
    private final SmileyMap smileyMap;
    private final SpannableStringBuilder history;

    public MessageStylerWithHistory(final Context context) {
        Validate.notNull(context, "Context can not be null");

        smileyMap = new SmileyMap(context);
        smileyLocator = new SmileyLocator(smileyMap.getSmileyCodes());
        history = new SpannableStringBuilder();
    }

    /**
     * Adds styling to the message, and appends it to the history.
     *
     * <p>Trims the message first, to avoid blank lines.</p>
     *
     * @param message The message to style and add to the history.
     * @param color The color to style the message with.
     * @return The styled message.
     */
    public CharSequence styleAndAppend(final String message, final int color) {
        final String trimmedMessage = message.trim();
        final SpannableStringBuilder messageBuilder = new SpannableStringBuilder(trimmedMessage + "\n");

        addColor(trimmedMessage, color, messageBuilder);
        addSmileys(trimmedMessage, messageBuilder);
        addLinks(messageBuilder);
        fixLineHeight(trimmedMessage, messageBuilder);

        history.append(messageBuilder);

        return messageBuilder;
    }

    /**
     * Returns all the styled messages added to the history.
     *
     * @return All messages.
     */
    public CharSequence getHistory() {
        return history;
    }

    private void addColor(final String message, final int color, final SpannableStringBuilder messageBuilder) {
        messageBuilder.setSpan(new ForegroundColorSpan(color), 0, message.length(), 0);
    }

    private void addLinks(final SpannableStringBuilder messageBuilder) {
        Linkify.addLinks(messageBuilder, Linkify.WEB_URLS);
    }

    private void addSmileys(final String message, final SpannableStringBuilder messageBuilder) {
        final List<Smiley> smileys = smileyLocator.findSmileys(message);

        for (final Smiley smiley : smileys) {
            final Drawable drawableSmiley = smileyMap.getSmiley(smiley.getCode());
            final ImageSpan smileySpan = new ImageSpan(drawableSmiley, smiley.getCode(), ImageSpan.ALIGN_BOTTOM);

            messageBuilder.setSpan(smileySpan, smiley.getStartPosition(), smiley.getEndPosition(), 0);
        }
    }

    private void fixLineHeight(final String message, final SpannableStringBuilder messageBuilder) {
        // Line spacing is broken on Android 5, 6 and 7. See #77941
        // Don't add this span, or the smileys will partially overlap the text.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            messageBuilder.setSpan(new DefaultLineHeightSpan(), 0, message.length(), 0);
        }
    }
}
