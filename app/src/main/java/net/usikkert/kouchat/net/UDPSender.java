
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

import static net.usikkert.kouchat.net.NetworkUtils.IPTOS_RELIABILITY;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.util.Validate;

/**
 * Sends UDP packets directly to a user. Useful for private chat,
 * where not everyone should get the packets.
 *
 * @author Christian Ihle
 */
public class UDPSender {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(UDPSender.class.getName());

    /** The datagram socket used for sending messages. */
    private DatagramSocket udpSocket;

    /** If connected to the network or not. */
    private boolean connected;

    /** The error handler for registering important messages. */
    private final ErrorHandler errorHandler;

    /**
     * Default constructor.
     *
     * @param errorHandler The error handler to use.
     */
    public UDPSender(final ErrorHandler errorHandler) {
        Validate.notNull(errorHandler, "Error handler can not be null");

        this.errorHandler = errorHandler;
    }

    /**
     * Sends a packet with a message to a user.
     *
     * @param message The message to send.
     * @param ip The ip address of the user.
     * @param port The port to send the message to.
     * @return If the message was sent or not.
     */
    public boolean send(final String message, final String ip, final int port) {
        if (connected) {
            try {
                final InetAddress address = InetAddress.getByName(ip);
                final byte[] encodedMsg = message.getBytes(Constants.MESSAGE_CHARSET);
                final int size = encodedMsg.length;

                if (size > Constants.NETWORK_PACKET_SIZE) {
                    LOG.log(Level.WARNING, "Message was " + size + " bytes, which is too large.\n" +
                            " The receiver might not get the complete message.\n'" + message + "'");
                }

                final DatagramPacket packet = new DatagramPacket(encodedMsg, size, address, port);
                udpSocket.send(packet);
                LOG.log(Level.FINE, "Sent message: " + message + " to " + ip + ":" + port);

                return true;
            }

            catch (final IOException e) {
                LOG.log(Level.SEVERE, "Could not send message: " + message, e);
            }
        }

        return false;
    }

    /**
     * Creates a new UDP socket.
     */
    public void startSender() {
        LOG.log(Level.FINE, "Connecting...");

        if (connected) {
            LOG.log(Level.FINE, "Already connected.");
        }

        else {
            try {
                udpSocket = new DatagramSocket();
                udpSocket.setTrafficClass(IPTOS_RELIABILITY);

                connected = true;
                LOG.log(Level.FINE, "Connected.");
            }

            catch (final IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
                errorHandler.showError("Failed to initialize network:\n" + e +
                        "\n\nYou will not be able to send private messages!");
            }
        }
    }

    /**
     * Closes the UDP socket.
     */
    public void stopSender() {
        LOG.log(Level.FINE, "Disconnecting...");

        if (!connected) {
            LOG.log(Level.FINE, "Not connected.");
        }

        else {
            connected = false;

            if (udpSocket != null && !udpSocket.isClosed()) {
                udpSocket.close();
            }

            LOG.log(Level.FINE, "Disconnected.");
        }
    }
}
