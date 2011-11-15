
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

package net.usikkert.kouchat.ui;

import java.awt.GraphicsEnvironment;

import net.usikkert.kouchat.ui.console.KouChatConsole;
import net.usikkert.kouchat.ui.swing.KouChatFrame;

/**
 * This factory decides which User Interface to load.
 *
 * @author Christian Ihle
 */
public class UIFactory
{
	private boolean done;

	/**
	 * Loads the User Interface matching the ui argument.
	 *
	 * @param choice Which ui to load.
	 * Two choices are available at this moment: 'swing' and 'console'.
	 *
	 * @throws UIException If a ui has already been loaded, or if an
	 * unknown ui type was requested, or if no graphical environment was detected.
	 */
	public void loadUI( final UIChoice choice ) throws UIException
	{
		if ( done )
		{
			throw new UIException( "A User Interface has already been loaded." );
		}

		else
		{
			if ( choice == UIChoice.SWING )
			{
				if ( GraphicsEnvironment.isHeadless() )
				{
					throw new UIException( "The Swing User Interface could not be loaded"
							+ " because a graphical environment could not be detected." );
				}

				else
				{
					new KouChatFrame();
					done = true;
				}
			}

			else if ( choice == UIChoice.CONSOLE )
			{
				new KouChatConsole();
				done = true;
			}

			else
			{
				throw new UIException( "Unknown User Interface requested." );
			}
		}
	}
}
