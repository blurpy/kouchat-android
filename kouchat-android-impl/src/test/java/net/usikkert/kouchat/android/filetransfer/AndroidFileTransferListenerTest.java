
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

package net.usikkert.kouchat.android.filetransfer;

import static org.mockito.Mockito.*;

import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link AndroidFileTransferListener}.
 *
 * @author Christian Ihle
 */
public class AndroidFileTransferListenerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AndroidFileTransferListener listener;

    @Before
    public void setUp() {
        listener = new AndroidFileTransferListener(mock(FileSender.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfFileReceiverIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("FileReceiver can not be null");

        new AndroidFileTransferListener((FileReceiver) null);
    }

    @Test
    public void constructorShouldThrowExceptionIfFileSenderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("FileSender can not be null");

        new AndroidFileTransferListener((FileSender) null);
    }

    @Test
    public void statusWaitingShouldDoNothing() {
        listener.statusWaiting();
    }

    @Test
    public void statusConnectingShouldDoNothing() {
        listener.statusConnecting();
    }

    @Test
    public void statusTransferringShouldDoNothing() {
        listener.statusTransferring();
    }

    @Test
    public void statusCompletedShouldDoNothing() {
        listener.statusCompleted();
    }

    @Test
    public void statusFailedShouldDoNothing() {
        listener.statusFailed();
    }

    @Test
    public void transferUpdateShouldDoNothing() {
        listener.transferUpdate();
    }
}
