
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

package net.usikkert.kouchat.android.component;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.net.FileReceiver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

import android.app.Activity;

/**
 * Test of {@link ReceiveFileDialog}
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class ReceiveFileDialogTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ShadowAlertDialog shadowDialog;

    @Before
    public void setUp() {
        new ReceiveFileDialog(new Activity(), mock(FileReceiver.class)); // Dialog would be shown after this

        shadowDialog = Robolectric.shadowOf(ShadowAlertDialog.getLatestAlertDialog());
    }

    @Test
    public void constructorShouldThrowExceptionIfActivityIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Activity can not be null");

        new ReceiveFileDialog(null, mock(FileReceiver.class));
    }

    @Test
    public void dialogTitleShouldBeSet() {
        assertEquals("File transfer request", shadowDialog.getTitle());
    }
}
