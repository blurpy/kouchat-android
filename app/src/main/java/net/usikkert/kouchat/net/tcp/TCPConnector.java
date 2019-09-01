
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Logger;
import net.usikkert.kouchat.util.Sleeper;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * Creates a tcp connection to a user.
 *
 * @author Christian Ihle
 */
public class TCPConnector {

    private static final Logger LOG = Logger.getLogger(TCPConnector.class);
    private static final int SOCKET_CONNECT_TIMEOUT = 10_000;

    private final User user;
    private final Sleeper sleeper;

    public TCPConnector(final User user) {
        Validate.notNull(user, "User can not be null");

        this.user = user;
        this.sleeper = new Sleeper();
    }

    @Nullable
    public Socket connect() {
        LOG.fine("Connecting to user=%s", user.getNick());

        waitForPort();

        if (user.getTcpChatPort() <= 0) {
            LOG.warning("User has no tcp port. Giving up.");
            return null;
        }

        try {
            LOG.fine("Connecting to: %s@%s:%s", user.getNick(), user.getIpAddress(), user.getTcpChatPort());
            final Socket socket = new Socket();
            final SocketAddress address = new InetSocketAddress(InetAddress.getByName(user.getIpAddress()), user.getTcpChatPort());
            socket.connect(address, SOCKET_CONNECT_TIMEOUT);
            LOG.fine("Connected to: %s@%s:%s", user.getNick(), socket.getInetAddress().getHostAddress(), socket.getPort());

            return socket;
        }

        catch (final IOException e) {
            LOG.severe("Failed to connect to user=%s: %s", user.getNick(), e.getMessage());
        }

        return null;
    }

    private void waitForPort() {
        int tries = 0;

        while (user.getTcpChatPort() <= 0 && tries < 50) {
            sleeper.sleep(50);
            tries++;
        }
    }
}
