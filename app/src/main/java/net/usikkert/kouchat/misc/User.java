
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

import net.usikkert.kouchat.ui.PrivateChatWindow;

import org.jetbrains.annotations.Nullable;

/**
 * This class represents a user in the chat.
 *
 * @author Christian Ihle
 */
public class User implements Comparable<User> {

    /** The nick name of the user. */
    private String nick;

    /** The user's away message. Can not be blank if away, and must be blank if not away. */
    private String awayMsg;

    /** The user's ip address. */
    private String ipAddress;

    /** The user's operating system, like <code>Windows Vista</code> or <code>Linux</code>. */
    private String operatingSystem;

    /** Which type of chat client the user is connected with, like <code>KouChat v.1.0.0 Swing</code>. */
    private String client;

    /** The user's host name. */
    @Nullable
    private String hostName;

    /** The unique code identifying this user. */
    private final int code;

    /** The port to use when connecting to this user's private chat. */
    private int privateChatPort;

    /** The port to use when connecting to this user directly using tcp. */
    private int tcpChatPort;

    /** Whether a tcp connection is enabled for this user. */
    private boolean tcpEnabled;

    /** The time when the last idle message came from this user. */
    private long lastIdle;

    /** The time when this user logged on the chat. */
    private long logonTime;

    /** If the user is writing at the moment. */
    private boolean writing;

    /** If the user is away. Needs an away message as well if away. */
    private boolean away;

    /** If the user is the application user, and not some other user in the chat. */
    private boolean me;

    /** If a new unread private message has arrived. */
    private boolean newPrivMsg;

    /** If the user is logged on to the chat. */
    private boolean online;

    /** If a new unread message has arrived to the main chat. */
    private boolean newMsg;

    /** The private chat window where the chat session with this user happens. */
    @Nullable
    private PrivateChatWindow privchat;

    /** The chat logger used for logging communication with this user. */
    private ChatLogger privateChatLogger;

    /**
     * Constructor. Initializes variables.
     *
     * @param nick The nick name of the user.
     * @param code A unique code identifying the user.
     */
    public User(final String nick, final int code) {
        this.nick = nick;
        this.code = code;

        lastIdle = 0;
        awayMsg = "";
        writing = false;
        away = false;
        ipAddress = "<unknown>";
        me = false;
        logonTime = 0;
        operatingSystem = "<unknown>";
        client = "<unknown>";
        hostName = null;
        newMsg = false;
        privateChatPort = 0;
        tcpChatPort = 0;
        privchat = null;
        online = true;
        newPrivMsg = false;
    }

    /**
     * Resets some of the fields to default, to reset the user's state.
     */
    public void reset() {
        awayMsg = "";
        writing = false;
        away = false;
        ipAddress = "<unknown>";
        hostName = null;
        newMsg = false;
        privateChatPort = 0;
        tcpChatPort = 0;
        privchat = null;
        newPrivMsg = false;
    }

    /**
     * Checks if this user is the application user.
     *
     * @return If this user is me.
     */
    public boolean isMe() {
        return me;
    }

    /**
     * Sets if this user is the application user.
     *
     * @param me If this user is me.
     */
    public void setMe(final boolean me) {
        this.me = me;
    }

    /**
     * Gets the user's unique code.
     *
     * @return The user's unique code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets the user's unique nick name.
     *
     * @return The user's unique nick name.
     */
    public String getNick() {
        return nick;
    }

    /**
     * Sets the user's unique nick name.
     *
     * @param nick The user's unique nick name.
     */
    public void setNick(final String nick) {
        this.nick = nick;
    }

    /**
     * Gets the time when the last idle message came from this user.
     *
     * @return The time of the last idle message.
     */
    public long getLastIdle() {
        return lastIdle;
    }

    /**
     * Sets the time when the last idle message came from this user.
     *
     * @param lastIdle The time of the last idle message.
     */
    public void setLastIdle(final long lastIdle) {
        this.lastIdle = lastIdle;
    }

    /**
     * Checks if the user is away.
     *
     * @return If the user is away.
     */
    public boolean isAway() {
        return away;
    }

    /**
     * Sets if the user is away.
     *
     * @param away If the user is away.
     */
    public void setAway(final boolean away) {
        this.away = away;
    }

    /**
     * Gets the user's away message.
     *
     * @return The user's away message.
     */
    public String getAwayMsg() {
        return awayMsg;
    }

    /**
     * Sets the user's away message.
     *
     * @param awayMsg The user's away message.
     */
    public void setAwayMsg(final String awayMsg) {
        this.awayMsg = awayMsg;
    }

    /**
     * Checks if the user is writing.
     *
     * @return If the user is writing.
     */
    public boolean isWriting() {
        return writing;
    }

    /**
     * Sets if the user is writing.
     *
     * @param writing If the user is writing.
     */
    public void setWriting(final boolean writing) {
        this.writing = writing;
    }

    /**
     * Gets the user's ip address.
     *
     * @return The user's ip address.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the user's ip address.
     *
     * @param ipAddress The user's ip address.
     */
    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Gets the user's operating system.
     *
     * @return The user's operating system.
     */
    public String getOperatingSystem() {
        return operatingSystem;
    }

    /**
     * Sets the user's operating system.
     *
     * @param operatingSystem The user's operating system.
     */
    public void setOperatingSystem(final String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    /**
     * Gets the time when the user logged on.
     *
     * @return The time when the user logged on.
     */
    public long getLogonTime() {
        return logonTime;
    }

    /**
     * Sets the time when the user logged on.
     *
     * @param logonTime The time when the user logged on.
     */
    public void setLogonTime(final long logonTime) {
        this.logonTime = logonTime;
    }

    /**
     * Gets the client the user is using.
     *
     * @return The client the user is using.
     */
    public String getClient() {
        return client;
    }

    /**
     * Sets the client the user is using.
     *
     * @param client The client the user is using.
     */
    public void setClient(final String client) {
        this.client = client;
    }

    /**
     * Gets the private chat window connected to this user.
     *
     * @return The user's private chat window.
     */
    @Nullable
    public PrivateChatWindow getPrivchat() {
        return privchat;
    }

    /**
     * Sets the private chat window connected to this user.
     *
     * @param privchat The user's private chat window.
     */
    public void setPrivchat(final PrivateChatWindow privchat) {
        this.privchat = privchat;
    }

    /**
     * Checks if a new unread message has arrived in the main chat.
     *
     * @return If a new message has arrived.
     */
    public boolean isNewMsg() {
        return newMsg;
    }

    /**
     * Sets if a new unread message has arrived in the main chat.
     *
     * @param newMsg If a new message has arrived.
     */
    public void setNewMsg(final boolean newMsg) {
        this.newMsg = newMsg;
    }

    /**
     * Gets the port to use when sending private chat messages to this user.
     *
     * @return The port to use for private chat with the user.
     */
    public int getPrivateChatPort() {
        return privateChatPort;
    }

    /**
     * Sets the port to use when sending private chat messages to this user.
     *
     * @param privateChatPort The port to use for private chat with the user.
     */
    public void setPrivateChatPort(final int privateChatPort) {
        this.privateChatPort = privateChatPort;
    }

    /**
     * Gets the port to use when sending chat messages to this user using tcp.
     *
     * @return The port to use for tcp chat with the user.
     */
    public int getTcpChatPort() {
        return tcpChatPort;
    }

    /**
     * Sets the port to use when sending chat messages to this user using tcp.
     *
     * @param tcpChatPort The port to use for tcp chat with the user.
     */
    public void setTcpChatPort(final int tcpChatPort) {
        this.tcpChatPort = tcpChatPort;
    }

    /**
     * Gets whether a tcp connection is enabled for this user.
     *
     * @return If a tcp connection is enabled for this user.
     */
    public boolean isTcpEnabled() {
        return tcpEnabled;
    }

    /**
     * Sets whether a tcp connection is enabled for this user.
     *
     * @param tcpEnabled If a tcp connection is enabled for this user.
     */
    public void setTcpEnabled(final boolean tcpEnabled) {
        this.tcpEnabled = tcpEnabled;
    }

    /**
     * Checks if this user is logged on to the chat.
     *
     * @return If the user is online.
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * Sets if this user is logged on to the chat.
     *
     * @param online If the user is online.
     */
    public void setOnline(final boolean online) {
        this.online = online;
    }

    /**
     * Checks if a new unread private message has arrived.
     *
     * @return If a new private message has arrived.
     */
    public boolean isNewPrivMsg() {
        return newPrivMsg;
    }

    /**
     * Sets if a new unread private message has arrived.
     *
     * @param newPrivMsg If a new private message has arrived.
     */
    public void setNewPrivMsg(final boolean newPrivMsg) {
        this.newPrivMsg = newPrivMsg;
    }

    /**
     * Gets the host name of the user.
     *
     * @return The host name.
     */
    @Nullable
    public String getHostName() {
        return hostName;
    }

    /**
     * Sets the host name of the user.
     *
     * @param hostName The host name.
     */
    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    /**
     * Gets the chat logger for this user.
     *
     * @return The private chat logger for this user.
     */
    public ChatLogger getPrivateChatLogger() {
        return privateChatLogger;
    }

    /**
     * Sets the chat logger for this user.
     *
     * @param privateChatLogger The chat logger instance to use for logging messages with this user.
     */
    public void setPrivateChatLogger(final ChatLogger privateChatLogger) {
        this.privateChatLogger = privateChatLogger;
    }

    /**
     * Returns the nick name.
     *
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return nick;
    }

    /**
     * Sorts by nick name alphabetically.
     *
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final User compNick) {
        return nick.compareToIgnoreCase(compNick.getNick());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)  {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final User user = (User) o;

        return code == user.code;
    }

    @Override
    public int hashCode() {
        return code;
    }
}
