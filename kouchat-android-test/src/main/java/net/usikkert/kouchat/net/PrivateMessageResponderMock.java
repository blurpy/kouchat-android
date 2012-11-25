
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

import java.util.ArrayList;
import java.util.List;

/**
 * This is a mock implementation of {@link PrivateMessageResponder}, that just stores the messages in a map.
 *
 * @author Christian Ihle
 */
public class PrivateMessageResponderMock implements PrivateMessageResponder {

    final List<Object[]> messagesArrived = new ArrayList<Object[]>();

    @Override
    public void messageArrived(final int userCode, final String msg, final int color) {
        messagesArrived.add(new Object[] { userCode, msg, color });
    }

    public boolean gotMessageArrived(final String message) {
        for (final Object[] messageArrived : messagesArrived) {
            if (messageArrived[1].equals(message)) {
                return true;
            }
        }

        return false;
    }
}
