
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

/**
 * This is the interface for responders to private udp messages.
 *
 * <p>The responder gets the message after it has been parsed by the
 * {@link PrivateMessageParser}.</p>
 *
 * @author Christian Ihle
 */
public interface PrivateMessageResponder {

    /**
     * A new private message has arrived.
     *
     * @param userCode The unique code for the user that sent the private message.
     * @param msg The message from the user.
     * @param color The color to show the message in.
     */
    void messageArrived(int userCode, String msg, int color);
}
