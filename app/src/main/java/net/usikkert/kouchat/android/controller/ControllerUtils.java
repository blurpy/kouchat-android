
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

import net.usikkert.kouchat.android.component.LinkMovementMethodWithSelectSupport;

import android.text.NoCopySpan;
import android.text.Spannable;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Reusable functionality for controllers.
 *
 * @author Christian Ihle
 */
public class ControllerUtils {

    /** Number of milliseconds in a second. */
    public static final int ONE_SECOND = 1000;

    /**
     * Scrolls to the last line of text in a text view.
     *
     * @param textView The text view to scroll.
     * @param scrollView The surrounding scroll view.
     */
    public void scrollTextViewToBottom(final TextView textView, final ScrollView scrollView) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, scrollView.getBottom() + textView.getHeight());
            }
        });
    }

    /**
     * Makes sure the links you click on opens in the browser.
     *
     * @param textView The text view to activate link clicking on.
     */
    public void makeLinksClickable(final TextView textView) {
        textView.setMovementMethod(LinkMovementMethodWithSelectSupport.getInstance());
    }

    /**
     * Removes spans with references to the text view.
     *
     * <p>There are several spans that are added to the text here and there, like:</p>
     *
     * <ul>
     *   <li>android.text.DynamicLayout$ChangeWatcher</li>
     *   <li>android.widget.TextView$ChangeWatcher</li>
     *   <li>android.widget.Editor$EasyEditSpanController</li>
     * </ul>
     *
     * <p>These spans are of type {@link NoCopySpan}, and are not important to the text or how it's shown.
     * Since they are inner classes, they keep a reference to their parent, like a {@link TextView}.
     * And that references a context, which is usually an activity.</p>
     *
     * <p>If the text was garbage collected, then this would still not be a problem, but often it's not.
     * The reason is that some of these lines are cached by the TextLine class, including all the spans.
     * So to give the text view and the activity a chance to be garbage collected, then these spans must be
     * removed.</p>
     *
     * @param textView The text view with the text to remove references from.
     */
    public void removeReferencesToTextViewFromText(final TextView textView) {
        final Spannable text = (Spannable) textView.getText();
        final NoCopySpan[] noCopySpans = text.getSpans(0, text.length(), NoCopySpan.class);

        for (final NoCopySpan noCopySpan : noCopySpans) {
            text.removeSpan(noCopySpan);
        }
    }
}
