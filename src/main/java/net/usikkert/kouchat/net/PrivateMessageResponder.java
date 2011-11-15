
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

/**
 * This is the interface for responders to private udp messages.
 *
 * <p>The responder gets the message after it has been parsed by the
 * {@link PrivateMessageParser}.</p>
 *
 * @author Christian Ihle
 */
public interface PrivateMessageResponder
{
	/**
	 * A new private message has arrived.
	 *
	 * @param userCode The unique code for the user that sent the private message.
	 * @param msg The message from the user.
	 * @param color The color to show the message in.
	 */
	void messageArrived( int userCode, String msg, int color );
}
