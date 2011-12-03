
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

package net.usikkert.kouchat.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class will register itself as the default uncaught
 * exception handler, and log any uncaught exceptions.
 *
 * <p>If any listeners are registered, they will be notified
 * when the logging is done.</p>
 *
 * @author Christian Ihle
 */
public class UncaughtExceptionLogger implements Thread.UncaughtExceptionHandler
{
	/** The logger. */
	private static final Logger LOG = Logger.getLogger( UncaughtExceptionLogger.class.getName() );

	/** The listeners being notified of uncaught exceptions. */
	private final Collection<UncaughtExceptionListener> listeners;

	/**
	 * Default constructor. Registers this class as the
	 * default uncaught exception handler.
	 */
	public UncaughtExceptionLogger()
	{
		Thread.setDefaultUncaughtExceptionHandler( this );
		listeners = new ArrayList<UncaughtExceptionListener>();
	}

	/**
	 * Logs the exception with information about which thread
	 * the exception happened in, and notifies listeners.
	 *
	 * @param thread The thread that got the exception.
	 * @param throwable The exception the thread got.
	 */
	@Override
	public void uncaughtException( final Thread thread, final Throwable throwable )
	{
		LOG.log( Level.SEVERE, "UncaughtException in thread: " + thread.getName()
				+ " (id " + thread.getId() + ", priority " + thread.getPriority() + ")", throwable );

		for ( UncaughtExceptionListener listener : listeners )
		{
			listener.uncaughtException( thread, throwable );
		}
	}

	/**
	 * Registers a new uncaught exceptions listener.
	 *
	 * @param listener The listener to register.
	 */
	public void registerUncaughtExceptionListener( final UncaughtExceptionListener listener )
	{
		listeners.add( listener );
	}
}
