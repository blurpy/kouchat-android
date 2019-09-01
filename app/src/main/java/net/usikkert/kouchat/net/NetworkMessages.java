
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

package net.usikkert.kouchat.net;

import static net.usikkert.kouchat.net.NetworkMessageType.*;

import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.Validate;

/**
 * This class gives access to sending the different kinds of network messages
 * that this application supports. Both multicast, and normal udp.
 *
 * @author Christian Ihle
 */
public class NetworkMessages {

    /** The network service used for sending the actual messages. */
    private final NetworkService networkService;

    /** The application user. */
    private final User me;

    /** Settings. */
    private final Settings settings;

    /**
     * Constructor.
     *
     * @param networkService The network service used for sending the actual messages.
     * @param settings The settings to use.
     */
    public NetworkMessages(final NetworkService networkService, final Settings settings) {
        Validate.notNull(networkService, "Network service can not be null");
        Validate.notNull(settings, "Settings can not be null");

        this.networkService = networkService;
        this.settings = settings;
        me = settings.getMe();
    }

    /**
     * Sends a message notifying other clients that this client is still alive.
     *
     * <p>Note: the network will be checked if this fails!</p>
     */
    public void sendIdleMessage() {
        final String msg = createMessage(IDLE);
        final boolean sent = networkService.sendMessageToAllUsers(msg);

        if (!sent) {
            checkNetwork();
        }
    }

    /**
     * Sends a message to change the topic.
     *
     * <p>Note: the network will be checked if this fails!</p>
     *
     * @param topic The new topic to send.
     */
    public void sendTopicChangeMessage(final Topic topic) {
        final String msg = createTopicMessage(topic);
        final boolean sent = networkService.sendMessageToAllUsers(msg);

        if (!sent) {
            checkNetwork();
        }
    }

    /**
     * Sends a message with the current topic.
     *
     * @param topic The current topic to send.
     */
    public void sendTopicRequestedMessage(final Topic topic) {
        final String msg = createTopicMessage(topic);
        networkService.sendMessageToAllUsers(msg);
    }

    /**
     * Sends a message to set the user as away, including the away message.
     *
     * <p>Note: the network will be checked if this fails!</p>
     *
     * @param awayMsg The away message to set.
     */
    public void sendAwayMessage(final String awayMsg) {
        final String msg = createMessage(AWAY) + awayMsg;
        final boolean sent = networkService.sendMessageToAllUsers(msg);

        if (!sent) {
            checkNetwork();
        }
    }

    /**
     * Sends a message to set the user as back from away.
     *
     * <p>Note: the network will be checked if this fails!</p>
     */
    public void sendBackMessage() {
        final String msg = createMessage(BACK);
        final boolean sent = networkService.sendMessageToAllUsers(msg);

        if (!sent) {
            checkNetwork();
        }
    }

    /**
     * Sends a normal chat message, that is part of the main chat.
     *
     * <p>Note: the network will be checked, and the user notified if this fails!</p>
     *
     * @param chatMsg The message for the main chat.
     * @throws CommandException If the message was not sent successfully.
     */
    public void sendChatMessage(final String chatMsg) throws CommandException {
        final String msg = createMessage(MSG) +
                "[" + settings.getOwnColor() + "]" +
                chatMsg;

        final boolean sent = networkService.sendMessageToAllUsers(msg);

        if (!sent) {
            checkNetwork();
            notifyUser("Failed to send message: " + chatMsg);
        }
    }

    /**
     * Sends a message to log this client on the network.
     */
    public void sendLogonMessage() {
        final String msg = createMessage(LOGON);
        networkService.sendMessageToAllUsers(msg);
    }

    /**
     * Sends a message to log this client off the network.
     */
    public void sendLogoffMessage() {
        final String msg = createMessage(LOGOFF);
        networkService.sendMessageToAllUsers(msg);
    }

    /**
     * Sends a message asking the other clients to identify themselves.
     */
    public void sendExposeMessage() {
        final String msg = createMessage(EXPOSE);
        networkService.sendMessageToAllUsers(msg);
    }

    /**
     * Sends a message to identify this client.
     */
    public void sendExposingMessage() {
        final String msg = createMessage(EXPOSING) + me.getAwayMsg();
        networkService.sendMessageToAllUsers(msg);
    }

    /**
     * Sends a message to ask for the current topic.
     */
    public void sendGetTopicMessage() {
        final String msg = createMessage(GETTOPIC);
        networkService.sendMessageToAllUsers(msg);
    }

    /**
     * Sends a message to notify that the user is writing.
     */
    public void sendWritingMessage() {
        final String msg = createMessage(WRITING);
        networkService.sendMessageToAllUsers(msg);
    }

    /**
     * Sends a message to notify that the user has stopped writing.
     */
    public void sendStoppedWritingMessage() {
        final String msg = createMessage(STOPPEDWRITING);
        networkService.sendMessageToAllUsers(msg);
    }

    /**
     * Sends a message to change the nick name of the user.
     *
     * <p>Note: the network will be checked if this fails!</p>
     *
     * @param newNick The new nick to send.
     */
    public void sendNickMessage(final String newNick) {
        final String msg = createMessage(NICK, newNick);
        final boolean sent = networkService.sendMessageToAllUsers(msg);

        if (!sent) {
            checkNetwork();
        }
    }

    /**
     * Sends a message to inform that another user has logged on with
     * the same nick name as this user.
     *
     * @param crashNick The nick name that is already in use by the user.
     */
    public void sendNickCrashMessage(final String crashNick) {
        final String msg = createMessage(NICKCRASH) + crashNick;
        networkService.sendMessageToAllUsers(msg);
    }

    /**
     * Sends a message to notify the file sender that you aborted the file transfer.
     *
     * @param user The user sending a file.
     * @param fileHash The unique hash code of the file.
     * @param fileName The name of the file.
     */
    public void sendFileAbort(final User user, final int fileHash, final String fileName) {
        final String msg = createMessage(SENDFILEABORT) +
                "(" + user.getCode() + ")" +
                "{" + fileHash + "}" +
                fileName;

        final boolean sent = networkService.sendMessageToAllUsers(msg);

        if (!sent) {
            checkNetwork();
        }
    }

    /**
     * Sends a message to notify the file sender that you
     * accepted the file transfer.
     *
     * <p>Note: the network will be checked, and the user notified if this fails!</p>
     *
     * @param user The user sending a file.
     * @param port The port the file sender can connect to on this client
     *             to start the file transfer.
     * @param fileHash The unique hash code of the file.
     * @param fileName The name of the file.
     * @throws CommandException If the message was not sent successfully.
     */
    public void sendFileAccept(final User user, final int port,
            final int fileHash, final String fileName) throws CommandException {
        final String msg = createMessage(SENDFILEACCEPT) +
                "(" + user.getCode() + ")" +
                "[" + port + "]" +
                "{" + fileHash + "}" +
                fileName;

        final boolean sent = networkService.sendMessageToAllUsers(msg);

        if (!sent) {
            checkNetwork();
            notifyUser("Failed to accept file transfer from " + user.getNick() + ": " + fileName);
        }
    }

    /**
     * Sends a message to notify another user that you want to send a file.
     *
     * <p>Note: the network will be checked, and the user notified if this fails!</p>
     *
     * @param user The user asked to receive a file.
     * @param file The file to send.
     * @throws CommandException If the message was not sent successfully.
     */
    public void sendFile(final User user, final FileToSend file) throws CommandException {
        final String msg = createMessage(SENDFILE) +
                "(" + user.getCode() + ")" +
                "[" + file.length() + "]" +
                "{" + file.hashCode() + "}" +
                file.getName();

        final boolean sent = networkService.sendMessageToAllUsers(msg);

        if (!sent) {
            checkNetwork();
            notifyUser("Failed to send file to " + user.getNick() + ": " + file.getName());
        }
    }

    /**
     * Sends a message with extra client information:
     *
     * <ul>
     *   <li>Name of the client.</li>
     *   <li>Client uptime.</li>
     *   <li>Operating system.</li>
     *   <li>Port to connect to for private chat.</li>
     *   <li>Port to connect to for tcp chat.</li>
     * </ul>
     */
    public void sendClient() {
        final String msg = createMessage(CLIENT) +
                "(" + me.getClient() + ")" +
                "[" + (System.currentTimeMillis() - me.getLogonTime()) + "]" +
                "{" + me.getOperatingSystem() + "}" +
                "<" + me.getPrivateChatPort() + ">" +
                "/" + me.getTcpChatPort() + "\\";

        networkService.sendMessageToAllUsers(msg);
    }

    /**
     * Sends a private message to a user.
     *
     * <p>Note: the network will be checked, and the user notified if this fails!</p>
     *
     * @param privMsg The private message to send.
     * @param user The user to send the message to.
     * @throws CommandException If the message was not sent successfully.
     */
    public void sendPrivateMessage(final String privMsg, final User user) throws CommandException {
        final String msg = createMessage(PRIVMSG) +
                "(" + user.getCode() + ")" +
                "[" + settings.getOwnColor() + "]" +
                privMsg;

        final boolean sent = networkService.sendMessageToUser(msg, user);

        if (!sent) {
            checkNetwork();
            notifyUser("Failed to send private message to " + user.getNick() + ": " + privMsg);
        }
    }

    /**
     * Creates the standard part of all messages, with the specified type
     * as the message type.
     *
     * @param type The message type.
     * @return The standard part of the message.
     */
    private String createMessage(final String type) {
        return createMessage(type, me.getNick());
    }

    /**
     * Creates the standard part of all messages, with the specified type
     * as the message type, and a specified nick name.
     *
     * @param type The message type.
     * @param nick The nick name to use in the message.
     * @return The standard part of the message.
     */
    private String createMessage(final String type, final String nick) {
        return me.getCode() + "!" + type + "#" + nick + ":";
    }

    /**
     * Creates a new message for sending the topic.
     *
     * @param topic The topic to use in the message.
     * @return The new message.
     */
    private String createTopicMessage(final Topic topic) {
        return createMessage(TOPIC) +
                "(" + topic.getNick() + ")" +
                "[" + topic.getTime() + "]" +
                topic.getTopic();
    }

    /**
     * Informs the user that the message could not be delivered.
     *
     * @param infoMsg The message to give the user.
     * @throws CommandException The exception returned with the message.
     */
    private void notifyUser(final String infoMsg) throws CommandException {
        throw new CommandException(infoMsg);
    }

    /**
     * Asks the network service to check the network status.
     */
    private void checkNetwork() {
        networkService.checkNetwork();
    }
}
