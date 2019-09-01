
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
import static org.mockito.Mockito.*;

import java.util.List;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowContextThemeWrapper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Test of {@link GoAwayDialog}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class GoAwayDialogTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ShadowAlertDialog shadowDialog;
    private AlertDialog dialog;

    private TextView dialogMessage;
    private EditText dialogInput;

    private AndroidUserInterface ui;

    @Before
    public void setUp() {
        ui = mock(AndroidUserInterface.class);

        new GoAwayDialog(Robolectric.application, ui); // Dialog would be shown after this

        dialog = ShadowAlertDialog.getLatestAlertDialog();
        shadowDialog = Robolectric.shadowOf(dialog);

        final View dialogView = shadowDialog.getView();
        dialogMessage = (TextView) dialogView.findViewById(R.id.goAwayDialogMessage);
        dialogInput = (EditText) dialogView.findViewById(R.id.goAwayDialogInput);
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new GoAwayDialog(null, ui);
    }

    @Test
    public void constructorShouldThrowExceptionIfAndroidUserInterfaceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("AndroidUserInterface can not be null");

        new GoAwayDialog(Robolectric.application, null);
    }

    @Test
    public void dialogTitleShouldBeSet() {
        assertEquals("Away", shadowDialog.getTitle());
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
    public void dialogMessageShouldBeSet() {
        assertEquals("Go away?", dialogMessage.getText());
    }

    @Test
    public void dialogInputShouldSetTextHint() {
        assertEquals("Enter away message", dialogInput.getHint());
    }

    @Test
    public void dialogInputShouldHaveNoNewLineTextWatcher() {
        final List<TextWatcher> textWatchers = getTextWatchers(dialogInput);

        assertEquals(1, textWatchers.size());
        assertEquals(NoNewLineTextWatcher.class, textWatchers.get(0).getClass());
    }

    @Test
    public void dialogShouldHaveCancelButtonThatDoesNothingButClose() {
        final Button button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        assertEquals("Cancel", button.getText());

        button.performClick();

        verify(ui, never()).goAway(anyString());
        assertFalse(dialog.isShowing());
    }

    @Test
    public void dialogShouldHaveOKButtonThatSetsAwayAndCloses() {
        dialogInput.setText("Going away");

        final Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        assertEquals("OK", button.getText());

        button.performClick();

        verify(ui).goAway("Going away");
        assertFalse(dialog.isShowing());
    }

    @Test
    public void dialogWindowShouldSetSoftKeyboardVisible() {
        final WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();

        assertEquals(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE, attributes.softInputMode);
    }

    @SuppressWarnings("unchecked")
    private List<TextWatcher> getTextWatchers(final EditText editText) {
        return TestUtils.getFieldValue(editText, List.class, "mListeners");
    }
}
