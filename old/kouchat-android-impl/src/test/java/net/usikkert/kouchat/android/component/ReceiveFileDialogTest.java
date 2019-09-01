
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

import java.util.Locale;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowContextThemeWrapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Test of {@link ReceiveFileDialog}
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class ReceiveFileDialogTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ReceiveFileDialog receiveFileDialog;

    private FileReceiver fileReceiver;
    private Activity activity;

    @Before
    public void setUp() {
        Locale.setDefault(Locale.US); // To avoid issues with "." and "," in asserts containing file sizes

        fileReceiver = mock(FileReceiver.class);
        when(fileReceiver.getUser()).thenReturn(new User("Ferdinand", 12345));
        when(fileReceiver.getFileName()).thenReturn("superkou.png");
        when(fileReceiver.getFileSize()).thenReturn(165000L);

        activity = Robolectric.buildActivity(Activity.class).create().get();
        receiveFileDialog = new ReceiveFileDialog();
    }

    @Test
    public void showReceiveFileDialogShouldThrowExceptionIfActivityIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Activity can not be null");

        receiveFileDialog.showReceiveFileDialog(null, mock(FileReceiver.class));
    }

    @Test
    public void showReceiveFileDialogShouldThrowExceptionIfFileReceiverIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("FileReceiver can not be null");

        receiveFileDialog.showReceiveFileDialog(mock(Activity.class), null);
    }

    @Test
    public void showReceiveFileDialogShouldShowTheDialog() {
        assertNull(getDialog());

        receiveFileDialog.showReceiveFileDialog(activity, fileReceiver);

        assertNotNull(getDialog());
    }

    @Test
    public void showReceiveFileDialogShouldSetTitle() {
        receiveFileDialog.showReceiveFileDialog(activity, fileReceiver);

        final ShadowAlertDialog shadowDialog = getShadowDialog();
        assertEquals("File transfer request", shadowDialog.getTitle());
    }

    @Test
    public void showReceiveFileDialogShouldSetIcon() {
        receiveFileDialog.showReceiveFileDialog(activity, fileReceiver);

        final ShadowAlertDialog shadowDialog = getShadowDialog();
        assertEquals(R.drawable.ic_dialog, shadowDialog.getShadowAlertController().getIconId());
    }

    @Test
    public void showReceiveFileDialogShouldSetTheme() {
        receiveFileDialog.showReceiveFileDialog(activity, fileReceiver);

        final AlertDialog dialog = getDialog();
        final ContextThemeWrapper context = (ContextThemeWrapper) dialog.getContext();
        final ContextThemeWrapper baseContext = (ContextThemeWrapper) context.getBaseContext();
        final ShadowContextThemeWrapper shadowBaseContext = (ShadowContextThemeWrapper) Robolectric.shadowOf(baseContext);
        final int themeResId = shadowBaseContext.callGetThemeResId();

        assertEquals(R.style.Theme_Default_Dialog, themeResId);
    }

    @Test
    public void showReceiveFileDialogShouldSetMessage() {
        receiveFileDialog.showReceiveFileDialog(activity, fileReceiver);

        final ShadowAlertDialog shadowDialog = getShadowDialog();
        assertEquals("Ferdinand is trying to send you the file ‘superkou.png’ (161.13KB)." +
                " Do you want to accept the file transfer?", shadowDialog.getMessage());
    }

    @Test
    @Config(qualifiers = "sw720dp")
    public void showReceiveFileDialogShouldHaveMessageOfTheCorrectSize() {
        receiveFileDialog.showReceiveFileDialog(activity, fileReceiver);

        final AlertDialog dialog = getDialog();
        final TextView messageView = (TextView) dialog.findViewById(android.R.id.message);

        assertEquals(16, messageView.getTextSize(), 0);
    }

    @Test
    public void showReceiveFileDialogShouldConfigurePositiveButtonToAcceptFileTransferAndCloseEverything() {
        receiveFileDialog.showReceiveFileDialog(activity, fileReceiver);

        reset(fileReceiver); // Don't care about verifying the message setup

        final AlertDialog dialog = getDialog();
        final Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);

        assertNotNull(positiveButton);
        assertEquals("Accept", positiveButton.getText());
        assertEquals(View.VISIBLE, positiveButton.getVisibility());

        verifyZeroInteractions(fileReceiver);
        assertFalse(activity.isFinishing());
        assertTrue(dialog.isShowing());

        positiveButton.performClick();

        verify(fileReceiver).accept();
        assertTrue(activity.isFinishing());
        assertFalse(dialog.isShowing());
    }

    @Test
    public void showReceiveFileDialogShouldConfigureNegativeButtonToRejectFileTransferAndCloseEverything() {
        receiveFileDialog.showReceiveFileDialog(activity, fileReceiver);

        reset(fileReceiver); // Don't care about verifying the message setup

        final AlertDialog dialog = getDialog();
        final Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);

        assertNotNull(negativeButton);
        assertEquals("Reject", negativeButton.getText());
        assertEquals(View.VISIBLE, negativeButton.getVisibility());

        verifyZeroInteractions(fileReceiver);
        assertFalse(activity.isFinishing());
        assertTrue(dialog.isShowing());

        negativeButton.performClick();

        verify(fileReceiver).reject();
        assertTrue(activity.isFinishing());
        assertFalse(dialog.isShowing());
    }

    @Test
    public void showReceiveFileDialogShouldNotShowNeutralButton() {
        receiveFileDialog.showReceiveFileDialog(activity, fileReceiver);

        final AlertDialog dialog = getDialog();
        final Button neutralButton = dialog.getButton(Dialog.BUTTON_NEUTRAL);

        assertNotNull(neutralButton);
        assertEquals("", neutralButton.getText());
        assertEquals(View.GONE, neutralButton.getVisibility());
    }

    @Test
    public void showReceiveFileDialogShouldConfigureCancelToJustCloseEverything() {
        receiveFileDialog.showReceiveFileDialog(activity, fileReceiver);

        reset(fileReceiver); // Don't care about verifying the message setup

        final AlertDialog dialog = getDialog();

        assertFalse(activity.isFinishing());
        assertTrue(dialog.isShowing());

        dialog.cancel();

        verifyZeroInteractions(fileReceiver);
        assertTrue(activity.isFinishing());
        assertFalse(dialog.isShowing());
    }

    @Test
    public void showMissingFileDialogShouldThrowExceptionIfActivityIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Activity can not be null");

        receiveFileDialog.showMissingFileDialog(null);
    }

    @Test
    public void showMissingFileDialogShouldShowTheDialog() {
        assertNull(getDialog());

        receiveFileDialog.showMissingFileDialog(activity);

        assertNotNull(getDialog());
    }

    @Test
    public void showMissingFileDialogShouldSetTitle() {
        receiveFileDialog.showMissingFileDialog(activity);

        final ShadowAlertDialog shadowDialog = getShadowDialog();
        assertEquals("File transfer request", shadowDialog.getTitle());
    }

    @Test
    public void showMissingFileDialogShouldSetIcon() {
        receiveFileDialog.showMissingFileDialog(activity);

        final ShadowAlertDialog shadowDialog = getShadowDialog();
        assertEquals(R.drawable.ic_dialog, shadowDialog.getShadowAlertController().getIconId());
    }

    @Test
    public void showMissingFileDialogShouldSetTheme() {
        receiveFileDialog.showMissingFileDialog(activity);

        final AlertDialog dialog = getDialog();
        final ContextThemeWrapper context = (ContextThemeWrapper) dialog.getContext();
        final ContextThemeWrapper baseContext = (ContextThemeWrapper) context.getBaseContext();
        final ShadowContextThemeWrapper shadowBaseContext = (ShadowContextThemeWrapper) Robolectric.shadowOf(baseContext);
        final int themeResId = shadowBaseContext.callGetThemeResId();

        assertEquals(R.style.Theme_Default_Dialog, themeResId);
    }

    @Test
    public void showMissingFileDialogShouldSetMessage() {
        receiveFileDialog.showMissingFileDialog(activity);

        final ShadowAlertDialog shadowDialog = getShadowDialog();
        assertEquals("Unable to find the specified file transfer request.", shadowDialog.getMessage());
    }

    @Test
    public void showMissingFileDialogShouldHaveMessageOfTheCorrectSize() {
        receiveFileDialog.showMissingFileDialog(activity);

        final AlertDialog dialog = getDialog();
        final TextView messageView = (TextView) dialog.findViewById(android.R.id.message);

        assertEquals(14, messageView.getTextSize(), 0);
    }

    @Test
    public void showMissingFileDialogShouldConfigurePositiveButtonToCloseEverything() {
        receiveFileDialog.showMissingFileDialog(activity);

        final AlertDialog dialog = getDialog();
        final Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);

        assertNotNull(positiveButton);
        assertEquals("OK", positiveButton.getText());
        assertEquals(View.VISIBLE, positiveButton.getVisibility());

        assertFalse(activity.isFinishing());
        assertTrue(dialog.isShowing());

        positiveButton.performClick();

        assertTrue(activity.isFinishing());
        assertFalse(dialog.isShowing());
    }

    @Test
    public void showMissingFileDialogShouldNotShowNegativeButton() {
        receiveFileDialog.showMissingFileDialog(activity);

        final AlertDialog dialog = getDialog();
        final Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);

        assertNotNull(negativeButton);
        assertEquals("", negativeButton.getText());
        assertEquals(View.GONE, negativeButton.getVisibility());
    }

    @Test
    public void showMissingFileDialogShouldNotShowNeutralButton() {
        receiveFileDialog.showMissingFileDialog(activity);

        final AlertDialog dialog = getDialog();
        final Button neutralButton = dialog.getButton(Dialog.BUTTON_NEUTRAL);

        assertNotNull(neutralButton);
        assertEquals("", neutralButton.getText());
        assertEquals(View.GONE, neutralButton.getVisibility());
    }

    @Test
    public void showMissingFileDialogShouldConfigureCancelToCloseEverything() {
        receiveFileDialog.showMissingFileDialog(activity);

        final AlertDialog dialog = getDialog();

        assertFalse(activity.isFinishing());
        assertTrue(dialog.isShowing());

        dialog.cancel();

        assertTrue(activity.isFinishing());
        assertFalse(dialog.isShowing());
    }

    private AlertDialog getDialog() {
        return ShadowAlertDialog.getLatestAlertDialog();
    }

    private ShadowAlertDialog getShadowDialog() {
        final AlertDialog dialog = getDialog();
        return Robolectric.shadowOf(dialog);
    }
}
