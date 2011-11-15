
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

package net.usikkert.kouchat.ui.swing;

import java.awt.Image;

import net.usikkert.kouchat.util.Validate;

/**
 * Loads the different icons to use when the application is
 * in different states.
 *
 * @author Christian Ihle
 */
public class StatusIcons
{
	/** User is not away, and has no new messages. */
	private final Image normalIcon;

	/** User is not away, but has new messages. */
	private final Image normalActivityIcon;

	/** User is away, but has no new messages. */
	private final Image awayIcon;

	/** User is away, and has new messages. */
	private final Image awayActivityIcon;

	/**
	 * Constructor. Loads the icons.
	 *
	 * @param imageLoader The image loader.
	 */
	public StatusIcons( final ImageLoader imageLoader )
	{
		Validate.notNull( imageLoader, "Image loader can not be null" );

		normalIcon = imageLoader.getKouNormalIcon().getImage();
		normalActivityIcon = imageLoader.getKouNormalActivityIcon().getImage();
		awayIcon = imageLoader.getKouAwayIcon().getImage();
		awayActivityIcon = imageLoader.getKouAwayActivityIcon().getImage();
	}

	/**
	 * Gets the normal icon.
	 *
	 * @return The normal icon.
	 */
	public Image getNormalIcon()
	{
		return normalIcon;
	}

	/**
	 * Gets the normal activity icon.
	 *
	 * @return The normal activity icon.
	 */
	public Image getNormalActivityIcon()
	{
		return normalActivityIcon;
	}

	/**
	 * Gets the away icon.
	 *
	 * @return The away icon.
	 */
	public Image getAwayIcon()
	{
		return awayIcon;
	}

	/**
	 * Gets the away activity icon.
	 *
	 * @return The away activity icon.
	 */
	public Image getAwayActivityIcon()
	{
		return awayActivityIcon;
	}
}
