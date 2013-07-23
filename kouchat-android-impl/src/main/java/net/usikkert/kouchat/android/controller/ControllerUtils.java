
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

package net.usikkert.kouchat.android.controller;

import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Reusable functionality for controllers.
 *
 * @author Christian Ihle
 */
public final class ControllerUtils {

    private ControllerUtils() {
        // Only static methods
    }

    /** Number of milliseconds in a second. */
    public static final int ONE_SECOND = 1000;

    /**
     * Scrolls to the last line of text in a text view.
     *
     * @param textView The text view to scroll.
     * @param scrollView The surrounding scroll view.
     */
    public static void scrollTextViewToBottom(final TextView textView, final ScrollView scrollView) {
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
    public static void makeLinksClickable(final TextView textView) {
        textView.setMovementMethod(LinkMovementMethodWithSelectSupport.getInstance());
    }
}
