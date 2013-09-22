
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

import java.io.File;

import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import android.content.Context;

/**
 * Test of {@link AndroidFileTransferListener}.
 *
 * @author Christian Ihle
 */
public class AndroidFileTransferListenerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private FileSender fileSender;
    private FileReceiver fileReceiver;
    private Context context;
    private AndroidFileUtils androidFileUtils;

    private AndroidFileTransferListener fileSenderListener;
    private AndroidFileTransferListener fileReceiverListener;

    @Before
    public void setUp() {
        fileSender = mock(FileSender.class);
        context = mock(Context.class);
        androidFileUtils = mock(AndroidFileUtils.class);
        fileReceiver = mock(FileReceiver.class);

        fileSenderListener = new AndroidFileTransferListener(fileSender);
        fileReceiverListener = new AndroidFileTransferListener(fileReceiver, context, androidFileUtils);
    }

    @Test
    public void constructorWithFileReceiverShouldThrowExceptionIfFileReceiverIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("FileReceiver can not be null");

        new AndroidFileTransferListener(null, context, androidFileUtils);
    }

    @Test
    public void constructorWithFileReceiverShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new AndroidFileTransferListener(fileReceiver, null, androidFileUtils);
    }

    @Test
    public void constructorWithFileReceiverShouldThrowExceptionIfAndroidFileUtilsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("AndroidFileUtils can not be null");

        new AndroidFileTransferListener(fileReceiver, context, null);
    }

    @Test
    public void constructorWithFileReceiverShouldRegisterListener() {
        verify(fileReceiver).registerListener(fileReceiverListener);
    }

    @Test
    public void constructorWithFileSenderShouldThrowExceptionIfFileSenderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("FileSender can not be null");

        new AndroidFileTransferListener(null);
    }

    @Test
    public void constructorWithFileSenderShouldRegisterListener() {
        verify(fileSender).registerListener(fileSenderListener);
    }

    @Test
    public void statusWaitingShouldDoNothing() {
        fileSenderListener.statusWaiting();
    }

    @Test
    public void statusConnectingShouldDoNothing() {
        fileSenderListener.statusConnecting();
    }

    @Test
    public void statusTransferringShouldDoNothing() {
        fileSenderListener.statusTransferring();
    }

    @Test
    public void statusCompletedForFileSenderShouldDoNothing() {
        fileSenderListener.statusCompleted();
    }

    @Test
    public void statusCompletedForFileReceiverShouldAddMediaToDatabase() {
        final File file = mock(File.class);
        when(fileReceiver.getFile()).thenReturn(file);

        fileReceiverListener.statusCompleted();

        verify(androidFileUtils).addFileToMediaDatabase(context, file);
    }

    @Test
    public void statusFailedShouldDoNothing() {
        fileSenderListener.statusFailed();
    }

    @Test
    public void transferUpdateShouldDoNothing() {
        fileSenderListener.transferUpdate();
    }
}
