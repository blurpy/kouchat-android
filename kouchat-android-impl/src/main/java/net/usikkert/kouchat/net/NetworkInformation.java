
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
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
import java.util.Enumeration;
import java.util.List;

import net.usikkert.kouchat.util.Validate;

/**
 * This is a JMX MBean for the network service.
 *
 * @author Christian Ihle
 */
public class NetworkInformation implements NetworkInformationMBean {

    /** Information and control of the network. */
    private final ConnectionWorker connectionWorker;

    /**
     * Constructor.
     *
     * @param connectionWorker To get information about the network, and control the network.
     */
    public NetworkInformation(final ConnectionWorker connectionWorker) {
        Validate.notNull(connectionWorker, "Connection worker can not be null");
        this.connectionWorker = connectionWorker;
    }

    /** {@inheritDoc} */
    @Override
    public String showCurrentNetwork() {
        final NetworkInterface networkInterface = connectionWorker.getCurrentNetworkInterface();

        if (networkInterface == null) {
            return "No current network interface.";
        } else {
            return NetworkUtils.getNetworkInterfaceInfo(networkInterface);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String showOperatingSystemNetwork() {
        final OperatingSystemNetworkInfo osNicInfo = new OperatingSystemNetworkInfo();
        final NetworkInterface osInterface = osNicInfo.getOperatingSystemNetworkInterface();

        if (osInterface == null) {
            return "No network interface detected.";
        } else {
            return NetworkUtils.getNetworkInterfaceInfo(osInterface);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String[] showUsableNetworks() {
        final List<String> list = new ArrayList<String>();

        final Enumeration<NetworkInterface> networkInterfaces = NetworkUtils.getNetworkInterfaces();

        if (networkInterfaces == null) {
            return new String[]{"No network interfaces detected."};
        }

        while (networkInterfaces.hasMoreElements()) {
            final NetworkInterface netif = networkInterfaces.nextElement();

            if (NetworkUtils.isUsable(netif)) {
                list.add(NetworkUtils.getNetworkInterfaceInfo(netif));
            }
        }

        if (list.size() == 0) {
            return new String[]{"No usable network interfaces detected."};
        }

        return list.toArray(new String[0]);
    }

    /** {@inheritDoc} */
    @Override
    public String[] showAllNetworks() {
        final List<String> list = new ArrayList<String>();

        final Enumeration<NetworkInterface> networkInterfaces = NetworkUtils.getNetworkInterfaces();

        if (networkInterfaces == null) {
            return new String[]{"No network interfaces detected."};
        }

        while (networkInterfaces.hasMoreElements()) {
            final NetworkInterface netif = networkInterfaces.nextElement();
            list.add(NetworkUtils.getNetworkInterfaceInfo(netif));
        }

        return list.toArray(new String[0]);
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect() {
        connectionWorker.stop();
    }

    /** {@inheritDoc} */
    @Override
    public void connect() {
        connectionWorker.start();
    }
}
