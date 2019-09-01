
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
import java.net.ServerSocket;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.Logger;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * Server listening for tcp connections from users.
 *
 * @author Christian Ihle
 */
public class TCPServer implements Runnable {

    private static final Logger LOG = Logger.getLogger(TCPServer.class);

    private static final int MAX_PORT_ATTEMPTS = 50;

    private final User me;
    private final ErrorHandler errorHandler;
    private final TCPConnectionListener tcpConnectionListener;

    private boolean connected;

    @Nullable
    private ServerSocket serverSocket;

    public TCPServer(final Settings settings, final ErrorHandler errorHandler,
                     final TCPConnectionListener tcpConnectionListener) {
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");
        Validate.notNull(tcpConnectionListener, "TCP connection listener can not be null");

        this.me = settings.getMe();
        this.errorHandler = errorHandler;
        this.tcpConnectionListener = tcpConnectionListener;
    }

    @Override
    public void run() {
        while (connected && serverSocket != null) {
            try {
                tcpConnectionListener.socketAdded(serverSocket.accept());
            }

            // Happens when server socket is closed, or network is down
            catch (final IOException e) {
                if (connected) {
                    LOG.warning(e.toString());
                }

                else {
                    LOG.fine(e.toString());
                }
            }
        }
    }

    public void startServer() {
        LOG.fine("Connecting...");

        if (connected) {
            LOG.fine("Already connected.");
            return;
        }

        int port = Constants.NETWORK_TCP_CHAT_PORT;
        int portAttempt = 0;

        while (portAttempt < MAX_PORT_ATTEMPTS && !connected) {
            try {
                serverSocket = new ServerSocket(port);
                connected = true;

                // The background thread watching for connections from the network.
                final Thread worker = new Thread(this, getClass().getSimpleName());
                worker.start();

                me.setTcpChatPort(port);
                LOG.fine("Connected to port: %s", port);
            }

            catch (final IOException e) {
                LOG.severe("%s %s", e.toString(), port);

                portAttempt++;
                port++;
                me.setTcpChatPort(0);
            }
        }

        if (!connected) {
            final String error = "Failed to initialize tcp network:" +
                    "\nNo available listening port between " + Constants.NETWORK_TCP_CHAT_PORT +
                    " and " + (port - 1) + "." +
                    "\n\nYou will not be able to receive tcp messages!";

            LOG.severe(error);
            errorHandler.showError(error);
        }
    }

    public void stopServer() {
        LOG.fine("Disconnecting...");

        if (!connected) {
            LOG.fine("Not connected.");
            return;
        }

        connected = false;

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (final IOException e) {
                LOG.severe(e.toString());
            }
        }

        LOG.fine("Disconnected.");
    }
}
