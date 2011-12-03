
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

package net.usikkert.kouchat.net;

import java.io.File;

import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.User;

/**
 * This is the interface for both sending and receiving file transfers
 * between users.
 *
 * <p>Useful for the user interface, as it doesn't need to know what kind of
 * file transfer it is showing progress information about.</p>
 *
 * @author Christian Ihle
 */
public interface FileTransfer
{
	/**
	 * Enum to describe if a file is being sent or received.
	 */
	public enum Direction
	{
		SEND,
		RECEIVE
	};

	/**
	 * Gets if the file transfer is sending or receiving.
	 *
	 * @return The direction of the file transfer.
	 */
	Direction getDirection();

	/**
	 * The other user, which sends or receives a file.
	 *
	 * @return The other user.
	 */
	User getUser();

	/**
	 * The percent of the file transfer that is completed.
	 *
	 * @return Percent completed.
	 */
	int getPercent();

	/**
	 * Number of bytes transferred.
	 *
	 * @return Bytes transferred.
	 */
	long getTransferred();

	/**
	 * Gets the file that is being transferred.
	 *
	 * @return The file.
	 */
	File getFile();

	/**
	 * Gets the size of the file being transferred, in bytes.
	 *
	 * @return The file size.
	 */
	long getFileSize();

	/**
	 * Gets the number of bytes transferred per second.
	 *
	 * @return The speed in bytes per second.
	 */
	long getSpeed();

	/**
	 * Cancels the file transfer.
	 */
	void cancel();

	/**
	 * Checks if the file transfer has been canceled.
	 *
	 * @return If the file transfer has been canceled.
	 */
	boolean isCanceled();

	/**
	 * Checks if the file transfer is complete.
	 *
	 * @return If the file transfer is complete.
	 */
	boolean isTransferred();

	/**
	 * Registers a file transfer listener, which will receive updates
	 * when certain events happen in the progression of the file transfer.
	 *
	 * @param listener The listener to register.
	 */
	void registerListener( FileTransferListener listener );
}
