
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

import java.util.ArrayList;
import java.util.List;

import net.usikkert.kouchat.misc.User;

/**
 * This class keeps a list of all the ongoing file transfers.
 *
 * @author Christian Ihle
 */
public class TransferList
{
	/** The list of all the file senders. */
	private final List<FileSender> senders;

	/** The list of all the file receivers. */
	private final List<FileReceiver> receivers;

	/**
	 * Constructor.
	 */
	public TransferList()
	{
		senders = new ArrayList<FileSender>();
		receivers = new ArrayList<FileReceiver>();
	}

	/**
	 * Adds a new file sender to the list.
	 *
	 * @param fileSender The file sender to add.
	 */
	public void addFileSender( final FileSender fileSender )
	{
		senders.add( fileSender );
	}

	/**
	 * Removes a file sender from the list.
	 *
	 * @param fileSender The file sender to remove.
	 */
	public void removeFileSender( final FileSender fileSender )
	{
		senders.remove( fileSender );
	}

	/**
	 * Gets the file sender object for the specified user and file.
	 *
	 * @param user The file sending user.
	 * @param fileName The name of the file being sent.
	 * @param fileHash The file's hash code.
	 * @return The file sender object, or <code>null</code> if none was found.
	 */
	public FileSender getFileSender( final User user, final String fileName, final int fileHash )
	{
		FileSender fileSender = null;

		for ( FileSender fs : senders )
		{
			if ( fs.getUser() == user && fs.getFile().getName().equals( fileName ) && fs.getFile().hashCode() == fileHash )
			{
				fileSender = fs;
				break;
			}
		}

		return fileSender;
	}

	/**
	 * Gets the file sender object for the specified user and file.
	 *
	 * @param user The file sending user.
	 * @param fileName The name of the file being sent.
	 * @return The file sender object, or <code>null</code> if none was found.
	 */
	public FileSender getFileSender( final User user, final String fileName )
	{
		FileSender fileSender = null;

		for ( FileSender fs : senders )
		{
			if ( fs.getUser() == user && fs.getFile().getName().equals( fileName ) )
			{
				fileSender = fs;
				break;
			}
		}

		return fileSender;
	}

	/**
	 * Gets all the file sender objects for a given user.
	 *
	 * @param user The given user.
	 * @return A list of all the file senders for the user.
	 */
	public List<FileSender> getFileSenders( final User user )
	{
		List<FileSender> list = new ArrayList<FileSender>();

		for ( FileSender fs : senders )
		{
			if ( fs.getUser() == user )
			{
				list.add( fs );
			}
		}

		return list;
	}

	/**
	 * Gets all the file sender objects for all the users.
	 *
	 * @return A list of all the file senders.
	 */
	public List<FileSender> getFileSenders()
	{
		List<FileSender> list = new ArrayList<FileSender>();

		for ( FileSender fs : senders )
		{
			list.add( fs );
		}

		return list;
	}

	/**
	 * Adds a new file receiver to the list.
	 *
	 * @param fileReceiver The file receiver to add.
	 */
	public void addFileReceiver( final FileReceiver fileReceiver )
	{
		receivers.add( fileReceiver );
	}

	/**
	 * Removes a file receiver from the list.
	 *
	 * @param fileReceiver The file receiver to remove.
	 */
	public void removeFileReceiver( final FileReceiver fileReceiver )
	{
		receivers.remove( fileReceiver );
	}

	/**
	 * Gets all the file receiver objects for a given user.
	 *
	 * @param user The given user.
	 * @return A list of all the file receivers for the user.
	 */
	public List<FileReceiver> getFileReceivers( final User user )
	{
		List<FileReceiver> list = new ArrayList<FileReceiver>();

		for ( FileReceiver fr : receivers )
		{
			if ( fr.getUser() == user )
			{
				list.add( fr );
			}
		}

		return list;
	}

	/**
	 * Gets the file receiver object for the specified user and file.
	 *
	 * @param user The file receiver user.
	 * @param fileName The name of the file being received.
	 * @return The file receiver object, or <code>null</code> if none was found.
	 */
	public FileReceiver getFileReceiver( final User user, final String fileName )
	{
		FileReceiver fileReceiver = null;

		for ( FileReceiver fr : receivers )
		{
			if ( fr.getUser() == user && fr.getFile().getName().equals( fileName ) )
			{
				fileReceiver = fr;
				break;
			}
		}

		return fileReceiver;
	}

	/**
	 * Gets all the file receiver objects for all the users.
	 *
	 * @return A list of all the file receivers.
	 */
	public List<FileReceiver> getFileReceivers()
	{
		List<FileReceiver> list = new ArrayList<FileReceiver>();

		for ( FileReceiver fr : receivers )
		{
			list.add( fr );
		}

		return list;
	}

	/**
	 * Gets a file transfer object for the given user and file.
	 *
	 * @param user The user to find a file transfer for.
	 * @param fileName The filename to look for.
	 * @return Either a file receiver, a file sender, or <code>null</code>
	 * if none of them was found.
	 */
	public FileTransfer getFileTransfer( final User user, final String fileName )
	{
		FileReceiver fileReceiver = getFileReceiver( user, fileName );

		if ( fileReceiver != null )
			return fileReceiver;
		else
			return getFileSender( user, fileName );
	}
}
