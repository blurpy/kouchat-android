
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.util.Validate;

/**
 * Opens a text file in a 80x24 character dialog window.
 *
 * @author Christian Ihle
 */
public class TextViewerDialog extends JDialog
{
	private static final Logger LOG = Logger.getLogger( TextViewerDialog.class.getName() );
	private static final long serialVersionUID = 1L;

	private final ErrorHandler errorHandler;
	private final JTextPane viewerTP;
	private final JScrollPane viewerScroll;
	private final MutableAttributeSet viewerAttr;
	private final StyledDocument viewerDoc;
	private final String textFile;
	private boolean fileOpened;

	/**
	 * Constructor.
	 *
	 * Creates the dialog window, and opens the file.
	 *
	 * @param textFile The text file to open and view.
	 * @param title The title to use for the dialog window.
	 * @param links True to enabled support for opening urls by clicking on them.
	 * @param imageLoader The image loader.
	 */
	public TextViewerDialog( final String textFile, final String title, final boolean links, final ImageLoader imageLoader )
	{
		Validate.notNull( textFile, "Text file can not be null" );
		Validate.notNull( title, "Title can not be null" );
		Validate.notNull( imageLoader, "Image loader can not be null" );

		this.textFile = textFile;
		errorHandler = ErrorHandler.getErrorHandler();

		viewerTP = new JTextPane();
		viewerTP.setFont( new Font( "Monospaced", Font.PLAIN, viewerTP.getFont().getSize() ) );
		viewerTP.setEditable( false );
		viewerDoc = viewerTP.getStyledDocument();

		// Enables the url support
		if ( links )
		{
			URLMouseListener urlML = new URLMouseListener( viewerTP );
			viewerTP.addMouseListener( urlML );
			viewerTP.addMouseMotionListener( urlML );
			AbstractDocument doc = (AbstractDocument) viewerDoc;
			doc.setDocumentFilter( new URLDocumentFilter( true ) );
		}

		new CopyPopup( viewerTP );
		viewerAttr = new SimpleAttributeSet();
		viewerScroll = new JScrollPane( viewerTP );
		viewerScroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

		JPanel panel = new JPanel( new BorderLayout() );
		panel.setBorder( BorderFactory.createEmptyBorder( 5, 4, 4, 4 ) );
		panel.add( viewerScroll, BorderLayout.CENTER );
		add( panel, BorderLayout.CENTER );

		// To get 80 columns and 24 rows
		FontMetrics fm = viewerTP.getFontMetrics( viewerTP.getFont() );
		int width = fm.charWidth( '_' ) * 80;
		int height = fm.getHeight() * 24;
		viewerTP.setPreferredSize( new Dimension( width, height ) );

		setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
		setTitle( UITools.createTitle( title ) );
		setIconImage( imageLoader.getAppIcon().getImage() );
		setResizable( false );
		readFile();
		pack();
	}

	/**
	 * Shows the window if the text file was opened, and scrolls to the
	 * beginning of the text.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void setVisible( final boolean visible )
	{
		if ( fileOpened )
		{
			if ( visible )
			{
				try
				{
					Rectangle r = viewerTP.modelToView( 0 );
					viewerScroll.getViewport().setViewPosition( new Point( r.x, r.y ) );
				}

				catch ( final BadLocationException e )
				{
					LOG.log( Level.SEVERE, e.toString() );
				}
			}

			super.setVisible( visible );
		}

		else
		{
			errorHandler.showError( "The file " + textFile + " could not be opened." );
		}
	}

	/**
	 * Reads the text file, and adds the contents to the text area.
	 */
	private void readFile()
	{
		URL fileURL = getClass().getResource( "/" + textFile );

		if ( fileURL != null )
		{
			BufferedReader reader = null;

			try
			{
				reader = new BufferedReader( new InputStreamReader( fileURL.openStream() ) );

				while ( reader.ready() )
				{
					viewerDoc.insertString( viewerDoc.getLength(), reader.readLine() + "\n", viewerAttr );
				}

				viewerTP.setCaretPosition( 0 );
				fileOpened = true;
			}

			catch ( final IOException e )
			{
				LOG.log( Level.SEVERE, e.toString() );
			}

			catch ( final BadLocationException e )
			{
				LOG.log( Level.SEVERE, e.toString() );
			}

			finally
			{
				if ( reader != null )
				{
					try
					{
						reader.close();
					}

					catch ( final IOException e )
					{
						LOG.log( Level.WARNING, "Problems closing: " + textFile );
					}
				}
			}
		}

		else
		{
			LOG.log( Level.SEVERE, "Text file not found: " + textFile );
		}
	}
}
