
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

/**
 * Used in the ui factory to notify the main class
 * if there is a problem with the loading of the ui.
 *
 * @author Christian Ihle
 */
public class UIException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a UIException with no message or cause.
	 */
	public UIException()
	{
		super();
	}

	/**
	 * Creates a UIException with the specified message and cause.
	 *
	 * @param message The exception message to use.
	 * @param cause The cause of the exception.
	 */
	public UIException( final String message, final Throwable cause )
	{
		super( message, cause );
	}

	/**
	 * Creates a UIException with the specified message.
	 *
	 * @param message The exception message to use.
	 */
	public UIException( final String message )
	{
		super( message );
	}

	/**
	 * Creates a UIException with the specified cause.
	 *
	 * @param cause The cause of the exception.
	 */
	public UIException( final Throwable cause )
	{
		super( cause );
	}
}
