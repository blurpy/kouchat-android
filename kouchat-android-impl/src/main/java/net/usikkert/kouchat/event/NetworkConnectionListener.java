
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

package net.usikkert.kouchat.event;

/**
 * Implement this interface and register as a listener with
 * {@link net.usikkert.kouchat.net.NetworkService#registerNetworkConnectionListener(NetworkConnectionListener)}
 * to get notified of changes in the network connection.
 *
 * @author Christian Ihle
 */
public interface NetworkConnectionListener {

    /**
     * The connection to the network was lost.
     *
     * @param silent Don't give any messages to the user about the change.
     */
    void networkWentDown(boolean silent);

    /**
     * The network came up, and any preparations necessary to make it ready for use needs to happen here.
     * All listeners get this notification before anyone gets notified with {@link #networkCameUp(boolean)}.
     */
    void beforeNetworkCameUp();

    /**
     * The connection to the network came up and is ready for use.
     *
     * @param silent Don't give any messages to the user about the change.
     */
    void networkCameUp(boolean silent);
}
