
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

/**
 * This is a list of all the images used by the application.
 *
 * @author Christian Ihle
 */
public interface Images
{
	/** Path to the icons. */
	String ICON_PATH = "/icons";

	/** Path to the smileys. */
	String SMILEY_PATH = "/smileys";

	/** The image used when there is no activity, and the user is not away. */
	String ICON_KOU_NORMAL = ICON_PATH + "/kou_normal.png";

	/** The image used when there is activity, and the user is not away. */
	String ICON_KOU_NORMAL_ACT = ICON_PATH + "/kou_normal_activity.png";

	/** The image used when there is no activity, and the user is away. */
	String ICON_KOU_AWAY = ICON_PATH + "/kou_away.png";

	/** The image used when there is activity, and the user is away. */
	String ICON_KOU_AWAY_ACT = ICON_PATH + "/kou_away_activity.png";

	/** Path to the envelope icon. */
	String ICON_ENVELOPE = ICON_PATH + "/envelope.png";

	/** Path to the dot icon. */
	String ICON_DOT = ICON_PATH + "/dot.png";

	/** The image for the <code>:)</code> smiley. */
	String SMILEY_SMILE = SMILEY_PATH + "/smile.png";

	/** The image for the <code>:(</code> smiley. */
	String SMILEY_SAD = SMILEY_PATH + "/sad.png";

	/** The image for the <code>:p</code> smiley. */
	String SMILEY_TONGUE = SMILEY_PATH + "/tongue.png";

	/** The image for the <code>:D</code> smiley. */
	String SMILEY_TEETH = SMILEY_PATH + "/teeth.png";

	/** The image for the <code>;)</code> smiley. */
	String SMILEY_WINK = SMILEY_PATH + "/wink.png";

	/** The image for the <code>:O</code> smiley. */
	String SMILEY_OMG = SMILEY_PATH + "/omg.png";

	/** The image for the <code>:@</code> smiley. */
	String SMILEY_ANGRY = SMILEY_PATH + "/angry.png";

	/** The image for the <code>:S</code> smiley. */
	String SMILEY_CONFUSED = SMILEY_PATH + "/confused.png";

	/** The image for the <code>;(</code> smiley. */
	String SMILEY_CRY = SMILEY_PATH + "/cry.png";

	/** The image for the <code>:$</code> smiley. */
	String SMILEY_EMBARRASSED = SMILEY_PATH + "/embarrassed.png";

	/** The image for the <code>8)</code> smiley. */
	String SMILEY_SHADE = SMILEY_PATH + "/shade.png";

	/** The icon used to identify the application. */
	String APP_ICON = ICON_KOU_NORMAL;
}
