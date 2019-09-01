
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

import net.usikkert.kouchat.event.ReceiverListener;

import org.jetbrains.annotations.Nullable;

/**
 * This is a very simple {@link ReceiverListener} for getting the message
 * and ip address when a message arrives.
 *
 * @author Christian Ihle
 */
public class SimpleReceiverListener implements ReceiverListener {

    /** An expected message. */
    private final String expectedMessage;

    /** The arrived message, or null. */
    @Nullable
    private String message;

    /** The ip address of the arrived message, or null. */
    @Nullable
    private String ipAddress;

    /**
     * Constructor.
     *
     * @param expectedMessage An expected message, or <code>null</code>.
     */
    public SimpleReceiverListener(final String expectedMessage) {
        this.expectedMessage = expectedMessage;
    }

    /**
     * Stores the message and ip address, and nothing more.
     *
     * <p>If {@link #expectedMessage} is not null, then the message and ip
     * is stored only if the message equals the expected message.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public void messageArrived(final String message, final String ipAddress) {
        if (expectedMessage == null || expectedMessage.equals(message)) {
            this.message = message;
            this.ipAddress = ipAddress;
        }
    }

    /**
     * Gets the arrived message.
     *
     * @return The message.
     */
    @Nullable
    public String getMessage() {
        return message;
    }

    /**
     * Gets the ip address of the arrived message.
     *
     * @return The ip address.
     */
    @Nullable
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Resets the message and ip address to <code>null</code>.
     */
    public void reset() {
        message = null;
        ipAddress = null;
    }
}
