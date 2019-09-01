
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

import java.util.ArrayList;
import java.util.List;

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Logger;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * Maps one or more tcp clients to a user.
 *
 * @author Christian Ihle
 */
public class TCPUserClient implements TCPClientListener {

    private static final Logger LOG = Logger.getLogger(TCPUserClient.class);
    private static final String MESSAGE_DISCONNECT_ADDITIONAL = "SYS-DISCONNECT-ADDITIONAL";

    private final List<TCPClient> clients;
    private final User user;
    private final TCPReceiverListener listener;

    public TCPUserClient(final TCPClient client, final User user, final TCPReceiverListener listener) {
        Validate.notNull(client, "Client can not be null");
        Validate.notNull(user, "User can not be null");
        Validate.notNull(listener, "TCP message listener can not be null");

        this.clients = new ArrayList<>();
        this.user = user;
        this.listener = listener;

        add(client);
    }

    public void add(final TCPClient client) {
        clients.add(client);
        client.registerClientListener(this);
        user.setTcpEnabled(true);
    }

    public void disconnect() {
        user.setTcpEnabled(false);

        for (final TCPClient client : clients) {
            client.registerClientListener(null);
            client.disconnect();
        }

        clients.clear();
    }

    @Override
    public void disconnected(final TCPClient client) {
        client.registerClientListener(null);
        clients.remove(client);

        if (clients.isEmpty()) {
            user.setTcpEnabled(false);
        }
    }

    @Override
    public void messageArrived(final String message, final TCPClient client) {
        if (message.equals(MESSAGE_DISCONNECT_ADDITIONAL)) {
            LOG.fine("Client for %s asked to disconnect", user.getNick());

            if (clients.size() <= 1) {
                LOG.fine("Not enough clients left for %s to disconnect", user.getNick());
                return;
            }

            for (final TCPClient tcpClient : clients) {
                if (tcpClient.isDisconnecting()) {
                    LOG.fine("Another client for %s is already waiting to be disconnected", user.getNick());
                    return;
                }
            }

            client.disconnect();
        }

        else {
            listener.messageArrived(message, client.getIPAddress(), user);
        }
    }

    public void send(final String message) {
        for (final TCPClient client : clients) {
            if (!client.isDisconnecting()) {
                client.send(message);
                return;
            }
        }
    }

    public int getClientCount() {
        return clients.size();
    }

    /**
     * Using a two step process to try to avoid a situation where clients on both side disconnect
     * different sockets at the same time.
     *
     * This works by marking a client as "disconnecting". If a request arrives from the other side to
     * disconnect it will be ignored if only 1 client left, or if this side has already sent a request to
     * disconnect. This may end up with none of the sides disconnecting, but hopefully resolving on the next attempt.
     */
    public void disconnectAdditionalClients() {
        if (clients.size() <= 1) {
            LOG.fine("Not enough clients left for %s to ask to disconnect", user.getNick());
            return;
        }

        for (final TCPClient client : clients) {
            if (client.isDisconnecting()) {
                LOG.fine("A client for %s is already waiting to be disconnected", user.getNick());
                return;
            }
        }

        final TCPClient client = clients.get(0);
        client.setDisconnecting(true);
        client.send(MESSAGE_DISCONNECT_ADDITIONAL);

        for (int i = 0; i < 50; i++) {
            Tools.sleep(50);

            if (!client.isConnected()) {
                return;
            }
        }

        LOG.warning("Client for %s didn't disconnect as expected", user.getNick());
        client.setDisconnecting(false);
    }
}
