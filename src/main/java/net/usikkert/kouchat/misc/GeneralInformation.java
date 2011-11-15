
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

package net.usikkert.kouchat.misc;

/**
 * This is a JMX MBean for general information.
 *
 * @author Christian Ihle
 */
public class GeneralInformation implements GeneralInformationMBean
{
	/** {@inheritDoc} */
	@Override
	public String about()
	{
		User me = Settings.getSettings().getMe();
		StringBuilder sb = new StringBuilder();

		sb.append( "Client: " + me.getClient() + "\n" );
		sb.append( "User name: " + me.getNick() + "\n" );
		sb.append( "IP address: " + me.getIpAddress() + "\n" );
		sb.append( "Host name: " + me.getHostName() + "\n" );
		sb.append( "Operating System: " + me.getOperatingSystem() );

		return sb.toString();
	}
}
