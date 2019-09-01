
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

package net.usikkert.kouchat.net.tcp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.Logger;
import net.usikkert.kouchat.util.Sleeper;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * Message listener for a client that will identify the user on the other side
 * if that user sends its user code as the first message.
 *
 * @author Christian Ihle
 */
public class TCPUserIdentifier implements TCPClientListener {

    private static final Logger LOG = Logger.getLogger(TCPUserIdentifier.class);

    /**
     * Identification format consisting of both the user sending the message and the user expected
     * to receive the message. This is to avoid issues where client is restarted but one side hasn't
     * noticed yet, and connects to both on the same ip and port. This makes sure only one of the connections succeed.
     */
    private final Pattern messagePattern = Pattern.compile("^SYS-IDENTIFY:(\\d+):(\\d+)$");

    private final Controller controller;
    private final Settings settings;
    private final TCPClient client;
    private final Sleeper sleeper;

    @Nullable
    private String message;

    public TCPUserIdentifier(final Controller controller, final Settings settings, final TCPClient client) {
        Validate.notNull(controller, "Controller can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(client, "Client can not be null");

        this.controller = controller;
        this.settings = settings;
        this.client = client;
        this.sleeper = new Sleeper();

        client.registerClientListener(this);
    }

    @Override
    public void messageArrived(final String theMessage, final TCPClient theClient) {
        client.registerClientListener(null);
        LOG.fine("Received message: %s", theMessage);

        this.message = theMessage;
    }

    @Override
    public void disconnected(final TCPClient theClient) {

    }

    @Nullable
    public User waitForUser() {
        waitForMessage();

        final User user = userFromMessage();

        if (user != null && !user.getIpAddress().equals(client.getIPAddress())) {
            LOG.warning("Unexpected client ip connected. user=%s, userIP=%s, clientIP=%s",
                        user.getNick(), user.getIpAddress(), client.getIPAddress());
            return null;
        }

        return user;
    }

    private void waitForMessage() {
        int tries = 0;

        while (message == null && tries < 50) {
            sleeper.sleep(50);
            tries++;
        }
    }

    @Nullable
    private User userFromMessage() {
        if (message == null) {
            return null;
        }

        final Matcher messageMatcher = messagePattern.matcher(message);

        if (!messageMatcher.matches()) {
            LOG.warning("Unexpected format of identification. message=%s, clientIP=%s",
                    message, client.getIPAddress());
            return null;
        }

        try {
            final int userCode = Integer.valueOf(messageMatcher.group(1));
            final int recipientCode = Integer.valueOf(messageMatcher.group(2));

            if (recipientCode != settings.getMe().getCode()) {
                LOG.warning("Unexpected recipient code. userCode=%s, recipientCode=%s, clientIP=%s",
                        userCode, recipientCode, client.getIPAddress());
                return null;
            }

            return controller.getUser(userCode);
        }

        catch (final NumberFormatException e) {
            LOG.severe("Failed to identify user from message. %s", e.toString());
        }

        return null;
    }
}
