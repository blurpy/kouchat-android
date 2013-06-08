
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.event.NetworkConnectionListener;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.Validate;

/**
 * This thread is responsible for keeping the application connected
 * to the network.
 *
 * Every now and then, the thread will check if there are better
 * networks available, and reconnect to that network instead.
 *
 * @author Christian Ihle
 */
public class ConnectionWorker implements Runnable {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(ConnectionWorker.class.getName());

    /** Period of time to sleep if network is up. 60 sec. */
    private static final int SLEEP_UP = 1000 * 60;

    /** Period of time to sleep if network is down. 15 sec. */
    private static final int SLEEP_DOWN = 1000 * 15;

    /** Indicates whether the thread should run or not. */
    private boolean run;

    /** Whether the network is up or not. */
    private boolean networkUp;

    /** The current network interface. */
    private NetworkInterface networkInterface;

    /** The working thread. */
    private Thread worker;

    /** A list of connection listeners. */
    private final List<NetworkConnectionListener> listeners;

    /** For locating the operating system's choice of network interface. */
    private final OperatingSystemNetworkInfo osNetworkInfo;

    /** The settings to use for the network. */
    private final Settings settings;

    /**
     * Constructor.
     *
     * @param settings The settings to use.
     */
    public ConnectionWorker(final Settings settings) {
        Validate.notNull(settings, "Settings can not be null");

        this.settings = settings;

        listeners = new ArrayList<NetworkConnectionListener>();
        osNetworkInfo = new OperatingSystemNetworkInfo(settings);
    }

    /**
     * The thread. See {@link #updateNetwork()} for details.
     */
    @Override
    public void run() {
        LOG.log(Level.FINE, "Network is starting");

        while (run) {
            final boolean networkUp = updateNetwork();

            try {
                if (networkUp) {
                    Thread.sleep(SLEEP_UP);
                } else {
                    Thread.sleep(SLEEP_DOWN);
                }
            }

            // Sleep interrupted - probably from stop() or checkNetwork()
            catch (final InterruptedException e) {
                LOG.log(Level.FINE, e.toString());
            }
        }

        LOG.log(Level.FINE, "Network is stopping");

        if (networkUp) {
            notifyNetworkDown(false);
        }

        networkInterface = null;
    }

    /**
     * Asks the thread to check the network now to detect loss of network connectivity.
     */
    public void checkNetwork() {
        if (worker != null) {
            worker.interrupt();
        }
    }

    /**
     * Checks the state of the network, and tries to keep the best possible
     * network connection up. Listeners are notified of any changes.
     *
     * @return If the network is up or not after this update is done.
     */
    private synchronized boolean updateNetwork() {
        final NetworkInterface netif = selectNetworkInterface();

        // No network interface to connect with
        if (!NetworkUtils.isUsable(netif)) {
            LOG.log(Level.FINE, "Network is down");

            if (networkUp) {
                notifyNetworkDown(false);
            }

            return false;
        }

        // Switching network interface, like going from cable to wireless
        else if (isNewNetworkInterface(netif)) {
            final String origNetwork = networkInterface == null ? "[null]" : networkInterface.getName();
            LOG.log(Level.FINE, "Changing network from " + origNetwork + " to " + netif.getName());
            networkInterface = netif;

            if (networkUp) {
                notifyNetworkDown(true);
                notifyNetworkUp(true);
            }

            else {
                notifyNetworkUp(false);
            }
        }

        // If the connection was lost, like unplugging cable, and plugging back in
        else if (!networkUp) {
            LOG.log(Level.FINE, "Network " + netif.getName() + " is up again");
            networkInterface = netif;
            notifyNetworkUp(false);
        }

        // Else, the old connection is still up

        return true;
    }

    /**
     * Compares <code>netif</code> with the current network interface.
     *
     * @param netif The new network interface to compare against the original.
     * @return True if netif is new.
     */
    private boolean isNewNetworkInterface(final NetworkInterface netif) {
        return !NetworkUtils.sameNetworkInterface(netif, networkInterface);
    }

    /**
     * Notifies all the listeners that they can prepare to be notified that the network is up.
     */
    private synchronized void notifyBeforeNetworkUp() {
        for (final NetworkConnectionListener listener : listeners) {
            listener.beforeNetworkCameUp();
        }
    }

    /**
     * Notifies all the listeners that the network is up.
     *
     * @param silent Don't give any messages to the user about the change.
     */
    private synchronized void notifyNetworkUp(final boolean silent) {
        notifyBeforeNetworkUp();

        networkUp = true;

        for (final NetworkConnectionListener listener : listeners) {
            listener.networkCameUp(silent);
        }
    }

    /**
     * Notifies all the listeners that the network is down.
     *
     * @param silent Don't give any messages to the user about the change.
     */
    private synchronized void notifyNetworkDown(final boolean silent) {
        networkUp = false;

        for (final NetworkConnectionListener listener : listeners) {
            listener.networkWentDown(silent);
        }
    }

    /**
     * Registers the listener as a connection listener.
     *
     * @param listener The listener to register.
     */
    public void registerNetworkConnectionListener(final NetworkConnectionListener listener) {
        listeners.add(listener);
    }

    /**
     * Starts a new thread if no thread is already running.
     */
    public synchronized void start() {
        if (!run && !isAlive()) {
            run = true;
            worker = new Thread(this, "ConnectionWorker");
            worker.start();
        }
    }

    /**
     * Stops the thread.
     */
    public void stop() {
        run = false;

        if (worker != null) {
            worker.interrupt();
        }
    }

    /**
     * Locates the best network interface to use.
     *
     * <ol>
     *   <li>The first priority is the network interface selected in the settings.</li>
     *   <li>The second priority is the operating system's choice of network interface.</li>
     *   <li>The last priority is KouChat's own choice of network interface.</li>
     * </ol>
     *
     * <p>If no usable network interfaces are found, then <code>null</code>
     * is returned.</p>
     *
     * @return The network interface found, or <code>null</code>.
     * @see NetworkUtils#isUsable(NetworkInterface)
     */
    private NetworkInterface selectNetworkInterface() {
        final NetworkInterface firstUsableNetIf = NetworkUtils.findFirstUsableNetworkInterface();

        if (firstUsableNetIf == null) {
            LOG.log(Level.FINER, "No usable network interface detected.");
            return null;
        }

        final NetworkInterface savedNetworkInterface =
                NetworkUtils.getNetworkInterfaceByName(settings.getNetworkInterface());

        if (NetworkUtils.isUsable(savedNetworkInterface)) {
            LOG.log(Level.FINER, "Using saved network interface: \n" +
                    NetworkUtils.getNetworkInterfaceInfo(savedNetworkInterface));
            return savedNetworkInterface;
        }

        LOG.log(Level.FINER, "Saved network interface '" + settings.getNetworkInterface() + "' is invalid: \n" +
                NetworkUtils.getNetworkInterfaceInfo(savedNetworkInterface));

        final NetworkInterface osNetIf = osNetworkInfo.getOperatingSystemNetworkInterface();

        if (NetworkUtils.isUsable(osNetIf)) {
            LOG.log(Level.FINER, "Using operating system's choice of network interface: \n" +
                    NetworkUtils.getNetworkInterfaceInfo(osNetIf));
            return osNetIf;
        }

        LOG.finer("The operating system suggested the following invalid network interface: \n" +
                NetworkUtils.getNetworkInterfaceInfo(osNetIf));
        LOG.log(Level.FINER, "Overriding operating system's choice of network interface with: \n" +
                NetworkUtils.getNetworkInterfaceInfo(firstUsableNetIf));

        return firstUsableNetIf;
    }

    /**
     * Finds the current network interface.
     *
     * @return The current network interface.
     */
    public NetworkInterface getCurrentNetworkInterface() {
        final NetworkInterface updatedNetworkInterface =
                NetworkUtils.getUpdatedNetworkInterface(networkInterface);

        if (updatedNetworkInterface != null) {
            return updatedNetworkInterface;
        }

        return networkInterface;
    }

    /**
     * Checks if the network is up.
     *
     * @return If the network is up.
     */
    public boolean isNetworkUp() {
        return networkUp;
    }

    /**
     * Checks if the thread is alive.
     *
     * @return If the thread is alive.
     */
    public boolean isAlive() {
        if (worker == null) {
            return false;
        } else {
            return worker.isAlive();
        }
    }
}
