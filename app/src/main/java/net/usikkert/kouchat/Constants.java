
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

package net.usikkert.kouchat;

/**
 * This interface contains constants used globally in the application.
 *
 * @author Christian Ihle
 */
public interface Constants {

    /**
     * The name of the application.
     */
    String APP_NAME = "KouChat";

    /**
     * The application version.
     */
    String APP_VERSION = "1.5.0";

    /**
     * Which license the application has.
     */
    String APP_LICENSE_NAME = "GNU LGPL v3";

    /**
     * The home page of this application.
     */
    String APP_WEB = "https://www.kouchat.net/";

    /**
     * Copyright from year, to year.
     */
    String APP_COPYRIGHT_YEARS = "2006-2019";

    /**
     * Name of the author of this application.
     */
    String AUTHOR_NAME = "Christian Ihle";

    /**
     * The email address of the author.
     */
    String AUTHOR_MAIL = "contact@kouchat.net";

    /**
     * The multicast udp port used for sending and receiving
     * packets for the main chat.
     */
    int NETWORK_CHAT_PORT = 40556;

    /**
     * The normal udp port used for sending and receiving
     * packets for private chats. This is only the starting port.
     * If it is already in use, port +1 is tried, and so on.
     */
    int NETWORK_PRIVCHAT_PORT = 40656;

    /**
     * The tcp port used for sending and receiving
     * packets for both main and private chats. This is only the starting port.
     * If it is already in use, port +1 is tried, and so on.
     */
    int NETWORK_TCP_CHAT_PORT = 40656;

    /**
     * The temporary multicast udp port used for sending and
     * receiving packets.
     */
    int NETWORK_TEMP_PORT = 50050;

    /**
     * The tcp port used for receiving file transfers.
     * This is only the starting port.
     * If it is already in use, port +1 is tried, and so on.
     */
    int NETWORK_FILE_TRANSFER_PORT = 40756;

    /**
     * The size of the udp packets sent from normal and
     * private chats.
     */
    int NETWORK_PACKET_SIZE = 512;

    /**
     * The multicast address used for sending and receiving
     * packets for the main chat.
     */
    String NETWORK_IP = "224.168.5.200";

    /**
     * The temporary multicast address used for sending and
     * receiving packets.
     */
    String NETWORK_TEMP_IP = "224.168.5.250";

    /**
     * The character set used for messages.
     */
    String MESSAGE_CHARSET = "UTF-8";

    /**
     * Max number of bytes allowed in a message to send
     * over a udp connection.
     */
    int MESSAGE_MAX_BYTES = 450;

    /**
     * The folder where the application can save files.
     */
    String APP_FOLDER = System.getProperty("user.home") +
            System.getProperty("file.separator") + "." + APP_NAME.toLowerCase() +
            System.getProperty("file.separator");

    /**
     * The folder where log files are stored.
     */
    String APP_LOG_FOLDER = APP_FOLDER + "logs" + System.getProperty("file.separator");

    /**
     * Which file to find the license text.
     */
    String FILE_LICENSE = "COPYING";

    /**
     * Which file to find the frequently asked questions.
     */
    String FILE_FAQ = "FAQ";

    /**
     * Which file to find the tips & tricks.
     */
    String FILE_TIPS = "TipsAndTricks.txt";
}
