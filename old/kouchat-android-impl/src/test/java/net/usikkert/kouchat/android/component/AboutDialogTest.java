
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

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.junit.ExpectedException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowContextThemeWrapper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Test of {@link AboutDialog}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class AboutDialogTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AlertDialog dialog;
    private ShadowAlertDialog shadowDialog;

    @Before
    public void setUp() {
        new AboutDialog(Robolectric.application); // Dialog would be shown after this

        dialog = ShadowAlertDialog.getLatestAlertDialog();
        shadowDialog = Robolectric.shadowOf(dialog);
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new AboutDialog(null);
    }

    @Test
    public void dialogTitleShouldBeApplicationNameAndVersion() {
        assertEquals("KouChat v" + Constants.APP_VERSION, shadowDialog.getTitle());
    }

    @Test
    public void dialogIconShouldBeSet() {
        assertEquals(R.drawable.ic_dialog, shadowDialog.getShadowAlertController().getIconId());
    }

    @Test
    public void dialogThemeShouldBeSet() {
        final ContextThemeWrapper context = (ContextThemeWrapper) dialog.getContext();
        final ContextThemeWrapper baseContext = (ContextThemeWrapper) context.getBaseContext();
        final ShadowContextThemeWrapper shadowBaseContext = (ShadowContextThemeWrapper) Robolectric.shadowOf(baseContext);
        final int themeResId = shadowBaseContext.callGetThemeResId();

        assertEquals(R.style.Theme_Default_Dialog, themeResId);
    }

    @Test
    public void dialogShouldBeCancelable() {
        assertTrue(shadowDialog.isCancelable());
    }

    @Test
    public void dialogShouldShowPositiveButtonThatClosesDialog() {
        final Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);

        assertNotNull(positiveButton);
        assertEquals("OK", positiveButton.getText());
        assertEquals(View.VISIBLE, positiveButton.getVisibility());

        assertTrue(dialog.isShowing());

        positiveButton.performClick();

        assertFalse(dialog.isShowing());
    }

    @Test
    public void dialogShouldNotShowNegativeButton() {
        final Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);

        assertNotNull(negativeButton);
        assertEquals("", negativeButton.getText());
        assertEquals(View.GONE, negativeButton.getVisibility());
    }

    @Test
    public void dialogShouldHaveMessage() {
        final TextView messageView = (TextView) shadowDialog.getView();

        assertTrue(messageView.getText().toString().contains("Copyright 2006-20"));
    }

    @Test
    @Config(qualifiers = "sw600dp")
    public void dialogShouldHaveMessageOfTheCorrectSize() {
        final TextView messageView = (TextView) shadowDialog.getView();

        assertEquals(15, messageView.getTextSize(), 0);
    }

    @Test
    public void dialogShouldHaveUrlAndMailToLinks() {
        final TextView messageView = (TextView) shadowDialog.getView();
        final SpannableString message = (SpannableString) messageView.getText();

        final URLSpan[] urls = message.getSpans(0, message.length(), URLSpan.class);
        assertNotNull(urls);
        assertEquals(3, urls.length);

        assertEquals("mailto:contact@kouchat.net", urls[0].getURL());
        assertEquals("http://www.kouchat.net", urls[1].getURL());
        assertEquals("http://www.gnu.org/licenses/lgpl-3.0.txt", urls[2].getURL());
    }
}
