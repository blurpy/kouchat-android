
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

import java.net.NetworkInterface;
import java.util.logging.Logger;

import net.usikkert.kouchat.event.NetworkConnectionListener;
import net.usikkert.kouchat.event.ReceiverListener;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.tcp.TCPNetworkService;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.Validate;

/**
 * This class has services for connecting to the network.
 *
 * @author Christian Ihle
 */
public class NetworkService implements NetworkConnectionListener {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(NetworkService.class.getName());

    /** The thread responsible for keeping the network connection up. */
    private final ConnectionWorker connectionWorker;

    /** The multicast message sender. */
    private final MessageSender messageSender;

    /** The multicast message receiver. */
    private final MessageReceiver messageReceiver;

    /** The private message sender. */
    private final UDPSender udpSender;

    /** The private message receiver. */
    private final UDPReceiver udpReceiver;

    /** The network service for tcp connections. */
    private final TCPNetworkService tcpNetworkService;

    /** Proxy for deduplicating multicast and tcp messages. */
    private final MessageDeduplicator messageDeduplicator;

    /** If private chat should be enabled. */
    private final boolean privateChatEnabled;

    /**
     * Constructor.
     *
     * @param controller The controller to use.
     * @param settings The settings to use.
     * @param errorHandler The error handler to use.
     */
    public NetworkService(final Controller controller, final Settings settings, final ErrorHandler errorHandler) {
        Validate.notNull(controller, "Controller can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");

        LOG.fine("Initializing network");

        privateChatEnabled = !settings.isNoPrivateChat();

        messageReceiver = new MessageReceiver(errorHandler);
        messageSender = new MessageSender(errorHandler);
        connectionWorker = new ConnectionWorker(settings, errorHandler);
        tcpNetworkService = new TCPNetworkService(controller, settings, errorHandler);
        messageDeduplicator = new MessageDeduplicator(controller);

        if (privateChatEnabled) {
            udpReceiver = new UDPReceiver(settings, errorHandler);
            udpSender = new UDPSender(errorHandler);
        }

        else {
            LOG.fine("Private chat is disabled");
            udpReceiver = null;
            udpSender = null;
        }

        connectionWorker.registerNetworkConnectionListener(this);
    }

    /**
     * Starts the thread responsible for connecting to the network.
     */
    public void connect() {
        connectionWorker.start();
    }

    /**
     * Stops the thread responsible for connecting to the network.
     */
    public void disconnect() {
        connectionWorker.stop();
    }

    /**
     * Gets the connection worker.
     *
     * @return The connection worker.
     */
    public ConnectionWorker getConnectionWorker() {
        return connectionWorker;
    }

    /**
     * Checks if the connection thread is alive.
     *
     * @return If the connection thread is alive.
     */
    public boolean isConnectionWorkerAlive() {
        return connectionWorker.isAlive();
    }

    /**
     * Checks if the network is up.
     *
     * @return If the network is up.
     */
    public boolean isNetworkUp() {
        return connectionWorker.isNetworkUp();
    }

    /**
     * Registers the listener as a connection listener.
     *
     * @param listener The listener to register.
     */
    public void registerNetworkConnectionListener(final NetworkConnectionListener listener) {
        connectionWorker.registerNetworkConnectionListener(listener);
    }

    /**
     * Register a listener for incoming main chat messages from the network.
     *
     * @param listener The listener to register.
     */
    public void registerMainChatMessageReceiverListener(final ReceiverListener listener) {
        messageDeduplicator.registerMainChatReceiverListener(listener);
        messageReceiver.registerReceiverListener(messageDeduplicator);
        tcpNetworkService.registerReceiverListener(messageDeduplicator);
    }

    /**
     * Register a listener for incoming private chat messages from the network.
     *
     * @param listener The listener to register.
     */
    public void registerPrivateChatReceiverListener(final ReceiverListener listener) {
        if (privateChatEnabled) {
            messageDeduplicator.registerPrivateChatReceiverListener(listener);
            udpReceiver.registerReceiverListener(messageDeduplicator);
        }
    }

    /**
     * Send a message to all users.
     *
     * @param message The message to send.
     * @return If the message was sent or not.
     */
    public boolean sendMessageToAllUsers(final String message) {
        tcpNetworkService.sendMessageToAll(message);
        return messageSender.send(message);
    }

    /**
     * Send a message to a single user.
     *
     * @param message The message to send.
     * @param user The user to send the message to.
     * @return If the message was sent or not.
     */
    public boolean sendMessageToUser(final String message, final User user) {
        if (privateChatEnabled) {
            tcpNetworkService.sendMessageToUser(message, user);
            return udpSender.send(message, user.getIpAddress(), user.getPrivateChatPort());
        }

        else {
            return false;
        }
    }

    /**
     * Checks the state of the network, and tries to keep the best possible
     * network connection up.
     */
    public void checkNetwork() {
        connectionWorker.checkNetwork();
    }

    /**
     * Stops all senders and receivers.
     *
     * {@inheritDoc}
     */
    @Override
    public void networkWentDown(final boolean silent) {
        if (privateChatEnabled) {
            udpSender.stopSender();
            udpReceiver.stopReceiver();
        }

        messageSender.stopSender();
        messageReceiver.stopReceiver();
        tcpNetworkService.stopService();
    }

    @Override
    public void beforeNetworkCameUp() {
        // Nothing to do here
    }

    /**
     * Starts all senders and receivers.
     *
     * {@inheritDoc}
     */
    @Override
    public void networkCameUp(final boolean silent) {
        if (privateChatEnabled) {
            udpSender.startSender();
            udpReceiver.startReceiver();
        }

        final NetworkInterface currentNetworkInterface = connectionWorker.getCurrentNetworkInterface();
        messageSender.startSender(currentNetworkInterface);
        messageReceiver.startReceiver(currentNetworkInterface);
        tcpNetworkService.startService();
    }
}
