
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

/**
 * This takes care of drag and drop of files to send.
 * When a file is dropped the mediator opens the file.
 *
 * @author Christian Ihle
 */
public class FileTransferHandler extends TransferHandler
{
	/** The logger. */
	private static final Logger LOG = Logger.getLogger( FileTransferHandler.class.getName() );

	/** Standard serial version UID. */
	private static final long serialVersionUID = 1L;

	/** The object where the file gets dropped. */
	private final FileDropSource fileDropSource;

	/** The mediator. */
	private Mediator mediator;

	/** Mime type for uri-list, which is how dropped files are recognized in linux. */
	private DataFlavor uriListFlavor;

	/**
	 * Constructor. Sets the file drop source.
	 *
	 * @param fileDropSource The source to find which user the file was dropped on.
	 */
	public FileTransferHandler( final FileDropSource fileDropSource )
	{
		this.fileDropSource = fileDropSource;

		try
		{
			uriListFlavor = new DataFlavor( "text/uri-list;class=java.lang.String" );
		}

		catch ( final ClassNotFoundException e )
		{
			LOG.log( Level.WARNING, e.toString() );
		}
	}

	/**
	 * Sets the mediator to use for opening the dropped file.
	 *
	 * @param mediator The mediator to use.
	 */
	public void setMediator( final Mediator mediator )
	{
		this.mediator = mediator;
	}

	/**
	 * Checks to see if the dropped data is a URI list or a file list.
	 * Returns false if the data is of any other type.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public boolean canImport( final TransferSupport support )
	{
		return support.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) || support.isDataFlavorSupported( uriListFlavor );
	}

	/**
	 * Double checks to see if the data is of the correct type,
	 * and then tries to create a file object to send to the mediator.
	 * Supports both Linux and Windows file lists.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public boolean importData( final TransferSupport support )
	{
		if ( canImport( support ) )
		{
			try
			{
				File file = null;

				if ( support.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) )
				{
					@SuppressWarnings( "unchecked" )
					List<File> fileList = (List<File>) support.getTransferable().getTransferData( DataFlavor.javaFileListFlavor );

					if ( fileList.size() > 0 )
						file = fileList.get( 0 );
				}

				else if ( support.isDataFlavorSupported( uriListFlavor ) )
				{
					Object data = support.getTransferable().getTransferData( uriListFlavor );

					if ( data != null )
					{
						String[] uriList = data.toString().split( "\r\n" );
						String fileURI = "";

						for ( int i = 0; i < uriList.length; i++ )
						{
							if ( uriList[i].startsWith( "file:/" ) )
							{
								fileURI = uriList[i];
								break;
							}
						}

						try
						{
							URI uri = new URI( fileURI );

							if ( uri != null )
								file = new File( uri );
						}

						catch ( final URISyntaxException e )
						{
							LOG.log( Level.WARNING, e.toString() );
						}
					}
				}

				else
				{
					LOG.log( Level.WARNING, "Data flavor not supported." );
				}

				if ( file != null )
				{
					mediator.sendFile( fileDropSource.getUser(), file );
					return true;
				}

				else
					LOG.log( Level.WARNING, "No file dropped." );
			}

			catch ( final UnsupportedFlavorException e )
			{
				LOG.log( Level.WARNING, e.toString() );
			}

			catch ( final IOException e )
			{
				LOG.log( Level.WARNING, e.toString() );
			}
		}

		return false;
	}

	/**
	 * Adds (back) support for copying the contents of the component
	 * this transfer handler is registered on.
	 *
	 * {@inheritDoc}
	 */
	@Override
	protected Transferable createTransferable( final JComponent c )
	{
		if ( c instanceof JTextComponent )
		{
			String data = ( (JTextComponent) c ).getSelectedText();
			return new StringSelection( data );
		}

		else if ( c instanceof JList )
		{
			String data = ( (JList) c ).getSelectedValue().toString();
			return new StringSelection( data );
		}

		else
			return null;
	}

	/**
	 * To enable copy to clipboard.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public int getSourceActions( final JComponent c )
	{
		return COPY;
	}
}
