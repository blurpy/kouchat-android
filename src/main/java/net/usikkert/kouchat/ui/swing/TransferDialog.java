
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * This is the dialog window for file transfers in the swing user interface.
 *
 * @author Christian Ihle
 */
public class TransferDialog extends JDialog implements FileTransferListener, ActionListener
{
	/** Standard serial version UID. */
	private static final long serialVersionUID = 1L;

	/** Button to cancel file transfer, or close the dialog when transfer is stopped. */
	private final JButton cancelB;

	/** Button to open the folder where the received file was saved. */
	private final JButton openB;

	/** Label for the file transfer status. */
	private final JLabel statusL;

	/** Label for the sender of the file. */
	private final JLabel sourceL;

	/** Label for the receiver of the file. */
	private final JLabel destinationL;

	/** Label for the file name. */
	private final JLabel filenameL;

	/** Label for transfer information. */
	private final JLabel transferredL;

	/** Progress bar to show file transfer progress in percent complete. */
	private final JProgressBar transferProgressPB;

	/** The file transfer object this dialog is showing the state of. */
	private final FileTransfer fileTransfer;

	/** The mediator. */
	private final Mediator mediator;

	/**
	 * Constructor. Initializes components and registers this dialog
	 * as a listener on the file transfer object.
	 *
	 * @param mediator The mediator.
	 * @param fileTransfer The file transfer object this dialog is showing the state of.
	 * @param imageLoader The image loader.
	 */
	public TransferDialog( final Mediator mediator, final FileTransfer fileTransfer, final ImageLoader imageLoader )
	{
		Validate.notNull( mediator, "Mediator can not be null" );
		Validate.notNull( fileTransfer, "File transfer can not be null" );
		Validate.notNull( imageLoader, "Image loader can not be null" );

		this.mediator = mediator;
		this.fileTransfer = fileTransfer;

		cancelB = new JButton( "Cancel" );
		cancelB.addActionListener( this );

		openB = new JButton( "Open folder" );
		openB.addActionListener( this );
		openB.setVisible( false );
		openB.setEnabled( false );

		transferProgressPB = new JProgressBar( 0, 100 );
		transferProgressPB.setStringPainted( true );
        transferProgressPB.setPreferredSize( new Dimension( 410, 25 ) );

        JLabel transferredHeaderL = new JLabel( "Transferred:" );
		int headerHeight = transferredHeaderL.getPreferredSize().height;
		int headerWidth = transferredHeaderL.getPreferredSize().width + 8;
		transferredHeaderL.setPreferredSize( new Dimension( headerWidth, headerHeight ) );
		transferredL = new JLabel( "0KB of 0KB at 0KB/s" );

		JLabel filenameHeaderL = new JLabel( "Filename:" );
		filenameHeaderL.setPreferredSize( new Dimension( headerWidth, headerHeight ) );
		filenameL = new JLabel( "(No file)" );
		filenameL.setPreferredSize( new Dimension( 410 - headerWidth, headerHeight ) );

		JLabel statusHeaderL = new JLabel( "Status:" );
		statusHeaderL.setPreferredSize( new Dimension( headerWidth, headerHeight ) );
		statusL = new JLabel( "Waiting..." );

		JLabel sourceHeaderL = new JLabel( "Source:" );
		sourceHeaderL.setPreferredSize( new Dimension( headerWidth, headerHeight ) );
		sourceL = new JLabel( "Source (No IP)" );

		JLabel destinationHeaderL = new JLabel( "Destination:" );
		destinationHeaderL.setPreferredSize( new Dimension( headerWidth, headerHeight ) );
		destinationL = new JLabel( "Destination (No IP)" );

		JPanel topP = new JPanel();
		topP.setBorder( BorderFactory.createEmptyBorder( 4, 8, 4, 8 ) );
        topP.setLayout( new BoxLayout( topP, BoxLayout.PAGE_AXIS ) );

        JPanel bottomP = new JPanel();
        bottomP.setBorder( BorderFactory.createEmptyBorder( 4, 8, 8, 8 ) );
        bottomP.setLayout( new BoxLayout( bottomP, BoxLayout.LINE_AXIS ) );

        JPanel statusP = new JPanel();
        statusP.setLayout( new BoxLayout( statusP, BoxLayout.LINE_AXIS ) );
        statusP.setBorder( BorderFactory.createEmptyBorder( 2, 0, 2, 0 ) );
        statusP.add( statusHeaderL );
        statusP.add( statusL );
        statusP.add( Box.createHorizontalGlue() );

        JPanel sourceP = new JPanel();
        sourceP.setLayout( new BoxLayout( sourceP, BoxLayout.LINE_AXIS ) );
        sourceP.setBorder( BorderFactory.createEmptyBorder( 4, 0, 2, 0 ) );
        sourceP.add( sourceHeaderL );
        sourceP.add( sourceL );
        sourceP.add( Box.createHorizontalGlue() );

        JPanel destP = new JPanel();
        destP.setLayout( new BoxLayout( destP, BoxLayout.LINE_AXIS ) );
        destP.setBorder( BorderFactory.createEmptyBorder( 4, 0, 2, 0 ) );
        destP.add( destinationHeaderL );
        destP.add( destinationL );
        destP.add( Box.createHorizontalGlue() );

        JPanel fileP = new JPanel();
        fileP.setLayout( new BoxLayout( fileP, BoxLayout.LINE_AXIS ) );
        fileP.setBorder( BorderFactory.createEmptyBorder( 4, 0, 6, 0 ) );
		fileP.add( filenameHeaderL );
        fileP.add( filenameL );
        fileP.add( Box.createHorizontalGlue() );

        JPanel progressP = new JPanel( new BorderLayout() );
		progressP.add( transferProgressPB, BorderLayout.CENTER );

		JPanel transP = new JPanel();
		transP.setLayout( new BoxLayout( transP, BoxLayout.LINE_AXIS ) );
		transP.setBorder( BorderFactory.createEmptyBorder( 4, 0, 2, 0 ) );
        transP.add( transferredHeaderL );
        transP.add( transferredL );
        transP.add( Box.createHorizontalGlue() );

		topP.add( statusP );
		topP.add( sourceP );
		topP.add( destP );
		topP.add( fileP );
		topP.add( progressP );
		topP.add( transP );

        bottomP.add( Box.createHorizontalGlue() );
        bottomP.add( openB );
        bottomP.add( Box.createRigidArea( new Dimension( 8, 0 ) ) );
        bottomP.add( cancelB );

        getContentPane().add( topP, BorderLayout.NORTH );
        getContentPane().add( bottomP, BorderLayout.SOUTH );

		setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		updateTitle( 0 );
		setIconImage( imageLoader.getAppIcon().getImage() );
		getRootPane().setDefaultButton( cancelB );

		pack();
		setResizable( false );
		setVisible( true );
		fileTransfer.registerListener( this );
	}

	/**
	 * Changes the button text on the cancel button.
	 *
	 * @param text The new text on the button.
	 */
	public void setCancelButtonText( final String text )
	{
		cancelB.setText( text );
	}

	/**
	 * Gets the button text on the cancel button.
	 *
	 * @return The button text.
	 */
	public String getCancelButtonText()
	{
		return cancelB.getText();
	}

	/**
	 * Gets the file transfer object this dialog is listening to.
	 *
	 * @return The file transfer object.
	 */
	public FileTransfer getFileTransfer()
	{
		return fileTransfer;
	}

	/**
	 * Listener for the buttons.
	 *
	 * <p>The buttons:</p>
	 * <ul>
	 *   <li>Cancel/Close: cancels the file transfer, or closes the dialog
	 *       window if it's done transferring.</li>
	 *   <li>Open: opens the folder where the file was saved.</li>
	 * </ul>
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed( final ActionEvent event )
	{
		if ( event.getSource() == cancelB )
			mediator.transferCancelled( this );

		else if ( event.getSource() == openB )
		{
			File folder = fileTransfer.getFile().getParentFile();
			UITools.open( folder );
		}
	}

	/**
	 * This method is called from the file transfer object when
	 * the file transfer was completed successfully.
	 */
	@Override
	public void statusCompleted()
	{
		statusL.setForeground( new Color( 0, 176, 0 ) );

		if ( fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE )
		{
			statusL.setText( "File successfully received" );
			openB.setEnabled( true );
		}

		else if ( fileTransfer.getDirection() == FileTransfer.Direction.SEND )
			statusL.setText( "File successfully sent" );

		cancelB.setText( "Close" );
	}

	/**
	 * This method is called from the file transfer object when
	 * it is ready to connect.
	 */
	@Override
	public void statusConnecting()
	{
		statusL.setText( "Connecting..." );
	}

	/**
	 * This method is called from the file transfer object when
	 * a file transfer was canceled or failed somehow.
	 */
	@Override
	public void statusFailed()
	{
		statusL.setForeground( Color.RED );

		if ( fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE )
			statusL.setText( "Failed to receive file" );
		else if ( fileTransfer.getDirection() == FileTransfer.Direction.SEND )
			statusL.setText( "Failed to send file" );

		cancelB.setText( "Close" );
	}

	/**
	 * This method is called from the file transfer object when
	 * the connection was successful and the transfer is in progress.
	 */
	@Override
	public void statusTransferring()
	{
		if ( fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE )
			statusL.setText( "Receiving..." );
		else if ( fileTransfer.getDirection() == FileTransfer.Direction.SEND )
			statusL.setText( "Sending..." );
	}

	/**
	 * This method is called from the file transfer object when
	 * this dialog registers as a listener. Nothing is happening
	 * with the file transfer, but the necessary information to
	 * initialize the dialog fields are ready.
	 */
	@Override
	public void statusWaiting()
	{
		User me = Settings.getSettings().getMe();
		User other = fileTransfer.getUser();

		statusL.setText( "Waiting..." );

		if ( fileTransfer.getDirection() == FileTransfer.Direction.RECEIVE )
		{
			sourceL.setText( other.getNick() + " (" + other.getIpAddress() + ")" );
			destinationL.setText( me.getNick() + " (" + me.getIpAddress() + ")" );
			openB.setVisible( true );
		}

		else if ( fileTransfer.getDirection() == FileTransfer.Direction.SEND )
		{
			destinationL.setText( other.getNick() + " (" + other.getIpAddress() + ")" );
			sourceL.setText( me.getNick() + " (" + me.getIpAddress() + ")" );
		}

		String fileName = fileTransfer.getFile().getName();
		filenameL.setText( fileName );
		double width = UITools.getTextWidth( fileName, getGraphics(), filenameL.getFont() );

		if ( width > filenameL.getSize().width )
			filenameL.setToolTipText( fileName );
		else
			filenameL.setToolTipText( null );

		transferredL.setText( "0KB of "
				+ Tools.byteToString( fileTransfer.getFileSize() ) + " at 0KB/s" );
		transferProgressPB.setValue( 0 );
	}

	/**
	 * This method is called from the file transfer object when
	 * it's time to update the status of the file transfer.
	 * This happens several times while the file transfer is
	 * in progress.
	 */
	@Override
	public void transferUpdate()
	{
		transferredL.setText( Tools.byteToString( fileTransfer.getTransferred() ) + " of "
				+ Tools.byteToString( fileTransfer.getFileSize() ) + " at "
				+ Tools.byteToString( fileTransfer.getSpeed() ) + "/s" );
		transferProgressPB.setValue( fileTransfer.getPercent() );
		updateTitle( fileTransfer.getPercent() );
	}

	/**
	 * Updates the window title with percentage transferred.
	 *
	 * @param percent The percentage of the file transferred.
	 */
	private void updateTitle( final int percent )
	{
		setTitle( UITools.createTitle( percent + "% - File transfer" ) );
	}
}
