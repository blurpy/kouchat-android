
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
 * All the supported types of network messages.
 *
 * @author Christian Ihle
 */
public interface NetworkMessageType {

    String MSG = "MSG";
    String LOGON = "LOGON";
    String EXPOSING = "EXPOSING";
    String LOGOFF = "LOGOFF";
    String AWAY = "AWAY";
    String BACK = "BACK";
    String EXPOSE = "EXPOSE";
    String NICKCRASH = "NICKCRASH";
    String WRITING = "WRITING";
    String STOPPEDWRITING = "STOPPEDWRITING";
    String GETTOPIC = "GETTOPIC";
    String TOPIC = "TOPIC";
    String NICK = "NICK";
    String IDLE = "IDLE";
    String SENDFILEACCEPT = "SENDFILEACCEPT";
    String SENDFILEABORT = "SENDFILEABORT";
    String SENDFILE = "SENDFILE";
    String CLIENT = "CLIENT";
    String PRIVMSG = "PRIVMSG";
}
