
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

import net.usikkert.kouchat.misc.User;

/**
 * This is the interface for responders to multicast messages.
 *
 * <p>The responder gets the message after it has been parsed by the
 * {@link MessageParser}.</p>
 *
 * @author Christian Ihle
 */
public interface MessageResponder {

    /**
     * A chat message has arrived from a user.
     *
     * @param userCode The unique code of the user who sent the message.
     * @param msg The message.
     * @param color The color the message has.
     */
    void messageArrived(int userCode, String msg, int color);

    /**
     * A user has changed the topic.
     *
     * @param userCode The unique code of the user who changed the topic.
     * @param newTopic The new topic.
     * @param nick The nick name of the user who changed the topic.
     * @param time The time when the topic was set.
     */
    void topicChanged(int userCode, String newTopic, String nick, long time);

    /**
     * A user has requested the get the current topic.
     */
    void topicRequested();

    /**
     * The away status of a user has changed.
     *
     * @param userCode The unique code of the user who changed away status.
     * @param away If the user is away or not.
     * @param awayMsg The away message if the user is away, or an empty string.
     */
    void awayChanged(int userCode, boolean away, String awayMsg);

    /**
     * A user has changed it's nick name.
     *
     * @param userCode The unique code of the user who changed nick name.
     * @param newNick The new nick name.
     */
    void nickChanged(int userCode, String newNick);

    /**
     * A user sent a message that the application user's nick is already in use.
     */
    void nickCrash();

    /**
     * The application user has logged on to the chat.
     *
     * @param ipAddress The IP address of the application user.
     */
    void meLogOn(String ipAddress);

    /**
     * A new user has logged on to the chat.
     *
     * @param newUser The user logging on to the chat.
     */
    void userLogOn(User newUser);

    /**
     * A user has logged off the chat.
     *
     * @param userCode The unique code of the user who logged off.
     */
    void userLogOff(int userCode);

    /**
     * A user is notifying that it is available and logged on to the chat.
     *
     * @param user The unknown user who was exposed.
     */
    void userExposing(User user);

    /**
     * A user has requested information about the other clients that are logged
     * on to the chat.
     */
    void exposeRequested();

    /**
     * A user has started or stopped writing.
     *
     * @param userCode The unique code of the user who started or stopped writing.
     * @param writing If the user is writing or not.
     */
    void writingChanged(int userCode, boolean writing);

    /**
     * The application user notifies that it is still connected to the network,
     * and logged on to the chat.
     *
     * @param ipAddress The IP address of the application user.
     */
    void meIdle(String ipAddress);

    /**
     * A user notifies that it is still connected to the network,
     * and logged on to the chat.
     *
     * @param userCode The unique code of the user who sent the idle message.
     * @param ipAddress The IP address of that user.
     */
    void userIdle(int userCode, String ipAddress);

    /**
     * A user is asking the application user to receive a file.
     *
     * @param userCode The unique code of the user who wants to send a file.
     * @param byteSize The size of the file in bytes.
     * @param fileName The name of the file.
     * @param user The nick name of the user.
     * @param fileHash The hash code of the file.
     */
    void fileSend(int userCode, long byteSize, String fileName, String user, int fileHash);

    /**
     * A user has aborted a file transfer from the application user.
     *
     * @param userCode The unique code of the user who aborted the file transfer.
     * @param fileName The name of the file.
     * @param fileHash The hash code of the file.
     */
    void fileSendAborted(int userCode, String fileName, int fileHash);

    /**
     * A user has accepted a file transfer from the application user.
     *
     * @param userCode The unique code of the user who accepted a file transfer.
     * @param fileName The name of the file.
     * @param fileHash The hash code of the file.
     * @param port The port to use for connecting to the other user.
     */
    void fileSendAccepted(int userCode, String fileName, int fileHash, int port);

    /**
     * A user has sent information about it's client.
     *
     * @param userCode The unique code of the user who sent client info.
     * @param client The client the user is using.
     * @param timeSinceLogon Number of milliseconds since the user logged on.
     * @param operatingSystem The user's operating system.
     * @param privateChatPort The port to use for sending private chat messages to this user.
     * @param tcpChatPort The port to use for sending chat messages to this user using tcp.
     */
    void clientInfo(int userCode, String client, long timeSinceLogon, String operatingSystem, int privateChatPort, int tcpChatPort);
}
