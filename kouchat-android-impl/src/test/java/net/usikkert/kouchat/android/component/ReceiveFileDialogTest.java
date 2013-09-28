
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
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

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;

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

    private ShadowAlertDialog shadowDialog;
    private AlertDialog dialog;
    private FileReceiver fileReceiver;
    private Activity activity;

    @Before
    public void setUp() {
        fileReceiver = mock(FileReceiver.class);
        when(fileReceiver.getUser()).thenReturn(new User("Ferdinand", 12345));
        when(fileReceiver.getFileName()).thenReturn("superkou.png");
        when(fileReceiver.getFileSize()).thenReturn(165000L);

        activity = new Activity();

        receiveFileDialog = new ReceiveFileDialog();
        receiveFileDialog.showReceiveFileDialog(activity, fileReceiver); // Dialog should be shown after this

        dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(dialog);

        shadowDialog = Robolectric.shadowOf(dialog);

        reset(fileReceiver); // Don't care to verify the usage in the constructor of the dialog
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
    public void dialogTitleShouldBeSet() {
        assertEquals("File transfer request", shadowDialog.getTitle());
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void dialogIconShouldBeSet() {
//        assertEquals(R.drawable.ic_dialog, shadowDialog.getIcon()); // Does not compile
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void dialogThemeShouldBeSet() {
//        assertEquals(R.style.Theme_Default_Dialog, shadowDialog.getTheme()); // Does not compile
    }

    @Test
    public void dialogMessageShouldBeSet() {
        assertEquals("Ferdinand is trying to send you the file ‘superkou.png’ (161.13KB)." +
                " Do you want to accept the file transfer?", shadowDialog.getMessage());
    }

    @Test
    public void positiveButtonShouldAcceptFileTransferAndCloseEverything() {
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
    public void negativeButtonShouldRejectFileTransferAndCloseEverything() {
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
    public void neutralButtonShouldNotBeVisible() {
        final Button neutralButton = dialog.getButton(Dialog.BUTTON_NEUTRAL);

        assertNotNull(neutralButton);
        assertEquals("", neutralButton.getText());
        assertEquals(View.GONE, neutralButton.getVisibility());
    }

    @Test
    public void cancelShouldJustCloseEverything() {
        assertFalse(activity.isFinishing());
        assertTrue(dialog.isShowing());

        dialog.cancel();

        verifyZeroInteractions(fileReceiver);
        assertTrue(activity.isFinishing());
        assertFalse(dialog.isShowing());
    }
}
