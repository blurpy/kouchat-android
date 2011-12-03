
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

package net.usikkert.kouchat.event;

/**
 * This is the listener interface used by the ErrorHandler.
 * The implementing class should be able to show the error message
 * to the user of the application.
 *
 * @author Christian Ihle
 */
public interface ErrorListener
{
	/**
	 * This method is called when an error occurs.
	 *
	 * @param errorMsg The message to show.
	 */
	void errorReported( String errorMsg );

	/**
	 * This method is called when a critical error occurs.
	 *
	 * @param criticalErrorMsg The message to show.
	 */
	void criticalErrorReported( String criticalErrorMsg );
}
