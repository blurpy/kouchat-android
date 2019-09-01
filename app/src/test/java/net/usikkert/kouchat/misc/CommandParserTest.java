
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

package net.usikkert.kouchat.misc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.message.CoreMessages;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.FileToSend;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.DateTools;
import net.usikkert.kouchat.util.TestUtils;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test of {@link CommandParser}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class CommandParserTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CommandParser parser;

    private MessageController messageController;
    private Controller controller;
    private TransferList transferList;
    private UserList userList;
    private UserInterface userInterface;
    private Settings settings;
    private CoreMessages coreMessages;
    private DateTools dateTools;

    private User me;
    private Topic topic;

    @Before
    public void setUp() {
        Locale.setDefault(Locale.US); // To avoid issues with "," and "." in numbers

        controller = mock(Controller.class);

        transferList = mock(TransferList.class);
        when(controller.getTransferList()).thenReturn(transferList);

        userList = new SortedUserList();
        when(controller.getUserList()).thenReturn(userList);

        topic = new Topic();
        when(controller.getTopic()).thenReturn(topic);

        userInterface = mock(UserInterface.class);

        messageController = mock(MessageController.class);
        when(userInterface.getMessageController()).thenReturn(messageController);

        settings = new Settings();
        me = settings.getMe();
        me.setNick("MySelf");
        userList.add(me);

        coreMessages = new CoreMessages();

        parser = spy(new CommandParser(controller, userInterface, settings, coreMessages));

        dateTools = TestUtils.setFieldValueWithMock(parser, "dateTools", DateTools.class);

        // From constructor
        verify(controller).getTransferList();
        verify(userInterface).getMessageController();
    }

    @Test
    public void constructorShouldThrowExceptionIfControllerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Controller can not be null");

        new CommandParser(null, userInterface, settings, coreMessages);
    }

    @Test
    public void constructorShouldThrowExceptionIfUserInterfaceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("UserInterface can not be null");

        new CommandParser(controller, null, settings, coreMessages);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new CommandParser(controller, userInterface, null, coreMessages);
    }

    @Test
    public void constructorShouldThrowExceptionIfCoreMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Core messages can not be null");

        new CommandParser(controller, userInterface, settings, null);
    }

    /*
     * /reject
     */

    @Test
    public void rejectShouldReturnIfNoArguments() {
        parser.parse("/reject");

        verify(messageController).showSystemMessage("/reject - missing arguments <nick> <id>");
    }

    @Test
    public void rejectShouldReturnIfOneArgument() {
        parser.parse("/reject SomeOne");

        verify(messageController).showSystemMessage("/reject - missing arguments <nick> <id>");
    }

    @Test
    public void rejectShouldReturnIfThreeArguments() {
        parser.parse("/reject SomeOne some thing");

        verify(messageController).showSystemMessage("/reject - missing arguments <nick> <id>");
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
     * /receive
     */

    @Test
    public void receiveShouldReturnIfNoArguments() {
        parser.parse("/receive");

        verify(messageController).showSystemMessage("/receive - missing arguments <nick> <id>");
    }

    @Test
    public void receiveShouldReturnIfOneArgument() {
        parser.parse("/receive SomeOne");

        verify(messageController).showSystemMessage("/receive - missing arguments <nick> <id>");
    }

    @Test
    public void receiveShouldReturnIfThreeArguments() {
        parser.parse("/receive SomeOne some thing");

        verify(messageController).showSystemMessage("/receive - missing arguments <nick> <id>");
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
    * /cancel
    */

    @Test
    public void cancelShouldReturnIfNoArguments() {
        parser.parse("/cancel");

        verify(messageController).showSystemMessage("/cancel - missing arguments <nick> <id>");
    }

    @Test
    public void cancelShouldReturnIfOneArgument() {
        parser.parse("/cancel SomeOne");

        verify(messageController).showSystemMessage("/cancel - missing arguments <nick> <id>");
    }

    @Test
    public void cancelShouldReturnIfThreeArguments() {
        parser.parse("/cancel SomeOne some thing");

        verify(messageController).showSystemMessage("/cancel - missing arguments <nick> <id>");
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
        final FileToSend file = setupFile(fileSender);
        when(fileSender.getUser()).thenReturn(someOne);

        parser.parse("/cancel SomeOne 1");

        verify(transferList).getFileTransfer(someOne, 1);
        verify(transferList).removeFileSender(fileSender);
        verify(messageController).showSystemMessage("You cancelled sending of doc.pdf to SomeOne");
        verify(controller).sendFileAbort(someOne, file.hashCode(), "doc.pdf");
        verify(fileSender).cancel();
    }

   /*
    * /away
    */

    @Test
    public void awayShouldReturnIfNoArguments() {
        parser.parse("/away");

        verify(messageController).showSystemMessage("/away - missing argument <away message>");
        verifyZeroInteractions(controller);
    }

    @Test
    public void awayShouldReturnIfAlreadyAway() {
        me.setAway(true);
        me.setAwayMsg("Gone with the wind");

        parser.parse("/away again");

        verify(messageController).showSystemMessage("/away - you are already away: 'Gone with the wind'");
        verifyZeroInteractions(controller);
    }

    @Test
    public void awayShouldSetAsAway() throws CommandException {
        parser.parse("/away Out shopping");

        verify(controller).goAway("Out shopping");
        verifyZeroInteractions(messageController);
    }

    @Test
    public void awayShouldShowSystemMessageIfChangeFails() throws CommandException {
        doThrow(new CommandException("Don't go away")).when(controller).goAway(anyString());

        parser.parse("/away Leaving for good");

        verify(controller).goAway("Leaving for good");
        verify(messageController).showSystemMessage("Don't go away");
    }

    /*
     * /back
     */

    @Test
    public void backShouldReturnIfNotAway() {
        parser.parse("/back");

        verify(messageController).showSystemMessage("/back - you are not away");
        verifyZeroInteractions(controller);
    }

    @Test
    public void backShouldSetAsBack() throws CommandException {
        me.setAway(true);
        me.setAwayMsg("Just away");

        parser.parse("/back");

        verify(controller).comeBack();
        verifyZeroInteractions(messageController);
    }

    @Test
    public void backShouldShowSystemMessageIfChangeFails() throws CommandException {
        doThrow(new CommandException("Don't come back")).when(controller).comeBack();

        me.setAway(true);
        me.setAwayMsg("Just away");

        parser.parse("/back");

        verify(controller).comeBack();
        verify(messageController).showSystemMessage("Don't come back");
    }

   /*
    * /topic
    */

    @Test
    public void topicShouldShowNoTopicSystemMessageIfNoArgumentsAndNoTopicSet() throws CommandException {
        assertFalse(topic.hasTopic());

        parser.parse("/topic");

        verify(messageController).showSystemMessage("No topic set");
        verify(parser, never()).fixTopic(anyString());
    }

    @Test
    public void topicShouldShowCurrentTopicSystemMessageIfNoArgumentsAndTopicSet() throws CommandException {
        final long date = new DateTime().withDate(2010, 3, 4).withTime(20, 45, 13, 0).getMillis();
        topic.changeTopic(new Topic("What a nice day", "Niles", date));
        when(dateTools.dateToString(any(Date.class), anyString())).thenCallRealMethod();

        parser.parse("/topic");

        verify(messageController).showSystemMessage(
                "Topic is: What a nice day (set by Niles at 20:45:13, 04. Mar. 10)");
        verify(parser, never()).fixTopic(anyString());
    }

    @Test
    public void topicShouldCallFixTopicWhenArgumentsAreSpecified() throws CommandException {
        doNothing().when(parser).fixTopic(anyString());

        parser.parse("/topic hello");

        verify(parser).fixTopic(" hello");
        verifyZeroInteractions(messageController);
    }

    @Test
    public void topicShouldShowErrorInSystemMessageOnCommandException() throws CommandException {
        doThrow(new CommandException("Don't set the topic!")).when(controller).changeTopic(anyString());

        parser.parse("/topic hello");

        verify(parser).fixTopic(" hello");
        verify(messageController).showSystemMessage("Don't set the topic!");
    }

    @Test
    public void fixTopicShouldDoNothingIfTopicIsUnchanged() throws CommandException {
        topic.changeTopic(new Topic("Hey there", "Peter", 12345678));

        parser.fixTopic("Hey there");

        verify(controller).getTopic();
        verifyNoMoreInteractions(controller);
        verifyZeroInteractions(messageController, userInterface);
    }

    @Test
    public void fixTopicShouldDoNothingIfTopicIsUnchangedIncludingTrim() throws CommandException {
        topic.changeTopic(new Topic("Hey there     ", "Peter", 12345678));

        parser.fixTopic("     Hey there");

        verify(controller).getTopic();
        verifyNoMoreInteractions(controller);
        verifyZeroInteractions(messageController, userInterface);
    }

    @Test
    public void fixTopicShouldChangeAndUpdateAndNotifyRemovedTopic() throws CommandException {
        topic.changeTopic(new Topic("Topic", "Peter", 12345678));

        parser.fixTopic(" ");

        verify(controller).changeTopic("");
        verify(messageController).showSystemMessage("You removed the topic");
        verify(userInterface).showTopic();
    }

    @Test
    public void fixTopicShouldChangeAndUpdateAndNotifyNewTopic() throws CommandException {
        topic.changeTopic(new Topic("Topic", "Peter", 12345678));

        parser.fixTopic(" new topic"); // Includes whitespace from "/topic new topic"

        verify(controller).changeTopic("new topic");
        verify(messageController).showSystemMessage("You changed the topic to: new topic");
        verify(userInterface).showTopic();
    }

    @Test
    public void fixTopicShouldChangeAndUpdateAndNotifyNewTopicIfTopicIsNotSet() throws CommandException {
        assertFalse(topic.hasTopic());

        parser.fixTopic(" new topic"); // Includes whitespace from "/topic new topic"

        verify(controller).changeTopic("new topic");
        verify(messageController).showSystemMessage("You changed the topic to: new topic");
        verify(userInterface).showTopic();
    }

    /*
     * /clear
     */

    @Test
    public void clearShouldUseUserInterface() {
        parser.parse("/clear");

        verify(userInterface).clearChat();
    }

    /*
     * /about
     */

    @Test
    public void aboutShouldShowVersionAndContactDetails() {
        parser.parse("/about");

        verify(messageController).showSystemMessage(
                "This is KouChat v" + Constants.APP_VERSION +
                        ", by Christian Ihle - contact@kouchat.net - https://www.kouchat.net/");
    }

    /*
     * /help
     */

    @Test
    public void helpShouldShowInfoAboutAllCommands() {
        parser.parse("/help");

        verify(parser).showCommands();
        verify(messageController).showSystemMessage(
                "KouChat commands:\n" +
                        "/about - information about KouChat\n" +
                        "/away <away message> - set status to away\n" +
                        "/back - set status to not away\n" +
                        "/cancel <nick> <id> - cancel an ongoing file transfer with a user\n" +
                        "/clear - clear all the text from the chat\n" +
                        "/help - show this help message\n" +
                        "/msg <nick> <msg> - send a private message to a user\n" +
                        "/nick <new nick> - changes your nick name\n" +
                        "/quit - quit from the chat\n" +
                        "/receive <nick> <id> - accept a file transfer request from a user\n" +
                        "/reject <nick> <id> - reject a file transfer request from a user\n" +
                        "/send <nick> <file> - send a file to a user\n" +
                        "/topic <optional new topic> - prints the current topic, or changes the topic\n" +
                        "/transfers - shows a list of all file transfers and their status\n" +
                        "/users - show the user list\n" +
                        "/whois <nick> - show information about a user\n" +
                        "//<text> - send the text as a normal message, with a single slash");
    }

    /*
     * /whois
     */

    @Test
    public void whoisShouldReturnIfNoArguments() {
        parser.parse("/whois");

        verify(messageController).showSystemMessage("/whois - missing argument <nick>");
        verifyNoMoreInteractions(messageController);
    }

    @Test
    public void whoisShouldReturnIfUserNotFound() {
        parser.parse("/whois none");

        verify(messageController).showSystemMessage("/whois - no such user 'none'");
        verifyNoMoreInteractions(messageController);
    }

    @Test
    public void whoisShouldShowUserDetailsForOnlineUser() {
        final User penny = new User("penny", 123);
        penny.setClient("TestClient");
        penny.setOperatingSystem("Red Hat");
        penny.setIpAddress("10.0.0.80");
        penny.setLogonTime(56789);

        when(controller.getUser("penny")).thenReturn(penny);
        when(dateTools.howLongFromNow(anyLong())).thenReturn("Too long");

        parser.parse("/whois penny");

        verify(messageController).showSystemMessage("/whois - penny:\n" +
                                                            "IP address: 10.0.0.80\n" +
                                                            "Client: TestClient\n" +
                                                            "Operating System: Red Hat\n" +
                                                            "Online: Too long");
        verifyNoMoreInteractions(messageController);
        verify(dateTools).howLongFromNow(56789);
        verify(controller).getUser("penny");
    }

    @Test
    public void whoisShouldShowUserDetailsForUserWithHostName() {
        final User amy = new User("Amy", 123);
        amy.setClient("JUnit");
        amy.setOperatingSystem("DOS");
        amy.setIpAddress("10.0.0.81");
        amy.setHostName("amy.com");

        when(controller.getUser("amy")).thenReturn(amy);
        when(dateTools.howLongFromNow(anyLong())).thenReturn("A year");

        parser.parse("/whois amy");

        verify(messageController).showSystemMessage("/whois - Amy:\n" +
                                                            "IP address: 10.0.0.81\n" +
                                                            "Host name: amy.com\n" +
                                                            "Client: JUnit\n" +
                                                            "Operating System: DOS\n" +
                                                            "Online: A year");
    }

    @Test
    public void whoisShouldShowUserDetailsForUserWithTCP() {
        final User amy = new User("Amy", 123);
        amy.setClient("JUnit");
        amy.setOperatingSystem("DOS");
        amy.setIpAddress("10.0.0.81");
        amy.setHostName("amy.com");
        amy.setTcpEnabled(true);

        when(controller.getUser("amy")).thenReturn(amy);
        when(dateTools.howLongFromNow(anyLong())).thenReturn("A year");

        parser.parse("/whois amy");

        verify(messageController).showSystemMessage("/whois - Amy:\n" +
                                                            "IP address: 10.0.0.81\n" +
                                                            "Host name: amy.com\n" +
                                                            "Client: JUnit TCP\n" +
                                                            "Operating System: DOS\n" +
                                                            "Online: A year");
    }

    @Test
    public void whoisShouldShowUserDetailsForUserWhoIsAway() {
        final User amy = new User("Amy", 123);
        amy.setClient("JUnit");
        amy.setOperatingSystem("DOS");
        amy.setIpAddress("10.0.0.81");
        amy.setAway(true);
        amy.setAwayMsg("Gone home");

        when(controller.getUser("amy")).thenReturn(amy);
        when(dateTools.howLongFromNow(anyLong())).thenReturn("A year");

        parser.parse("/whois amy");

        verify(messageController).showSystemMessage("/whois - Amy (Away):\n" +
                                                            "IP address: 10.0.0.81\n" +
                                                            "Client: JUnit\n" +
                                                            "Operating System: DOS\n" +
                                                            "Online: A year\n" +
                                                            "Away message: Gone home");
    }

    /*
     * /send
     */

    @Test
    public void sendShouldReturnIfNoArguments() throws CommandException {
        parser.parse("/send");

        verify(messageController).showSystemMessage("/send - missing arguments <nick> <file>");
        verify(parser, never()).sendFile(any(User.class), any(FileToSend.class));
    }

    @Test
    public void sendShouldReturnIfOneArgument() throws CommandException {
        parser.parse("/send niles");

        verify(messageController).showSystemMessage("/send - missing arguments <nick> <file>");
        verify(parser, never()).sendFile(any(User.class), any(FileToSend.class));
    }

    @Test
    public void sendShouldReturnIfUserIsMe() throws CommandException {
        when(controller.getUser("MySelf")).thenReturn(me);

        parser.parse("/send MySelf image.png");

        verify(messageController).showSystemMessage("/send - no point in doing that!");
        verify(parser, never()).sendFile(any(User.class), any(FileToSend.class));
    }

    @Test
    public void sendShouldReturnIfUserIsNotFound() throws CommandException {
        parser.parse("/send NoOne image.png");

        verify(messageController).showSystemMessage("/send - no such user 'NoOne'");
        verify(parser, never()).sendFile(any(User.class), any(FileToSend.class));
    }

    @Test
    public void sendShouldReturnIfFileIsNotFound() throws CommandException {
        setupSomeOne();

        final File noneExistingFile = new File("image.png");
        assertFalse(noneExistingFile.exists());

        parser.parse("/send SomeOne image.png");

        verify(messageController).showSystemMessage("/send - no such file 'image.png'");
        verify(parser, never()).sendFile(any(User.class), any(FileToSend.class));
    }

    @Test
    public void sendShouldReturnIfFileIsDirectory() throws CommandException {
        setupSomeOne();

        final File directory = new File("target");
        assertTrue(directory.exists());
        assertFalse(directory.isFile());

        parser.parse("/send SomeOne target");

        verify(messageController).showSystemMessage("/send - no such file 'target'");
        verify(parser, never()).sendFile(any(User.class), any(FileToSend.class));
    }

    @Test
    public void sendShouldSendFileIfFileIsValid() throws CommandException {
        final User someOne = setupSomeOne();
        doNothing().when(parser).sendFile(any(User.class), any(FileToSend.class));

        final File file = new File("src/test/resources/test-messages.properties");
        assertTrue(file.exists());
        assertTrue(file.isFile());

        parser.parse("/send SomeOne src/test/resources/test-messages.properties");

        verify(messageController, never()).showSystemMessage(anyString());
        verify(parser).sendFile(someOne, new FileToSend(file));
    }

    @Test
    public void sendShouldSendFileWithSpaceInName() throws CommandException {
        final User someOne = setupSomeOne();
        doNothing().when(parser).sendFile(any(User.class), any(FileToSend.class));

        final File file = new File("src/test/resources/with some space.txt");
        assertTrue(file.exists());
        assertTrue(file.isFile());

        parser.parse("/send SomeOne src/test/resources/with some space.txt ");

        verify(messageController, never()).showSystemMessage(anyString());
        verify(parser).sendFile(someOne, new FileToSend(file));
    }

    @Test
    public void sendShouldShowSystemMessageIfSendFileFails() throws CommandException {
        final User someOne = setupSomeOne();
        doThrow(new CommandException("Stop that file")).when(parser).sendFile(any(User.class), any(FileToSend.class));

        final File file = new File("src/test/resources/test-messages.properties");
        assertTrue(file.exists());
        assertTrue(file.isFile());

        parser.parse("/send SomeOne src/test/resources/test-messages.properties");

        verify(messageController).showSystemMessage("Stop that file");
        verify(parser).sendFile(someOne, new FileToSend(file));
    }

    @Test
    public void sendFileShouldSendUsingControllerAndAddToTransferListAndShowFileTransfer() throws CommandException {
        final User user = new User("User", 123456);
        final FileToSend file = new FileToSend(new File(""));
        final FileSender fileSender = mock(FileSender.class);

        when(transferList.addFileSender(any(User.class), any(FileToSend.class))).thenReturn(fileSender);

        parser.sendFile(user, file);

        verify(controller).sendFile(user, file);
        verify(transferList).addFileSender(user, file);
        verify(userInterface).showTransfer(fileSender);
    }

    @Test
    public void sendFileShouldShowSystemMessage() throws CommandException {
        final User user = new User("Kelly", 123456);
        final FileToSend file = mock(FileToSend.class);
        final FileSender fileSender = mock(FileSender.class);

        when(file.getName()).thenReturn("picture.png");
        when(file.length()).thenReturn(1024 * 1024 * 54L);
        when(fileSender.getId()).thenReturn(2);
        when(transferList.addFileSender(any(User.class), any(FileToSend.class))).thenReturn(fileSender);

        parser.sendFile(user, file);

        verify(messageController).showSystemMessage("Trying to send the file picture.png (#2) [54.00MB] to Kelly");
    }

    /*
     * /msg
     */

    @Test
    public void msgShouldReturnIfNoArguments() throws CommandException {
        parser.parse("/msg");

        verify(messageController).showSystemMessage("/msg - missing arguments <nick> <msg>");
        verify(controller, never()).sendPrivateMessage(anyString(), any(User.class));
    }

    @Test
    public void msgShouldReturnIfOneArgument() throws CommandException {
        parser.parse("/msg niles");

        verify(messageController).showSystemMessage("/msg - missing arguments <nick> <msg>");
        verify(controller, never()).sendPrivateMessage(anyString(), any(User.class));
    }

    @Test
    public void msgShouldReturnIfUserIsMe() throws CommandException {
        when(controller.getUser("MySelf")).thenReturn(me);

        parser.parse("/msg MySelf hello");

        verify(messageController).showSystemMessage("/msg - no point in doing that!");
        verify(controller, never()).sendPrivateMessage(anyString(), any(User.class));
    }

    @Test
    public void msgShouldReturnIfUserIsNotFound() throws CommandException {
        parser.parse("/msg NoOne hello");

        verify(messageController).showSystemMessage("/msg - no such user 'NoOne'");
        verify(controller, never()).sendPrivateMessage(anyString(), any(User.class));
    }

    @Test
    public void msgShouldReturnIfPrivateChatIsDisabled() throws CommandException {
        settings.setNoPrivateChat(true);
        setupSomeOne();

        parser.parse("/msg SomeOne hello");

        verify(messageController).showSystemMessage(
                "/msg - can't send private chat message when private chat is disabled");
        verify(controller, never()).sendPrivateMessage(anyString(), any(User.class));
    }

    @Test
    public void msgShouldReturnIfUserHasNoPrivateChatPort() throws CommandException {
        final User someOne = setupSomeOne();
        assertEquals(0, someOne.getPrivateChatPort());

        parser.parse("/msg SomeOne hello");

        verify(messageController).showSystemMessage("/msg - SomeOne can't receive private chat messages");
        verify(controller, never()).sendPrivateMessage(anyString(), any(User.class));
    }

    @Test
    public void msgShouldSendWholePrivateMessageToUser() throws CommandException {
        final User someOne = setupSomeOne();
        someOne.setPrivateChatPort(1000);

        parser.parse("/msg SomeOne hello there, how are you? ");

        verify(messageController).showPrivateOwnMessage(someOne, "hello there, how are you?");
        verify(controller).sendPrivateMessage("hello there, how are you?", someOne);
    }

    @Test
    public void msgShouldShowSystemMessageIfSendPrivateMessageFails() throws CommandException {
        final User someOne = setupSomeOne();
        someOne.setPrivateChatPort(1000);

        doThrow(new CommandException("No message")).when(controller).sendPrivateMessage(anyString(), any(User.class));

        parser.parse("/msg SomeOne hello");

        verify(controller).sendPrivateMessage("hello", someOne);
        verify(messageController).showSystemMessage("No message");
    }

    /*
     * /nick
     */

    @Test
    public void nickShouldReturnIfNoArguments() throws CommandException {
        parser.parse("/nick");

        verify(messageController).showSystemMessage("/nick - missing argument <nick>");
        verify(controller, never()).changeMyNick(anyString());
    }

    @Test
    public void nickShouldReturnIfNickIsUnchanged() throws CommandException {
        parser.parse("/nick MySelf");

        verify(messageController).showSystemMessage("/nick - you are already called 'MySelf'");
        verify(controller, never()).changeMyNick(anyString());
    }

    @Test
    public void nickShouldReturnIfNickIsInUse() throws CommandException {
        when(controller.isNickInUse("Other")).thenReturn(true);

        parser.parse("/nick Other");

        verify(messageController).showSystemMessage("/nick - 'Other' is in use by someone else");
        verify(controller, never()).changeMyNick(anyString());
    }

    @Test
    public void nickShouldReturnIfNickIsInvalid() throws CommandException {
        parser.parse("/nick @Boss");

        verify(messageController).showSystemMessage("/nick - '@Boss' is not a valid nick name. (1-10 letters)");
        verify(controller, never()).changeMyNick(anyString());
    }

    @Test
    public void nickShouldChangeNickNameAndShowSystemMessageAndUpdateUserInterface() throws CommandException {
        doAnswer(withSetNickNameOnMe()).when(controller).changeMyNick(anyString());

        parser.parse("/nick NewNick");

        verify(messageController).showSystemMessage("You changed nick to NewNick");
        verify(controller).changeMyNick("NewNick");
        verify(userInterface).showTopic();
    }

    @Test
    public void nickShouldShowSystemMessageIfNickChangeFails() throws CommandException {
        doThrow(new CommandException("No!")).when(controller).changeMyNick(anyString());

        parser.parse("/nick NewNick");

        verify(messageController).showSystemMessage("No!");
        verify(controller).changeMyNick("NewNick");
    }

    /*
     * /users
     */

    @Test
    public void usersShouldListMeIfOnlyMeConnected() {
        parser.parse("/users");

        verify(messageController).showSystemMessage("Users: MySelf");
    }

    @Test
    public void usersShouldListAllUserSeparatedWithComma() {
        userList.add(new User("Amy", 1));
        userList.add(new User("Peter", 2));
        userList.add(new User("Zelda", 3));

        parser.parse("/users");

        verify(messageController).showSystemMessage("Users: Amy, MySelf, Peter, Zelda");
    }

    /*
     * /transfers
     */

    @Test
    public void transfersShouldShowSystemMessageWhenNoActiveTransfers() {
        parser.parse("/transfers");

        verify(messageController).showSystemMessage("File transfers: no active file transfers");
    }

    @Test
    public void transfersShouldShowSystemMessageWithActiveSender() {
        final FileSender fileSender = createFileSender(5, "image.png", 500L, 12, 80L, "Amy");

        when(transferList.getFileSenders()).thenReturn(Arrays.asList(fileSender));

        parser.parse("/transfers");

        verify(messageController).showSystemMessage("File transfers:\n" +
                                                            "- Sending:\n" +
                                                            "  #5 image.png [500.00KB] (12%, 80.00KB/s) to Amy");
    }

    @Test
    public void transfersShouldShowSystemMessageWithMultipleActiveSenders() {
        final FileSender fileSender1 = createFileSender(1, "video.mp4", 15000L, 44, 56L, "Amy");
        final FileSender fileSender2 = createFileSender(2, "kou.png", 10L, 80, 5L, "Donald");
        final FileSender fileSender3 = createFileSender(4, "radiohead.mp3", 3500L, 25, 103L, "Dolly");

        when(transferList.getFileSenders()).thenReturn(Arrays.asList(fileSender1, fileSender2, fileSender3));

        parser.parse("/transfers");

        verify(messageController).showSystemMessage("File transfers:\n" +
                                                            "- Sending:\n" +
                                                            "  #1 video.mp4 [14.65MB] (44%, 56.00KB/s) to Amy\n" +
                                                            "  #2 kou.png [10.00KB] (80%, 5.00KB/s) to Donald\n" +
                                                            "  #4 radiohead.mp3 [3.42MB] (25%, 103.00KB/s) to Dolly");
    }

    @Test
    public void transfersShouldShowSystemMessageWithActiveReceiver() {
        final FileReceiver fileReceiver = createFileReceiver(5, "image.png", 500L, 12, 80L, "Amy");

        when(transferList.getFileReceivers()).thenReturn(Arrays.asList(fileReceiver));

        parser.parse("/transfers");

        verify(messageController).showSystemMessage("File transfers:\n" +
                                                            "- Receiving:\n" +
                                                            "  #5 image.png [500.00KB] (12%, 80.00KB/s) from Amy");
    }

    @Test
    public void transfersShouldShowSystemMessageWithMultipleActiveReceivers() {
        final FileReceiver fileReceiver1 = createFileReceiver(1, "video.mp4", 15000L, 44, 56L, "Amy");
        final FileReceiver fileReceiver2 = createFileReceiver(2, "kou.png", 10L, 80, 5L, "Donald");
        final FileReceiver fileReceiver3 = createFileReceiver(4, "radiohead.mp3", 3500L, 25, 103L, "Dolly");

        when(transferList.getFileReceivers()).thenReturn(Arrays.asList(fileReceiver1, fileReceiver2, fileReceiver3));

        parser.parse("/transfers");

        verify(messageController).showSystemMessage("File transfers:\n" +
                                                            "- Receiving:\n" +
                                                            "  #1 video.mp4 [14.65MB] (44%, 56.00KB/s) from Amy\n" +
                                                            "  #2 kou.png [10.00KB] (80%, 5.00KB/s) from Donald\n" +
                                                            "  #4 radiohead.mp3 [3.42MB] (25%, 103.00KB/s) from Dolly");
    }

    @Test
    public void transfersShouldShowSystemMessageWithBothActiveSenderAndReceiver() {
        final FileSender fileSender = createFileSender(1, "image1.png", 501L, 11, 81L, "Amber");
        when(transferList.getFileSenders()).thenReturn(Arrays.asList(fileSender));

        final FileReceiver fileReceiver = createFileReceiver(2, "image2.png", 502L, 12, 82L, "Donna");
        when(transferList.getFileReceivers()).thenReturn(Arrays.asList(fileReceiver));

        parser.parse("/transfers");

        verify(messageController).showSystemMessage("File transfers:\n" +
                                                            "- Sending:\n" +
                                                            "  #1 image1.png [501.00KB] (11%, 81.00KB/s) to Amber\n" +
                                                            "- Receiving:\n" +
                                                            "  #2 image2.png [502.00KB] (12%, 82.00KB/s) from Donna");
    }

    @Test
    public void transfersShouldShowSystemMessageWithMultipleActiveSendersAndReceivers() {
        final FileSender fileSender1 = createFileSender(1, "image1.png", 501L, 11, 81L, "Amber");
        final FileSender fileSender2 = createFileSender(3, "image3.png", 503L, 13, 83L, "Lilly");
        when(transferList.getFileSenders()).thenReturn(Arrays.asList(fileSender1, fileSender2));

        final FileReceiver fileReceiver1 = createFileReceiver(2, "image2.png", 502L, 12, 82L, "Donna");
        final FileReceiver fileReceiver2 = createFileReceiver(4, "image4.png", 504L, 14, 84L, "Kelly");
        when(transferList.getFileReceivers()).thenReturn(Arrays.asList(fileReceiver1, fileReceiver2));

        parser.parse("/transfers");

        verify(messageController).showSystemMessage("File transfers:\n" +
                                                            "- Sending:\n" +
                                                            "  #1 image1.png [501.00KB] (11%, 81.00KB/s) to Amber\n" +
                                                            "  #3 image3.png [503.00KB] (13%, 83.00KB/s) to Lilly\n" +
                                                            "- Receiving:\n" +
                                                            "  #2 image2.png [502.00KB] (12%, 82.00KB/s) from Donna\n" +
                                                            "  #4 image4.png [504.00KB] (14%, 84.00KB/s) from Kelly");
    }

    /*
     * /quit
     */

    @Test
    public void quitShouldQuit() {
        parser.parse("/quit");

        verify(userInterface).quit();
    }

    /*
     * /slash
     */

    @Test
    public void ekstraSlashShouldBeTreatedAsRegularMessage() throws CommandException {
        parser.parse("//msg niles hello");

        verify(controller).sendChatMessage("/msg niles hello");
        verify(messageController).showOwnMessage("/msg niles hello");
    }

    @Test
    public void ekstraSlashShouldShowSystemMessageIfChatMessageFails() throws CommandException {
        doThrow(new CommandException("No slash for you")).when(controller).sendChatMessage(anyString());

        parser.parse("//msg niles hello");

        verify(controller).sendChatMessage("/msg niles hello");
        verify(messageController).showSystemMessage("No slash for you");
    }

    /*
     * /unknown
     */

    @Test
    public void unknownCommandShouldShowSystemMessage() {
        parser.parse("/nope");

        verify(messageController).showSystemMessage("Unknown command 'nope'. Type /help for a list of commands");
    }

    @Test
    public void missingCommandShouldShowSystemMessage() {
        parser.parse("/");

        verify(messageController).showSystemMessage("Unknown command ''. Type /help for a list of commands");
    }

    /*
     * Reusable test methods.
     */

    private File setupFile(final FileReceiver fileReceiver) {
        final File file = mock(File.class);
        when(file.getName()).thenReturn("doc.pdf");

        when(fileReceiver.getFile()).thenReturn(file);

        return file;
    }

    private FileToSend setupFile(final FileSender fileSender) {
        final FileToSend file = mock(FileToSend.class);
        when(file.getName()).thenReturn("doc.pdf");

        when(fileSender.getFile()).thenReturn(file);

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

    private Answer<Void> withSetNickNameOnMe() {
        return new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                final String newNick = (String) invocation.getArguments()[0];
                me.setNick(newNick);

                return null;
            }
        };
    }

    private FileSender createFileSender(final int id, final String fileName, final long fileSize, final int percent,
                                        final long speed, final String nick) {
        final FileSender fileSender = spy(new FileSender(new User(nick, 1), createFileToSend(fileName, 1024 * fileSize), id));

        when(fileSender.getPercent()).thenReturn(percent);
        when(fileSender.getSpeed()).thenReturn(1024 * speed);

        return fileSender;
    }

    private FileReceiver createFileReceiver(final int id, final String fileName, final long fileSize, final int percent,
                                            final long speed, final String nick) {
        final FileReceiver fileReceiver =
                spy(new FileReceiver(new User(nick, 1), createFile(fileName, 0), 1024 * fileSize, id));

        when(fileReceiver.getPercent()).thenReturn(percent);
        when(fileReceiver.getSpeed()).thenReturn(1024 * speed);

        return fileReceiver;
    }

    private FileToSend createFileToSend(final String fileName, final long fileSize) {
        final File file = mock(File.class);

        when(file.getName()).thenReturn(fileName);
        when(file.length()).thenReturn(fileSize);

        return new FileToSend(file);
    }

    private File createFile(final String fileName, final long fileSize) {
        final File file = mock(File.class);

        when(file.getName()).thenReturn(fileName);
        when(file.length()).thenReturn(fileSize);

        return file;
    }
}
