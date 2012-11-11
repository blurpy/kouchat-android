
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

package net.usikkert.kouchat.misc;

/**
 * This is a JMX MBean for general information.
 *
 * @author Christian Ihle
 */
public class GeneralInformation implements GeneralInformationMBean {

    /** {@inheritDoc} */
    @Override
    public String about() {
        final User me = Settings.getSettings().getMe();
        final StringBuilder sb = new StringBuilder();

        sb.append("Client: " + me.getClient() + "\n");
        sb.append("User name: " + me.getNick() + "\n");
        sb.append("IP address: " + me.getIpAddress() + "\n");
        sb.append("Host name: " + me.getHostName() + "\n");
        sb.append("Operating System: " + me.getOperatingSystem());

        return sb.toString();
    }
}
