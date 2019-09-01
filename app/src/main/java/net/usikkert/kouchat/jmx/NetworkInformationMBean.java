
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

package net.usikkert.kouchat.jmx;

import java.net.SocketException;

/**
 * This is the JMX MBean interface for the network service.
 *
 * @author Christian Ihle
 */
public interface NetworkInformationMBean extends JMXBean {

    /**
     * Shows the current connected network.
     *
     * @return A string with information.
     * @throws SocketException In case of network errors.
     */
    String showCurrentNetwork() throws SocketException;

    /**
     * Shows the network that the operation system would have chosen.
     *
     * @return A string with information.
     * @throws SocketException In case of network errors.
     */
    String showOperatingSystemNetwork() throws SocketException;

    /**
     * Shows the available networks that are usable for chat.
     *
     * @return A string with information.
     * @throws SocketException In case of network errors.
     */
    String[] showUsableNetworks() throws SocketException;

    /**
     * Shows all the available networks.
     *
     * @return A string with information.
     * @throws SocketException In case of network errors.
     */
    String[] showAllNetworks() throws SocketException;

    /**
     * Disconnects from the network, without logging off.
     */
    void disconnect();

    /**
     * Connects to the network.
     */
    void connect();
}
