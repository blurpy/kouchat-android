
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

package net.usikkert.kouchat.misc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;

import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.ui.UserInterface;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * Test of {@link CommandParser}.
 *
 * @author Christian Ihle
 */
public class CommandParserTest {

    private CommandParser parser;
    private MessageController messageController;
    private Controller controller;
    private TransferList transferList;
    private User me;

    @Before
    public void setUp() {
        controller = mock(Controller.class);

        transferList = mock(TransferList.class);
        when(controller.getTransferList()).thenReturn(transferList);

        final UserInterface userInterface = mock(UserInterface.class);

        messageController = mock(MessageController.class);
        when(userInterface.getMessageController()).thenReturn(messageController);

        me = new User("MySelf", 123);

        final Settings settings = mock(Settings.class);
        when(settings.getMe()).thenReturn(me);

        parser = new CommandParser(controller, userInterface, settings);
    }

    /*
     * reject
     */

    @Test
    public void rejectShouldReturnIfNoArguments() {
        parser.parse("/reject");

        verify(messageController).showSystemMessage("/reject - wrong number of arguments: <nick> <id>");
    }

    @Test
    public void rejectShouldReturnIfOneArgument() {
        parser.parse("/reject SomeOne");

        verify(messageController).showSystemMessage("/reject - wrong number of arguments: <nick> <id>");
    }

    @Test
    public void rejectShouldReturnIfThreeArguments() {
        parser.parse("/reject SomeOne some thing");

        verify(messageController).showSystemMessage("/reject - wrong number of arguments: <nick> <id>");
    }

    @Test
    public void rejectShouldReturnIfUserDoesntExist() {
        parser.parse("/reject NoUser 1");

        verify(messageController).showSystemMessage("/reject - no such user 'NoUser'");
    }

    @Test
    public void rejectShouldReturnIfUserIsMe() {
        when(controller.getUser("MySelf")).thenReturn(me);

        parser.parse("/reject MySelf 1");

        verify(messageController).showSystemMessage("/reject - no point in doing that!");
    }

    @Test
    public void rejectShouldReturnIfFileTransferIdIsNotAnInteger() {
        setupSomeOne();

        parser.parse("/reject SomeOne monkey");

        verify(messageController).showSystemMessage("/reject - invalid file id argument: 'monkey'");
    }

    @Test
    public void rejectShouldReturnIfFileTransferIdDoesntExist() {
        final User someOne = setupSomeOne();

        parser.parse("/reject SomeOne 1");

        verify(transferList).getFileReceiver(someOne, 1);
        verify(messageController).showSystemMessage("/reject - no file with id 1 offered by SomeOne");
    }

    @Test
    public void rejectShouldReturnIfFileTransferHasAlreadyBeingAccepted() {
        final User someOne = setupSomeOne();
        final FileReceiver fileReceiver = setupFileReceiver(someOne);
        when(fileReceiver.isAccepted()).thenReturn(true);

        parser.parse("/reject SomeOne 1");

        verify(transferList).getFileReceiver(someOne, 1);
        verify(messageController).showSystemMessage("/reject - already receiving 'doc.pdf' from SomeOne");
        verify(fileReceiver, never()).reject();
    }

    @Test
    public void rejectShouldRejectFileTransferIfArgumentsMatch() {
        final User someOne = setupSomeOne();
        final FileReceiver fileReceiver = setupFileReceiver(someOne);

        parser.parse("/reject SomeOne 1");

        verify(transferList).getFileReceiver(someOne, 1);
        verifyZeroInteractions(messageController);
        verify(fileReceiver).reject();
    }

    @Test
    public void rejectShouldRejectFileTransferIfArgumentsMatchEvenIfExtraSpaces() {
        final User someOne = setupSomeOne();
        final FileReceiver fileReceiver = setupFileReceiver(someOne);

        parser.parse("/reject SomeOne 1  ");

        verify(transferList).getFileReceiver(someOne, 1);
        verifyZeroInteractions(messageController);
        verify(fileReceiver).reject();
    }

    /*
     * receive
     */

    @Test
    public void receiveShouldReturnIfNoArguments() {
        parser.parse("/receive");

        verify(messageController).showSystemMessage("/receive - wrong number of arguments: <nick> <id>");
    }

    @Test
    public void receiveShouldReturnIfOneArgument() {
        parser.parse("/receive SomeOne");

        verify(messageController).showSystemMessage("/receive - wrong number of arguments: <nick> <id>");
    }

    @Test
    public void receiveShouldReturnIfThreeArguments() {
        parser.parse("/receive SomeOne some thing");

        verify(messageController).showSystemMessage("/receive - wrong number of arguments: <nick> <id>");
    }

    @Test
    public void receiveShouldReturnIfUserDoesntExist() {
        parser.parse("/receive NoUser 1");

        verify(messageController).showSystemMessage("/receive - no such user 'NoUser'");
    }

    @Test
    public void receiveShouldReturnIfUserIsMe() {
        when(controller.getUser("MySelf")).thenReturn(me);

        parser.parse("/receive MySelf 1");

        verify(messageController).showSystemMessage("/receive - no point in doing that!");
    }

    @Test
    public void receiveShouldReturnIfFileTransferIdIsNotAnInteger() {
        setupSomeOne();

        parser.parse("/receive SomeOne monkey");

        verify(messageController).showSystemMessage("/receive - invalid file id argument: 'monkey'");
    }

    @Test
    public void receiveShouldReturnIfFileTransferIdDoesntExist() {
        final User someOne = setupSomeOne();

        parser.parse("/receive SomeOne 1");

        verify(transferList).getFileReceiver(someOne, 1);
        verify(messageController).showSystemMessage("/receive - no file with id 1 offered by SomeOne");
    }

    @Test
    public void receiveShouldReturnIfFileTransferHasAlreadyBeingAccepted() {
        final User someOne = setupSomeOne();
        final FileReceiver fileReceiver = setupFileReceiver(someOne);
        when(fileReceiver.isAccepted()).thenReturn(true);

        parser.parse("/receive SomeOne 1");

        verify(transferList).getFileReceiver(someOne, 1);
        verify(messageController).showSystemMessage("/receive - already receiving 'doc.pdf' from SomeOne");
        verify(fileReceiver, never()).accept();
    }

    @Test
    public void receiveShouldAcceptFileTransferIfArgumentsMatch() {
        final User someOne = setupSomeOne();
        final FileReceiver fileReceiver = setupFileReceiver(someOne);
        setupFile(fileReceiver);

        parser.parse("/receive SomeOne 1");

        verify(transferList).getFileReceiver(someOne, 1);
        verifyZeroInteractions(messageController);
        verify(fileReceiver).accept();
    }

    @Test
    public void receiveShouldAcceptFileTransferIfArgumentsMatchEvenIfExtraSpaces() {
        final User someOne = setupSomeOne();
        final FileReceiver fileReceiver = setupFileReceiver(someOne);
        setupFile(fileReceiver);

        parser.parse("/receive SomeOne 1  ");

        verify(transferList).getFileReceiver(someOne, 1);
        verifyZeroInteractions(messageController);
        verify(fileReceiver).accept();
    }

    @Test
    public void receiveShouldAcceptFileTransferIfArgumentsMatchAndRenameExistingFile() {
        final User someOne = setupSomeOne();
        final FileReceiver fileReceiver = setupFileReceiver(someOne);
        final File file = setupFile(fileReceiver);
        when(file.exists()).thenReturn(true);

        final ArgumentCaptor<File> newFileCaptor = ArgumentCaptor.forClass(File.class);

        parser.parse("/receive SomeOne 1");

        verify(transferList).getFileReceiver(someOne, 1);
        verify(messageController).showSystemMessage("/receive - file 'doc.pdf' already exists - renaming to 'doc_1.pdf'");
        verify(fileReceiver).accept();

        verify(fileReceiver).setFile(newFileCaptor.capture());
        assertEquals("doc_1.pdf", newFileCaptor.getValue().getName());
    }

    /*
    * cancel
    */

    @Test
    public void cancelShouldReturnIfNoArguments() {
        parser.parse("/cancel");

        verify(messageController).showSystemMessage("/cancel - wrong number of arguments: <nick> <id>");
    }

    @Test
    public void cancelShouldReturnIfOneArgument() {
        parser.parse("/cancel SomeOne");

        verify(messageController).showSystemMessage("/cancel - wrong number of arguments: <nick> <id>");
    }

    @Test
    public void cancelShouldReturnIfThreeArguments() {
        parser.parse("/cancel SomeOne some thing");

        verify(messageController).showSystemMessage("/cancel - wrong number of arguments: <nick> <id>");
    }

    @Test
    public void cancelShouldReturnIfUserDoesntExist() {
        parser.parse("/cancel NoUser 1");

        verify(messageController).showSystemMessage("/cancel - no such user 'NoUser'");
    }

    @Test
    public void cancelShouldReturnIfUserIsMe() {
        when(controller.getUser("MySelf")).thenReturn(me);

        parser.parse("/cancel MySelf 1");

        verify(messageController).showSystemMessage("/cancel - no point in doing that!");
    }

    @Test
    public void cancelShouldReturnIfFileTransferIdIsNotAnInteger() {
        setupSomeOne();

        parser.parse("/cancel SomeOne monkey");

        verify(messageController).showSystemMessage("/cancel - invalid file id argument: 'monkey'");
    }

    @Test
    public void cancelShouldReturnIfFileTransferIdDoesntExist() {
        final User someOne = setupSomeOne();

        parser.parse("/cancel SomeOne 1");

        verify(transferList).getFileTransfer(someOne, 1);
        verify(messageController).showSystemMessage("/cancel - no file transfer with id 1 going on with SomeOne");
    }

    @Test
    public void cancelShouldReturnIfFileReceiverIsNotAccepted() {
        final User someOne = setupSomeOne();
        final FileReceiver fileReceiver = setupFileReceiver(someOne);

        parser.parse("/cancel SomeOne 1");

        verify(transferList).getFileTransfer(someOne, 1);
        verify(messageController).showSystemMessage("/cancel - transfer of 'doc.pdf' from SomeOne has not started yet");
        verify(fileReceiver, never()).cancel();
    }

    @Test
    public void cancelShouldCancelIfFileReceiverIsAccepted() {
        final User someOne = setupSomeOne();
        final FileReceiver fileReceiver = setupFileReceiver(someOne);
        when(fileReceiver.isAccepted()).thenReturn(true);

        parser.parse("/cancel SomeOne 1");

        verify(transferList).getFileTransfer(someOne, 1);
        verifyZeroInteractions(messageController);
        verify(fileReceiver).cancel();
    }

    @Test
    public void cancelShouldCancelIfFileSenderAndArgumentsMatch() {
        final User someOne = setupSomeOne();
        final FileSender fileSender = setupFileSender(someOne);

        parser.parse("/cancel SomeOne 1");

        verify(transferList).getFileTransfer(someOne, 1);
        verifyZeroInteractions(messageController);
        verify(fileSender).cancel();
    }

    @Test
    public void cancelShouldCancelAndNotifyRecipientIfFileSendIsWaiting() {
        final User someOne = setupSomeOne();
        final FileSender fileSender = setupFileSender(someOne);
        when(fileSender.isWaiting()).thenReturn(true);
        final File file = setupFile(fileSender);
        when(fileSender.getUser()).thenReturn(someOne);

        parser.parse("/cancel SomeOne 1");

        verify(transferList).getFileTransfer(someOne, 1);
        verify(transferList).removeFileSender(fileSender);
        verify(messageController).showSystemMessage("You cancelled sending of doc.pdf to SomeOne");
        verify(controller).sendFileAbort(someOne, file.hashCode(), "doc.pdf");
        verify(fileSender).cancel();
    }

    /*
     * Rusable test methods.
     */

    private File setupFile(final FileTransfer fileTransfer) {
        final File file = mock(File.class);
        when(file.getName()).thenReturn("doc.pdf");

        when(fileTransfer.getFile()).thenReturn(file);

        return file;
    }

    private FileReceiver setupFileReceiver(final User user) {
        final FileReceiver fileReceiver = mock(FileReceiver.class);

        when(transferList.getFileReceiver(user, 1)).thenReturn(fileReceiver);
        when(transferList.getFileTransfer(user, 1)).thenReturn(fileReceiver);

        when(fileReceiver.getFileName()).thenReturn("doc.pdf");

        return fileReceiver;
    }

    private FileSender setupFileSender(final User user) {
        final FileSender fileSender = mock(FileSender.class);

        when(transferList.getFileTransfer(user, 1)).thenReturn(fileSender);

        return fileSender;
    }

    private User setupSomeOne() {
        final User someOne = new User("SomeOne", 12345678);
        when(controller.getUser("SomeOne")).thenReturn(someOne);

        return someOne;
    }
}
