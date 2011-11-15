
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

package net.usikkert.kouchat;

import net.usikkert.kouchat.ui.UIChoice;
import net.usikkert.kouchat.ui.UIException;
import net.usikkert.kouchat.ui.UIFactory;
import net.usikkert.kouchat.util.LogInitializer;
import net.usikkert.kouchat.util.UncaughtExceptionLogger;

/**
 * This class contains KouChat's main method.
 *
 * It prints out some information at the console, and
 * parses the arguments, if any.
 *
 * Two different User Interfaces can be loaded from here.
 * Swing is the default, and a console version can be loaded
 * by using the --console argument.
 *
 * @author Christian Ihle
 */
public final class KouChat
{
	/**
	 * Private constructor. This class should be run like an application,
	 * not instantiated.
	 */
	private KouChat()
	{

	}

	/**
	 * The main method, for starting the application.
	 *
	 * <p>Takes the following options:</p>
	 * <ul>
	 *   <li>-c, --console - starts KouChat in console mode.</li>
	 *   <li>-d, --debug - starts KouChat with verbose debug output enabled.</li>
	 *   <li>-h, --help - shows information about available options.</li>
	 *   <li>-v, --version - shows version information.</li>
	 * </ul>
	 *
	 * @param options The options given when starting KouChat.
	 */
	public static void main( final String[] options )
	{
		System.out.println( Constants.APP_NAME + " v" + Constants.APP_VERSION );
		System.out.println( "By " + Constants.AUTHOR_NAME + " - " + Constants.AUTHOR_MAIL + " - " + Constants.APP_WEB );

		if ( options.length == 0 )
			System.out.println( "Use --help for more information" );

		boolean swing = true;
		boolean help = false;
		boolean debug = false;
		boolean version = false;

		for ( String option : options )
		{
			if ( option.equals( "--console" ) || option.equals( "-c" ) )
				swing = false;

			else if ( option.equals( "--help" ) || option.equals( "-h" ) )
				help = true;

			else if ( option.equals( "--debug" ) || option.equals( "-d" ) )
				debug = true;

			else if ( option.equals( "--version" ) || option.equals( "-v" ) )
				version = true;

			else
			{
				System.out.println( "\nUnknown option '" + option + "'. Use --help for more information" );
				return;
			}
		}

		if ( version )
			return;

		if ( help )
		{
			System.out.println( "\nOptions:"
					+ "\n -c, --console \tstarts " + Constants.APP_NAME + " in console mode"
					+ "\n -d, --debug \tstarts " + Constants.APP_NAME + " with verbose debug output enabled"
					+ "\n -h, --help \tshows this help message"
					+ "\n -v, --version \tshows version information" );
			return;
		}

		new LogInitializer( debug );
		// Initialize as early as possible to catch all exceptions
		new UncaughtExceptionLogger();

		try
		{
			if ( swing )
			{
				System.out.println( "\nLoading Swing User Interface\n" );
				new UIFactory().loadUI( UIChoice.SWING );
			}

			else
			{
				System.out.println( "\nLoading Console User Interface\n" );
				new UIFactory().loadUI( UIChoice.CONSOLE );
			}
		}

		catch ( final UIException e )
		{
			System.err.println( e );
		}
	}
}
