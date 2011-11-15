
/***************************************************************************
 *   Copyright 2006-2009 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package net.usikkert.kouchat.net;

import java.net.SocketException;

/**
 * This is the JMX MBean interface for the network service.
 *
 * @author Christian Ihle
 */
public interface NetworkInformationMBean
{
	/** The name of this MBean. */
	String NAME = "Network";

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

	/** Disconnects from the network, without logging off. */
	void disconnect();

	/** Connects to the network. */
	void connect();
}
