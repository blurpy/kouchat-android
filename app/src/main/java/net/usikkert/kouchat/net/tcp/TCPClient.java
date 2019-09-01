
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import net.usikkert.kouchat.util.Logger;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * Client for communicating over a tcp socket.
 *
 * @author Christian Ihle
 */
public class TCPClient implements Runnable {

    private static final Logger LOG = Logger.getLogger(TCPClient.class);

    private final Socket socket;

    @Nullable
    private DataInputStream inputStream;

    @Nullable
    private DataOutputStream outputStream; // TODO how is this outside of Java?

    @Nullable
    private TCPClientListener clientListener;

    private boolean connected;
    private boolean disconnecting;

    public TCPClient(final Socket socket) {
        Validate.notNull(socket, "Socket can not be null");
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (connected && inputStream != null) {
                final String message = inputStream.readUTF();
                LOG.fine("Message arrived from %s: %s", getIPAddress(), message);

                if (clientListener != null) {
                    clientListener.messageArrived(message, this);
                }
            }
        }

        catch (final IOException e) {
            LOG.severe(e.toString());
            connected = false;

            if (clientListener != null) {
                clientListener.disconnected(this);
            }
        }
    }

    public void send(final String message) {
        if (!connected || outputStream == null) {
            return;
        }

        try {
            outputStream.writeUTF(message);
            LOG.fine("Sent message: %s", message);
        }

        catch (final IOException e) {
            LOG.severe(e.toString());
            connected = false;

            if (clientListener != null) {
                clientListener.disconnected(this);
            }
        }
    }

    public boolean connect() {
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            LOG.fine("Connected to %s:%s", getIPAddress(), socket.getPort());

            connected = true;
            new Thread(this, getClass().getSimpleName()).start();

            return true;
        }

        catch (final IOException e) {
            LOG.severe(e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            LOG.fine("Disconnected from %s:%s", getIPAddress(), socket.getPort());
            connected = false;
            disconnecting = true;

            if (clientListener != null) {
                clientListener.disconnected(this);
            }

            socket.close();
        }

        catch (final IOException e) {
            LOG.warning(e.getMessage());
        }
    }

    public String getIPAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    public void registerClientListener(@Nullable final TCPClientListener theClientListener) {
        this.clientListener = theClientListener;
    }

    public void setDisconnecting(final boolean isDisconnecting) {
        disconnecting = isDisconnecting;
    }

    public boolean isDisconnecting() {
        return disconnecting;
    }

    public boolean isConnected() {
        return connected;
    }
}
