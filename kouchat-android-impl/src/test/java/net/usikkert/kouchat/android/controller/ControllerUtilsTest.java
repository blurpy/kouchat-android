
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.component.LinkMovementMethodWithSelectSupport;
import net.usikkert.kouchat.android.util.RunRunnableAnswer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Test of {@link ControllerUtils}.
 *
 * @author Christian Ihle
 */
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
}
