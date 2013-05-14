
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

import android.text.SpannableString;
import android.text.style.URLSpan;
import android.widget.TextView;
import net.usikkert.kouchat.Constants;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test of {@link AboutDialog}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class AboutDialogTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ShadowAlertDialog shadowDialog;

    @Before
    public void setUp() {
        new AboutDialog(Robolectric.application.getApplicationContext()); // Dialog would be shown after this

        shadowDialog = Robolectric.shadowOf_(ShadowAlertDialog.getLatestAlertDialog());
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new AboutDialog(null);
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void dialogTitleShouldBeApplicationNameAndVersion() {
        assertEquals("KouChat v" + Constants.APP_VERSION, shadowDialog.getTitle()); // Returns v1.0, which is wrong
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void dialogIconShouldBeSet() {
//        assertEquals(R.drawable.kou_icon_32x32, shadowDialog.getIcon()); // Does not compile
    }

    @Test
    public void dialogShouldBeCancelable() {
        assertTrue(shadowDialog.isCancelable());
    }

    //@Test
    //public void dialogShouldHaveOKButton() {
    //    final Button button = shadowDialog.getButton(AlertDialog.BUTTON_POSITIVE);
    //    assertEquals("OK", button.getText());
    //}

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void dialogShouldHaveMessage() {
        final TextView messageView = (TextView) shadowDialog.getView();

        // Returns "android.text.SpannableString@7f49b327", as the actual toString
        assertEquals("Copyright 2006-2013 by Christian Ihle\n" +
                "kontakt@usikkert.net\n" +
                "http://kouchat.googlecode.com\n\n" +
                "Source available under the GNU LGPLv3:\n" +
                "http://www.gnu.org/licenses/lgpl-3.0.txt", messageView.getText().toString());

    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void dialogShouldHaveUrlAndMailToLinks() {
        final TextView messageView = (TextView) shadowDialog.getView();
        final SpannableString message = (SpannableString) messageView.getText();

        final URLSpan[] urls = message.getSpans(0, message.length(), URLSpan.class);
        assertNotNull(urls); // Fails here
        assertEquals(3, urls.length);

        assertEquals("mailto:kontakt@usikkert.net", urls[0].getURL());
        assertEquals("http://kouchat.googlecode.com/", urls[1].getURL());
        assertEquals("http://www.gnu.org/licenses/lgpl-3.0.txt", urls[2].getURL());
    }
}
