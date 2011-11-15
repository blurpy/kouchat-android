
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.SettingsListener;
import net.usikkert.kouchat.util.Tools;

/**
 * This is a simple logger. Creates a new unique log file for each time
 * KouChat is started.
 *
 * @author Christian Ihle
 */
public class ChatLogger implements SettingsListener
{
	/**
	 * The name of the log file. Uses date, time, and milliseconds to make sure
	 * it is unique.
	 */
	private static final String LOG_FILE = "kouchat-" + Tools.dateToString( null, "yyyy.MM.dd-HH.mm.ss-SSS" ) + ".log";

	/** The logger. */
	private static final Logger LOG = Logger.getLogger( ChatLogger.class.getName() );

	private final Settings settings;
	private final ErrorHandler errorHandler;
	private BufferedWriter writer;
	private boolean open;

	/**
	 * Default constructor. Adds a shutdown hook to make sure the log file
	 * is closed on shutdown.
	 */
	public ChatLogger()
	{
		settings = Settings.getSettings();
		settings.addSettingsListener( this );

		errorHandler = ErrorHandler.getErrorHandler();

		if ( settings.isLogging() )
		{
			open();
		}

		Runtime.getRuntime().addShutdownHook( new Thread( "ChatLoggerShutdownHook" )
		{
			@Override
			public void run()
			{
				close();
			}
		} );
	}

	/**
	 * Opens the log file for writing.
	 * Will append if the log file already exists.
	 */
	public void open()
	{
		close();

		try
		{
			File logdir = new File( Constants.APP_LOG_FOLDER );

			if ( !logdir.exists() )
				logdir.mkdirs();

			writer = new BufferedWriter( new FileWriter( Constants.APP_LOG_FOLDER + LOG_FILE, true ) );
			open = true;
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
			settings.setLogging( false );
			errorHandler.showError( "Could not initialize the logging:\n" + e );
		}
	}

	/**
	 * Flushed and closes the current open log file.
	 */
	public void close()
	{
		if ( open )
		{
			try
			{
				writer.flush();
				writer.close();
			}

			catch ( final IOException e )
			{
				LOG.log( Level.SEVERE, e.toString(), e );
			}

			finally
			{
				open = false;
			}
		}
	}

	/**
	 * Adds a new line of text to the current open log file, if any.
	 *
	 * @param line The line of text to add to the log.
	 */
	public void append( final String line )
	{
		if ( open )
		{
			try
			{
				writer.append( line );
				writer.newLine();
				writer.flush();
			}

			catch ( final IOException e )
			{
				LOG.log( Level.SEVERE, e.toString(), e );
				close();
			}
		}
	}

	/**
	 * Returns if a log file is opened for writing or not.
	 *
	 * @return True if a log file is open.
	 */
	public boolean isOpen()
	{
		return open;
	}

	/**
	 * Opens or closes the log file when the logging setting is changed.
	 *
	 * @param setting The setting that was changed.
	 */
	@Override
	public void settingChanged( final String setting )
	{
		if ( setting.equals( "logging" ) )
		{
			if ( settings.isLogging() )
			{
				if ( !isOpen() )
				{
					open();
				}
			}

			else
			{
				close();
			}
		}
	}
}
