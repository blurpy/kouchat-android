
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

import static net.usikkert.kouchat.net.NetworkMessageType.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.event.ReceiverListener;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.Validate;

/**
 * This class listens for udp messages from the network,
 * and parses them into a format the {@link PrivateMessageResponder} can use.
 *
 * <p>The supported message types:</p>
 *
 * <ul>
 *   <li>PRIVMSG</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class PrivateMessageParser implements ReceiverListener {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(PrivateMessageParser.class.getName());

    private final Settings settings;
    private final PrivateMessageResponder privmsgResponder;

    /**
     * Constructor.
     *
     * @param privmsgResponder The private message responder.
     * @param settings The settings to use.
     */
    public PrivateMessageParser(final PrivateMessageResponder privmsgResponder, final Settings settings) {
        Validate.notNull(privmsgResponder, "PrivateMessageResponder can not be null");
        Validate.notNull(settings, "Settings can not be null");

        this.privmsgResponder = privmsgResponder;
        this.settings = settings;
    }

    /**
     * Parses raw udp messages from the network, and gives
     * the result to the message responder.
     *
     * {@inheritDoc}
     */
    @Override
    public void messageArrived(final String message, final String ipAddress) {
        try {
            final int exclamation = message.indexOf("!");
            final int hash = message.indexOf("#");
            final int colon = message.indexOf(":");

            final int fromCode = Integer.parseInt(message.substring(0, exclamation));

            final String type = message.substring(exclamation + 1, hash);
            final String msg = message.substring(colon + 1, message.length());

            final int leftPara = msg.indexOf("(");
            final int rightPara = msg.indexOf(")");
            final int toCode = Integer.parseInt(msg.substring(leftPara + 1, rightPara));

            final User tempme = settings.getMe();

            if (fromCode != tempme.getCode() && toCode == tempme.getCode()) {
                if (type.equals(PRIVMSG)) {
                    final int leftBracket = msg.indexOf("[");
                    final int rightBracket = msg.indexOf("]");
                    final int rgb = Integer.parseInt(msg.substring(leftBracket + 1, rightBracket));
                    final String privmsg = msg.substring(rightBracket + 1, msg.length());

                    privmsgResponder.messageArrived(fromCode, privmsg, rgb);
                }
            }
        }

        // Just ignore, someone sent a badly formatted message
        catch (final StringIndexOutOfBoundsException e) {
            LOG.log(Level.SEVERE, "Failed to parse message. message=" + message + ", ipAddress=" + ipAddress, e);
        }

        // Just ignore, someone sent a badly formatted message
        catch (final NumberFormatException e) {
            LOG.log(Level.SEVERE, "Failed to parse message. message=" + message + ", ipAddress=" + ipAddress, e);
        }
    }
}
