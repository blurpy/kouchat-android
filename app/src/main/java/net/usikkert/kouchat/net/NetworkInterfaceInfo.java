
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

import net.usikkert.kouchat.util.Validate;

/**
 * Wrapper around {@link NetworkInterface}.
 *
 * @author Christian Ihle
 */
public class NetworkInterfaceInfo {

    private final NetworkInterface networkInterface;

    public NetworkInterfaceInfo(final NetworkInterface networkInterface) {
        Validate.notNull(networkInterface, "Network interface to wrap can not be null");

        this.networkInterface = networkInterface;
    }

    /**
     * Returns a human readable name of the network interface, if available.
     *
     * <p>On Linux it might be just <code>eth0</code>, but on Windows it could be more readable, like
     * <code>Intel(R) PRO/1000 MT Network Connection</code>.</p>
     *
     * @return The display name of the network interface.
     */
    public String getDisplayName() {
        return networkInterface.getDisplayName();
    }

    /**
     * Returns the name identifying this exact network interface, like <code>eth0</code>.
     *
     * @return The identifying name of the network interface.
     */
    public String getName() {
        return networkInterface.getName();
    }

    /**
     * Gets the real network interface behind the wrapper.
     *
     * @return The real network interface.
     */
    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }
}
