
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

package net.usikkert.kouchat.event;

/**
 * Implement this interface and register as a listener with
 * {@link NetworkService#registerNetworkConnectionListener(NetworkConnectionListener)}
 * to get notified of changes in the network connection.
 *
 * @author Christian Ihle
 */
public interface NetworkConnectionListener
{
	/**
	 * The connection to the network was lost.
	 *
	 * @param silent Don't give any messages to the user about the change.
	 */
	void networkWentDown( boolean silent );

	/**
	 * The connection to the network came up and is ready for use.
	 *
	 * @param silent Don't give any messages to the user about the change.
	 */
	void networkCameUp( boolean silent );
}
