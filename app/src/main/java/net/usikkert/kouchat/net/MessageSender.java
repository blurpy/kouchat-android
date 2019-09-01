
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
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * This is the class that sends multicast messages over the network.
 *
 * @author Christian Ihle
 */
public class MessageSender {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(MessageSender.class.getName());

    /** The multicast socket used for sending messages. */
    @Nullable
    private MulticastSocket mcSocket;

    /** The inetaddress object with the multicast ip address to send messages to. */
    private InetAddress address;

    /** If connected to the network or not. */
    private boolean connected;

    /** The port to send messages to. */
    private final int port;

    /**
     * Default constructor.
     *
     * <p>Initializes the network with the default ip address and port.</p>
     *
     * @see Constants#NETWORK_IP
     * @see Constants#NETWORK_CHAT_PORT
     * @param errorHandler The error handler to use.
     */
    public MessageSender(final ErrorHandler errorHandler) {
        this(Constants.NETWORK_IP, Constants.NETWORK_CHAT_PORT, errorHandler);
    }

    /**
     * Alternative constructor.
     *
     * <p>Initializes the network with the given ip address and port.</p>
     *
     * @param ipAddress Multicast ip address to connect to.
     * @param port Port to connect to.
     * @param errorHandler The error handler to use.
     */
    public MessageSender(final String ipAddress, final int port, final ErrorHandler errorHandler) {
        LOG.fine("Creating MessageSender on " + ipAddress + ":" + port);

        Validate.notEmpty(ipAddress, "IP address can not be empty");
        Validate.notNull(errorHandler, "Error handler can not be null");

        this.port = port;

        try {
            address = InetAddress.getByName(ipAddress);
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);

            errorHandler.showCriticalError("Failed to initialize the network:\n" + e + "\n" +
                    Constants.APP_NAME + " will now shutdown.");

            System.exit(1);
        }
    }

    /**
     * Sends a multicast packet to other clients over the network.
     *
     * @param message The message to send in the packet.
     * @return If the message was sent or not.
     * @see Constants#MESSAGE_CHARSET
     * @see Constants#NETWORK_PACKET_SIZE
     */
    public synchronized boolean send(final String message) {
        if (connected) {
            try {
                final byte[] encodedMsg = message.getBytes(Constants.MESSAGE_CHARSET);
                final int size = encodedMsg.length;

                if (size > Constants.NETWORK_PACKET_SIZE) {
                    LOG.log(Level.WARNING, "Message was " + size + " bytes, which is too large.\n" +
                            " The receiver might not get the complete message.\n'" + message + "'");
                }

                final DatagramPacket packet = new DatagramPacket(encodedMsg, size, address, port);
                mcSocket.send(packet);
                LOG.log(Level.FINE, "Sent message: " + message);

                return true;
            }

            catch (final IOException e) {
                LOG.log(Level.WARNING, "Could not send message: " + message, e);
            }
        }

        return false;
    }

    /**
     * Connects to the network with the given network interface, or gives
     * the control to the operating system to choose if <code>null</code>
     * is given.
     *
     * @param networkInterface The network interface to use, or <code>null</code>.
     * @return If connected to the network or not.
     */
    public synchronized boolean startSender(@Nullable final NetworkInterface networkInterface) {
        LOG.log(Level.FINE, "Connecting to " + address.getHostAddress() + ":" + port + " on " + networkInterface);

        try {
            if (connected) {
                LOG.log(Level.FINE, "Already connected.");
            }

            else {
                if (mcSocket == null) {
                    mcSocket = new MulticastSocket(port);
                }

                if (networkInterface != null) {
                    mcSocket.setNetworkInterface(networkInterface);
                }

                mcSocket.setTrafficClass(IPTOS_RELIABILITY);

                mcSocket.joinGroup(address);
                mcSocket.setTimeToLive(64);
                LOG.log(Level.FINE, "Connected to " + mcSocket.getNetworkInterface());
                connected = true;
            }
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, "Could not start sender: " + e.toString(), e);

            if (mcSocket != null) {
                if (!mcSocket.isClosed()) {
                    mcSocket.close();
                }

                mcSocket = null;
            }
        }

        return connected;
    }

    /**
     * Disconnects from the network and closes the multicast socket.
     */
    public synchronized void stopSender() {
        LOG.log(Level.FINE, "Disconnecting from " + address.getHostAddress() + ":" + port);

        if (!connected) {
            LOG.log(Level.FINE, "Not connected.");
        }

        else {
            connected = false;

            try {
                if (!mcSocket.isClosed()) {
                    mcSocket.leaveGroup(address);
                }
            }

            catch (final IOException e) {
                LOG.log(Level.WARNING, e.toString());
            }

            if (!mcSocket.isClosed()) {
                mcSocket.close();
                mcSocket = null;
            }

            LOG.log(Level.FINE, "Disconnected from " + address.getHostAddress() + ":" + port);
        }
    }
}
