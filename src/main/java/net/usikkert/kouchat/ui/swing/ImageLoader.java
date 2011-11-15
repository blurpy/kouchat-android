
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

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.util.ResourceValidator;

/**
 * Loads, validates and gives access to all the images used in the application.
 *
 * <p>Note: if any of the images fails to load the application will exit.</p>
 *
 * @author Christian Ihle
 */
public class ImageLoader
{
	/** The logger. */
	private static final Logger LOG = Logger.getLogger( ImageLoader.class.getName() );

	/** The smile image icon. */
	private final ImageIcon smileIcon;

	/** The sad image icon. */
	private final ImageIcon sadIcon;

	/** The tongue image icon. */
	private final ImageIcon tongueIcon;

	/** The teeth image icon. */
	private final ImageIcon teethIcon;

	/** The wink image icon. */
	private final ImageIcon winkIcon;

	/** The omg image icon. */
	private final ImageIcon omgIcon;

	/** The angry image icon. */
	private final ImageIcon angryIcon;

	/** The confused image icon. */
	private final ImageIcon confusedIcon;

	/** The cry image icon. */
	private final ImageIcon cryIcon;

	/** The embarrassed image icon. */
	private final ImageIcon embarrassedIcon;

	/** The shade image icon. */
	private final ImageIcon shadeIcon;

	/** The normal kou image icon. */
	private final ImageIcon kouNormalIcon;

	/** The normal activity kou image icon. */
	private final ImageIcon kouNormalActivityIcon;

	/** The away kou image icon. */
	private final ImageIcon kouAwayIcon;

	/** The away activity kou image icon. */
	private final ImageIcon kouAwayActivityIcon;

	/** The envelope image icon. */
	private final ImageIcon envelopeIcon;

	/** The dot image icon. */
	private final ImageIcon dotIcon;

	/** The application image icon. */
	private final ImageIcon appIcon;

	/**
	 * Constructor. Loads and validates the images.
	 */
	public ImageLoader()
	{
		ResourceValidator resourceValidator = new ResourceValidator();

		// Load resources from jar or local file system
		URL smileURL = loadImage( resourceValidator, Images.SMILEY_SMILE );
		URL sadURL = loadImage( resourceValidator, Images.SMILEY_SAD );
		URL tongueURL = loadImage( resourceValidator, Images.SMILEY_TONGUE );
		URL teethURL = loadImage( resourceValidator, Images.SMILEY_TEETH );
		URL winkURL = loadImage( resourceValidator, Images.SMILEY_WINK );
		URL omgURL = loadImage( resourceValidator, Images.SMILEY_OMG );
		URL angryURL = loadImage( resourceValidator, Images.SMILEY_ANGRY );
		URL confusedURL = loadImage( resourceValidator, Images.SMILEY_CONFUSED );
		URL cryURL = loadImage( resourceValidator, Images.SMILEY_CRY );
		URL embarrassedURL = loadImage( resourceValidator, Images.SMILEY_EMBARRASSED );
		URL shadeURL = loadImage( resourceValidator, Images.SMILEY_SHADE );
		URL kouNormURL = loadImage( resourceValidator, Images.ICON_KOU_NORMAL );
		URL kouNormActURL = loadImage( resourceValidator, Images.ICON_KOU_NORMAL_ACT );
		URL kouAwayURL = loadImage( resourceValidator, Images.ICON_KOU_AWAY );
		URL kouAwayActURL = loadImage( resourceValidator, Images.ICON_KOU_AWAY_ACT );
		URL envelopeURL = loadImage( resourceValidator, Images.ICON_ENVELOPE );
		URL dotURL = loadImage( resourceValidator, Images.ICON_DOT );

		validate( resourceValidator );

		// Create icons from the resources
		smileIcon = new ImageIcon( smileURL );
		sadIcon = new ImageIcon( sadURL );
		tongueIcon = new ImageIcon( tongueURL );
		teethIcon = new ImageIcon( teethURL );
		winkIcon = new ImageIcon( winkURL );
		omgIcon = new ImageIcon( omgURL );
		angryIcon = new ImageIcon( angryURL );
		confusedIcon = new ImageIcon( confusedURL );
		cryIcon = new ImageIcon( cryURL );
		embarrassedIcon = new ImageIcon( embarrassedURL );
		shadeIcon = new ImageIcon( shadeURL );
		kouNormalIcon = new ImageIcon( kouNormURL );
		kouNormalActivityIcon = new ImageIcon( kouNormActURL );
		kouAwayIcon = new ImageIcon( kouAwayURL );
		kouAwayActivityIcon = new ImageIcon( kouAwayActURL );
		envelopeIcon = new ImageIcon( envelopeURL );
		dotIcon = new ImageIcon( dotURL );
		appIcon = kouNormalIcon;
	}

	/**
	 * Loads the image to a URL, and updates the validator with the result.
	 * Either the image was loaded, or it was not.
	 *
	 * @param resourceValidator The validator.
	 * @param image The image to load, with path.
	 * @return The URL to the image, or <code>null</code> if the image wasn't loaded.
	 */
	private URL loadImage( final ResourceValidator resourceValidator, final String image )
	{
		URL url = getClass().getResource( image );
		resourceValidator.addResource( url, image );
		return url;
	}

	/**
	 * Goes through all the images, and checks if they were loaded successfully.
	 * If any of the images did not load successfully then a message is shown
	 * to the user, and the application exits.
	 *
	 * @param resourceValidator The validator.
	 */
	private void validate( final ResourceValidator resourceValidator )
	{
		String missing = resourceValidator.validate();

		if ( missing.length() > 0 )
		{
			String error = "These images were expected, but not found:\n\n" + missing + "\n\n"
					+ Constants.APP_NAME + " will now shutdown.";

			LOG.log( Level.SEVERE, error );
			ErrorHandler.getErrorHandler().showCriticalError( error );
			System.exit( 1 );
		}
	}

	/**
	 * Gets the smileIcon.
	 *
	 * @return The smileIcon.
	 */
	public ImageIcon getSmileIcon()
	{
		return smileIcon;
	}

	/**
	 * Gets the sadIcon.
	 *
	 * @return The sadIcon.
	 */
	public ImageIcon getSadIcon()
	{
		return sadIcon;
	}

	/**
	 * Gets the tongueIcon.
	 *
	 * @return The tongueIcon.
	 */
	public ImageIcon getTongueIcon()
	{
		return tongueIcon;
	}

	/**
	 * Gets the teethIcon.
	 *
	 * @return The teethIcon.
	 */
	public ImageIcon getTeethIcon()
	{
		return teethIcon;
	}

	/**
	 * Gets the winkIcon.
	 *
	 * @return The winkIcon.
	 */
	public ImageIcon getWinkIcon()
	{
		return winkIcon;
	}

	/**
	 * Gets the omgIcon.
	 *
	 * @return The omgIcon.
	 */
	public ImageIcon getOmgIcon()
	{
		return omgIcon;
	}

	/**
	 * Gets the angryIcon.
	 *
	 * @return The angryIcon.
	 */
	public ImageIcon getAngryIcon()
	{
		return angryIcon;
	}

	/**
	 * Gets the confusedIcon.
	 *
	 * @return The confusedIcon.
	 */
	public ImageIcon getConfusedIcon()
	{
		return confusedIcon;
	}

	/**
	 * Gets the cryIcon.
	 *
	 * @return The cryIcon.
	 */
	public ImageIcon getCryIcon()
	{
		return cryIcon;
	}

	/**
	 * Gets the embarrassedIcon.
	 *
	 * @return The embarrassedIcon.
	 */
	public ImageIcon getEmbarrassedIcon()
	{
		return embarrassedIcon;
	}

	/**
	 * Gets the shadeIcon.
	 *
	 * @return The shadeIcon.
	 */
	public ImageIcon getShadeIcon()
	{
		return shadeIcon;
	}

	/**
	 * Gets the kouNormalIcon.
	 *
	 * @return The kouNormalIcon.
	 */
	public ImageIcon getKouNormalIcon()
	{
		return kouNormalIcon;
	}

	/**
	 * Gets the kouNormalActivityIcon.
	 *
	 * @return The kouNormalActivityIcon.
	 */
	public ImageIcon getKouNormalActivityIcon()
	{
		return kouNormalActivityIcon;
	}

	/**
	 * Gets the kouAwayIcon.
	 *
	 * @return The kouAwayIcon.
	 */
	public ImageIcon getKouAwayIcon()
	{
		return kouAwayIcon;
	}

	/**
	 * Gets the kouAwayActivityIcon.
	 *
	 * @return The kouAwayActivityIcon.
	 */
	public ImageIcon getKouAwayActivityIcon()
	{
		return kouAwayActivityIcon;
	}

	/**
	 * Gets the envelopeIcon.
	 *
	 * @return The envelopeIcon.
	 */
	public ImageIcon getEnvelopeIcon()
	{
		return envelopeIcon;
	}

	/**
	 * Gets the dotIcon.
	 *
	 * @return The dotIcon.
	 */
	public ImageIcon getDotIcon()
	{
		return dotIcon;
	}

	/**
	 * Gets the appIcon.
	 *
	 * @return The appIcon.
	 */
	public ImageIcon getAppIcon()
	{
		return appIcon;
	}
}
