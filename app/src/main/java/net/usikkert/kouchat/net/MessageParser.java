
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
 * This class listens for multicast messages from the network,
 * and parses them into a format the {@link MessageResponder} can use.
 *
 * <p>The supported message types:</p>
 *
 * <ul>
 *   <li>MSG</li>
 *   <li>LOGON</li>
 *   <li>EXPOSING</li>
 *   <li>LOGOFF</li>
 *   <li>AWAY</li>
 *   <li>BACK</li>
 *   <li>EXPOSE</li>
 *   <li>NICKCRASH</li>
 *   <li>WRITING</li>
 *   <li>STOPPEDWRITING</li>
 *   <li>GETTOPIC</li>
 *   <li>TOPIC</li>
 *   <li>NICK</li>
 *   <li>IDLE</li>
 *   <li>SENDFILEACCEPT</li>
 *   <li>SENDFILEABORT</li>
 *   <li>SENDFILE</li>
 *   <li>CLIENT</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class MessageParser implements ReceiverListener {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(MessageParser.class.getName());

    /** To handle the different kind of messages parsed here. */
    private final MessageResponder responder;

    /** The application settings. */
    private final Settings settings;

    /** If logged on to the chat or not. */
    private boolean loggedOn;

    /**
     * Constructor.
     *
     * @param responder To handle the different kind of messages parsed here.
     * @param settings The settings to use.
     */
    public MessageParser(final MessageResponder responder, final Settings settings) {
        Validate.notNull(responder, "MessageResponder can not be null");
        Validate.notNull(settings, "Settings can not be null");

        this.responder = responder;
        this.settings = settings;
    }

    /**
     * The parser. Checks what kind of message it is,
     * and then gives the correct data to the responder for
     * more processing.
     *
     * @param message The raw message to parse.
     * @param ipAddress The IP address of the user who sent the message.
     */
    @Override
    public void messageArrived(final String message, final String ipAddress) {
        try {
            final int exclamation = message.indexOf("!");
            final int hash = message.indexOf("#");
            final int colon = message.indexOf(":");

            final int msgCode = Integer.parseInt(message.substring(0, exclamation));
            final String type = message.substring(exclamation + 1, hash);
            final String msgNick = message.substring(hash + 1, colon);
            final String msg = message.substring(colon + 1, message.length());

            final User tempme = settings.getMe();

            if (msgCode != tempme.getCode() && loggedOn) {
                if (type.equals(MSG)) {
                    final int leftBracket = msg.indexOf("[");
                    final int rightBracket = msg.indexOf("]");
                    final int rgb = Integer.parseInt(msg.substring(leftBracket + 1, rightBracket));

                    responder.messageArrived(msgCode, msg.substring(rightBracket + 1, msg.length()), rgb);
                }

                else if (type.equals(LOGON)) {
                    final User newUser = new User(msgNick, msgCode);
                    newUser.setIpAddress(ipAddress);
                    newUser.setLastIdle(System.currentTimeMillis());
                    newUser.setLogonTime(System.currentTimeMillis());

                    responder.userLogOn(newUser);
                }

                else if (type.equals(EXPOSING)) {
                    final User user = new User(msgNick, msgCode);
                    user.setIpAddress(ipAddress);
                    user.setAwayMsg(msg);

                    if (msg.length() > 0) {
                        user.setAway(true);
                    }

                    user.setLastIdle(System.currentTimeMillis());
                    user.setLogonTime(System.currentTimeMillis());

                    responder.userExposing(user);
                }

                else if (type.equals(LOGOFF)) {
                    responder.userLogOff(msgCode);
                }

                else if (type.equals(AWAY)) {
                    responder.awayChanged(msgCode, true, msg);
                }

                else if (type.equals(BACK)) {
                    responder.awayChanged(msgCode, false, "");
                }

                else if (type.equals(EXPOSE)) {
                    responder.exposeRequested();
                }

                else if (type.equals(NICKCRASH)) {
                    if (tempme.getNick().equals(msg)) {
                        responder.nickCrash();
                    }
                }

                else if (type.equals(WRITING)) {
                    responder.writingChanged(msgCode, true);
                }

                else if (type.equals(STOPPEDWRITING)) {
                    responder.writingChanged(msgCode, false);
                }

                else if (type.equals(GETTOPIC)) {
                    responder.topicRequested();
                }

                else if (type.equals(TOPIC)) {
                    final int leftBracket = msg.indexOf("[");
                    final int rightBracket = msg.indexOf("]");
                    final int leftPara = msg.indexOf("(");
                    final int rightPara = msg.indexOf(")");

                    if (rightBracket != -1 && leftBracket != -1) {
                        final String theNick = msg.substring(leftPara + 1, rightPara);
                        final long theTime = Long.parseLong(msg.substring(leftBracket + 1, rightBracket));
                        String theTopic = null;

                        if (msg.length() > rightBracket + 1) {
                            theTopic = msg.substring(rightBracket + 1, msg.length());
                        }

                        responder.topicChanged(msgCode, theTopic, theNick, theTime);
                    }
                }

                else if (type.equals(NICK)) {
                    responder.nickChanged(msgCode, msgNick);
                }

                else if (type.equals(IDLE)) {
                    responder.userIdle(msgCode, ipAddress);
                }

                else if (type.equals(SENDFILEACCEPT)) {
                    final int leftPara = msg.indexOf("(");
                    final int rightPara = msg.indexOf(")");
                    final int fileCode = Integer.parseInt(msg.substring(leftPara + 1, rightPara));

                    if (fileCode == tempme.getCode()) {
                        final int leftCurly = msg.indexOf("{");
                        final int rightCurly = msg.indexOf("}");
                        final int leftBracket = msg.indexOf("[");
                        final int rightBracket = msg.indexOf("]");
                        final int port = Integer.parseInt(msg.substring(leftBracket + 1, rightBracket));
                        final int fileHash = Integer.parseInt(msg.substring(leftCurly + 1, rightCurly));
                        final String fileName = msg.substring(rightCurly + 1, msg.length());

                        responder.fileSendAccepted(msgCode, fileName, fileHash, port);
                    }
                }

                else if (type.equals(SENDFILEABORT)) {
                    final int leftPara = msg.indexOf("(");
                    final int rightPara = msg.indexOf(")");
                    final int fileCode = Integer.parseInt(msg.substring(leftPara + 1, rightPara));

                    if (fileCode == tempme.getCode()) {
                        final int leftCurly = msg.indexOf("{");
                        final int rightCurly = msg.indexOf("}");
                        final String fileName = msg.substring(rightCurly + 1, msg.length());
                        final int fileHash = Integer.parseInt(msg.substring(leftCurly + 1, rightCurly));

                        responder.fileSendAborted(msgCode, fileName, fileHash);
                    }
                }

                else if (type.equals(SENDFILE)) {
                    final int leftPara = msg.indexOf("(");
                    final int rightPara = msg.indexOf(")");
                    final int fileCode = Integer.parseInt(msg.substring(leftPara + 1, rightPara));

                    if (fileCode == tempme.getCode()) {
                        final int leftCurly = msg.indexOf("{");
                        final int rightCurly = msg.indexOf("}");
                        final int leftBracket = msg.indexOf("[");
                        final int rightBracket = msg.indexOf("]");
                        final long byteSize = Long.parseLong(msg.substring(leftBracket + 1, rightBracket));
                        final String fileName = msg.substring(rightCurly + 1, msg.length());
                        final int fileHash = Integer.parseInt(msg.substring(leftCurly + 1, rightCurly));

                        responder.fileSend(msgCode, byteSize, fileName, msgNick, fileHash);
                    }
                }

                else if (type.equals(CLIENT)) {
                    final int leftPara = msg.indexOf("(");
                    final int rightPara = msg.indexOf(")");
                    final int leftBracket = msg.indexOf("[");
                    final int rightBracket = msg.indexOf("]");
                    final int leftCurly = msg.indexOf("{");
                    final int rightCurly = msg.indexOf("}");
                    final int lessThan = msg.indexOf("<");
                    final int greaterThan = msg.indexOf(">");
                    final int slash = msg.indexOf("/");
                    final int backslash = msg.indexOf("\\");

                    final String client = msg.substring(leftPara + 1, rightPara);
                    final long timeSinceLogon = Long.parseLong(msg.substring(leftBracket + 1, rightBracket));
                    final String operatingSystem = msg.substring(leftCurly + 1, rightCurly);

                    int privateChatPort = 0;

                    try {
                        privateChatPort = Integer.parseInt(msg.substring(lessThan + 1, greaterThan));
                    }

                    catch (final NumberFormatException e) {
                        LOG.log(Level.WARNING, "Failed to parse private chat port. message=" + message + ", ipAddress=" + ipAddress, e);
                    }

                    int tcpChatPort = 0;

                    if (slash != -1 && backslash != -1) {
                        try {
                            tcpChatPort = Integer.parseInt(msg.substring(slash + 1, backslash));
                        }

                        catch (final NumberFormatException e) {
                            LOG.log(Level.WARNING, "Failed to parse tcp chat port. message=" + message + ", ipAddress=" + ipAddress, e);
                        }
                    }

                    responder.clientInfo(msgCode, client, timeSinceLogon, operatingSystem, privateChatPort, tcpChatPort);
                }
            }

            else if (msgCode == tempme.getCode() && type.equals(LOGON)) {
                responder.meLogOn(ipAddress);
                loggedOn = true;
            }

            else if (msgCode == tempme.getCode() && type.equals(IDLE) && loggedOn) {
                responder.meIdle(ipAddress);
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
