
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import net.usikkert.kouchat.util.Validate;

/**
 * This class has a list of all the supported smileys.
 *
 * @author Christian Ihle
 */
public class SmileyMap
{
	/** The map linking the smiley code with the smiley image. */
	private final Map<String, ImageIcon> smileyMap;

	/**
	 * Constructor. Puts all the smileys in the map.
	 *
	 * @param imageLoader The image loader.
	 */
	public SmileyMap( final ImageLoader imageLoader )
	{
		Validate.notNull( imageLoader, "Image loader can not be null" );

		// Map smiley codes to icons
		smileyMap = new HashMap<String, ImageIcon>();
		smileyMap.put( ":)", imageLoader.getSmileIcon() );
		smileyMap.put( ":(", imageLoader.getSadIcon() );
		smileyMap.put( ":p", imageLoader.getTongueIcon() );
		smileyMap.put( ":D", imageLoader.getTeethIcon() );
		smileyMap.put( ";)", imageLoader.getWinkIcon() );
		smileyMap.put( ":O", imageLoader.getOmgIcon() );
		smileyMap.put( ":@", imageLoader.getAngryIcon() );
		smileyMap.put( ":S", imageLoader.getConfusedIcon() );
		smileyMap.put( ";(", imageLoader.getCryIcon() );
		smileyMap.put( ":$", imageLoader.getEmbarrassedIcon() );
		smileyMap.put( "8)", imageLoader.getShadeIcon() );
	}

	/**
	 * Gets the smiley with the specified key.
	 *
	 * @param key The key for the smiley to get.
	 * @return The smiley with the specified key.
	 */
	public ImageIcon getSmiley( final String key )
	{
		return smileyMap.get( key );
	}

	/**
	 * Gets a set of all the smiley codes.
	 *
	 * @return A set of all the smiley codes.
	 */
	public Set<String> getTextSmileys()
	{
		return smileyMap.keySet();
	}
}
