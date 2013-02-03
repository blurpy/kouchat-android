
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

package net.usikkert.kouchat.net;

import static org.mockito.Mockito.*;

import java.io.File;

import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.misc.User;

import org.junit.Test;

/**
 * Test of {@link Messages}.
 *
 * @author Christian Ihle
 */
public class MessagesTest {

    /** The settings. */
    private final Settings settings;

    /** The application user. */
    private final User me;

    /** The message class tested here. */
    private final Messages messages;

    /** Mocked network service used by messages. */
    private final NetworkService service;

    /**
     * Constructor.
     */
    public MessagesTest() {
        settings = Settings.getSettings();
        me = settings.getMe();
        service = mock(NetworkService.class);
        when(service.sendMulticastMsg(anyString())).thenReturn(true);
        when(service.sendUDPMsg(anyString(), anyString(), anyInt())).thenReturn(true);
        messages = new Messages(service);
    }

    /**
     * Tests sendAwayMessage().
     *
     * Expects: 11515687!AWAY#Christian:I am away
     */
    @Test
    public void testSendAwayMessage() {
        final String awayMsg = "I am away";
        messages.sendAwayMessage(awayMsg);
        verify(service).sendMulticastMsg(createMessage("AWAY") + awayMsg);
    }

    /**
     * Tests sendBackMessage().
     *
     * Expects: 12485102!BACK#Christian:
     */
    @Test
    public void testSendBackMessage() {
        messages.sendBackMessage();
        verify(service).sendMulticastMsg(createMessage("BACK"));
    }

    /**
     * Tests sendChatMessage().
     *
     * Expects: 16899115!MSG#Christian:[-15987646]Some chat message
     *
     * @throws CommandException In case the message could not be sent.
     */
    @Test
    public void testSendChatMessage() throws CommandException {
        final String msg = "Some chat message";
        messages.sendChatMessage(msg);
        verify(service).sendMulticastMsg(createMessage("MSG") + "[" + settings.getOwnColor() + "]" + msg);
    }

    /**
     * Tests sendClient().
     *
     * Expects: 13132531!CLIENT#Christian:(KouChat v0.9.9-dev null)[134]{Linux}<0>
     */
    @Test
    public void testSendClientMessage() {
        final String startsWith = "(" + me.getClient() + ")[";
        final String middle = ".+\\)\\[\\d+\\]\\{.+"; // like:)[134[{
        final String endsWidth = "]{" + me.getOperatingSystem() + "}<" + me.getPrivateChatPort() + ">";

        messages.sendClient();

        verify(service).sendMulticastMsg(startsWith(createMessage("CLIENT") + startsWith));
        verify(service).sendMulticastMsg(matches(middle));
        verify(service).sendMulticastMsg(endsWith(endsWidth));
    }

    /**
     * Tests sendExposeMessage().
     *
     * Expects: 16424378!EXPOSE#Christian:
     */
    @Test
    public void testSendExposeMessage() {
        messages.sendExposeMessage();
        verify(service).sendMulticastMsg(createMessage("EXPOSE"));
    }

    /**
     * Tests sendExposingMessage().
     *
     * Expects: 17871777!EXPOSING#Christian:
     */
    @Test
    public void testSendExposingMessage() {
        messages.sendExposingMessage();
        verify(service).sendMulticastMsg(createMessage("EXPOSING"));
    }

    /**
     * Tests sendFile().
     *
     * Expects: 14394329!SENDFILE#Christian:(1234)[80800]{37563645}a_file.txt
     *
     * @throws CommandException In case the message could not be sent.
     */
    @Test
    public void testSendFileMessage() throws CommandException {
        final int userCode = 1234;
        final long fileLength = 80800L;
        final String fileName = "a_file.txt";

        final File file = mock(File.class);
        when(file.getName()).thenReturn(fileName);
        when(file.length()).thenReturn(fileLength);
        final int fileHash = file.hashCode(); // Cannot be mocked it seems

        final String info = "(" + userCode + ")" +
                "[" + fileLength + "]" +
                "{" + fileHash + "}" +
                fileName;

        final User user = new User("TestUser", userCode);

        messages.sendFile(user, file);
        verify(service).sendMulticastMsg(createMessage("SENDFILE") + info);
    }

    /**
     * Tests sendFileAbort().
     *
     * Expects: 15234876!SENDFILEABORT#Christian:(4321){8578765}another_file.txt
     */
    @Test
    public void testSendFileAbortMessage() {
        final int userCode = 4321;
        final int fileHash = 8578765;
        final String fileName = "another_file.txt";

        final String info = "(" + userCode + ")" +
                "{" + fileHash + "}" +
                fileName;

        final User user = new User("TestUser", userCode);

        messages.sendFileAbort(user, fileHash, fileName);
        verify(service).sendMulticastMsg(createMessage("SENDFILEABORT") + info);
    }

    /**
     * Tests sendFileAccept().
     *
     * Expects: 17247198!SENDFILEACCEPT#Christian:(4321)[20103]{8578765}some_file.txt
     *
     * @throws CommandException In case the message could not be sent.
     */
    @Test
    public void testSendFileAcceptMessage() throws CommandException {
        final int userCode = 4321;
        final int port = 20103;
        final int fileHash = 8578765;
        final String fileName = "some_file.txt";

        final String info = "(" + userCode + ")" +
                "[" + port + "]" +
                "{" + fileHash + "}" +
                fileName;

        final User user = new User("TestUser", userCode);

        messages.sendFileAccept(user, port, fileHash, fileName);
        verify(service).sendMulticastMsg(createMessage("SENDFILEACCEPT") + info);
    }

    /**
     * Tests sendGetTopicMessage().
     *
     * Expects: 19909338!GETTOPIC#Christian:
     */
    @Test
    public void testSendGetTopicMessage() {
        messages.sendGetTopicMessage();
        verify(service).sendMulticastMsg(createMessage("GETTOPIC"));
    }

    /**
     * Tests sendIdleMessage().
     *
     * Expects: 10223997!IDLE#Christian:
     */
    @Test
    public void testSendIdleMessage() {
        messages.sendIdleMessage();
        verify(service).sendMulticastMsg(createMessage("IDLE"));
    }

    /**
     * Tests sendLogoffMessage().
     *
     * Expects: 18265486!LOGOFF#Christian:
     */
    @Test
    public void testSendLogoffMessage() {
        messages.sendLogoffMessage();
        verify(service).sendMulticastMsg(createMessage("LOGOFF"));
    }

    /**
     * Tests sendLogonMessage().
     *
     * Expects: 10794786!LOGON#Christian:
     */
    @Test
    public void testSendLogonMessage() {
        messages.sendLogonMessage();
        verify(service).sendMulticastMsg(createMessage("LOGON"));
    }

    /**
     * Tests sendNickCrashMessage().
     *
     * Expects: 16321536!NICKCRASH#Christian:niles
     */
    @Test
    public void testSendNickCrashMessage() {
        final String nick = "niles";
        messages.sendNickCrashMessage(nick);
        verify(service).sendMulticastMsg(createMessage("NICKCRASH") + nick);
    }

    /**
     * Tests sendNickMessage().
     *
     * Expects: 14795611!NICK#Christian:
     */
    @Test
    public void testSendNickMessage() {
        final String newNick = "Cookie";
        messages.sendNickMessage(newNick);
        verify(service).sendMulticastMsg(createMessage("NICK", newNick));
    }

    /**
     * Tests sendPrivateMessage().
     *
     * Expects: 10897608!PRIVMSG#Christian:(435435)[-15987646]this is a private message
     *
     * @throws CommandException In case the message could not be sent.
     */
    @Test
    public void testSendPrivateMessage() throws CommandException {
        final String privmsg = "this is a private message";
        final String userIP = "192.168.5.155";
        final int userPort = 12345;
        final int userCode = 435435;

        final String message = "(" + userCode + ")" +
                "[" + settings.getOwnColor() + "]" +
                privmsg;

        final User user = new User("TestUser", userCode);
        user.setPrivateChatPort(userPort);
        user.setIpAddress(userIP);

        messages.sendPrivateMessage(privmsg, user);
        verify(service).sendUDPMsg(createMessage("PRIVMSG") + message, userIP, userPort);
    }

    /**
     * Tests sendStoppedWritingMessage().
     *
     * Expects: 15140738!STOPPEDWRITING#Christian:
     */
    @Test
    public void testSendStoppedWritingMessage() {
        messages.sendStoppedWritingMessage();
        verify(service).sendMulticastMsg(createMessage("STOPPEDWRITING"));
    }

    /**
     * Tests sendTopicChangeMessage().
     *
     * Expects: 18102542!TOPIC#Christian:(Snoopy)[2132321323]Interesting changed topic
     */
    @Test
    public void testSendTopicChangeMessage() {
        final Topic topic = new Topic("Interesting changed topic", "Snoopy", 2132321323L);
        final String message = "(" + topic.getNick() + ")" +
                "[" + topic.getTime() + "]" +
                topic.getTopic();

        messages.sendTopicChangeMessage(topic);
        verify(service).sendMulticastMsg(createMessage("TOPIC") + message);
    }

    /**
     * Tests sendTopicRequestedMessage().
     *
     * Expects: 18102542!TOPIC#Christian:(Snoopy)[66532345]Interesting requested topic
     */
    @Test
    public void testSendTopicRequestedMessage() {
        final Topic topic = new Topic("Interesting requested topic", "Snoopy", 66532345L);
        final String message = "(" + topic.getNick() + ")" +
                "[" + topic.getTime() + "]" +
                topic.getTopic();

        messages.sendTopicRequestedMessage(topic);
        verify(service).sendMulticastMsg(createMessage("TOPIC") + message);
    }

    /**
     * Tests sendWritingMessage().
     *
     * Expects: 19610068!WRITING#Christian:
     */
    @Test
    public void testSendWritingMessage() {
        messages.sendWritingMessage();
        verify(service).sendMulticastMsg(createMessage("WRITING"));
    }

    /**
     * Creates the standard part for most of the message types.
     *
     * @param type The message type.
     * @return A message.
     */
    private String createMessage(final String type) {
        return me.getCode() + "!" + type + "#" + me.getNick() + ":";
    }

    /**
     * Creates the standard part for most of the message types.
     *
     * @param type The message type.
     * @param nick Nick name to use in the message instead of the default.
     * @return A message.
     */
    private String createMessage(final String type, final String nick) {
        return me.getCode() + "!" + type + "#" + nick + ":";
    }
}
